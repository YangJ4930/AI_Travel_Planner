<template>
  <aside class="app-sidebar" :class="{ 'collapsed': collapsed }">
    <div class="sidebar-content">
      <!-- 侧边栏头部 -->
      <div class="sidebar-header">
        <div v-if="!collapsed" class="logo flex items-center space-x-2 px-4 py-3">
          <el-icon class="text-2xl text-primary-600">
            <Location />
          </el-icon>
          <span class="text-lg font-bold text-gray-800">AI旅行规划师</span>
        </div>
        
        <!-- 折叠按钮 -->
        <div class="collapse-btn">
          <el-button 
            :icon="collapsed ? Expand : Fold" 
            circle 
            size="small"
            @click="toggleCollapse"
          />
        </div>
      </div>
      
      <!-- 用户信息 -->
      <div v-if="authStore.isAuthenticated" class="user-section px-4 py-3">
        <div class="user-card bg-gradient-to-r from-primary-500 to-secondary-500 rounded-lg p-3 text-white">
          <div class="flex items-center space-x-3">
            <el-avatar :size="collapsed ? 32 : 40" :src="authStore.user?.avatar">
              {{ authStore.user?.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            
            <div v-if="!collapsed" class="user-info flex-1 min-w-0">
              <p class="font-medium truncate">{{ authStore.user?.username }}</p>
              <p class="text-xs opacity-90 truncate">{{ authStore.user?.email }}</p>
            </div>
          </div>
          
          <div v-if="!collapsed" class="user-stats mt-3 grid grid-cols-2 gap-2 text-xs">
            <div class="stat-item text-center">
              <div class="font-semibold">{{ travelStore.planCount }}</div>
              <div class="opacity-80">计划</div>
            </div>
            <div class="stat-item text-center">
              <div class="font-semibold">{{ travelStore.activePlans.length }}</div>
              <div class="opacity-80">进行中</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 主导航菜单 -->
      <nav class="main-navigation px-2 py-4">
        <el-menu
          :default-active="activeMenu"
          :collapse="collapsed"
          class="sidebar-menu border-none bg-transparent"
          @select="handleMenuSelect"
        >
          <!-- 主要功能 -->
          <div v-if="!collapsed" class="menu-group-title">主要功能</div>
          
          <el-menu-item index="dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>仪表板</template>
          </el-menu-item>
          
          <el-menu-item index="plans">
            <el-icon><MapLocation /></el-icon>
            <template #title>我的计划</template>
          </el-menu-item>
          
          <el-menu-item index="ai-assistant">
            <el-icon><BrainFilled /></el-icon>
            <template #title>AI助手</template>
          </el-menu-item>
          
          <el-menu-item index="map">
            <el-icon><Map /></el-icon>
            <template #title>地图探索</template>
          </el-menu-item>
          
          <!-- 管理功能 -->
          <div v-if="!collapsed" class="menu-group-title">管理功能</div>
          
          <el-menu-item index="expenses">
            <el-icon><Wallet /></el-icon>
            <template #title>费用管理</template>
          </el-menu-item>
          
          <el-menu-item index="favorites">
            <el-icon><Star /></el-icon>
            <template #title>我的收藏</template>
          </el-menu-item>
          
          <el-menu-item index="shared">
            <el-icon><Share /></el-icon>
            <template #title>共享计划</template>
          </el-menu-item>
          
          <!-- 个人设置 -->
          <div v-if="!collapsed" class="menu-group-title">个人设置</div>
          
          <el-menu-item index="profile">
            <el-icon><User /></el-icon>
            <template #title>个人资料</template>
          </el-menu-item>
          
          <el-menu-item index="settings">
            <el-icon><Setting /></el-icon>
            <template #title>设置</template>
          </el-menu-item>
        </el-menu>
      </nav>
      
      <!-- 快速操作 -->
      <div v-if="!collapsed" class="quick-actions px-4 py-3">
        <div class="space-y-2">
          <el-button 
            type="primary" 
            class="w-full"
            @click="$router.push('/plans/create')"
          >
            <el-icon class="mr-2"><Plus /></el-icon>
            创建计划
          </el-button>
          
          <el-button 
            class="w-full"
            @click="$router.push('/ai-assistant')"
          >
            <el-icon class="mr-2"><BrainFilled /></el-icon>
            AI助手
          </el-button>
        </div>
      </div>
      
      <!-- 最近的计划 -->
      <div v-if="!collapsed && recentPlans.length > 0" class="recent-plans px-4 py-3">
        <div class="section-title text-xs font-medium text-gray-500 uppercase tracking-wide mb-3">
          最近的计划
        </div>
        
        <div class="space-y-2">
          <div 
            v-for="plan in recentPlans.slice(0, 3)" 
            :key="plan.id"
            class="recent-plan-item p-2 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors"
            @click="$router.push(`/plans/${plan.id}`)"
          >
            <div class="flex items-center space-x-2">
              <div class="w-8 h-8 bg-gradient-to-r from-blue-400 to-purple-500 rounded-lg flex items-center justify-center">
                <el-icon class="text-white text-sm"><MapLocation /></el-icon>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-800 truncate">{{ plan.title }}</p>
                <p class="text-xs text-gray-500 truncate">{{ plan.destination }}</p>
              </div>
            </div>
            
            <div class="mt-2">
              <div class="flex items-center justify-between text-xs mb-1">
                <span class="text-gray-500">进度</span>
                <span class="text-gray-700">{{ plan.progress }}%</span>
              </div>
              <el-progress :percentage="plan.progress" :show-text="false" :stroke-width="4" />
            </div>
          </div>
        </div>
        
        <div class="mt-3">
          <el-button 
            text 
            class="w-full text-xs"
            @click="$router.push('/plans')"
          >
            查看全部计划
          </el-button>
        </div>
      </div>
      
      <!-- 帮助和反馈 -->
      <div class="sidebar-footer px-4 py-3 mt-auto">
        <div v-if="!collapsed" class="space-y-2">
          <el-button 
            text 
            class="w-full justify-start"
            @click="showHelp"
          >
            <el-icon class="mr-2"><QuestionFilled /></el-icon>
            帮助中心
          </el-button>
          
          <el-button 
            text 
            class="w-full justify-start"
            @click="showFeedback"
          >
            <el-icon class="mr-2"><ChatDotRound /></el-icon>
            意见反馈
          </el-button>
        </div>
        
        <div v-else class="space-y-2">
          <el-tooltip content="帮助中心" placement="right">
            <el-button :icon="QuestionFilled" circle @click="showHelp" />
          </el-tooltip>
          
          <el-tooltip content="意见反馈" placement="right">
            <el-button :icon="ChatDotRound" circle @click="showFeedback" />
          </el-tooltip>
        </div>
      </div>
    </div>
    
    <!-- 反馈对话框 -->
    <el-dialog
      v-model="feedbackDialog"
      title="意见反馈"
      width="500px"
      :before-close="closeFeedbackDialog"
    >
      <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef">
        <el-form-item label="反馈类型" prop="type">
          <el-select v-model="feedbackForm.type" placeholder="请选择反馈类型" class="w-full">
            <el-option label="功能建议" value="suggestion" />
            <el-option label="问题反馈" value="bug" />
            <el-option label="使用体验" value="experience" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="反馈内容" prop="content">
          <el-input
            v-model="feedbackForm.content"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您的意见或建议..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="联系方式">
          <el-input
            v-model="feedbackForm.contact"
            placeholder="邮箱或手机号（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeFeedbackDialog">取消</el-button>
          <el-button type="primary" @click="submitFeedback" :loading="feedbackLoading">
            提交反馈
          </el-button>
        </div>
      </template>
    </el-dialog>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useTravelStore } from '@/stores/travel'
import {
  Location,
  Expand,
  Fold,
  Odometer,
  MapLocation,
  BrainFilled,
  Map,
  Wallet,
  Star,
  Share,
  User,
  Setting,
  Plus,
  QuestionFilled,
  ChatDotRound
} from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { TravelPlan } from '@/types'

// Props
interface Props {
  collapsed?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  collapsed: false
})

