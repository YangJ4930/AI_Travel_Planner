<template>
  <header class="app-header bg-white shadow-sm border-b">
    <div class="header-container flex items-center justify-between px-6 py-4">
      <!-- 左侧：Logo和导航 -->
      <div class="header-left flex items-center space-x-6">
        <!-- Logo -->
        <div class="logo flex items-center space-x-2 cursor-pointer" @click="$router.push('/')">
          <el-icon class="text-2xl text-primary-600">
            <Location />
          </el-icon>
          <span class="text-xl font-bold text-gray-800">AI旅行规划师</span>
        </div>
        
        <!-- 主导航 (仅在已登录时显示) -->
        <nav v-if="authStore.isAuthenticated" class="main-nav hidden md:flex">
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            class="border-none bg-transparent"
            @select="handleMenuSelect"
          >
            <el-menu-item index="plans">
              <el-icon><MapLocation /></el-icon>
              <span>我的计划</span>
            </el-menu-item>
            
            <el-menu-item index="ai-assistant">
              <el-icon><MagicStick /></el-icon>
              <span>AI助手</span>
            </el-menu-item>
            
            <el-menu-item index="map">
              <el-icon><Location /></el-icon>
              <span>地图探索</span>
            </el-menu-item>
          </el-menu>
        </nav>
      </div>
      
      <!-- 右侧：搜索、通知、用户菜单 -->
      <div class="header-right flex items-center space-x-4">
        <!-- 搜索框 (仅在已登录时显示) -->
        <div v-if="authStore.isAuthenticated" class="search-box hidden lg:block relative">
          <el-input
            v-model="searchQuery"
            placeholder="搜索旅行计划、目的地..."
            :prefix-icon="Search"
            class="w-64"
            clearable
            @keyup.enter="handleSearch"
          />
          <!-- 语音搜索按钮 -->
          <div class="absolute right-2 top-1/2 transform -translate-y-1/2 z-10">
            <VoiceInput
              placeholder="点击开始语音搜索"
              :useCloud="true"
              :continuous="false"
              @result="handleVoiceSearchResult"
              @start="handleVoiceStart"
              @end="handleVoiceEnd"
              @error="handleVoiceError"
            />
          </div>
        </div>
        
        <!-- 快速创建按钮 (仅在已登录时显示) -->
        <el-button 
          v-if="authStore.isAuthenticated" 
          type="primary" 
          @click="$router.push('/travel-plans/create')"
          class="hidden md:flex"
        >
          <el-icon class="mr-2"><Plus /></el-icon>
          创建计划
        </el-button>
        
        <!-- 通知 (仅在已登录时显示) -->
        <div v-if="authStore.isAuthenticated" class="notification">
          <el-badge :value="notificationCount" :hidden="notificationCount === 0">
            <el-button :icon="Bell" circle @click="showNotifications" />
          </el-badge>
        </div>
        
        <!-- 用户菜单 -->
        <div v-if="authStore.isAuthenticated" class="user-menu">
          <el-dropdown @command="handleUserCommand" trigger="click">
            <div class="user-info flex items-center space-x-2 cursor-pointer hover:bg-gray-50 rounded-lg px-3 py-2 transition-colors">
              <el-avatar :size="32" :src="authStore.user?.avatar">
                {{ authStore.user?.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="user-details hidden md:block">
                <p class="text-sm font-medium text-gray-800">{{ authStore.user?.username }}</p>
                <p class="text-xs text-gray-500">{{ authStore.user?.email }}</p>
              </div>
              <el-icon class="text-gray-400"><ArrowDown /></el-icon>
            </div>
            
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
                <el-dropdown-item command="help">
                  <el-icon><QuestionFilled /></el-icon>
                  帮助中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        
        <!-- 未登录时的按钮 -->
        <div v-else class="auth-buttons flex items-center space-x-3">
          <el-button @click="$router.push('/login')">
            登录
          </el-button>
          <el-button type="primary" @click="$router.push('/register')">
            注册
          </el-button>
        </div>
        
        <!-- 移动端菜单按钮 -->
        <el-button 
          v-if="authStore.isAuthenticated"
          :icon="Menu" 
          circle 
          class="md:hidden"
          @click="toggleMobileMenu"
        />
      </div>
    </div>
    
    <!-- 移动端导航菜单 -->
    <div v-if="showMobileMenu && authStore.isAuthenticated" class="mobile-nav md:hidden border-t bg-white">
      <div class="px-4 py-2">
        <!-- 移动端搜索 -->
        <div class="mb-4">
          <el-input
            v-model="searchQuery"
            placeholder="搜索..."
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
          />
        </div>
        
        <!-- 移动端菜单项 -->
        <div class="space-y-1">
          <div 
            v-for="item in mobileMenuItems" 
            :key="item.index"
            class="mobile-menu-item flex items-center space-x-3 px-3 py-2 rounded-lg cursor-pointer hover:bg-gray-50"
            :class="{ 'bg-primary-50 text-primary-600': activeMenu === item.index }"
            @click="handleMobileMenuClick(item.index)"
          >
            <el-icon>
              <component :is="item.icon" />
            </el-icon>
            <span>{{ item.label }}</span>
          </div>
        </div>
        
        <!-- 移动端快速操作 -->
        <div class="mt-4 pt-4 border-t">
          <el-button 
            type="primary" 
            class="w-full"
            @click="$router.push('/travel-plans/create')"
          >
            <el-icon class="mr-2"><Plus /></el-icon>
            创建计划
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 通知抽屉 -->
    <el-drawer
      v-model="notificationDrawer"
      title="通知"
      direction="rtl"
      size="400px"
    >
      <div class="notification-content">
        <div v-if="notifications.length === 0" class="empty-notifications text-center py-8">
          <el-icon class="text-4xl text-gray-300 mb-2"><Bell /></el-icon>
          <p class="text-gray-500">暂无新通知</p>
        </div>
        
        <div v-else class="notifications-list space-y-3">
          <div 
            v-for="notification in notifications" 
            :key="notification.id"
            class="notification-item p-3 border rounded-lg hover:bg-gray-50 cursor-pointer"
            :class="{ 'border-primary-200 bg-primary-50': !notification.read }"
            @click="markAsRead(notification)"
          >
            <div class="flex items-start space-x-3">
              <div class="notification-icon">
                <el-icon 
                  class="text-lg"
                  :class="getNotificationIconClass(notification.type)"
                >
                  <component :is="getNotificationIcon(notification.type)" />
                </el-icon>
              </div>
              <div class="notification-content flex-1">
                <h4 class="text-sm font-medium text-gray-800 mb-1">
                  {{ notification.title }}
                </h4>
                <p class="text-xs text-gray-600 mb-2">
                  {{ notification.content }}
                </p>
                <p class="text-xs text-gray-400">
                  {{ formatTime(notification.createdAt) }}
                </p>
              </div>
              <div v-if="!notification.read" class="notification-badge">
                <div class="w-2 h-2 bg-primary-500 rounded-full"></div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="notifications.length > 0" class="notification-actions mt-4 pt-4 border-t">
          <el-button class="w-full" @click="markAllAsRead">
            全部标记为已读
          </el-button>
        </div>
      </div>
    </el-drawer>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import VoiceInput from './VoiceInput.vue'
import {
  Location,
  Search,
  Plus,
  Bell,
  ArrowDown,
  User,
  Setting,
  QuestionFilled,
  SwitchButton,
  Menu,
  Odometer,
  MapLocation,
  MagicStick,
  Location,
  // 通知图标
  InfoFilled,
  WarningFilled,
  SuccessFilled,
  CircleCheckFilled
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 响应式数据
const searchQuery = ref('')
const showMobileMenu = ref(false)
const notificationDrawer = ref(false)
const activeMenu = ref('')

// 通知数据
const notifications = ref([
  {
    id: '1',
    type: 'success',
    title: '计划创建成功',
    content: '您的"日本关西深度游"计划已创建成功',
    read: false,
    createdAt: '2024-01-20T10:30:00Z'
  },
  {
    id: '2',
    type: 'info',
    title: 'AI推荐更新',
    content: '根据您的偏好，为您推荐了3个新的旅行目的地',
    read: false,
    createdAt: '2024-01-20T09:15:00Z'
  },
  {
    id: '3',
    type: 'warning',
    title: '预算提醒',
    content: '您的"欧洲四国游"计划预算即将超支',
    read: true,
    createdAt: '2024-01-19T16:45:00Z'
  }
])

// 移动端菜单项
const mobileMenuItems = [
  { index: 'plans', label: '我的计划', icon: 'MapLocation' },
  { index: 'ai-assistant', label: 'AI助手', icon: 'MagicStick' },
  { index: 'map', label: '地图探索', icon: 'Location' }
]

// 计算属性
const notificationCount = computed(() => 
  notifications.value.filter(n => !n.read).length
)

// 监听路由变化，更新活动菜单
watch(
  () => route.path,
  (newPath) => {
    updateActiveMenu(newPath)
  },
  { immediate: true }
)

// 方法
const updateActiveMenu = (path: string) => {
  if (path.startsWith('/travel-plans')) {
    activeMenu.value = 'plans'
  } else if (path.startsWith('/ai-assistant')) {
    activeMenu.value = 'ai-assistant'
  } else if (path.startsWith('/map')) {
    activeMenu.value = 'map'
  } else {
    activeMenu.value = ''
  }
}

const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  
  switch (index) {
    case 'plans':
      router.push('/travel-plans')
      break
    case 'ai-assistant':
      router.push('/ai-assistant')
      break
    case 'map':
      router.push('/map')
      break
  }
}

const handleMobileMenuClick = (index: string) => {
  handleMenuSelect(index)
  showMobileMenu.value = false
}

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    // 这里可以实现搜索功能
    ElMessage.info(`搜索: ${searchQuery.value}`)
  }
}

