<template>
  <div class="create-plan-view">
    <!-- 页面头部 -->
    <div class="page-header bg-white shadow-sm border-b px-6 py-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <el-button :icon="ArrowLeft" @click="$router.back()">
            返回
          </el-button>
          <div>
            <h1 class="text-2xl font-bold text-gray-800">创建旅行计划</h1>
            <p class="text-gray-600 mt-1">让AI帮您规划完美的旅程</p>
          </div>
        </div>
        
        <div class="flex items-center space-x-4">
          <el-button @click="saveDraft">
            <el-icon class="mr-2"><Document /></el-icon>
            保存草稿
          </el-button>
          <el-button type="primary" @click="createPlan" :loading="loading">
            <el-icon class="mr-2"><Check /></el-icon>
            创建计划
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content p-6">
      <div class="max-w-4xl mx-auto">
        <!-- 步骤指示器 -->
        <div class="steps-section mb-8">
          <el-steps :active="currentStep" align-center>
            <el-step title="基本信息" description="填写旅行基本信息" />
            <el-step title="偏好设置" description="设置旅行偏好" />
            <el-step title="AI生成" description="AI智能生成行程" />
            <el-step title="完成创建" description="确认并创建计划" />
          </el-steps>
        </div>

        <!-- 表单内容 -->
        <div class="form-section">
          <!-- 步骤1: 基本信息 -->
          <div v-show="currentStep === 0" class="step-content">
            <el-card class="step-card">
              <template #header>
                <div class="card-header">
                  <h3 class="text-lg font-semibold">基本信息</h3>
                  <p class="text-sm text-gray-600">请填写您的旅行基本信息</p>
                </div>
              </template>
              
              <el-form :model="planForm" :rules="basicRules" ref="basicFormRef" label-width="120px">
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="计划名称" prop="title">
                      <el-input 
                        v-model="planForm.title" 
                        placeholder="给您的旅行起个名字"
                        maxlength="50"
                        show-word-limit
                      />
                    </el-form-item>
                  </el-col>
                  
                  <el-col :span="12">
                    <el-form-item label="目的地" prop="destination">
                      <el-autocomplete
                        v-model="planForm.destination"
                        :fetch-suggestions="searchDestinations"
                        placeholder="输入目的地城市或景点"
                        class="w-full"
                        clearable
                      >
                        <template #default="{ item }">
                          <div class="flex items-center space-x-2">
                            <el-icon><MapLocation /></el-icon>
                            <span>{{ item.name }}</span>
                            <span class="text-gray-500 text-sm">{{ item.country }}</span>
                          </div>
                        </template>
                      </el-autocomplete>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="出发日期" prop="startDate">
                      <el-date-picker
                        v-model="planForm.startDate"
                        type="date"
                        placeholder="选择出发日期"
                        class="w-full"
                        :disabled-date="disabledStartDate"
                      />
                    </el-form-item>
                  </el-col>
                  
                  <el-col :span="12">
                    <el-form-item label="返程日期" prop="endDate">
                      <el-date-picker
                        v-model="planForm.endDate"
                        type="date"
                        placeholder="选择返程日期"
                        class="w-full"
                        :disabled-date="disabledEndDate"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="旅行人数" prop="travelers">
                      <el-input-number 
                        v-model="planForm.travelers" 
                        :min="1" 
                        :max="20"
                        class="w-full"
                      />
                    </el-form-item>
                  </el-col>
                  
                  <el-col :span="12">
                    <el-form-item label="预算范围" prop="budget">
                      <el-select v-model="planForm.budgetRange" placeholder="选择预算范围" class="w-full">
                        <el-option label="经济型 (< ¥3,000)" value="budget" />
                        <el-option label="舒适型 (¥3,000 - ¥8,000)" value="comfort" />
                        <el-option label="豪华型 (¥8,000 - ¥20,000)" value="luxury" />
                        <el-option label="奢华型 (> ¥20,000)" value="premium" />
                        <el-option label="自定义" value="custom" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row v-if="planForm.budgetRange === 'custom'" :gutter="24">
                  <el-col :span="12">
                    <el-form-item label="自定义预算">
                      <el-input-number 
                        v-model="planForm.budget" 
                        :min="0"
                        :step="100"
                        class="w-full"
                        placeholder="输入预算金额"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-form-item label="旅行描述">
                  <el-input
                    v-model="planForm.description"
                    type="textarea"
                    :rows="3"
                    placeholder="简单描述一下您的旅行想法..."
                    maxlength="200"
                    show-word-limit
                  />
                </el-form-item>
              </el-form>
            </el-card>
          </div>

          <!-- 步骤2: 偏好设置 -->
          <div v-show="currentStep === 1" class="step-content">
            <el-card class="step-card">
              <template #header>
                <div class="card-header">
                  <h3 class="text-lg font-semibold">偏好设置</h3>
                  <p class="text-sm text-gray-600">告诉我们您的旅行偏好，AI将为您量身定制</p>
                </div>
              </template>
              
              <div class="preferences-content space-y-6">
                <!-- 旅行类型 -->
                <div class="preference-section">
                  <h4 class="text-md font-medium mb-4">旅行类型</h4>
                  <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div 
                      v-for="type in travelTypes" 
                      :key="type.value"
                      class="preference-card"
                      :class="{ 'selected': planForm.travelType === type.value }"
                      @click="planForm.travelType = type.value"
                    >
                      <el-icon class="text-2xl mb-2">
                        <component :is="type.icon" />
                      </el-icon>
                      <p class="text-sm font-medium">{{ type.label }}</p>
                    </div>
                  </div>
                </div>
                
                <!-- 兴趣偏好 -->
                <div class="preference-section">
                  <h4 class="text-md font-medium mb-4">兴趣偏好 (可多选)</h4>
                  <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div 
                      v-for="interest in interests" 
                      :key="interest.value"
                      class="preference-card"
                      :class="{ 'selected': planForm.interests.includes(interest.value) }"
                      @click="toggleInterest(interest.value)"
                    >
                      <el-icon class="text-2xl mb-2">
                        <component :is="interest.icon" />
                      </el-icon>
                      <p class="text-sm font-medium">{{ interest.label }}</p>
                    </div>
                  </div>
                </div>
                
                <!-- 住宿偏好 -->
                <div class="preference-section">
                  <h4 class="text-md font-medium mb-4">住宿偏好</h4>
                  <el-radio-group v-model="planForm.accommodationType" class="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <el-radio 
                      v-for="accommodation in accommodationTypes" 
                      :key="accommodation.value"
                      :label="accommodation.value"
                      class="accommodation-radio"
                    >
                      <div class="flex flex-col items-center p-4 border rounded-lg">
                        <el-icon class="text-2xl mb-2">
                          <component :is="accommodation.icon" />
                        </el-icon>
                        <span class="text-sm">{{ accommodation.label }}</span>
                      </div>
                    </el-radio>
                  </el-radio-group>
                </div>
                
                <!-- 交通偏好 -->
                <div class="preference-section">
                  <h4 class="text-md font-medium mb-4">交通偏好</h4>
                  <el-checkbox-group v-model="planForm.transportationTypes" class="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <el-checkbox 
                      v-for="transport in transportationTypes" 
                      :key="transport.value"
                      :label="transport.value"
                      class="transport-checkbox"
                    >
                      <div class="flex flex-col items-center p-4 border rounded-lg">
                        <el-icon class="text-2xl mb-2">
                          <component :is="transport.icon" />
                        </el-icon>
                        <span class="text-sm">{{ transport.label }}</span>
                      </div>
                    </el-checkbox>
                  </el-checkbox-group>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 步骤3: AI生成 -->
          <div v-show="currentStep === 2" class="step-content">
            <el-card class="step-card">
              <template #header>
                <div class="card-header">
                  <h3 class="text-lg font-semibold">AI智能生成</h3>
                  <p class="text-sm text-gray-600">AI正在为您生成个性化的旅行计划</p>
                </div>
              </template>
              
              <div class="ai-generation-content">
                <div v-if="!aiGenerated" class="generation-process text-center py-12">
                  <div class="mb-6">
                    <el-icon class="text-6xl text-primary-500 animate-spin"><Loading /></el-icon>
                  </div>
                  <h4 class="text-lg font-semibold mb-2">AI正在为您生成旅行计划...</h4>
                  <p class="text-gray-600 mb-6">{{ generationStatus }}</p>
                  <el-progress :percentage="generationProgress" class="max-w-md mx-auto" />
                </div>
                
                <div v-else class="generation-result">
                  <div class="result-summary mb-6 p-4 bg-green-50 border border-green-200 rounded-lg">
                    <div class="flex items-center mb-2">
                      <el-icon class="text-green-500 mr-2"><Check /></el-icon>
                      <h4 class="font-semibold text-green-800">AI生成完成！</h4>
                    </div>
                    <p class="text-green-700 text-sm">
                      已为您生成包含 {{ generatedItinerary.length }} 天行程的旅行计划，
                      涵盖 {{ generatedItinerary.reduce((acc, day) => acc + day.activities.length, 0) }} 个精选活动。
                    </p>
                  </div>
                  
                  <!-- 生成的行程预览 -->
                  <div class="itinerary-preview">
                    <h4 class="text-md font-semibold mb-4">行程预览</h4>
                    <div class="space-y-4">
                      <div 
                        v-for="(day, index) in generatedItinerary" 
                        :key="index"
                        class="day-item border rounded-lg p-4"
                      >
                        <div class="flex items-center justify-between mb-3">
                          <h5 class="font-medium">第{{ index + 1 }}天 - {{ day.date }}</h5>
                          <el-tag size="small">{{ day.activities.length }}个活动</el-tag>
                        </div>
                        <div class="activities-list space-y-2">
                          <div 
                            v-for="activity in day.activities" 
                            :key="activity.id"
                            class="activity-item flex items-center space-x-3 p-2 bg-gray-50 rounded"
                          >
                            <el-icon class="text-primary-500"><MapLocation /></el-icon>
                            <div class="flex-1">
                              <p class="font-medium text-sm">{{ activity.name }}</p>
                              <p class="text-xs text-gray-600">{{ activity.time }} | {{ activity.duration }}</p>
                            </div>
                            <span class="text-xs text-gray-500">¥{{ activity.estimatedCost }}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div class="generation-actions mt-6 flex justify-center space-x-4">
                    <el-button @click="regenerateItinerary">
                      <el-icon class="mr-2"><Refresh /></el-icon>
                      重新生成
                    </el-button>
                    <el-button type="primary" @click="nextStep">
                      <el-icon class="mr-2"><Check /></el-icon>
                      确认使用
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 步骤4: 完成创建 -->
          <div v-show="currentStep === 3" class="step-content">
            <el-card class="step-card">
              <template #header>
                <div class="card-header">
                  <h3 class="text-lg font-semibold">完成创建</h3>
                  <p class="text-sm text-gray-600">确认您的旅行计划信息</p>
                </div>
              </template>
              
              <div class="completion-content">
                <!-- 计划摘要 -->
                <div class="plan-summary mb-6">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div class="summary-section">
                      <h4 class="font-semibold mb-3">基本信息</h4>
                      <div class="space-y-2 text-sm">
                        <div class="flex justify-between">
                          <span class="text-gray-600">计划名称:</span>
                          <span>{{ planForm.title }}</span>
                        </div>
                        <div class="flex justify-between">
                          <span class="text-gray-600">目的地:</span>
                          <span>{{ planForm.destination }}</span>
                        </div>
                        <div class="flex justify-between">
                          <span class="text-gray-600">旅行日期:</span>
                          <span>{{ formatDateRange(planForm.startDate, planForm.endDate) }}</span>
                        </div>
                        <div class="flex justify-between">
                          <span class="text-gray-600">旅行人数:</span>
                          <span>{{ planForm.travelers }}人</span>
                        </div>
                        <div class="flex justify-between">
                          <span class="text-gray-600">预算:</span>
                          <span>{{ getBudgetText() }}</span>
                        </div>
                      </div>
                    </div>
                    
                    <div class="summary-section">
                      <h4 class="font-semibold mb-3">偏好设置</h4>
                      <div class="space-y-2 text-sm">
                        <div class="flex justify-between">
                          <span class="text-gray-600">旅行类型:</span>
                          <span>{{ getTravelTypeText() }}</span>
                        </div>
                        <div class="flex justify-between">
                          <span class="text-gray-600">住宿偏好:</span>
                          <span>{{ getAccommodationText() }}</span>
                        </div>
                        <div>
                          <span class="text-gray-600">兴趣偏好:</span>
                          <div class="mt-1">
                            <el-tag 
                              v-for="interest in planForm.interests" 
                              :key="interest"
                              size="small"
                              class="mr-1 mb-1"
                            >
                              {{ getInterestText(interest) }}
                            </el-tag>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- 最终确认 -->
                <div class="final-confirmation text-center">
                  <div class="mb-6">
                    <el-icon class="text-6xl text-green-500 mb-4"><SuccessFilled /></el-icon>
                    <h4 class="text-lg font-semibold mb-2">旅行计划准备就绪！</h4>
                    <p class="text-gray-600">
                      您的个性化旅行计划已经生成完成，包含详细的行程安排和预算规划。
                    </p>
                  </div>
                  
                  <div class="confirmation-actions space-x-4">
                    <el-button size="large" @click="prevStep">
                      <el-icon class="mr-2"><ArrowLeft /></el-icon>
                      返回修改
                    </el-button>
                    <el-button type="primary" size="large" @click="createPlan" :loading="loading">
                      <el-icon class="mr-2"><Check /></el-icon>
                      创建计划
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 步骤导航 -->
        <div class="step-navigation mt-8 flex justify-between">
          <el-button 
            v-if="currentStep > 0" 
            @click="prevStep"
            :disabled="currentStep === 2 && !aiGenerated"
          >
            <el-icon class="mr-2"><ArrowLeft /></el-icon>
            上一步
          </el-button>
          <div v-else></div>
          
          <el-button 
            v-if="currentStep < 3" 
            type="primary" 
            @click="nextStep"
            :disabled="!canProceed"
          >
            下一步
            <el-icon class="ml-2"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useTravelStore } from '@/stores/travel'