// Emits
const emit = defineEmits<{
  'update:collapsed': [value: boolean]
}>()

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const travelStore = useTravelStore()

// 响应式数据
const collapsed = ref(props.collapsed)
const activeMenu = ref('')
const feedbackDialog = ref(false)
const feedbackLoading = ref(false)

// 表单引用
const feedbackFormRef = ref<FormInstance>()

// 反馈表单
const feedbackForm = ref({
  type: '',
  content: '',
  contact: ''
})

// 表单验证规则
const feedbackRules: FormRules = {
  type: [
    { required: true, message: '请选择反馈类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' },
    { min: 10, message: '反馈内容至少10个字符', trigger: 'blur' }
  ]
}

// 模拟最近的计划数据
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
  }
])

// 监听路由变化，更新活动菜单
watch(
  () => route.path,
  (newPath) => {
    updateActiveMenu(newPath)
  },
  { immediate: true }
)

// 监听collapsed变化
watch(
  () => props.collapsed,
  (newValue) => {
    collapsed.value = newValue
  }
)

watch(
  collapsed,
  (newValue) => {
    emit('update:collapsed', newValue)
  }
)

// 方法
const updateActiveMenu = (path: string) => {
  if (path.startsWith('/dashboard')) {
    activeMenu.value = 'dashboard'
  } else if (path.startsWith('/plans')) {
    activeMenu.value = 'plans'
  } else if (path.startsWith('/ai-assistant')) {
    activeMenu.value = 'ai-assistant'
  } else if (path.startsWith('/map')) {
    activeMenu.value = 'map'
  } else if (path.startsWith('/expenses')) {
    activeMenu.value = 'expenses'
  } else if (path.startsWith('/favorites')) {
    activeMenu.value = 'favorites'
  } else if (path.startsWith('/shared')) {
    activeMenu.value = 'shared'
  } else if (path.startsWith('/profile')) {
    activeMenu.value = 'profile'
  } else if (path.startsWith('/settings')) {
    activeMenu.value = 'settings'
  } else {
    activeMenu.value = ''
  }
}

