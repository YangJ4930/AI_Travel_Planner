<template>
  <div class="plan-card" :class="{ 'grid-view': viewMode === 'grid', 'list-view': viewMode === 'list' }">
    <div class="card-content">
      <!-- 卡片头部 -->
      <div class="card-header">
        <div class="plan-image">
          <img 
            v-if="plan.coverImage" 
            :src="plan.coverImage" 
            :alt="plan.title"
            class="w-full h-full object-cover"
          />
          <div v-else class="default-image">
            <el-icon class="text-4xl text-gray-400">
              <MapLocation />
            </el-icon>
          </div>
          
          <!-- 状态标签 -->
          <div class="status-badge">
            <el-tag 
              :type="getStatusType(plan.status)" 
              size="small"
              effect="dark"
            >
              {{ getStatusText(plan.status) }}
            </el-tag>
          </div>
          
          <!-- 收藏按钮 -->
          <div class="favorite-btn">
            <el-button
              :icon="plan.isFavorite ? StarFilled : Star"
              circle
              size="small"
              :type="plan.isFavorite ? 'warning' : 'default'"
              @click.stop="toggleFavorite"
            />
          </div>
        </div>
        
        <div class="plan-info">
          <h3 class="plan-title" @click="viewPlan">{{ plan.title }}</h3>
          <p class="plan-destination">
            <el-icon class="mr-1"><Location /></el-icon>
            {{ plan.destination }}
          </p>
        </div>
      </div>
      
      <!-- 卡片主体 -->
      <div class="card-body">
        <!-- 日期信息 -->
        <div class="date-info">
          <div class="date-item">
            <el-icon class="text-gray-500"><Calendar /></el-icon>
            <span class="text-sm text-gray-600">
              {{ formatDateRange(plan.startDate, plan.endDate) }}
            </span>
          </div>
          <div class="duration">
            <span class="text-xs text-gray-500">{{ getDuration(plan.startDate, plan.endDate) }}天</span>
          </div>
        </div>
        
        <!-- 预算和人数 -->
        <div class="budget-travelers">
          <div class="budget-item">
            <el-icon class="text-green-500"><Wallet /></el-icon>
            <span class="text-sm font-medium text-gray-700">
              ¥{{ formatBudget(plan.budget) }}
            </span>
          </div>
          <div class="travelers-item">
            <el-icon class="text-blue-500"><User /></el-icon>
            <span class="text-sm text-gray-600">{{ plan.travelers }}人</span>
          </div>
        </div>
        
        <!-- 进度条 -->
        <div v-if="plan.status !== 'completed'" class="progress-section">
          <div class="progress-header">
            <span class="text-xs text-gray-500">完成进度</span>
            <span class="text-xs font-medium text-gray-700">{{ plan.progress }}%</span>
          </div>
          <el-progress 
            :percentage="plan.progress" 
            :show-text="false" 
            :stroke-width="6"
            :color="getProgressColor(plan.progress)"
          />
        </div>
        
        <!-- 标签 -->
        <div v-if="plan.tags && plan.tags.length > 0" class="tags-section">
          <el-tag
            v-for="tag in plan.tags.slice(0, 3)"
            :key="tag"
            size="small"
            class="mr-1 mb-1"
            effect="plain"
          >
            {{ tag }}
          </el-tag>
          <span v-if="plan.tags.length > 3" class="text-xs text-gray-500">
            +{{ plan.tags.length - 3 }}
          </span>
        </div>
        
        <!-- 描述 -->
        <div v-if="plan.description && viewMode === 'list'" class="description">
          <p class="text-sm text-gray-600 line-clamp-2">{{ plan.description }}</p>
        </div>
        
        <!-- 最后更新时间 -->
        <div class="update-time">
          <span class="text-xs text-gray-400">
            更新于 {{ formatRelativeTime(plan.updatedAt) }}
          </span>
        </div>
      </div>
      
      <!-- 卡片底部操作 -->
      <div class="card-footer">
        <div class="action-buttons">
          <el-button 
            size="small" 
            type="primary"
            @click="viewPlan"
          >
            查看详情
          </el-button>
          
          <el-dropdown @command="handleAction" trigger="click">
            <el-button size="small" :icon="MoreFilled" circle />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="edit" :icon="Edit">
                  编辑计划
                </el-dropdown-item>
                <el-dropdown-item command="copy" :icon="CopyDocument">
                  复制计划
                </el-dropdown-item>
                <el-dropdown-item command="share" :icon="Share">
                  分享计划
                </el-dropdown-item>
                <el-dropdown-item command="export" :icon="Download">
                  导出计划
                </el-dropdown-item>
                <el-dropdown-item 
                  v-if="plan.status !== 'archived'" 
                  command="archive" 
                  :icon="FolderOpened"
                >
                  归档计划
                </el-dropdown-item>
                <el-dropdown-item 
                  v-else 
                  command="unarchive" 
                  :icon="FolderOpened"
                >
                  取消归档
                </el-dropdown-item>
                <el-dropdown-item 
                  command="delete" 
                  :icon="Delete"
                  class="text-red-500"
                  divided
                >
                  删除计划
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        
        <!-- 协作者头像 -->
        <div v-if="plan.collaborators && plan.collaborators.length > 0" class="collaborators">
          <el-avatar-group :max="3" size="small">
            <el-avatar
              v-for="collaborator in plan.collaborators"
              :key="collaborator.id"
              :src="collaborator.avatar"
              :title="collaborator.name"
            >
              {{ collaborator.name?.charAt(0).toUpperCase() }}
            </el-avatar>
          </el-avatar-group>
        </div>
      </div>
    </div>
    
    <!-- 悬浮效果 -->
    <div class="card-overlay" v-if="showOverlay">
      <div class="overlay-content">
        <el-button type="primary" @click="viewPlan">
          <el-icon class="mr-2"><View /></el-icon>
          查看计划
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  MapLocation,
  Location,
  Calendar,
  Wallet,
  User,
  Star,
  StarFilled,
  MoreFilled,
  Edit,
  CopyDocument,
  Share,
  Download,
  FolderOpened,
  Delete,
  View
} from '@element-plus/icons-vue'
import type { TravelPlan } from '@/types'

