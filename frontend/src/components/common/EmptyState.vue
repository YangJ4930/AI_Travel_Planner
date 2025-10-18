<template>
  <div class="empty-state" :class="sizeClass">
    <div class="empty-content">
      <!-- 图标或插图 -->
      <div class="empty-icon">
        <!-- 自定义图标 -->
        <div v-if="customIcon" class="custom-icon">
          <el-icon :size="iconSize">
            <component :is="customIcon" />
          </el-icon>
        </div>
        
        <!-- 预设图标 -->
        <div v-else-if="type" class="preset-icon">
          <el-icon :size="iconSize" :class="iconColorClass">
            <component :is="getPresetIcon(type)" />
          </el-icon>
        </div>
        
        <!-- SVG 插图 -->
        <div v-else class="illustration">
          <svg
            :width="illustrationSize"
            :height="illustrationSize"
            viewBox="0 0 200 200"
            class="empty-illustration"
          >
            <!-- 默认空状态插图 -->
            <defs>
              <linearGradient id="emptyGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#e3f2fd;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#bbdefb;stop-opacity:1" />
              </linearGradient>
            </defs>
            
            <!-- 背景圆 -->
            <circle cx="100" cy="100" r="80" fill="url(#emptyGradient)" opacity="0.3" />
            
            <!-- 文档图标 -->
            <rect x="70" y="60" width="60" height="80" rx="4" fill="#90caf9" opacity="0.6" />
            <rect x="75" y="70" width="50" height="3" rx="1.5" fill="#ffffff" />
            <rect x="75" y="80" width="35" height="3" rx="1.5" fill="#ffffff" />
            <rect x="75" y="90" width="40" height="3" rx="1.5" fill="#ffffff" />
            <rect x="75" y="100" width="30" height="3" rx="1.5" fill="#ffffff" />
            
            <!-- 装饰元素 -->
            <circle cx="60" cy="50" r="3" fill="#64b5f6" opacity="0.4" />
            <circle cx="140" cy="45" r="2" fill="#42a5f5" opacity="0.6" />
            <circle cx="150" cy="160" r="4" fill="#2196f3" opacity="0.3" />
            <circle cx="50" cy="150" r="2.5" fill="#1e88e5" opacity="0.5" />
          </svg>
        </div>
      </div>
      
      <!-- 标题 -->
      <div class="empty-title">
        <h3 :class="titleSizeClass">{{ title || getDefaultTitle(type) }}</h3>
      </div>
      
      <!-- 描述 -->
      <div v-if="description || getDefaultDescription(type)" class="empty-description">
        <p :class="descriptionSizeClass">
          {{ description || getDefaultDescription(type) }}
        </p>
      </div>
      
      <!-- 操作按钮 -->
      <div v-if="showAction" class="empty-actions">
        <slot name="actions">
          <el-button
            v-if="actionText"
            :type="actionType"
            :size="actionSize"
            :icon="actionIcon"
            @click="handleAction"
          >
            {{ actionText }}
          </el-button>
        </slot>
      </div>
      
      <!-- 额外内容 -->
      <div v-if="$slots.extra" class="empty-extra">
        <slot name="extra"></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  Document,
  FolderOpened,
  Search,
  Connection,
  Warning,
  CircleClose,
  MapLocation,
  User,
  Star,
  Calendar,
  Wallet,
  ChatDotRound,
  Setting,
  Plus
} from '@element-plus/icons-vue'

// Props
interface Props {
  type?: 'default' | 'no-data' | 'no-results' | 'no-plans' | 'no-favorites' | 'no-users' | 'network-error' | 'permission-denied' | 'not-found' | 'maintenance'
  size?: 'small' | 'medium' | 'large'
  title?: string
  description?: string
  customIcon?: any
  showAction?: boolean
  actionText?: string
  actionType?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  actionIcon?: any
  actionSize?: 'small' | 'default' | 'large'
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  size: 'medium',
  showAction: true,
  actionType: 'primary',
  actionSize: 'default'
})

// Emits
const emit = defineEmits<{
  'action': []
}>()

// 计算属性
const sizeClass = computed(() => {
  return `size-${props.size}`
})

const iconSize = computed(() => {
  const sizeMap = {
    small: 48,
    medium: 64,
    large: 80
  }
  return sizeMap[props.size]
})

const illustrationSize = computed(() => {
  const sizeMap = {
    small: 120,
    medium: 160,
    large: 200
  }
  return sizeMap[props.size]
})

const titleSizeClass = computed(() => {
  const sizeMap = {
    small: 'text-lg',
    medium: 'text-xl',
    large: 'text-2xl'
  }
  return sizeMap[props.size]
})

