<template>
  <div class="travel-plan-detail">
    <div class="container mx-auto px-4 py-8">
      <!-- 返回按钮 -->
      <div class="mb-6">
        <el-button @click="goBack" class="mb-4">
          <el-icon class="mr-2"><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>

      <div v-loading="loading" class="min-h-96">
        <!-- 计划详情 -->
        <div v-if="planDetail">
          <!-- 计划头部信息 -->
          <el-card class="shadow-lg mb-8">
            <template #header>
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <el-icon class="text-3xl text-blue-500 mr-4"><LocationFilled /></el-icon>
                  <div>
                    <h1 class="text-2xl font-bold text-gray-800">{{ planDetail.title || '旅行计划详情' }}</h1>
                    <p class="text-gray-500 mt-1">计划ID: {{ planDetail.id }}</p>
                  </div>
                </div>
                <div class="flex space-x-3">
                  <el-button type="primary" @click="editPlan">
                    <el-icon class="mr-2"><Edit /></el-icon>
                    编辑计划
                  </el-button>
                  <el-button @click="sharePlan">
                    <el-icon class="mr-2"><Share /></el-icon>
                    分享计划
                  </el-button>
                </div>
              </div>
            </template>

            <!-- 原始查询 -->
            <div class="space-y-4">
              <div>
                <h3 class="text-lg font-semibold text-gray-700 mb-3">
                  <el-icon class="mr-2"><ChatDotRound /></el-icon>
                  您的旅行需求
                </h3>
                <div class="bg-gray-50 p-4 rounded-lg border-l-4 border-blue-400">
                  <p class="text-gray-700 leading-relaxed">{{ planDetail.query }}</p>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 地图参考 -->
          <el-card class="shadow-lg mb-8">
            <template #header>
              <div class="flex items-center">
                <el-icon class="text-2xl text-blue-500 mr-3"><MapLocation /></el-icon>
                <span class="text-xl font-semibold">地图参考</span>
                <el-tag v-if="mapMarkers.length" type="info" class="ml-3">标注 {{ mapMarkers.length }} 处</el-tag>
              </div>
            </template>
            <PlanMap :markers="mapMarkers" :drawRoutes="true" :enableSearchUI="true" />
          </el-card>

          <!-- 详细行程计划 -->
          <div v-if="Array.isArray(planDetail.content) && planDetail.content.length > 0">
            <div class="flex items-center mb-6">
              <el-icon class="text-2xl text-green-500 mr-3"><Calendar /></el-icon>
              <h2 class="text-xl font-bold text-gray-800">详细行程安排</h2>
              <el-tag type="info" class="ml-3">共 {{ planDetail.content.length }} 项安排</el-tag>
            </div>

            <!-- 行程时间线 -->
            <div class="relative">
              <!-- 时间线 -->
              <div class="absolute left-8 top-0 bottom-0 w-0.5 bg-gradient-to-b from-blue-400 to-green-400"></div>

              <!-- 行程卡片 -->
              <div class="space-y-6">
                <div
                  v-for="(item, index) in planDetail.content"
                  :key="index"
                  class="relative flex items-start"
                >
                  <!-- 时间线节点 -->
                  <div class="relative z-10 flex items-center justify-center w-16 h-16 bg-white border-4 border-blue-400 rounded-full shadow-lg">
                    <span class="text-sm font-bold text-blue-600">{{ index + 1 }}</span>
                  </div>

                  <!-- 行程卡片 -->
                  <div class="flex-1 ml-6">
                    <el-card class="shadow-md hover:shadow-lg transition-shadow duration-300">
                      <template #header>
                        <div class="flex items-center justify-between">
                          <div class="flex items-center">
                            <el-icon class="text-xl text-orange-500 mr-3"><MapLocation /></el-icon>
                            <div>
                              <h3 class="text-lg font-semibold text-gray-800">{{ item.city }}</h3>
                              <p class="text-sm text-gray-500">{{ item.date }}</p>
                            </div>
                          </div>
                          <div class="flex items-center space-x-2">
                            <el-tag v-if="item.cost" type="warning" size="small">
                              ¥{{ item.cost }}
                            </el-tag>
                            <el-tag type="success" size="small">{{ item.keyword }}</el-tag>
                          </div>
                        </div>
                      </template>

                      <div class="space-y-4">
                        <!-- 推荐理由 -->
                        <div v-if="item.reason">
                          <h4 class="font-medium text-gray-700 mb-2 flex items-center">
                            <el-icon class="mr-2 text-purple-500"><Star /></el-icon>
                            推荐理由
                          </h4>
                          <p class="text-gray-600 leading-relaxed bg-purple-50 p-3 rounded-lg">
                            {{ item.reason }}
                          </p>
                        </div>

                        <!-- 交通方式 -->
                        <div v-if="item.travel">
                          <h4 class="font-medium text-gray-700 mb-2 flex items-center">
                            <el-icon class="mr-2 text-green-500"><Position /></el-icon>
                            交通方式
                          </h4>
                          <div class="flex items-center space-x-2">
                            <el-tag type="success" effect="light">{{ item.travel }}</el-tag>
                          </div>
                        </div>

                        <!-- 预估费用 -->
                        <div v-if="item.cost" class="flex items-center justify-between pt-3 border-t border-gray-100">
                          <span class="text-gray-600">预估费用</span>
                          <span class="text-lg font-semibold text-orange-600">¥{{ item.cost }}</span>
                        </div>
                      </div>
                    </el-card>
                  </div>
                </div>
              </div>
            </div>

            <!-- 费用统计 -->
            <div class="mt-8">
              <el-card class="shadow-lg">
                <template #header>
                  <div class="flex items-center">
                    <el-icon class="text-2xl text-yellow-500 mr-3"><Wallet /></el-icon>
                     <span class="text-xl font-semibold">费用统计</span>
                  </div>
                </template>

                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <div class="text-center p-4 bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
                    <div class="text-2xl font-bold text-blue-600">{{ totalCost }}</div>
                    <div class="text-sm text-gray-600 mt-1">总预算</div>
                  </div>
                  <div class="text-center p-4 bg-gradient-to-br from-green-50 to-green-100 rounded-lg">
                    <div class="text-2xl font-bold text-green-600">{{ averageCost }}</div>
                    <div class="text-sm text-gray-600 mt-1">平均每日</div>
                  </div>
                  <div class="text-center p-4 bg-gradient-to-br from-purple-50 to-purple-100 rounded-lg">
                    <div class="text-2xl font-bold text-purple-600">{{ planDetail.content.length }}</div>
                    <div class="text-sm text-gray-600 mt-1">行程天数</div>
                  </div>
                </div>
              </el-card>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="text-center py-16">
            <el-empty description="暂无详细行程安排">
              <el-button type="primary" @click="editPlan">添加行程</el-button>
            </el-empty>
          </div>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="!loading" class="text-center py-16">
          <el-result
            icon="error"
            title="加载失败"
            sub-title="无法获取计划详情，请稍后重试"
          >
            <template #extra>
              <el-button type="primary" @click="fetchPlanDetail">重新加载</el-button>
              <el-button @click="goBack">返回列表</el-button>
            </template>
          </el-result>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  LocationFilled,
  Edit,
  Share,
  ChatDotRound,
  Calendar,
  MapLocation,
  Star,
  Position,
  Wallet
} from '@element-plus/icons-vue'
import { travelPlanApi, type TravelPlanDetailVo } from '@/services/travelPlan'
import PlanMap from '@/components/PlanMap.vue'

