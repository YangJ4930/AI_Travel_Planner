<template>
  <div class="dashboard-view">
    <!-- 顶部导航栏 -->
    <header class="header bg-white shadow-sm border-b">
      <div class="flex items-center justify-between px-6 py-4">
        <div class="flex items-center space-x-4">
          <el-icon class="text-2xl text-primary-600">
            <Location />
          </el-icon>
          <h1 class="text-xl font-bold text-gray-800">AI旅行规划师</h1>
        </div>
        
        <div class="flex items-center space-x-4">
          <!-- 搜索框 -->
          <el-input
            v-model="searchQuery"
            placeholder="搜索旅行计划..."
            :prefix-icon="Search"
            class="w-64"
            clearable
          />
          
          <!-- 通知 -->
          <el-badge :value="3" class="item">
            <el-button :icon="Bell" circle />
          </el-badge>
          
          <!-- 用户菜单 -->
          <el-dropdown @command="handleUserCommand">
            <span class="el-dropdown-link flex items-center cursor-pointer">
              <el-avatar :size="32" :src="authStore.user?.avatar">
                {{ authStore.user?.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="ml-2 text-gray-700">{{ authStore.user?.username }}</span>
              <el-icon class="ml-1"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人资料
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <div class="dashboard-content flex">
      <!-- 侧边栏 -->
      <aside class="sidebar w-64 bg-white shadow-sm border-r min-h-screen">
        <nav class="p-4">
          <el-menu
            :default-active="activeMenu"
            class="border-none"
            @select="handleMenuSelect"
          >
            <el-menu-item index="dashboard">
              <el-icon><Odometer /></el-icon>
              <span>仪表板</span>
            </el-menu-item>
            
            <el-menu-item index="plans">
              <el-icon><MapLocation /></el-icon>
              <span>我的计划</span>
            </el-menu-item>
            
            <el-menu-item index="ai-assistant">
              <el-icon><BrainFilled /></el-icon>
              <span>AI助手</span>
            </el-menu-item>
            
            <el-menu-item index="map">
              <el-icon><Map /></el-icon>
              <span>地图探索</span>
            </el-menu-item>
            
            <el-menu-item index="expenses">
              <el-icon><Wallet /></el-icon>
              <span>费用管理</span>
            </el-menu-item>
            
            <el-menu-item index="favorites">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
          </el-menu>
        </nav>
      </aside>

      <!-- 主内容区域 -->
      <main class="main-content flex-1 p-6 bg-gray-50">
        <!-- 欢迎区域 -->
        <div class="welcome-section mb-8">
          <h2 class="text-2xl font-bold text-gray-800 mb-2">
            欢迎回来，{{ authStore.user?.username }}！
          </h2>
          <p class="text-gray-600">
            今天是个适合规划旅行的日子 ✈️
          </p>
        </div>

        <!-- 快速操作 -->
        <div class="quick-actions mb-8">
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <el-card class="action-card cursor-pointer hover:shadow-lg transition-shadow" @click="$router.push('/travel-plans/create')">
              <div class="flex items-center space-x-3">
                <div class="w-12 h-12 bg-primary-100 rounded-lg flex items-center justify-center">
                  <el-icon class="text-xl text-primary-600"><Plus /></el-icon>
                </div>
                <div>
                  <h3 class="font-semibold text-gray-800">创建计划</h3>
                  <p class="text-sm text-gray-600">开始新的旅行</p>
                </div>
              </div>
            </el-card>
            
            <el-card class="action-card cursor-pointer hover:shadow-lg transition-shadow" @click="$router.push('/ai-assistant')">
              <div class="flex items-center space-x-3">
                <div class="w-12 h-12 bg-secondary-100 rounded-lg flex items-center justify-center">
                  <el-icon class="text-xl text-secondary-600"><BrainFilled /></el-icon>
                </div>
                <div>
                  <h3 class="font-semibold text-gray-800">AI助手</h3>
                  <p class="text-sm text-gray-600">智能规划建议</p>
                </div>
              </div>
            </el-card>
            
            <el-card class="action-card cursor-pointer hover:shadow-lg transition-shadow" @click="$router.push('/map')">
              <div class="flex items-center space-x-3">
                <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                  <el-icon class="text-xl text-green-600"><Map /></el-icon>
                </div>
                <div>
                  <h3 class="font-semibold text-gray-800">地图探索</h3>
                  <p class="text-sm text-gray-600">发现新地点</p>
                </div>
              </div>
            </el-card>
            
            <el-card class="action-card cursor-pointer hover:shadow-lg transition-shadow" @click="importPlan">
              <div class="flex items-center space-x-3">
                <div class="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
                  <el-icon class="text-xl text-orange-600"><Upload /></el-icon>
                </div>
                <div>
                  <h3 class="font-semibold text-gray-800">导入计划</h3>
                  <p class="text-sm text-gray-600">从文件导入</p>
                </div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 统计卡片 -->
        <div class="stats-section mb-8">
          <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
            <el-card class="stat-card">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-600 mb-1">总计划数</p>
                  <p class="text-2xl font-bold text-gray-800">{{ travelStore.planCount }}</p>
                </div>
                <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                  <el-icon class="text-xl text-blue-600"><Document /></el-icon>
                </div>
              </div>
            </el-card>
            
            <el-card class="stat-card">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-600 mb-1">进行中</p>
                  <p class="text-2xl font-bold text-gray-800">{{ travelStore.activePlans.length }}</p>
                </div>
                <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                  <el-icon class="text-xl text-green-600"><Clock /></el-icon>
                </div>
              </div>
            </el-card>
            
            <el-card class="stat-card">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-600 mb-1">已完成</p>
                  <p class="text-2xl font-bold text-gray-800">{{ travelStore.completedPlans.length }}</p>
                </div>
                <div class="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                  <el-icon class="text-xl text-purple-600"><Check /></el-icon>
                </div>
              </div>
            </el-card>
            
            <el-card class="stat-card">
              <div class="flex items-center justify-between">
                <div>
                  <p class="text-sm text-gray-600 mb-1">总里程</p>
                  <p class="text-2xl font-bold text-gray-800">12,580</p>
                  <p class="text-xs text-gray-500">公里</p>
                </div>
                <div class="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
                  <el-icon class="text-xl text-red-600"><Position /></el-icon>
                </div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 最近的旅行计划 -->
        <div class="recent-plans-section">
          <div class="flex items-center justify-between mb-6">
            <h3 class="text-lg font-semibold text-gray-800">最近的旅行计划</h3>
            <el-button type="primary" @click="$router.push('/travel-plans')">
              查看全部
            </el-button>
          </div>
          
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <el-card 
              v-for="plan in recentPlans" 
              :key="plan.id"
              class="plan-card cursor-pointer hover:shadow-lg transition-shadow"
              @click="$router.push(`/travel-plans/${plan.id}`)"
            >
              <div class="plan-header mb-4">
                <div class="flex items-center justify-between">
                  <h4 class="font-semibold text-gray-800 truncate">{{ plan.title }}</h4>
                  <el-tag :type="getStatusType(plan.status)" size="small">
                    {{ getStatusText(plan.status) }}
                  </el-tag>
                </div>
                <p class="text-sm text-gray-600 mt-1">{{ plan.destination }}</p>
              </div>
              
              <div class="plan-details space-y-2">
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
              </div>
              
              <div class="plan-progress mt-4">
                <div class="flex items-center justify-between text-sm mb-1">
                  <span class="text-gray-600">完成度</span>
                  <span class="text-gray-800">{{ plan.progress }}%</span>
                </div>
                <el-progress :percentage="plan.progress" :show-text="false" />
              </div>
            </el-card>
          </div>
          
          <!-- 空状态 -->
          <div v-if="recentPlans.length === 0" class="empty-state text-center py-12">
            <el-icon class="text-6xl text-gray-300 mb-4"><Document /></el-icon>
            <h3 class="text-lg font-semibold text-gray-600 mb-2">还没有旅行计划</h3>
            <p class="text-gray-500 mb-6">创建您的第一个旅行计划，开始精彩的旅程吧！</p>
            <el-button type="primary" @click="$router.push('/travel-plans/create')">
              <el-icon class="mr-2"><Plus /></el-icon>
              创建计划
            </el-button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useTravelStore } from '@/stores/travel'
import {
  Location,
  Search,
  Bell,
  ArrowDown,
  User,
  Setting,
  SwitchButton,
  Odometer,
  MapLocation,
  BrainFilled,
  Map,
  Wallet,
  Star,
  Plus,
  Upload,
  Document,
  Clock,
  Check,
  Position,
  Calendar
} from '@element-plus/icons-vue'
import type { TravelPlan } from '@/types'

const router = useRouter()
const authStore = useAuthStore()
const travelStore = useTravelStore()

// 响应式数据
const searchQuery = ref('')
const activeMenu = ref('dashboard')

// 模拟最近的旅行计划数据
const recentPlans = ref<TravelPlan[]>([
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
    itinerary: [],
    expenses: [],
    createdAt: '2023-12-20',
    updatedAt: '2024-01-05'
  }
])