// Props
interface Props {
  plan: TravelPlan
  viewMode?: 'grid' | 'list'
  showOverlay?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  viewMode: 'grid',
  showOverlay: false
})

// Emits
const emit = defineEmits<{
  'favorite': [planId: string, isFavorite: boolean]
  'edit': [plan: TravelPlan]
  'copy': [plan: TravelPlan]
  'share': [plan: TravelPlan]
  'export': [plan: TravelPlan]
  'archive': [plan: TravelPlan]
  'delete': [plan: TravelPlan]
}>()

const router = useRouter()

// 方法
const getStatusType = (status: string) => {
  const statusMap = {
    'planning': 'info',
    'active': 'success',
    'completed': 'success',
    'cancelled': 'danger',
    'archived': 'warning'
  }
  return statusMap[status as keyof typeof statusMap] || 'info'
}

const getStatusText = (status: string) => {
  const statusMap = {
    'planning': '规划中',
    'active': '进行中',
    'completed': '已完成',
    'cancelled': '已取消',
    'archived': '已归档'
  }
  return statusMap[status as keyof typeof statusMap] || '未知'
}

const getProgressColor = (progress: number) => {
  if (progress < 30) return '#f56c6c'
  if (progress < 70) return '#e6a23c'
  return '#67c23a'
}

const formatDateRange = (startDate: string, endDate: string) => {
  const start = new Date(startDate)
  const end = new Date(endDate)
  
  const startMonth = start.getMonth() + 1
  const startDay = start.getDate()
  const endMonth = end.getMonth() + 1
  const endDay = end.getDate()
  
  if (startMonth === endMonth) {
    return `${startMonth}月${startDay}日-${endDay}日`
  } else {
    return `${startMonth}月${startDay}日-${endMonth}月${endDay}日`
  }
}

const getDuration = (startDate: string, endDate: string) => {
  const start = new Date(startDate)
  const end = new Date(endDate)
  const diffTime = Math.abs(end.getTime() - start.getTime())
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays + 1
}

const formatBudget = (budget: number) => {
  if (budget >= 10000) {
    return (budget / 10000).toFixed(1) + '万'
  }
  return budget.toLocaleString()
}

const formatRelativeTime = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = now.getTime() - date.getTime()
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) {
    const diffHours = Math.floor(diffTime / (1000 * 60 * 60))
    if (diffHours === 0) {
      const diffMinutes = Math.floor(diffTime / (1000 * 60))
      return `${diffMinutes}分钟前`
    }
    return `${diffHours}小时前`
  } else if (diffDays === 1) {
    return '昨天'
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return date.toLocaleDateString()
  }
}

const viewPlan = () => {
  router.push(`/plans/${props.plan.id}`)
}