const route = useRoute()
const router = useRouter()

// 状态
const loading = ref(false)
const planDetail = ref<TravelPlanDetailVo | null>(null)

// 计算属性
const totalCost = computed(() => {
  if (!planDetail.value?.content) return '¥0'
  const total = planDetail.value.content.reduce((sum, item) => sum + (item.cost || 0), 0)
  return `¥${total.toLocaleString()}`
})

const averageCost = computed(() => {
  if (!planDetail.value?.content || planDetail.value.content.length === 0) return '¥0'
  const total = planDetail.value.content.reduce((sum, item) => sum + (item.cost || 0), 0)
  const average = Math.round(total / planDetail.value.content.length)
  return `¥${average.toLocaleString()}`
})

// 地图标注数据，由行程中的 keyword 和 city 生成
const mapMarkers = computed(() => {
  const items = planDetail.value?.content || []
  return items
    .map((it: any) => {
      const keyword = it?.keyword || it?.title || ''
      const city = it?.city || undefined
      if (!keyword) return null
      return { keyword, city }
    })
    .filter(Boolean) as { keyword: string; city?: string }[]
})

// 获取计划详情
const fetchPlanDetail = async () => {
  const planId = Number(route.params.id)
  if (!planId) {
    ElMessage.error('无效的计划ID')
    goBack()
    return
  }

  try {
    loading.value = true
    const response = await travelPlanApi.getTravelPlan(planId)
    
    if (response.code === 0 && response.data) {
      const detail = response.data as any
      if (typeof detail.content === 'string') {
        try {
          const parsed = JSON.parse(detail.content)
          detail.content = Array.isArray(parsed) ? parsed : []
        } catch (e) {
          console.warn('计划内容解析失败，已使用空列表', e)
          detail.content = []
        }
      }
      if (!Array.isArray(detail.content)) {
        detail.content = []
      }
      planDetail.value = detail
    } else {
      ElMessage.error(response.message || '获取计划详情失败')
    }
  } catch (error) {
    console.error('获取计划详情失败:', error)
    ElMessage.error('获取计划详情失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 返回列表
const goBack = () => {
  router.replace({ name: 'travel-plans' })
}

// 编辑计划
const editPlan = () => {
  ElMessage.info('编辑功能开发中...')
}

// 分享计划
const sharePlan = () => {
  if (navigator.share) {
    navigator.share({
      title: planDetail.value?.title || '我的旅行计划',
      text: planDetail.value?.query || '',
      url: window.location.href
    }).catch(console.error)
  } else {
    // 复制链接到剪贴板
    navigator.clipboard.writeText(window.location.href).then(() => {
      ElMessage.success('链接已复制到剪贴板')
    }).catch(() => {
      ElMessage.error('分享失败')
    })
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchPlanDetail()
})
</script>

<style scoped>
.travel-plan-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 50%, #f0f9ff 100%);
}

:deep(.el-card) {
  border-radius: 16px;
  border: none;
  overflow: hidden;
}

:deep(.el-card__header) {
  background: linear-gradient(90deg, #ffffff 0%, #f8fafc 100%);
  border-bottom: 1px solid #e2e8f0;
  padding: 20px 24px;
}

:deep(.el-card__body) {
  padding: 24px;
}

.timeline-node {
  position: relative;
  z-index: 10;
}

.timeline-card {
  transition: all 0.3s ease;
}

.timeline-card:hover {
  transform: translateX(8px);
}

:deep(.el-tag) {
  border-radius: 12px;
  font-weight: 500;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border: none;
  border-radius: 8px;
  font-weight: 500;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
}

.cost-card {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: 1px solid #f59e0b;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .timeline-card {
    margin-left: 0;
  }
  
  .timeline-node {
    display: none;
  }
  
  .absolute.left-8 {
    display: none;
  }
}
</style>