const toggleCollapse = () => {
  collapsed.value = !collapsed.value
}

const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  
  switch (index) {
    case 'dashboard':
      router.push('/dashboard')
      break
    case 'plans':
      router.push('/plans')
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
    case 'shared':
      ElMessage.info('共享计划功能开发中...')
      break
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
  }
}

const showHelp = () => {
  ElMessage.info('帮助中心功能开发中...')
}

const showFeedback = () => {
  feedbackDialog.value = true
}

const closeFeedbackDialog = () => {
  feedbackDialog.value = false
  feedbackForm.value = {
    type: '',
    content: '',
    contact: ''
  }
}

const submitFeedback = async () => {
  if (!feedbackFormRef.value) return
  
  try {
    await feedbackFormRef.value.validate()
    
    feedbackLoading.value = true
    
    // 模拟提交反馈
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('反馈提交成功，感谢您的建议！')
    closeFeedbackDialog()
  } catch (error) {
    // 验证失败
  } finally {
    feedbackLoading.value = false
  }
}

// 组件挂载
onMounted(() => {
  updateActiveMenu(route.path)
})
</script>

<style scoped>
.app-sidebar {
  @apply w-64 bg-white border-r border-gray-200 flex flex-col transition-all duration-300;
  height: 100vh;
  position: sticky;
  top: 0;
}

.app-sidebar.collapsed {
  @apply w-16;
}

.sidebar-content {
  @apply flex flex-col h-full;
}

.sidebar-header {
  @apply relative border-b border-gray-100 p-2;
}

.collapse-btn {
  @apply absolute top-2 right-2;
}

.user-card {
  transition: all 0.3s ease;
}

.user-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.menu-group-title {
  @apply px-4 py-2 text-xs font-medium text-gray-500 uppercase tracking-wide;
}

.sidebar-menu {
  @apply flex-1;
}

.recent-plan-item {
  transition: all 0.2s ease;
}

.recent-plan-item:hover {
  transform: translateX(2px);
}

.section-title {
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 20px;
  height: 2px;
  background: linear-gradient(to right, #3b82f6, #8b5cf6);
  border-radius: 1px;
}

:deep(.el-menu) {
  border: none;
}

:deep(.el-menu-item) {
  @apply mx-2 mb-1 rounded-lg;
  border-radius: 8px;
}

:deep(.el-menu-item:hover) {
  @apply bg-gray-50;
}

:deep(.el-menu-item.is-active) {
  @apply bg-primary-50 text-primary-600;
}

:deep(.el-menu--collapse .el-menu-item) {
  @apply mx-1;
}

:deep(.el-progress-bar__outer) {
  border-radius: 2px;
}

:deep(.el-progress-bar__inner) {
  border-radius: 2px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-sidebar {
    position: fixed;
    z-index: 1000;
    transform: translateX(-100%);
  }
  
  .app-sidebar.show {
    transform: translateX(0);
  }
}
</style>