const descriptionSizeClass = computed(() => {
  const sizeMap = {
    small: 'text-sm',
    medium: 'text-base',
    large: 'text-lg'
  }
  return sizeMap[props.size]
})

const iconColorClass = computed(() => {
  const colorMap = {
    'default': 'text-gray-400',
    'no-data': 'text-blue-400',
    'no-results': 'text-orange-400',
    'no-plans': 'text-green-400',
    'no-favorites': 'text-yellow-400',
    'no-users': 'text-purple-400',
    'network-error': 'text-red-400',
    'permission-denied': 'text-red-500',
    'not-found': 'text-gray-500',
    'maintenance': 'text-orange-500'
  }
  return colorMap[props.type] || 'text-gray-400'
})

// 方法
const getPresetIcon = (type: string) => {
  const iconMap = {
    'default': Document,
    'no-data': FolderOpened,
    'no-results': Search,
    'no-plans': MapLocation,
    'no-favorites': Star,
    'no-users': User,
    'network-error': Connection,
    'permission-denied': Warning,
    'not-found': CircleClose,
    'maintenance': Setting
  }
  return iconMap[type as keyof typeof iconMap] || Document
}

const getDefaultTitle = (type: string) => {
  const titleMap = {
    'default': '暂无数据',
    'no-data': '暂无数据',
    'no-results': '没有找到相关结果',
    'no-plans': '还没有旅行计划',
    'no-favorites': '还没有收藏内容',
    'no-users': '暂无用户',
    'network-error': '网络连接失败',
    'permission-denied': '权限不足',
    'not-found': '页面不存在',
    'maintenance': '系统维护中'
  }
  return titleMap[type as keyof typeof titleMap] || '暂无数据'
}

const getDefaultDescription = (type: string) => {
  const descriptionMap = {
    'default': '当前没有可显示的内容',
    'no-data': '当前没有可显示的数据，请稍后再试',
    'no-results': '尝试调整搜索条件或关键词',
    'no-plans': '创建您的第一个旅行计划，开始精彩的旅程',
    'no-favorites': '收藏您喜欢的内容，方便下次查看',
    'no-users': '当前没有用户数据',
    'network-error': '请检查网络连接后重试',
    'permission-denied': '您没有访问此内容的权限',
    'not-found': '您访问的页面不存在或已被删除',
    'maintenance': '系统正在维护升级，请稍后访问'
  }
  return descriptionMap[type as keyof typeof descriptionMap] || ''
}

const handleAction = () => {
  emit('action')
}
</script>

<style scoped>
.empty-state {
  @apply flex items-center justify-center min-h-64 p-8;
}

.empty-content {
  @apply text-center max-w-md mx-auto;
}

.empty-icon {
  @apply mb-6;
}

.custom-icon,
.preset-icon {
  @apply flex justify-center;
}

.illustration {
  @apply flex justify-center;
}

.empty-illustration {
  @apply drop-shadow-sm;
}

.empty-title {
  @apply mb-4;
}

.empty-title h3 {
  @apply font-semibold text-gray-800 m-0;
}

.empty-description {
  @apply mb-6;
}

.empty-description p {
  @apply text-gray-600 leading-relaxed m-0;
}

.empty-actions {
  @apply mb-4;
}

.empty-extra {
  @apply mt-4;
}

/* 尺寸变体 */
.size-small {
  @apply min-h-48 p-4;
}

.size-small .empty-content {
  @apply max-w-xs;
}

.size-small .empty-icon {
  @apply mb-4;
}

.size-small .empty-title {
  @apply mb-3;
}

.size-small .empty-description {
  @apply mb-4;
}

.size-large {
  @apply min-h-80 p-12;
}

.size-large .empty-content {
  @apply max-w-lg;
}

.size-large .empty-icon {
  @apply mb-8;
}

.size-large .empty-title {
  @apply mb-6;
}

.size-large .empty-description {
  @apply mb-8;
}

/* 动画效果 */
.empty-icon {
  animation: fadeInUp 0.6s ease-out;
}

.empty-title {
  animation: fadeInUp 0.6s ease-out 0.1s both;
}

.empty-description {
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

.empty-actions {
  animation: fadeInUp 0.6s ease-out 0.3s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 悬浮效果 */
.empty-icon:hover .custom-icon,
.empty-icon:hover .preset-icon {
  transform: translateY(-2px);
  transition: transform 0.3s ease;
}

.empty-illustration:hover {
  transform: scale(1.05);
  transition: transform 0.3s ease;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .empty-state {
    @apply p-4;
  }
  
  .size-large {
    @apply p-6;
  }
}
</style>