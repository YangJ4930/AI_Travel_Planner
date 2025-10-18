<template>
  <div class="plans-list-view">
    <!-- 页面头部 -->
    <div class="page-header bg-white shadow-sm border-b px-6 py-4">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">我的旅行计划</h1>
          <p class="text-gray-600 mt-1">管理您的所有旅行计划</p>
        </div>
        
        <div class="flex items-center space-x-4">
          <!-- 搜索和筛选 -->
          <el-input
            v-model="searchQuery"
            placeholder="搜索计划..."
            :prefix-icon="Search"
            class="w-64"
            clearable
            @input="handleSearch"
          />
          
          <el-select
            v-model="statusFilter"
            placeholder="状态筛选"
            class="w-32"
            clearable
            @change="handleFilter"
          >
            <el-option label="全部" value="" />
            <el-option label="规划中" value="planning" />
            <el-option label="进行中" value="active" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
          
          <!-- 视图切换 -->
          <el-button-group>
            <el-button 
              :type="viewMode === 'grid' ? 'primary' : 'default'"
              :icon="Grid"
              @click="viewMode = 'grid'"
            />
            <el-button 
              :type="viewMode === 'list' ? 'primary' : 'default'"
              :icon="List"
              @click="viewMode = 'list'"
            />
          </el-button-group>
          
          <!-- 创建新计划 -->
          <el-button type="primary" @click="$router.push('/plans/create')">
            <el-icon class="mr-2"><Plus /></el-icon>
            创建计划
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content p-6">
      <!-- 统计信息 -->
      <div class="stats-section mb-6">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="stat-item bg-white rounded-lg p-4 border">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600">总计划</p>
                <p class="text-2xl font-bold text-gray-800">{{ filteredPlans.length }}</p>
              </div>
              <el-icon class="text-2xl text-blue-500"><Document /></el-icon>
            </div>
          </div>
          
          <div class="stat-item bg-white rounded-lg p-4 border">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600">规划中</p>
                <p class="text-2xl font-bold text-orange-600">{{ planningCount }}</p>
              </div>
              <el-icon class="text-2xl text-orange-500"><Edit /></el-icon>
            </div>
          </div>
          
          <div class="stat-item bg-white rounded-lg p-4 border">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600">进行中</p>
                <p class="text-2xl font-bold text-green-600">{{ activeCount }}</p>
              </div>
              <el-icon class="text-2xl text-green-500"><Clock /></el-icon>
            </div>
          </div>
          
          <div class="stat-item bg-white rounded-lg p-4 border">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600">已完成</p>
                <p class="text-2xl font-bold text-purple-600">{{ completedCount }}</p>
              </div>
              <el-icon class="text-2xl text-purple-500"><Check /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 计划列表 -->
      <div class="plans-section">
        <!-- 网格视图 -->
        <div v-if="viewMode === 'grid'" class="grid-view">
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <el-card 
              v-for="plan in paginatedPlans" 
              :key="plan.id"
              class="plan-card cursor-pointer hover:shadow-lg transition-all duration-300"
              @click="$router.push(`/plans/${plan.id}`)"
            >
              <!-- 计划封面 -->
              <div class="plan-cover mb-4">
                <div class="relative h-48 bg-gradient-to-r from-blue-400 to-purple-500 rounded-lg overflow-hidden">
                  <img 
                    v-if="plan.coverImage" 
                    :src="plan.coverImage" 
                    :alt="plan.title"
                    class="w-full h-full object-cover"
                  />
                  <div class="absolute inset-0 bg-black bg-opacity-20 flex items-end p-4">
                    <div class="text-white">
                      <h3 class="text-lg font-semibold mb-1">{{ plan.title }}</h3>
                      <p class="text-sm opacity-90">{{ plan.destination }}</p>
                    </div>
                  </div>
                  
                  <!-- 状态标签 -->
                  <div class="absolute top-4 right-4">
                    <el-tag :type="getStatusType(plan.status)" size="small">
                      {{ getStatusText(plan.status) }}
                    </el-tag>
                  </div>
                </div>
              </div>
              
              <!-- 计划信息 -->
              <div class="plan-info space-y-3">
                <div class="flex items-center text-sm text-gray-600">
                  <el-icon class="mr-2"><Calendar /></el-icon>
                  {{ formatDateRange(plan.startDate, plan.endDate) }}
                </div>
                
                <div class="flex items-center text-sm text-gray-600">
                  <el-icon class="mr-2"><Wallet /></el-icon>
                  预算: ¥{{ plan.budget?.toLocaleString() }}
                </div>
                
                <div class="flex items-center text-sm text-gray-600">
                  <el-icon class="mr-2"><User /></el-icon>
                  {{ plan.travelers }}人
                </div>
                
                <!-- 进度条 -->
                <div class="progress-section">
                  <div class="flex items-center justify-between text-sm mb-1">
                    <span class="text-gray-600">完成度</span>
                    <span class="text-gray-800">{{ plan.progress }}%</span>
                  </div>
                  <el-progress :percentage="plan.progress" :show-text="false" />
                </div>
              </div>
              
              <!-- 操作按钮 -->
              <div class="plan-actions mt-4 flex items-center justify-between">
                <div class="flex items-center space-x-2">
                  <el-button 
                    size="small" 
                    @click.stop="editPlan(plan)"
                  >
                    编辑
                  </el-button>
                  <el-button 
                    size="small" 
                    type="success"
                    @click.stop="copyPlan(plan)"
                  >
                    复制
                  </el-button>
                </div>
                
                <el-dropdown @command="(command) => handlePlanAction(command, plan)" @click.stop>
                  <el-button size="small" :icon="MoreFilled" circle />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="share">
                        <el-icon><Share /></el-icon>
                        分享
                      </el-dropdown-item>
                      <el-dropdown-item command="export">
                        <el-icon><Download /></el-icon>
                        导出
                      </el-dropdown-item>
                      <el-dropdown-item command="archive">
                        <el-icon><Box /></el-icon>
                        归档
                      </el-dropdown-item>
                      <el-dropdown-item divided command="delete">
                        <el-icon><Delete /></el-icon>
                        删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 列表视图 -->
        <div v-else class="list-view">
          <el-table 
            :data="paginatedPlans" 
            class="w-full"
            @row-click="(row) => $router.push(`/plans/${row.id}`)"
          >
            <el-table-column prop="title" label="计划名称" min-width="200">
              <template #default="{ row }">
                <div class="flex items-center space-x-3">
                  <div class="w-12 h-12 bg-gradient-to-r from-blue-400 to-purple-500 rounded-lg flex items-center justify-center">
                    <el-icon class="text-white"><MapLocation /></el-icon>
                  </div>
                  <div>
                    <p class="font-semibold text-gray-800">{{ row.title }}</p>
                    <p class="text-sm text-gray-600">{{ row.destination }}</p>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column label="日期" width="200">
              <template #default="{ row }">
                {{ formatDateRange(row.startDate, row.endDate) }}
              </template>
            </el-table-column>
            
            <el-table-column prop="budget" label="预算" width="120">
              <template #default="{ row }">
                ¥{{ row.budget?.toLocaleString() }}
              </template>
            </el-table-column>
            
            <el-table-column prop="travelers" label="人数" width="80" />
            
            <el-table-column label="进度" width="150">
              <template #default="{ row }">
                <div class="flex items-center space-x-2">
                  <el-progress :percentage="row.progress" :show-text="false" class="flex-1" />
                  <span class="text-sm text-gray-600">{{ row.progress }}%</span>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <div class="flex items-center space-x-2">
                  <el-button size="small" @click.stop="editPlan(row)">
                    编辑
                  </el-button>
                  <el-dropdown @command="(command) => handlePlanAction(command, row)" @click.stop>
                    <el-button size="small" :icon="MoreFilled" circle />
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="copy">复制</el-dropdown-item>
                        <el-dropdown-item command="share">分享</el-dropdown-item>
                        <el-dropdown-item command="export">导出</el-dropdown-item>
                        <el-dropdown-item divided command="delete">删除</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 空状态 -->
        <div v-if="filteredPlans.length === 0" class="empty-state text-center py-16">
          <el-icon class="text-6xl text-gray-300 mb-4"><Document /></el-icon>
          <h3 class="text-lg font-semibold text-gray-600 mb-2">
            {{ searchQuery || statusFilter ? '没有找到匹配的计划' : '还没有旅行计划' }}
          </h3>
          <p class="text-gray-500 mb-6">
            {{ searchQuery || statusFilter ? '尝试调整搜索条件或筛选器' : '创建您的第一个旅行计划，开始精彩的旅程吧！' }}
          </p>
          <el-button v-if="!searchQuery && !statusFilter" type="primary" @click="$router.push('/plans/create')">
            <el-icon class="mr-2"><Plus /></el-icon>
            创建计划
          </el-button>
        </div>

        <!-- 分页 -->
        <div v-if="filteredPlans.length > 0" class="pagination-section mt-8 flex justify-center">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[12, 24, 48, 96]"
            :total="filteredPlans.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTravelStore } from '@/stores/travel'