import {
  ArrowLeft,
  ArrowRight,
  Document,
  Check,
  MapLocation,
  Loading,
  Refresh,
  SuccessFilled,
  // 旅行类型图标
  Sunny,
  Camera,
  Coffee,
  ShoppingBag,
  // 兴趣偏好图标
  Picture,
  Trophy,
  Food,
  Star,
  // 住宿类型图标
  House,
  OfficeBuilding,
  // 交通类型图标
  CarFilled,
  Train
} from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const travelStore = useTravelStore()

// 响应式数据
const currentStep = ref(0)
const loading = ref(false)
const aiGenerated = ref(false)
const generationProgress = ref(0)
const generationStatus = ref('正在分析您的偏好...')

// 表单引用
const basicFormRef = ref<FormInstance>()

// 表单数据
const planForm = ref({
  title: '',
  destination: '',
  startDate: '',
  endDate: '',
  travelers: 2,
  budgetRange: 'comfort',
  budget: 0,
  description: '',
  travelType: 'leisure',
  interests: [] as string[],
  accommodationType: 'hotel',
  transportationTypes: [] as string[]
})

// 生成的行程数据
const generatedItinerary = ref([
  {
    date: '2024-03-15',
    activities: [
      {
        id: '1',
        name: '抵达大阪关西机场',
        time: '10:00',
        duration: '2小时',
        estimatedCost: 200
      },
      {
        id: '2',
        name: '大阪城公园游览',
        time: '14:00',
        duration: '3小时',
        estimatedCost: 50
      }
    ]
  }
])

