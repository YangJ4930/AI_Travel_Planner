<template>
  <div class="loading-spinner" :class="{ 'fullscreen': fullscreen, 'overlay': overlay }">
    <div class="spinner-container" :class="sizeClass">
      <!-- 默认旋转加载器 -->
      <div v-if="type === 'default'" class="spinner-default">
        <div class="spinner-ring"></div>
        <div class="spinner-ring"></div>
        <div class="spinner-ring"></div>
        <div class="spinner-ring"></div>
      </div>
      
      <!-- 点跳动加载器 -->
      <div v-else-if="type === 'dots'" class="spinner-dots">
        <div class="dot"></div>
        <div class="dot"></div>
        <div class="dot"></div>
      </div>
      
      <!-- 脉冲加载器 -->
      <div v-else-if="type === 'pulse'" class="spinner-pulse">
        <div class="pulse-ring"></div>
        <div class="pulse-ring"></div>
        <div class="pulse-ring"></div>
      </div>
      
      <!-- 波浪加载器 -->
      <div v-else-if="type === 'wave'" class="spinner-wave">
        <div class="wave-bar"></div>
        <div class="wave-bar"></div>
        <div class="wave-bar"></div>
        <div class="wave-bar"></div>
        <div class="wave-bar"></div>
      </div>
      
      <!-- 旅行主题加载器 -->
      <div v-else-if="type === 'travel'" class="spinner-travel">
        <div class="travel-icon">
          <el-icon class="airplane">
            <Promotion />
          </el-icon>
        </div>
        <div class="travel-path"></div>
      </div>
      
      <!-- 地图加载器 -->
      <div v-else-if="type === 'map'" class="spinner-map">
        <div class="map-pin">
          <el-icon><MapLocation /></el-icon>
        </div>
        <div class="map-ripple"></div>
      </div>
      
      <!-- 进度条加载器 -->
      <div v-else-if="type === 'progress'" class="spinner-progress">
        <div class="progress-bar">
          <div class="progress-fill"></div>
        </div>
        <div class="progress-text">{{ progressText }}</div>
      </div>
      
      <!-- 文字提示 -->
      <div v-if="text" class="loading-text" :class="textSizeClass">
        {{ text }}
      </div>
      
      <!-- 子文字提示 -->
      <div v-if="subText" class="loading-subtext">
        {{ subText }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Promotion, MapLocation } from '@element-plus/icons-vue'

// Props
interface Props {
  type?: 'default' | 'dots' | 'pulse' | 'wave' | 'travel' | 'map' | 'progress'
  size?: 'small' | 'medium' | 'large'
  text?: string
  subText?: string
  fullscreen?: boolean
  overlay?: boolean
  color?: string
  progressText?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  size: 'medium',
  fullscreen: false,
  overlay: false,
  color: '#409eff',
  progressText: '加载中...'
})

// 计算属性
const sizeClass = computed(() => {
  return `size-${props.size}`
})

const textSizeClass = computed(() => {
  const sizeMap = {
    small: 'text-sm',
    medium: 'text-base',
    large: 'text-lg'
  }
  return sizeMap[props.size]
})
</script>

<style scoped>
.loading-spinner {
  @apply flex items-center justify-center;
}

.loading-spinner.fullscreen {
  @apply fixed inset-0 z-50 bg-white;
}

.loading-spinner.overlay {
  @apply absolute inset-0 z-10 bg-white bg-opacity-90;
}

.spinner-container {
  @apply flex flex-col items-center space-y-4;
}

/* 尺寸样式 */
.size-small {
  @apply scale-75;
}

.size-medium {
  @apply scale-100;
}

.size-large {
  @apply scale-125;
}

/* 默认旋转加载器 */
.spinner-default {
  @apply relative w-16 h-16;
}

.spinner-ring {
  @apply absolute border-4 border-gray-200 rounded-full;
  width: 64px;
  height: 64px;
  animation: spin 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
}

.spinner-ring:nth-child(1) {
  border-top-color: #409eff;
  animation-delay: -0.45s;
}

.spinner-ring:nth-child(2) {
  border-top-color: #67c23a;
  animation-delay: -0.3s;
}

.spinner-ring:nth-child(3) {
  border-top-color: #e6a23c;
  animation-delay: -0.15s;
}

.spinner-ring:nth-child(4) {
  border-top-color: #f56c6c;
}