import {
  Search,
  Grid,
  List,
  Plus,
  Document,
  Edit,
  Clock,
  Check,
  Calendar,
  Wallet,
  User,
  MapLocation,
  MoreFilled,
  Share,
  Download,
  Box,
  Delete
} from '@element-plus/icons-vue'
import type { TravelPlan } from '@/types'

const router = useRouter()
const travelStore = useTravelStore()

// 响应式数据
const searchQuery = ref('')
const statusFilter = ref('')
const viewMode = ref<'grid' | 'list'>('grid')
const currentPage = ref(1)
const pageSize = ref(12)

// 模拟旅行计划数据
const plans = ref<TravelPlan[]>([
  {
    id: '1',
    title: '日本关西深度游',
    destination: '大阪、京都、奈良',
    startDate: '2024-03-15',
    endDate: '2024-03-22',
    budget: 15000,
    travelers: 2,
    status: 'active',
    progress: 75,
    coverImage: '',
    itinerary: [],
    expenses: [],
    createdAt: '2024-01-15',
    updatedAt: '2024-01-20'
  },
  {
    id: '2',
    title: '云南丽江古城之旅',
    destination: '丽江、大理、香格里拉',
    startDate: '2024-04-10',
    endDate: '2024-04-17',
    budget: 8000,
    travelers: 4,
    status: 'planning',
    progress: 30,
    coverImage: '',
    itinerary: [],
    expenses: [],
    createdAt: '2024-01-10',
    updatedAt: '2024-01-18'
  },
  {
    id: '3',
    title: '欧洲四国游',
    destination: '法国、意大利、瑞士、德国',
    startDate: '2024-06-01',
    endDate: '2024-06-15',
    budget: 35000,
    travelers: 2,
    status: 'completed',
    progress: 100,
    coverImage: '',
    itinerary: [],
    expenses: [],
    createdAt: '2023-12-20',
    updatedAt: '2024-01-05'
  },
  {
    id: '4',
    title: '新疆天山之旅',
    destination: '乌鲁木齐、天山、喀纳斯',
    startDate: '2024-07-01',
    endDate: '2024-07-10',
    budget: 12000,
    travelers: 3,
    status: 'planning',
    progress: 15,
    coverImage: '',
    itinerary: [],
    expenses: [],
    createdAt: '2024-01-25',
    updatedAt: '2024-01-25'
  },
  {
    id: '5',
    title: '泰国海岛度假',
    destination: '普吉岛、苏梅岛',
    startDate: '2024-05-01',
    endDate: '2024-05-08',
    budget: 9000,
    travelers: 2,
    status: 'active',
    progress: 60,
    coverImage: '',
    itinerary: [],
    expenses: [],
    createdAt: '2024-01-20',
    updatedAt: '2024-01-22'
  }
])

