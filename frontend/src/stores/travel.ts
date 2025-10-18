import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { travelApi } from '@/services/travel'
import type { TravelPlan, ItineraryItem, ExpenseRecord, PaginationParams } from '@/types'

export const useTravelStore = defineStore('travel', () => {
  // 状态
  const travelPlans = ref<TravelPlan[]>([])
  const currentPlan = ref<TravelPlan | null>(null)
  const loading = ref(false)
  const pagination = ref({
    current: 1,
    pageSize: 10,
    total: 0
  })

  // 计算属性
  const planCount = computed(() => travelPlans.value.length)
  const activePlans = computed(() => 
    travelPlans.value.filter(plan => plan.status === 'active')
  )
  const completedPlans = computed(() => 
    travelPlans.value.filter(plan => plan.status === 'completed')
  )

  // 获取旅行计划列表
  const fetchTravelPlans = async (params?: PaginationParams) => {
    try {
      loading.value = true
      const response = await travelApi.getTravelPlans(params)
      
      if (response.success && response.data) {
        travelPlans.value = response.data.items
        pagination.value = {
          current: response.data.current,
          pageSize: response.data.pageSize,
          total: response.data.total
        }
      }
    } catch (error) {
      console.error('获取旅行计划失败:', error)
      ElMessage.error('获取旅行计划失败')
    } finally {
      loading.value = false
    }
  }

  // 获取单个旅行计划详情
  const fetchTravelPlan = async (id: string) => {
    try {
      loading.value = true
      const response = await travelApi.getTravelPlan(id)
      
      if (response.success && response.data) {
        currentPlan.value = response.data
        return response.data
      }
    } catch (error) {
      console.error('获取旅行计划详情失败:', error)
      ElMessage.error('获取旅行计划详情失败')
    } finally {
      loading.value = false
    }
  }

  // 创建旅行计划
  const createTravelPlan = async (planData: Partial<TravelPlan>) => {
    try {
      loading.value = true
      const response = await travelApi.createTravelPlan(planData)
      
      if (response.success && response.data) {
        travelPlans.value.unshift(response.data)
        ElMessage.success('旅行计划创建成功')
        return response.data
      }
    } catch (error) {
      console.error('创建旅行计划失败:', error)
      ElMessage.error('创建旅行计划失败')
    } finally {
      loading.value = false
    }
  }

  // 更新旅行计划
  const updateTravelPlan = async (id: string, planData: Partial<TravelPlan>) => {
    try {
      loading.value = true
      const response = await travelApi.updateTravelPlan(id, planData)
      
      if (response.success && response.data) {
        const index = travelPlans.value.findIndex(plan => plan.id === id)
        if (index !== -1) {
          travelPlans.value[index] = response.data
        }
        
        if (currentPlan.value?.id === id) {
          currentPlan.value = response.data
        }
        
        ElMessage.success('旅行计划更新成功')
        return response.data
      }
    } catch (error) {
      console.error('更新旅行计划失败:', error)
      ElMessage.error('更新旅行计划失败')
    } finally {
      loading.value = false
    }
  }

  // 删除旅行计划
  const deleteTravelPlan = async (id: string) => {
    try {
      loading.value = true
      const response = await travelApi.deleteTravelPlan(id)
      
      if (response.success) {
        travelPlans.value = travelPlans.value.filter(plan => plan.id !== id)
        
        if (currentPlan.value?.id === id) {
          currentPlan.value = null
        }
        
        ElMessage.success('旅行计划删除成功')
        return true
      }
    } catch (error) {
      console.error('删除旅行计划失败:', error)
      ElMessage.error('删除旅行计划失败')
    } finally {
      loading.value = false
    }
  }

  // AI生成旅行计划
  const generateTravelPlan = async (requirements: any) => {
    try {
      loading.value = true
      const response = await travelApi.generateTravelPlan(requirements)
      
      if (response.success && response.data) {
        ElMessage.success('AI旅行计划生成成功')
        return response.data
      }
    } catch (error) {
      console.error('生成旅行计划失败:', error)
      ElMessage.error('生成旅行计划失败')
    } finally {
      loading.value = false
    }
  }

  // 优化旅行计划
  const optimizeTravelPlan = async (id: string, preferences: any) => {
    try {
      loading.value = true
      const response = await travelApi.optimizeTravelPlan(id, preferences)
      
      if (response.success && response.data) {
        const index = travelPlans.value.findIndex(plan => plan.id === id)
        if (index !== -1) {
          travelPlans.value[index] = response.data
        }
        
        if (currentPlan.value?.id === id) {
          currentPlan.value = response.data
        }
        
        ElMessage.success('旅行计划优化成功')
        return response.data
      }
    } catch (error) {
      console.error('优化旅行计划失败:', error)
      ElMessage.error('优化旅行计划失败')
    } finally {
      loading.value = false
    }
  }

  // 添加行程项目
  const addItineraryItem = async (planId: string, item: Partial<ItineraryItem>) => {
    try {
      const response = await travelApi.addItineraryItem(planId, item)
      
      if (response.success && response.data) {
        if (currentPlan.value?.id === planId) {
          currentPlan.value.itinerary.push(response.data)
        }
        
        ElMessage.success('行程项目添加成功')
        return response.data
      }
    } catch (error) {
      console.error('添加行程项目失败:', error)
      ElMessage.error('添加行程项目失败')
    }
  }

  // 更新行程项目
  const updateItineraryItem = async (planId: string, itemId: string, item: Partial<ItineraryItem>) => {
    try {
      const response = await travelApi.updateItineraryItem(planId, itemId, item)
      
      if (response.success && response.data) {
        if (currentPlan.value?.id === planId) {
          const index = currentPlan.value.itinerary.findIndex(i => i.id === itemId)
          if (index !== -1) {
            currentPlan.value.itinerary[index] = response.data
          }
        }
        
        ElMessage.success('行程项目更新成功')
        return response.data
      }
    } catch (error) {
      console.error('更新行程项目失败:', error)
      ElMessage.error('更新行程项目失败')
    }
  }

  // 删除行程项目
  const deleteItineraryItem = async (planId: string, itemId: string) => {
    try {
      const response = await travelApi.deleteItineraryItem(planId, itemId)
      
      if (response.success) {
        if (currentPlan.value?.id === planId) {
          currentPlan.value.itinerary = currentPlan.value.itinerary.filter(i => i.id !== itemId)
        }
        
        ElMessage.success('行程项目删除成功')
        return true
      }
    } catch (error) {
      console.error('删除行程项目失败:', error)
      ElMessage.error('删除行程项目失败')
    }
  }

  // 复制旅行计划
  const copyTravelPlan = async (id: string) => {
    try {
      loading.value = true
      const response = await travelApi.copyTravelPlan(id)
      
      if (response.success && response.data) {
        travelPlans.value.unshift(response.data)
        ElMessage.success('旅行计划复制成功')
        return response.data
      }
    } catch (error) {
      console.error('复制旅行计划失败:', error)
      ElMessage.error('复制旅行计划失败')
    } finally {
      loading.value = false
    }
  }

  // 分享旅行计划
  const shareTravelPlan = async (id: string, shareSettings: any) => {
    try {
      const response = await travelApi.shareTravelPlan(id, shareSettings)
      
      if (response.success && response.data) {
        ElMessage.success('分享链接已生成')
        return response.data
      }
    } catch (error) {
      console.error('分享旅行计划失败:', error)
      ElMessage.error('分享旅行计划失败')
    }
  }

  // 导出旅行计划
  const exportTravelPlan = async (id: string, format: 'pdf' | 'excel') => {
    try {
      const response = await travelApi.exportTravelPlan(id, format)
      
      if (response.success) {
        ElMessage.success('导出成功')
        return response.data
      }
    } catch (error) {
      console.error('导出旅行计划失败:', error)
      ElMessage.error('导出旅行计划失败')
    }
  }

  // 获取推荐目的地
  const getRecommendedDestinations = async (preferences: any) => {
    try {
      const response = await travelApi.getRecommendedDestinations(preferences)
      
      if (response.success && response.data) {
        return response.data
      }
    } catch (error) {
      console.error('获取推荐目的地失败:', error)
      ElMessage.error('获取推荐目的地失败')
    }
  }

  // 获取预算建议
  const getBudgetSuggestions = async (destination: string, duration: number, preferences: any) => {
    try {
      const response = await travelApi.getBudgetSuggestions(destination, duration, preferences)
      
      if (response.success && response.data) {
        return response.data
      }
    } catch (error) {
      console.error('获取预算建议失败:', error)
      ElMessage.error('获取预算建议失败')
    }
  }

  // 清除当前计划
  const clearCurrentPlan = () => {
    currentPlan.value = null
  }

  // 重置状态
  const resetState = () => {
    travelPlans.value = []
    currentPlan.value = null
    pagination.value = {
      current: 1,
      pageSize: 10,
      total: 0
    }
  }

  return {
    // 状态
    travelPlans,
    currentPlan,
    loading,
    pagination,
    
    // 计算属性
    planCount,
    activePlans,
    completedPlans,
    
    // 方法
    fetchTravelPlans,
    fetchTravelPlan,
    createTravelPlan,
    updateTravelPlan,
    deleteTravelPlan,
    generateTravelPlan,
    optimizeTravelPlan,
    addItineraryItem,
    updateItineraryItem,
    deleteItineraryItem,
    copyTravelPlan,
    shareTravelPlan,
    exportTravelPlan,
    getRecommendedDestinations,
    getBudgetSuggestions,
    clearCurrentPlan,
    resetState
  }
})