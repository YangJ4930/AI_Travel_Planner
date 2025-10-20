let amapLoadPromise: Promise<typeof window.AMap> | null = null;

function injectScript(src: string) {
  return new Promise<void>((resolve, reject) => {
    const existing = document.querySelector(`script[src="${src}"]`) as HTMLScriptElement | null;
    if (existing) {
      existing.addEventListener('load', () => resolve());
      existing.addEventListener('error', reject);
      // If already loaded
      if ((window as any).AMap) resolve();
      return;
    }
    const script = document.createElement('script');
    script.src = src;
    script.async = true;
    script.defer = true;
    script.onload = () => resolve();
    script.onerror = (e) => reject(e);
    document.head.appendChild(script);
  });
}

export async function loadAMap(): Promise<typeof window.AMap> {
  if ((window as any).AMap) return (window as any).AMap;
  if (amapLoadPromise) return amapLoadPromise;

  amapLoadPromise = new Promise(async (resolve, reject) => {
    try {
      // 从环境变量获取API Key
      const key = import.meta.env.VITE_AMAP_KEY as string | undefined;
      console.log('[AMap] Using API key from environment variables');
      if (!key) {
        reject(new Error('VITE_AMAP_KEY is not set. Add it to frontend/.env.local'));
        return;
      }

      // 注入安全密钥（若开启了服务安全）
      const securityJsCode = import.meta.env.VITE_AMAP_SECURITY_JS_CODE as string | undefined;
      if (securityJsCode) {
        console.log('[AMap] Security config set from environment variables');
        (window as any)._AMapSecurityConfig = { securityJsCode };
      } else {
        console.log('[AMap] No security config found in environment variables');
      }

      const plugins = (import.meta.env.VITE_AMAP_PLUGINS as string | undefined) ||
        'AMap.Driving,AMap.PlaceSearch,AMap.Scale,AMap.ToolBar,AMap.MarkerClusterer';
      const src = `https://webapi.amap.com/maps?v=2.0&key=${key}&plugin=${encodeURIComponent(plugins)}`;
      await injectScript(src);
      const AMap = (window as any).AMap as typeof window.AMap;
      if (!AMap) {
        reject(new Error('AMap failed to load'));
        return;
      }
      resolve(AMap);
    } catch (e) {
      reject(e);
    }
  });

  return amapLoadPromise;
}

export type KeywordMarker = {
  keyword: string;
  city?: string;
};