// 计算属性
const filteredPlans = computed(() => {
  let result = plans.value

  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(plan => 
      plan.title.toLowerCase().includes(query) ||
      plan.destination.toLowerCase().includes(query)
    )
  }

  // 状态筛选
  if (statusFilter.value) {
    result = result.filter(plan => plan.status === statusFilter.value)
  }

  return result
})

const paginatedPlans = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredPlans.value.slice(start, end)
})

const planningCount = computed(() => 
  plans.value.filter(plan => plan.status === 'planning').length
)

const activeCount = computed(() => 
  plans.value.filter(plan => plan.status === 'active').length
)

const completedCount = computed(() => 
  plans.value.filter(plan => plan.status === 'completed').length
)

// 方法
const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
}

const editPlan = (plan: TravelPlan) => {
  router.push(`/plans/${plan.id}/edit`)
}

const copyPlan = async (plan: TravelPlan) => {
  try {
    // 这里应该调用API复制计划
    ElMessage.success('计划复制成功')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const handlePlanAction = async (command: string, plan: TravelPlan) => {
  switch (command) {
    case 'copy':
      await copyPlan(plan)
      break
    case 'share':
      ElMessage.info('分享功能开发中...')
      break
    case 'export':
      ElMessage.info('导出功能开发中...')
      break
    case 'archive':
      ElMessage.info('归档功能开发中...')
      break
    case 'delete':
      try {
        await ElMessageBox.confirm(
          `确定要删除计划"${plan.title}"吗？此操作不可恢复。`,
          '确认删除',
          {
            confirmButtonText: '删除',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        // 这里应该调用API删除计划
        const index = plans.value.findIndex(p => p.id === plan.id)
        if (index > -1) {
          plans.value.splice(index, 1)
        }
        
        ElMessage.success('计划删除成功')
      } catch {
        // 用户取消删除
      }
      break
  }
}

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    'planning': 'warning',
    'active': 'success',
    'completed': 'info',
    'cancelled': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'planning': '规划中',
    'active': '进行中',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || '未知'
}

const formatDateRange = (startDate: string, endDate: string) => {
  const start = new Date(startDate).toLocaleDateString('zh-CN', { 
    month: 'short', 
    day: 'numeric' 
  })
  const end = new Date(endDate).toLocaleDateString('zh-CN', { 
    month: 'short', 
    day: 'numeric' 
  })
  return `${start} - ${end}`
}

// 组件挂载时获取数据
onMounted(async () => {
  // 这里应该从API获取真实数据
  // await travelStore.fetchTravelPlans()
})
</script>

<style scoped>
.plans-list-view {
  min-height: 100vh;
  background-color: #f9fafb;
}

.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
}

.plan-card {
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
}

.plan-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px 0 rgba(0, 0, 0, 0.15);
}

.stat-item {
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f8fafc;
}

:deep(.el-progress-bar__outer) {
  border-radius: 4px;
}

:deep(.el-progress-bar__inner) {
  border-radius: 4px;
}

.empty-state {
  background: white;
  border-radius: 12px;
  border: 2px dashed #e5e7eb;
}
</style>