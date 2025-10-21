<template>
  <div class="planning-container">
    <!-- 页面头部 -->
    <div class="header-section">
      <div class="container mx-auto px-4 py-6">
        <div class="flex justify-between items-center">
          <div>
            <h1 class="text-3xl font-bold text-gray-800">AI 旅行规划助手</h1>
            <p class="text-gray-600 mt-2">描述你的需求，我为你创建计划卡片</p>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-sm text-gray-500">欢迎，{{ authStore.user?.username }}</span>
            <el-button @click="goBackToRedirect" type="text" class="text-gray-500">返回列表</el-button>
            <el-button @click="handleLogout" type="text" class="text-gray-500">退出登录</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="container mx-auto px-4 py-8">
        <div class="max-w-5xl mx-auto">
          <!-- 输入区域（非对话，仅用于创建新计划） -->
          <div class="input-section bg-white rounded-lg shadow-lg p-6 mb-8">
            <h2 class="text-xl font-semibold text-gray-800 mb-4">描述你的旅行需求</h2>
            <div class="relative">
              <el-input
                v-model="travelInput"
                type="textarea"
                :rows="10"
                placeholder="例如：目的地、天数、预算、偏好、同行人数等。提交后将在下方生成一张计划卡片。&#10;&#10;💡 提示：您也可以点击右下角的语音按钮进行语音输入"
                class="planning-textarea"
                maxlength="1000"
                show-word-limit
              />
              <!-- 语音输入按钮 -->
              <div class="absolute bottom-2 right-12 z-10">
                <VoiceInput
                  placeholder="点击开始语音输入旅行需求"
                  :continuous="true"
                  @result="handleVoiceResult"
                  @start="handleVoiceStart"
                  @end="handleVoiceEnd"
                  @error="handleVoiceError"
                />
              </div>
            </div>
            <div class="flex justify-end mt-3 space-x-3">
              <el-button @click="clearInput">清空</el-button>
              <el-button type="primary" @click="submitQuery" :loading="isGenerating" :disabled="!travelInput.trim()">
                {{ isGenerating ? '生成中...' : '生成旅行计划' }}
              </el-button>
            </div>
          </div>

          <!-- 计划卡片列表（最新在首位，不显示生成时间） -->
          <div class="plans-section bg-white rounded-lg shadow-lg p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-semibold text-gray-800">我的旅行计划</h2>
              <el-button @click="refreshPlans" :loading="loadingPlans">刷新</el-button>
            </div>

            <div v-loading="loadingPlans" class="min-h-40">
              <div v-if="!loadingPlans && plansSorted.length === 0" class="text-center py-12">
                <el-empty description="暂无旅行计划"></el-empty>
              </div>

              <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div
                  v-for="plan in plansSorted"
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
                        <el-button type="text" size="small" @click.stop="viewPlanDetail(plan.id)">
                          查看详情
                          <el-icon class="ml-1"><ArrowRight /></el-icon>
                        </el-button>
                      </div>
                    </template>

                    <div class="space-y-4">
                      <!-- 计划描述（不显示生成时间） -->
                      <div class="text-gray-600 text-sm leading-relaxed">
                        <p class="line-clamp-3">{{ plan.query }}</p>
                      </div>

                      <!-- 简单标签（从查询中提取前几个词） -->
                      <div class="flex flex-wrap gap-2">
                        <el-tag
                          v-for="tag in extractTags(plan.query)"
                          :key="tag"
                          size="small" type="info" effect="light"
                        >
                          {{ tag }}
                        </el-tag>
                      </div>
                    </div>
                  </el-card>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { travelPlanApi, type TravelPlanVo } from '@/services/travelPlan'
import { LocationFilled, ArrowRight } from '@element-plus/icons-vue'
import VoiceInput from '@/components/common/VoiceInput.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const travelInput = ref('')
const isGenerating = ref(false)
const loadingPlans = ref(false)
const plans = ref<TravelPlanVo[]>([])

const plansSorted = computed(() => {
  return [...plans.value].sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
})

onMounted(() => {
  refreshPlans()
})

const handleLogout = async () => {
  await authStore.logout()
  router.push('/login')
}

const clearInput = () => {
  travelInput.value = ''
}

const submitQuery = async () => {
  const text = travelInput.value.trim()
  if (!text) return

  try {
    isGenerating.value = true
    const addResp = await travelPlanApi.addTravelPlan({ query: text })
    if (addResp.code !== 0) {
      throw new Error(addResp.message || '生成失败')
    }

    ElMessage.success('已创建旅行计划')
    travelInput.value = ''
    await refreshPlans()
    goBackToRedirect()
  } catch (err: any) {
    console.error(err)
    ElMessage.error(err.message || '生成计划失败，请稍后重试')
  } finally {
    isGenerating.value = false
  }
}

const goBackToRedirect = () => {
  const redirect = (route.query.redirect as string) || '/travel-plans'
  router.replace(redirect)
}

const refreshPlans = async () => {
  try {
    loadingPlans.value = true
    const listResp = await travelPlanApi.listTravelPlan()
    if (listResp.code === 0 && listResp.data) {
      plans.value = listResp.data
    } else {
      ElMessage.error(listResp.message || '获取计划列表失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('获取计划列表失败，请稍后重试')
  } finally {
    loadingPlans.value = false
  }
}

const viewPlanDetail = (id: number) => {
  router.push(`/travel-plans/${id}`)
}

const extractTags = (query: string): string[] => {
  return (query || '')
    .split(/[\s,，;；]+/)
    .filter(t => t && t.length > 0)
    .slice(0, 5)
}

// 语音输入处理方法
const handleVoiceResult = (text: string) => {
  console.log('语音识别结果:', text)
  if (text.trim()) {
    // 如果已有内容，在后面追加；否则直接设置
    if (travelInput.value.trim()) {
      travelInput.value += ' ' + text
    } else {
      travelInput.value = text
    }
    ElMessage.success('语音输入成功')
  }
}

const handleVoiceStart = () => {
  console.log('开始语音输入')
}

const handleVoiceEnd = () => {
  console.log('语音输入结束')
}

const handleVoiceError = (error: string) => {
  console.error('语音输入错误:', error)
  ElMessage.error(`语音输入失败: ${error}`)
}
</script>

<style scoped>
.planning-container { min-height: 100vh; background-color: #f5f7fa; }
.header-section { background-color: white; border-bottom: 1px solid #e5e7eb; }
.planning-textarea :deep(.el-textarea__inner) { font-size: 14px; line-height: 1.6; }
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>