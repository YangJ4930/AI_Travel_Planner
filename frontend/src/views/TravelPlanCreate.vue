<template>
  <div class="travel-plan-create">
    <div class="container mx-auto px-4 py-8">
      <!-- 页面标题 -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-800 mb-4">AI 旅行规划师</h1>
        <p class="text-lg text-gray-600">告诉我们您的旅行想法，AI将为您量身定制完美的旅行计划</p>
      </div>

      <!-- 输入表单 -->
      <div class="max-w-4xl mx-auto">
        <el-card class="shadow-lg">
          <template #header>
            <div class="flex items-center">
              <el-icon class="text-2xl text-blue-500 mr-3"><LocationFilled /></el-icon>
              <span class="text-xl font-semibold">描述您的旅行需求</span>
            </div>
          </template>

          <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
            <el-form-item label="旅行描述" prop="query">
              <div class="relative">
                <el-input
                  v-model="form.query"
                  type="textarea"
                  :rows="10"
                  placeholder="请详细描述您的旅行需求，例如：&#10;我想在春节期间和家人去三亚度假，预算1万元，希望住海景房，体验当地美食和水上运动...&#10;&#10;💡 提示：您也可以点击右下角的语音按钮进行语音输入"
                  maxlength="1000"
                  show-word-limit
                  class="w-full"
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
            </el-form-item>

            <!-- 示例提示 -->
            <div class="mb-6">
              <h3 class="text-lg font-medium mb-3 text-gray-700">💡 描述示例</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div 
                  v-for="example in examples" 
                  :key="example.title"
                  class="example-card p-4 border border-gray-200 rounded-lg cursor-pointer hover:border-blue-400 hover:shadow-md transition-all"
                  @click="useExample(example.content)"
                >
                  <h4 class="font-medium text-gray-800 mb-2">{{ example.title }}</h4>
                  <p class="text-sm text-gray-600">{{ example.content }}</p>
                </div>
              </div>
            </div>

            <el-form-item>
              <div class="flex justify-center space-x-4">
                <el-button 
                  type="primary" 
                  size="large"
                  :loading="loading"
                  @click="generatePlan"
                  class="px-8 py-3"
                  :disabled="loading"
                >
                  <el-icon v-if="!loading" class="mr-2"><MagicStick /></el-icon>
                  {{ getGenerateButtonText }}
                </el-button>
                <el-button 
                  size="large"
                  @click="resetForm"
                  class="px-8 py-3"
                  :disabled="loading"
                >
                  重置
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 生成结果 -->
        <div v-if="generatedPlan" class="mt-8">
          <el-card class="shadow-lg">
            <template #header>
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <el-icon class="text-2xl text-green-500 mr-3"><SuccessFilled /></el-icon>
                  <span class="text-xl font-semibold">计划生成成功！</span>
                </div>
                <el-button type="primary" @click="viewPlanList">查看我的计划</el-button>
              </div>
            </template>

            <div class="text-center py-8">
              <el-icon class="text-6xl text-green-500 mb-4"><CircleCheckFilled /></el-icon>
              <h3 class="text-2xl font-bold text-gray-800 mb-2">{{ generatedPlan.title }}</h3>
              <p class="text-gray-600 mb-6">您的专属旅行计划已生成完成</p>
              <div class="flex justify-center space-x-4">
                <el-button type="primary" size="large" @click="viewPlanDetail(generatedPlan.id)">
                  查看详细计划
                </el-button>
                <el-button size="large" @click="createAnother">
                  创建新计划
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { LocationFilled, MagicStick, SuccessFilled, CircleCheckFilled } from '@element-plus/icons-vue'
import { travelPlanApi, type TravelQueryParam } from '@/services/travelPlan'
import VoiceInput from '@/components/common/VoiceInput.vue'

const router = useRouter()

// 表单数据
const form = reactive<TravelQueryParam>({
  query: ''
})

// 表单验证规则
const rules = {
  query: [
    { required: true, message: '请输入旅行描述', trigger: 'blur' },
    { min: 10, message: '请至少输入10个字符的描述', trigger: 'blur' }
  ]
}

// 状态
const formRef = ref()
const loading = ref(false)
const generatedPlan = ref<any>(null)

// 示例数据
const examples = [
  {
    title: '家庭度假',
    content: '我想在春节期间和家人去三亚度假，预算1万元，希望住海景房，体验当地美食和水上运动'
  },
  {
    title: '文化之旅',
    content: '计划9月份去西安旅游3天，对历史文化感兴趣，想参观兵马俑、大雁塔等景点，预算5000元'
  },
  {
    title: '自然风光',
    content: '想在夏天去新疆看薰衣草和天山雪景，时间7天左右，喜欢摄影和户外活动'
  },
  {
    title: '美食探索',
    content: '计划去成都品尝正宗川菜，体验当地茶馆文化，停留4-5天，预算适中'
  }
]

// 使用示例
const useExample = (content: string) => {
  form.query = content
}

// 计算属性：生成按钮文字
const getGenerateButtonText = computed(() => {
  if (loading.value) {
    return '正在生成，请等待...'
  }
  return '生成旅行计划'
})

// 生成计划
const generatePlan = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    
    // 显示开始生成的提示
    ElMessage.info('开始生成旅行计划，请耐心等待...')
    
    // 调用后端API生成计划
    const response = await travelPlanApi.addTravelPlan(form)
    
    if (response.code === 0) {
      ElMessage.success('旅行计划生成成功！')
      // 获取最新的计划列表
      const listResponse = await travelPlanApi.listTravelPlan()
      if (listResponse.code === 0 && listResponse.data && listResponse.data.length > 0) {
        // 获取最新创建的计划
        const latestPlan = listResponse.data[listResponse.data.length - 1]
        // 获取计划详情
        const detailResponse = await travelPlanApi.getTravelPlan(latestPlan.id)
        if (detailResponse.code === 0 && detailResponse.data) {
          generatedPlan.value = detailResponse.data
        }
      }
    } else {
      ElMessage.error(response.message || '生成计划失败')
    }
  } catch (error) {
    console.error('生成计划失败:', error)
    ElMessage.error('生成计划失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  generatedPlan.value = null
}

// 查看计划列表
const viewPlanList = () => {
  router.push('/travel-plans')
}

// 查看计划详情
const viewPlanDetail = (id: number) => {
  router.push(`/travel-plans/${id}`)
}

// 创建新计划
const createAnother = () => {
  generatedPlan.value = null
  resetForm()
}

// 语音输入处理方法
const handleVoiceResult = (text: string) => {
  console.log('语音识别结果:', text)
  if (text.trim()) {
    // 如果已有内容，在后面追加；否则直接设置
    if (form.query.trim()) {
      form.query += ' ' + text
    } else {
      form.query = text
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
.travel-plan-create {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
}

.container {
  position: relative;
  z-index: 1;
}

.example-card {
  transition: all 0.3s ease;
}

.example-card:hover {
  transform: translateY(-2px);
}

:deep(.el-card) {
  border-radius: 12px;
  border: none;
}

:deep(.el-card__header) {
  background: linear-gradient(90deg, #f8fafc 0%, #e2e8f0 100%);
  border-bottom: 1px solid #e2e8f0;
}

:deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  transition: border-color 0.3s ease;
}

:deep(.el-textarea__inner:focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border: none;
  border-radius: 8px;
  font-weight: 600;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
}
</style>