/* 点跳动加载器 */
.spinner-dots {
  @apply flex space-x-2;
}

.dot {
  @apply w-3 h-3 bg-primary-500 rounded-full;
  animation: bounce-dot 1.4s ease-in-out infinite both;
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}

.dot:nth-child(2) {
  animation-delay: -0.16s;
}

/* 脉冲加载器 */
.spinner-pulse {
  @apply relative w-16 h-16;
}

.pulse-ring {
  @apply absolute w-full h-full border-2 border-primary-500 rounded-full opacity-75;
  animation: pulse-ring 1.25s cubic-bezier(0.215, 0.61, 0.355, 1) infinite;
}

.pulse-ring:nth-child(2) {
  animation-delay: 0.33s;
}

.pulse-ring:nth-child(3) {
  animation-delay: 0.66s;
}

/* 波浪加载器 */
.spinner-wave {
  @apply flex items-end space-x-1;
}

.wave-bar {
  @apply w-2 bg-primary-500;
  height: 20px;
  animation: wave-bar 1.2s ease-in-out infinite;
}

.wave-bar:nth-child(1) {
  animation-delay: -1.2s;
}

.wave-bar:nth-child(2) {
  animation-delay: -1.1s;
}

.wave-bar:nth-child(3) {
  animation-delay: -1s;
}

.wave-bar:nth-child(4) {
  animation-delay: -0.9s;
}

.wave-bar:nth-child(5) {
  animation-delay: -0.8s;
}

/* 旅行主题加载器 */
.spinner-travel {
  @apply relative w-20 h-12;
}

.travel-icon {
  @apply absolute left-0 top-1/2 transform -translate-y-1/2 text-2xl text-primary-500;
  animation: fly 2s ease-in-out infinite;
}

.travel-path {
  @apply absolute top-1/2 left-0 w-full h-0.5 bg-gradient-to-r from-primary-200 to-transparent transform -translate-y-1/2;
  animation: path 2s ease-in-out infinite;
}

/* 地图加载器 */
.spinner-map {
  @apply relative w-16 h-16 flex items-center justify-center;
}

.map-pin {
  @apply text-2xl text-red-500 z-10 relative;
  animation: pin-bounce 1s ease-in-out infinite;
}

.map-ripple {
  @apply absolute w-full h-full border-2 border-red-300 rounded-full;
  animation: ripple 1.5s ease-out infinite;
}

/* 进度条加载器 */
.spinner-progress {
  @apply w-48;
}

.progress-bar {
  @apply w-full h-2 bg-gray-200 rounded-full overflow-hidden;
}

.progress-fill {
  @apply h-full bg-gradient-to-r from-primary-400 to-primary-600 rounded-full;
  animation: progress-fill 2s ease-in-out infinite;
}

.progress-text {
  @apply text-center text-sm text-gray-600 mt-2;
}

/* 文字样式 */
.loading-text {
  @apply font-medium text-gray-700 text-center;
}

.loading-subtext {
  @apply text-sm text-gray-500 text-center;
}

/* 动画定义 */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes bounce-dot {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

@keyframes pulse-ring {
  0% {
    transform: scale(0);
    opacity: 1;
  }
  100% {
    transform: scale(1);
    opacity: 0;
  }
}

@keyframes wave-bar {
  0%, 40%, 100% {
    transform: scaleY(0.4);
  }
  20% {
    transform: scaleY(1);
  }
}

@keyframes fly {
  0% {
    left: -10%;
    transform: translateY(-50%) rotate(-15deg);
  }
  50% {
    transform: translateY(-50%) rotate(0deg);
  }
  100% {
    left: 90%;
    transform: translateY(-50%) rotate(15deg);
  }
}

@keyframes path {
  0% {
    opacity: 0;
    transform: translateY(-50%) scaleX(0);
  }
  50% {
    opacity: 1;
    transform: translateY(-50%) scaleX(1);
  }
  100% {
    opacity: 0;
    transform: translateY(-50%) scaleX(1);
  }
}

@keyframes pin-bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes ripple {
  0% {
    transform: scale(0.8);
    opacity: 1;
  }
  100% {
    transform: scale(2);
    opacity: 0;
  }
}

@keyframes progress-fill {
  0% {
    width: 0%;
  }
  50% {
    width: 70%;
  }
  100% {
    width: 100%;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .spinner-progress {
    @apply w-40;
  }
}
</style>