const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'help':
      ElMessage.info('帮助中心功能开发中...')
      break
    case 'logout':
      authStore.logout()
      router.push('/')
      ElMessage.success('已退出登录')
      break
  }
}

const showNotifications = () => {
  notificationDrawer.value = true
}

const markAsRead = (notification: any) => {
  notification.read = true
}

const markAllAsRead = () => {
  notifications.value.forEach(n => n.read = true)
}

const getNotificationIcon = (type: string) => {
  const iconMap: Record<string, string> = {
    'success': 'SuccessFilled',
    'info': 'InfoFilled',
    'warning': 'WarningFilled',
    'error': 'CircleCheckFilled'
  }
  return iconMap[type] || 'InfoFilled'
}

const getNotificationIconClass = (type: string) => {
  const classMap: Record<string, string> = {
    'success': 'text-green-500',
    'info': 'text-blue-500',
    'warning': 'text-orange-500',
    'error': 'text-red-500'
  }
  return classMap[type] || 'text-gray-500'
}

const formatTime = (time: string) => {
  const now = new Date()
  const notificationTime = new Date(time)
  const diff = now.getTime() - notificationTime.getTime()
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else {
    return `${days}天前`
  }
}

// 语音搜索处理方法
const handleVoiceSearchResult = (text: string) => {
  if (text.trim()) {
    // 如果搜索框为空，直接设置语音识别结果
    if (!searchQuery.value.trim()) {
      searchQuery.value = text
    } else {
      // 如果搜索框有内容，追加语音识别结果
      searchQuery.value += ' ' + text
    }
    // 自动执行搜索
    handleSearch()
  }
}

