<template>
  <div class="plan-map" style="position: relative;">
    <div ref="mapRef" class="map-container"></div>

    <div v-if="enableSearchUI" class="map-controls-overlay">
      <div class="map-toolbar">
        <el-select v-model="selectedCity" placeholder="选择城市" class="w-40 mr-3" clearable>
          <el-option v-for="c in availableCities" :key="c" :label="c" :value="c" />
        </el-select>
        <el-select v-model="selectedKeyword" placeholder="选择关键字" class="w-56 mr-3" clearable filterable>
          <el-option v-for="k in availableKeywords" :key="k" :label="k" :value="k" />
        </el-select>
      </div>
      <div class="chip-row">
        <span class="chip-label">快速选择关键字：</span>
        <div class="chip-wrap">
          <el-tag
            v-for="k in quickKeywords"
            :key="k"
            :type="selectedKeyword === k ? 'primary' : 'info'"
            class="chip"
            @click="selectKeywordAndSearch(k)"
          >{{ k }}</el-tag>
        </div>
      </div>
    </div>

    <div v-if="enableSearchUI" ref="panelRef" class="search-panel"></div>
    <div v-if="error" class="map-error">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, watch, computed } from 'vue';
import { loadAMap, type KeywordMarker } from '@/utils/amap';

const props = defineProps<{
  markers: KeywordMarker[];
  drawRoutes?: boolean;
  enableSearchUI?: boolean;
}>();

const enableSearchUI = computed(() => props.enableSearchUI !== false);

const mapRef = ref<HTMLDivElement | null>(null);
const panelRef = ref<HTMLDivElement | null>(null);
let map: any = null;
let currentMarkers: any[] = [];
let currentPlaceSearch: any = null;
let driving: any | null = null;
const error = ref<string | null>(null);

const availableCities = computed(() => {
  const set = new Set<string>();
  (props.markers || []).forEach(m => { if (m.city) set.add(m.city!); });
  return Array.from(set);
});

const availableKeywords = computed(() => {
  const set = new Set<string>();
  (props.markers || []).forEach(m => { if (m.keyword) set.add(m.keyword); });
  return Array.from(set);
});

const quickKeywords = computed(() => availableKeywords.value.slice(0, 12));

const selectedCity = ref<string | undefined>(undefined);
const selectedKeyword = ref<string | undefined>(undefined);

watch(availableCities, (cities) => {
  if (!selectedCity.value && cities.length > 0) selectedCity.value = cities[0];
}, { immediate: true });

watch(availableKeywords, (kws) => {
  if (!selectedKeyword.value && kws.length > 0) selectedKeyword.value = kws[0];
}, { immediate: true });

watch(selectedKeyword, (newKeyword) => {
  if (newKeyword) {
    searchPOI();
  }
});

function clearBaseMarkers() {
  if (!map) return;
  if (driving) {
    try { driving.clear(); } catch {}
    driving = null;
  }
  if (currentMarkers.length) {
    currentMarkers.forEach(m => { try { map.remove(m); } catch {} });
    currentMarkers = [];
  }
}

function clearSearchResults() {
  if (currentPlaceSearch) {
    try { currentPlaceSearch.clear(); } catch {}
  }
  if (panelRef.value) {
    panelRef.value.innerHTML = '';
  }
}

async function render() {
  try {
    error.value = null;
    const AMap = await loadAMap();
    if (!map) {
      map = new AMap.Map(mapRef.value!, {
        viewMode: '3D',
        zoom: 11,
        resizeEnable: true,
      });
      AMap.plugin(['AMap.ToolBar', 'AMap.Scale'], () => {
        map.addControl(new AMap.ToolBar());
        map.addControl(new AMap.Scale());
      });
    }
    clearBaseMarkers();

    if (!props.markers || !props.markers.length) {
      return;
    }

    const points: [number, number][] = [];

    await new Promise<void>((resolve) => {
      AMap.plugin('AMap.PlaceSearch', () => {
        const doSearchSequential = async () => {
          for (const mk of props.markers) {
            await new Promise<void>((res) => {
              const placeSearch = new AMap.PlaceSearch({
                city: mk.city || '全国',
                citylimit: !!mk.city,
              });
              placeSearch.search(mk.keyword, (status: string, result: any) => {
                try {
                  if (status === 'complete' && result?.poiList?.pois?.length) {
                    const poi = result.poiList.pois[0];
                    const loc = poi.location;
                    const lng = typeof loc?.getLng === 'function' ? Number(loc.getLng()) : Number(loc?.lng);
                    const lat = typeof loc?.getLat === 'function' ? Number(loc.getLat()) : Number(loc?.lat);
                    if (Number.isFinite(lng) && Number.isFinite(lat)) {
                      const lnglat: [number, number] = [lng, lat];
                      const marker = new AMap.Marker({ position: lnglat, title: poi.name });
                      map.add(marker);
                      currentMarkers.push(marker);
                      points.push(lnglat);
                    } else {
                      console.warn('[PlanMap] Invalid POI location, skip', poi.location);
                    }
                  }
                } finally {
                  res();
                }
              });
            });
          }
          resolve();
        };
        doSearchSequential();
      });
    });

    if (points.length) {
      map.setFitView(currentMarkers);
    }

    if (props.drawRoutes && points.length >= 2) {
      AMap.plugin('AMap.Driving', () => {
        driving = new AMap.Driving({ map, policy: (AMap as any).DrivingPolicy?.LEAST_TIME });
        for (let i = 0; i < points.length - 1; i++) {
          try { driving.search(points[i], points[i + 1]); } catch {}
        }
      });
    }
  } catch (e: any) {
    error.value = e?.message || '地图加载失败';
  }
}

