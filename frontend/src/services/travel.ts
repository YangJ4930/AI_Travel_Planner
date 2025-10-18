import { http } from '@/utils/request'
import type { TravelPlan, CreatePlanRequest, ItineraryItem, ApiResponse, PaginatedResponse } from '@/types'

export interface GeneratePlanRequest {
  destination: string
  startDate: string
  endDate: string
  budget: number
  travelerCount: number
  preferences: string
  travelStyle?: string
  interests?: string[]
}

export interface OptimizePlanRequest {
  planId: number
  preferences?: string
}

// 旅行计划相关API
export const travelApi = {
  // 获取旅行计划列表
  getPlans(params?: {
    page?: number
    pageSize?: number
    status?: string
    keyword?: string
  }): Promise<ApiResponse<PaginatedResponse<TravelPlan>>> {
    return http.get('/plans', { params })
  },
  
  // 获取旅行计划详情
  getPlan(id: number): Promise<ApiResponse<TravelPlan>> {
    return http.get(`/plans/${id}`)
  },
  
  // 创建旅行计划
  createPlan(data: CreatePlanRequest): Promise<ApiResponse<TravelPlan>> {
    return http.post('/plans', data)
  },
  
  // 更新旅行计划
  updatePlan(id: number, data: Partial<TravelPlan>): Promise<ApiResponse<TravelPlan>> {
    return http.put(`/plans/${id}`, data)
  },
  
  // 删除旅行计划
  deletePlan(id: number): Promise<ApiResponse> {
    return http.delete(`/plans/${id}`)
  },
  
  // AI生成旅行计划
  generatePlan(data: GeneratePlanRequest): Promise<ApiResponse<TravelPlan>> {
    return http.post('/plans/generate', data)
  },
  
  // 优化旅行计划
  optimizePlan(data: OptimizePlanRequest): Promise<ApiResponse<TravelPlan>> {
    return http.post('/plans/optimize', data)
  },
  
  // 获取行程项目列表
  getItineraryItems(planId: number): Promise<ApiResponse<ItineraryItem[]>> {
    return http.get(`/plans/${planId}/itinerary`)
  },
  
  // 添加行程项目
  addItineraryItem(planId: number, data: Omit<ItineraryItem, 'id' | 'travelPlanId'>): Promise<ApiResponse<ItineraryItem>> {
    return http.post(`/plans/${planId}/itinerary`, data)
  },
  
  // 更新行程项目
  updateItineraryItem(planId: number, itemId: number, data: Partial<ItineraryItem>): Promise<ApiResponse<ItineraryItem>> {
    return http.put(`/plans/${planId}/itinerary/${itemId}`, data)
  },
  
  // 删除行程项目
  deleteItineraryItem(planId: number, itemId: number): Promise<ApiResponse> {
    return http.delete(`/plans/${planId}/itinerary/${itemId}`)
  },
  
  // 复制旅行计划
  copyPlan(id: number, title?: string): Promise<ApiResponse<TravelPlan>> {
    return http.post(`/plans/${id}/copy`, { title })
  },
  
  // 分享旅行计划
  sharePlan(id: number): Promise<ApiResponse<{ shareUrl: string }>> {
    return http.post(`/plans/${id}/share`)
  },
  
  // 导出旅行计划
  exportPlan(id: number, format: 'pdf' | 'excel'): Promise<Blob> {
    return http.download(`/plans/${id}/export?format=${format}`)
  },
  
  // 获取推荐目的地
  getRecommendedDestinations(params?: {
    budget?: number
    season?: string
    interests?: string[]
  }): Promise<ApiResponse<string[]>> {
    return http.get('/plans/recommendations/destinations', { params })
  },
  
  // 获取预算建议
  getBudgetSuggestion(params: {
    destination: string
    days: number
    travelerCount: number
    travelStyle?: string
  }): Promise<ApiResponse<{ minBudget: number; maxBudget: number; avgBudget: number }>> {
    return http.get('/plans/budget-suggestion', { params })
  }
}