// 表单验证规则
const basicRules: FormRules = {
  title: [
    { required: true, message: '请输入计划名称', trigger: 'blur' }
  ],
  destination: [
    { required: true, message: '请输入目的地', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择出发日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择返程日期', trigger: 'change' }
  ],
  travelers: [
    { required: true, message: '请输入旅行人数', trigger: 'blur' }
  ]
}

// 选项数据
const travelTypes = [
  { value: 'leisure', label: '休闲度假', icon: 'Sunny' },
  { value: 'adventure', label: '探险冒险', icon: 'Trophy' },
  { value: 'cultural', label: '文化体验', icon: 'Camera' },
  { value: 'business', label: '商务出行', icon: 'OfficeBuilding' }
]

const interests = [
  { value: 'sightseeing', label: '观光游览', icon: 'Picture' },
  { value: 'food', label: '美食体验', icon: 'Food' },
  { value: 'shopping', label: '购物', icon: 'ShoppingBag' },
  { value: 'nightlife', label: '夜生活', icon: 'Star' },
  { value: 'nature', label: '自然风光', icon: 'Sunny' },
  { value: 'history', label: '历史文化', icon: 'Camera' },
  { value: 'sports', label: '运动健身', icon: 'Trophy' },
  { value: 'relaxation', label: '休闲放松', icon: 'Coffee' }
]

const accommodationTypes = [
  { value: 'hotel', label: '酒店', icon: 'OfficeBuilding' },
  { value: 'hostel', label: '青旅', icon: 'House' },
  { value: 'apartment', label: '公寓', icon: 'House' },
  { value: 'resort', label: '度假村', icon: 'Sunny' }
]

const transportationTypes = [
  { value: 'flight', label: '飞机', icon: 'Sunny' },
  { value: 'train', label: '火车', icon: 'Train' },
  { value: 'car', label: '汽车', icon: 'CarFilled' },
  { value: 'bus', label: '大巴', icon: 'CarFilled' }
]

// 计算属性
const canProceed = computed(() => {
  switch (currentStep.value) {
    case 0:
      return planForm.value.title && 
             planForm.value.destination && 
             planForm.value.startDate && 
             planForm.value.endDate &&
             planForm.value.travelers > 0
    case 1:
      return planForm.value.travelType && 
             planForm.value.accommodationType &&
             planForm.value.interests.length > 0
    case 2:
      return aiGenerated.value
    case 3:
      return true
    default:
      return false
  }
})

// 方法
const nextStep = async () => {
  if (currentStep.value === 0) {
    // 验证基本信息表单
    if (!basicFormRef.value) return
    
    try {
      await basicFormRef.value.validate()
    } catch {
      return
    }
  }
  
  if (currentStep.value === 1) {
    // 进入AI生成步骤
    currentStep.value++
    await generateItinerary()
    return
  }
  
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const generateItinerary = async () => {
  aiGenerated.value = false
  generationProgress.value = 0
  
  const steps = [
    '正在分析您的偏好...',
    '正在搜索最佳景点...',
    '正在规划最优路线...',
    '正在计算预算方案...',
    '正在生成详细行程...'
  ]
  
  for (let i = 0; i < steps.length; i++) {
    generationStatus.value = steps[i]
    generationProgress.value = (i + 1) * 20
    
    await new Promise(resolve => setTimeout(resolve, 1000))
  }
  
  aiGenerated.value = true
  ElMessage.success('AI行程生成完成！')
}

const regenerateItinerary = async () => {
  await generateItinerary()
}

const toggleInterest = (interest: string) => {
  const index = planForm.value.interests.indexOf(interest)
  if (index > -1) {
    planForm.value.interests.splice(index, 1)
  } else {
    planForm.value.interests.push(interest)
  }
}

const searchDestinations = (queryString: string, cb: Function) => {
  // 模拟目的地搜索
  const destinations = [
    { name: '东京', country: '日本' },
    { name: '大阪', country: '日本' },
    { name: '京都', country: '日本' },
    { name: '巴黎', country: '法国' },
    { name: '伦敦', country: '英国' },
    { name: '纽约', country: '美国' }
  ]
  
  const results = queryString 
    ? destinations.filter(item => 
        item.name.toLowerCase().includes(queryString.toLowerCase())
      )
    : destinations
  
  cb(results)
}

const disabledStartDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7
}

const disabledEndDate = (time: Date) => {
  if (!planForm.value.startDate) return false
  return time.getTime() <= new Date(planForm.value.startDate).getTime()
}

const formatDateRange = (startDate: string, endDate: string) => {
  if (!startDate || !endDate) return ''
  
  const start = new Date(startDate).toLocaleDateString('zh-CN')
  const end = new Date(endDate).toLocaleDateString('zh-CN')
  return `${start} - ${end}`
}

const getBudgetText = () => {
  if (planForm.value.budgetRange === 'custom') {
    return `¥${planForm.value.budget?.toLocaleString()}`
  }
  
  const budgetMap: Record<string, string> = {
    'budget': '经济型 (< ¥3,000)',
    'comfort': '舒适型 (¥3,000 - ¥8,000)',
    'luxury': '豪华型 (¥8,000 - ¥20,000)',
    'premium': '奢华型 (> ¥20,000)'
  }
  
  return budgetMap[planForm.value.budgetRange] || ''
}

const getTravelTypeText = () => {
  const type = travelTypes.find(t => t.value === planForm.value.travelType)
  return type?.label || ''
}

const getAccommodationText = () => {
  const accommodation = accommodationTypes.find(a => a.value === planForm.value.accommodationType)
  return accommodation?.label || ''
}

const getInterestText = (interest: string) => {
  const interestItem = interests.find(i => i.value === interest)
  return interestItem?.label || ''
}

const saveDraft = async () => {
  try {
    // 这里应该调用API保存草稿
    ElMessage.success('草稿保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const createPlan = async () => {
  loading.value = true
  
  try {
    // 这里应该调用API创建计划
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('旅行计划创建成功！')
    router.push('/plans')
  } catch (error) {
    ElMessage.error('创建失败')
  } finally {
    loading.value = false
  }
}

// 组件挂载
onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
.create-plan-view {
  min-height: 100vh;
  background-color: #f9fafb;
}

.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
}

.step-card {
  border: none;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  text-align: center;
}

.preference-card {
  @apply p-4 border-2 border-gray-200 rounded-lg cursor-pointer transition-all duration-200 text-center;
}

.preference-card:hover {
  @apply border-primary-300 bg-primary-50;
}

.preference-card.selected {
  @apply border-primary-500 bg-primary-100;
}

.accommodation-radio :deep(.el-radio__input) {
  display: none;
}

.accommodation-radio :deep(.el-radio__label) {
  padding: 0;
}

.accommodation-radio.is-checked :deep(.el-radio__label) {
  color: inherit;
}

.accommodation-radio.is-checked div {
  @apply border-primary-500 bg-primary-100;
}

.transport-checkbox :deep(.el-checkbox__input) {
  display: none;
}

.transport-checkbox :deep(.el-checkbox__label) {
  padding: 0;
}

.transport-checkbox.is-checked :deep(.el-checkbox__label) {
  color: inherit;
}

.transport-checkbox.is-checked div {
  @apply border-primary-500 bg-primary-100;
}

.day-item {
  transition: all 0.3s ease;
}

.day-item:hover {
  @apply shadow-md;
}

.activity-item {
  transition: all 0.2s ease;
}

.activity-item:hover {
  @apply bg-gray-100;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 2s linear infinite;
}

:deep(.el-steps) {
  margin-bottom: 2rem;
}

:deep(.el-step__title) {
  font-size: 14px;
}

:deep(.el-step__description) {
  font-size: 12px;
}
</style>