const handleVoiceStart = () => {
  console.log('开始语音搜索...')
}

const handleVoiceEnd = () => {
  console.log('语音搜索结束')
}

const handleVoiceError = (error: string) => {
  ElMessage.error(`语音搜索失败: ${error}`)
}

// 组件挂载
onMounted(() => {
  // 初始化活动菜单
  updateActiveMenu(route.path)
})
</script>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(8px);
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  transition: all 0.3s ease;
}

.logo:hover {
  transform: scale(1.05);
}

.user-info {
  transition: all 0.2s ease;
}

.mobile-menu-item {
  transition: all 0.2s ease;
}

.notification-item {
  transition: all 0.2s ease;
}

.notification-item:hover {
  transform: translateY(-1px);
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
}

:deep(.el-menu--horizontal .el-menu-item) {
  border-bottom: 2px solid transparent;
  margin: 0 8px;
  border-radius: 4px 4px 0 0;
}

:deep(.el-menu--horizontal .el-menu-item:hover) {
  background-color: #f3f4f6;
  border-bottom-color: #e5e7eb;
}

:deep(.el-menu--horizontal .el-menu-item.is-active) {
  background-color: #eff6ff;
  border-bottom-color: #3b82f6;
  color: #3b82f6;
}

:deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 20px 20px 0 20px;
}

:deep(.el-drawer__body) {
  padding: 20px;
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }
  
  .logo span {
    display: none;
  }
}
</style>