<template>
  <div class="travel-plan-list">
    <div class="container mx-auto px-4 py-8">
      <!-- 页面头部 -->
      <div class="flex justify-between items-center mb-8">
        <div>
          <h1 class="text-3xl font-bold text-gray-800 mb-2">我的旅行计划</h1>
          <p class="text-gray-600">管理和查看您的所有旅行计划</p>
        </div>
        <div class="flex items-center space-x-3">
          <el-button @click="refreshList" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button 
            type="primary" 
            size="large"
            @click="createNewPlan"
            class="px-6 py-3"
          >
            <el-icon class="mr-2"><Plus /></el-icon>
            创建新计划
          </el-button>
        </div>
      </div>

      <!-- 工具栏已合并到页头（删除下方刷新行） -->

      <!-- 计划列表 -->
      <div v-loading="loading" class="min-h-96">
        <!-- 空状态 -->
        <div v-if="!loading && filteredPlans.length === 0" class="text-center py-16">
          <el-empty 
            description="暂无旅行计划"
            :image-size="120"
          >
            <template #description>
              <p class="text-gray-500 mb-4">
                您还没有创建任何旅行计划
              </p>
            </template>
            <el-button type="primary" @click="createNewPlan">
              立即创建
            </el-button>
          </el-empty>
        </div>

        <!-- 计划卡片网格 -->
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="plan in filteredPlans"
            :key="plan.id"
            class="plan-card cursor-pointer transform transition-all duration-300 hover:scale-105 hover:shadow-xl"
            @click="viewPlanDetail(plan.id)"
          >
            <el-card class="h-full shadow-lg border-0">
              <template #header>
                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <el-icon class="text-2xl text-blue-500 mr-3"><LocationFilled /></el-icon>
                    <span class="font-semibold text-lg truncate">{{ plan.title || '旅行计划' }}</span>
                  </div>
                  <el-dropdown @command="handleCommand" trigger="click">
                    <el-button type="text" class="text-gray-400 hover:text-gray-600">
                      <el-icon><MoreFilled /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item :command="{action: 'view', id: plan.id}">
                          <el-icon><View /></el-icon>
                          查看详情
                        </el-dropdown-item>
                        <el-dropdown-item :command="{action: 'edit', id: plan.id}">
                          <el-icon><Edit /></el-icon>
                          编辑计划
                        </el-dropdown-item>
                        <el-dropdown-item :command="{action: 'delete', id: plan.id}" divided>
                          <el-icon><Delete /></el-icon>
                          删除计划
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </template>

              <div class="space-y-4">
                <!-- 计划描述 -->
                <div class="text-gray-600 text-sm leading-relaxed">
                  <p class="line-clamp-3">{{ plan.query }}</p>
                </div>

                <!-- 计划标签 -->
                <div class="flex flex-wrap gap-2">
                  <el-tag 
                    v-for="tag in extractTags(plan.query)" 
                    :key="tag"
                    size="small"
                    type="info"
                    effect="light"
                  >
                    {{ tag }}
                  </el-tag>
                </div>

                <!-- 底部操作：移除易误解日期，仅保留查看详情按钮 -->
                <div class="flex items-center justify-end pt-4 border-t border-gray-100">
                  <el-button 
                    type="primary" 
                    size="small"
                    @click.stop="viewPlanDetail(plan.id)"
                  >
                    查看详情
                    <el-icon class="ml-1"><ArrowRight /></el-icon>
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="filteredPlans.length > 0" class="flex justify-center mt-8">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[6, 12, 18, 24]"
          :total="filteredPlans.length"
          layout="total, sizes, prev, pager, next, jumper"
          class="justify-center"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  Refresh, 
  LocationFilled, 
  MoreFilled, 
  View, 
  Edit, 
  Delete, 
  Calendar, 
  ArrowRight 
} from '@element-plus/icons-vue'
import { travelPlanApi, type TravelPlanVo } from '@/services/travelPlan'

const router = useRouter()

// 状态
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(6)
const planList = ref<TravelPlanVo[]>([])

// 计算属性
const filteredPlans = computed(() => planList.value)

// 获取计划列表
const fetchPlanList = async () => {
  try {
    loading.value = true
    const response = await travelPlanApi.listTravelPlan()
    
    if (response.code === 0 && response.data) {
      planList.value = response.data
    } else {
      ElMessage.error(response.message || '获取计划列表失败')
    }
  } catch (error) {
    console.error('获取计划列表失败:', error)
    ElMessage.error('获取计划列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 刷新列表
const refreshList = () => {
  fetchPlanList()
}

// 创建新计划（跳转到 AI 规划输入页，完成后返回列表）
const createNewPlan = () => {
  router.push('/planning?redirect=/travel-plans')
}

// 查看计划详情
const viewPlanDetail = (id: number) => {
  router.push(`/travel-plans/${id}`)
}

// 处理下拉菜单命令
const handleCommand = async (command: {action: string, id: number}) => {
  const { action, id } = command
  
  switch (action) {
    case 'view':
      viewPlanDetail(id)
      break
    case 'edit':
      // TODO: 实现编辑功能
      ElMessage.info('编辑功能开发中...')
      break
    case 'delete':
      await deletePlan(id)
      break
  }
}

// 删除计划
const deletePlan = async (id: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个旅行计划吗？删除后无法恢复。',
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // TODO: 调用删除API
    planList.value = planList.value.filter(plan => plan.id !== id)
    ElMessage.success('计划删除成功')
  } catch (error) {
    // 用户取消删除
  }
}

// 提取标签
const extractTags = (query: string): string[] => {
  const tags: string[] = []
  
  // 简单的关键词提取逻辑
  if (query.includes('家庭') || query.includes('家人')) tags.push('家庭出游')
  if (query.includes('预算') || query.includes('元')) tags.push('预算规划')
  if (query.includes('美食') || query.includes('小吃')) tags.push('美食之旅')
  if (query.includes('文化') || query.includes('历史')) tags.push('文化体验')
  if (query.includes('自然') || query.includes('风景')) tags.push('自然风光')
  if (query.includes('摄影')) tags.push('摄影旅行')
  
  return tags.slice(0, 3) // 最多显示3个标签
}

// 格式化日期
const formatDate = (timestamp: number): string => {
  const date = new Date(timestamp)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// 组件挂载时获取数据
onMounted(() => {
  fetchPlanList()
})
</script>

<style scoped>
.travel-plan-list {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.plan-card {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.plan-card:hover {
  transform: translateY(-8px);
}

:deep(.el-card) {
  border-radius: 16px;
  border: none;
  overflow: hidden;
}

:deep(.el-card__header) {
  background: linear-gradient(90deg, #ffffff 0%, #f8fafc 100%);
  border-bottom: 1px solid #e2e8f0;
  padding: 16px 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

:deep(.el-tag) {
  border-radius: 12px;
  font-size: 12px;
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

:deep(.el-pagination) {
  justify-content: center;
}

:deep(.el-empty__description p) {
  font-size: 16px;
  color: #6b7280;
}
</style>