const toggleFavorite = () => {
  const newFavoriteState = !props.plan.isFavorite
  emit('favorite', props.plan.id, newFavoriteState)
  
  ElMessage.success(
    newFavoriteState ? '已添加到收藏' : '已取消收藏'
  )
}

const handleAction = async (command: string) => {
  switch (command) {
    case 'edit':
      emit('edit', props.plan)
      break
      
    case 'copy':
      emit('copy', props.plan)
      ElMessage.success('计划已复制')
      break
      
    case 'share':
      emit('share', props.plan)
      break
      
    case 'export':
      emit('export', props.plan)
      ElMessage.success('导出功能开发中...')
      break
      
    case 'archive':
    case 'unarchive':
      emit('archive', props.plan)
      ElMessage.success(
        command === 'archive' ? '计划已归档' : '计划已取消归档'
      )
      break
      
    case 'delete':
      try {
        await ElMessageBox.confirm(
          '确定要删除这个旅行计划吗？此操作不可恢复。',
          '确认删除',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning',
            confirmButtonClass: 'el-button--danger'
          }
        )
        
        emit('delete', props.plan)
        ElMessage.success('计划已删除')
      } catch {
        // 用户取消删除
      }
      break
  }
}
</script>

<style scoped>
.plan-card {
  @apply bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden transition-all duration-300 cursor-pointer relative;
}

.plan-card:hover {
  @apply shadow-lg border-gray-200;
  transform: translateY(-2px);
}

.plan-card.grid-view {
  @apply flex flex-col;
}

.plan-card.list-view {
  @apply flex flex-row;
}

.plan-card.list-view .card-content {
  @apply flex flex-row w-full;
}

.plan-card.list-view .card-header {
  @apply flex-shrink-0 w-48;
}

.plan-card.list-view .card-body {
  @apply flex-1 px-4;
}

.plan-card.list-view .card-footer {
  @apply flex-shrink-0 w-48 flex flex-col justify-between;
}

.card-content {
  @apply flex flex-col h-full;
}

.card-header {
  @apply relative;
}

.plan-image {
  @apply relative w-full h-48 bg-gray-100 overflow-hidden;
}

.plan-card.list-view .plan-image {
  @apply h-32;
}

.default-image {
  @apply w-full h-full flex items-center justify-center bg-gradient-to-br from-gray-100 to-gray-200;
}

.status-badge {
  @apply absolute top-3 left-3;
}

.favorite-btn {
  @apply absolute top-3 right-3;
}

.plan-info {
  @apply p-4 pb-2;
}

.plan-card.list-view .plan-info {
  @apply p-3;
}

.plan-title {
  @apply text-lg font-semibold text-gray-800 mb-2 line-clamp-2 hover:text-primary-600 transition-colors;
}

.plan-card.list-view .plan-title {
  @apply text-base mb-1;
}

.plan-destination {
  @apply flex items-center text-sm text-gray-600;
}

.card-body {
  @apply flex-1 px-4 pb-2 space-y-3;
}

.plan-card.list-view .card-body {
  @apply space-y-2;
}

.date-info {
  @apply flex items-center justify-between;
}

.date-item {
  @apply flex items-center space-x-1;
}

.budget-travelers {
  @apply flex items-center justify-between;
}

.budget-item,
.travelers-item {
  @apply flex items-center space-x-1;
}

.progress-section {
  @apply space-y-2;
}

.progress-header {
  @apply flex items-center justify-between;
}

.tags-section {
  @apply flex flex-wrap items-center;
}

.description {
  @apply mt-2;
}

.update-time {
  @apply text-right;
}

.card-footer {
  @apply p-4 pt-2 flex items-center justify-between border-t border-gray-50;
}

.plan-card.list-view .card-footer {
  @apply p-3 border-t-0 border-l border-gray-50;
}

.action-buttons {
  @apply flex items-center space-x-2;
}

.collaborators {
  @apply flex items-center;
}

.card-overlay {
  @apply absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center opacity-0 transition-opacity duration-300;
}

.plan-card:hover .card-overlay {
  @apply opacity-100;
}

.overlay-content {
  @apply text-center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .plan-card.list-view {
    @apply flex-col;
  }
  
  .plan-card.list-view .card-content {
    @apply flex-col;
  }
  
  .plan-card.list-view .card-header,
  .plan-card.list-view .card-footer {
    @apply w-full;
  }
  
  .plan-card.list-view .plan-image {
    @apply h-40;
  }
  
  .plan-card.list-view .card-footer {
    @apply border-t border-l-0;
  }
}

/* 文本截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>