// 处理用户菜单命令
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      authStore.logout()
      router.push('/')
      break
  }
}

// 处理菜单选择
const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  
  switch (index) {
    case 'dashboard':
      // 当前页面，不需要跳转
      break
    case 'plans':
      router.push('/travel-plans')
      break
    case 'ai-assistant':
      router.push('/ai-assistant')
      break
    case 'map':
      router.push('/map')
      break
    case 'expenses':
      ElMessage.info('费用管理功能开发中...')
      break
    case 'favorites':
      ElMessage.info('收藏功能开发中...')
      break
  }
}

// 导入计划
const importPlan = () => {
  ElMessage.info('导入计划功能开发中...')
}

// 获取状态类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    'planning': 'warning',
    'active': 'success',
    'completed': 'info',
    'cancelled': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'planning': '规划中',
    'active': '进行中',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || '未知'
}

// 格式化日期范围
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
  // 获取旅行计划数据
  await travelStore.fetchTravelPlans()
})
</script>

<style scoped>
.dashboard-view {
  min-height: 100vh;
  background-color: #f9fafb;
}

.header {
  position: sticky;
  top: 0;
  z-index: 100;
}

.sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}

.action-card {
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-2px);
}

.stat-card {
  border: none;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
}

.plan-card {
  transition: all 0.3s ease;
  border: none;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
}

.plan-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px 0 rgba(0, 0, 0, 0.1);
}

:deep(.el-menu) {
  border: none;
}

:deep(.el-menu-item) {
  border-radius: 8px;
  margin-bottom: 4px;
}

:deep(.el-menu-item:hover) {
  background-color: #f3f4f6;
}

:deep(.el-menu-item.is-active) {
  background-color: #eff6ff;
  color: #2563eb;
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