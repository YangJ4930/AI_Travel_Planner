import { http } from '@/utils/request'

// 旅行计划相关接口
export interface TravelQueryParam {
  query: string
}

export interface TravelPlanVo {
  id: number
  title: string
  query: string
  content: TravelPlanContentDto[]
  createTime: string
  updateTime: string
}

export interface TravelPlanContentDto {
  city: string
  date: string
  keyword: string
  reason: string
  cost: number
  travel: string
}

export interface TravelPlanDetailVo {
  id: number
  title: string
  query: string
  content: TravelPlanContentDto[]
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data?: T
}

// 旅行计划API
export const travelPlanApi = {
  // 添加旅行计划（生成）——设置较长超时（5分钟）
  addTravelPlan(data: TravelQueryParam): Promise<ApiResponse<void>> {
    return http.post('/travel-plan', data, { timeout: 300000 })
  },

  // 获取旅行计划列表
  listTravelPlan(): Promise<ApiResponse<TravelPlanVo[]>> {
    return http.get('/travel-plan/list')
  },

  // 获取旅行计划详情
  getTravelPlan(id: number): Promise<ApiResponse<TravelPlanDetailVo>> {
    return http.get(`/travel-plan/${id}`)
  }
}