async function searchPOI() {
  try {
    const AMap = await loadAMap();
    const city = selectedCity.value || '全国';
    const keyword = selectedKeyword.value || '';
    console.log('[PlanMap] searchPOI invoked', { city, keyword });
    if (!keyword) {
      console.warn('[PlanMap] searchPOI aborted: empty keyword');
      return;
    }

    clearSearchResults();

    // 创建 PlaceSearch 实例
    AMap.plugin(['AMap.PlaceSearch'], () => {
      console.log('[PlanMap] Creating PlaceSearch', { city, keyword });
      currentPlaceSearch = new AMap.PlaceSearch({
        pageSize: 5, // 减少每页结果数量
        pageIndex: 1,
        city,
        citylimit: !!selectedCity.value,
        map,
        panel: panelRef.value,
        autoFitView: false,
      });

      currentPlaceSearch.search(keyword, (status: string, result: any) => {
        console.log('[PlanMap] PlaceSearch callback', { status, result });
        if (status === 'complete' && result?.poiList?.pois?.length) {
          // Manually set the map view to the search results
          map.setFitView(currentPlaceSearch.getMarkers());
        } else {
          console.warn('[PlanMap] No search result, center city');
          // 无结果：将地图居中到所选城市
          AMap.plugin('AMap.Geocoder', () => {
            const geocoder = new AMap.Geocoder({ city });
            geocoder.getLocation(city, (gStatus: string, gResult: any) => {
              console.log('[PlanMap] Geocoder result', { gStatus, gResult });
              if (gStatus === 'complete' && gResult?.geocodes?.length) {
                const loc = gResult.geocodes[0].location;
                const lng = typeof loc?.getLng === 'function' ? Number(loc.getLng()) : Number(loc?.lng);
                const lat = typeof loc?.getLat === 'function' ? Number(loc.getLat()) : Number(loc?.lat);
                if (Number.isFinite(lng) && Number.isFinite(lat)) {
                  const center: [number, number] = [lng, lat];
                  try { map.setZoomAndCenter(12, center); } catch {}
                } else {
                  console.warn('[PlanMap] Invalid geocoder location, skip centering');
                }
              }
            });
          });
        }
      });
    });

  } catch (e: any) {
    error.value = e?.message || 'POI 搜索失败';
    console.error('[PlanMap] searchPOI error', e);
  }
}

function selectKeywordAndSearch(k: string) {
  selectedKeyword.value = k;
}

onMounted(render);
watch(() => props.markers, () => render(), { deep: true });

onBeforeUnmount(() => {
  clearBaseMarkers();
  clearSearchResults();
  map = null;
});
</script>

<style scoped>
.map-container { height: 380px; width: 100%; border-radius: 12px; overflow: hidden; }
.search-panel { 
  position: absolute; 
  background-color: white; 
  max-height: 200px; 
  overflow-y: auto; 
  top: 100px; /* Adjusted top position */
  right: 10px; 
  width: 280px; 
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 20;
}
.map-error { margin-top: 8px; color: #ef4444; font-size: 13px; }
.map-controls-overlay {
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  z-index: 10;
  background: rgba(255, 255, 255, 0.8);
  padding: 8px;
  border-radius: 8px;
  backdrop-filter: blur(4px);
}
.map-toolbar { display: flex; align-items: center; margin-bottom: 8px; }
.chip-row { display: flex; align-items: center; flex-wrap: wrap; }
.chip-label { color: #6b7280; font-size: 13px; margin-right: 4px; }
.chip-wrap { display: flex; gap: 6px; flex-wrap: wrap; }
.chip { cursor: pointer; }
</style>