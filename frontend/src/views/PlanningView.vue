<template>
  <div class="planning-container">
    <!-- 页面头部 -->
    <div class="header-section">
      <div class="container mx-auto px-4 py-6">
        <div class="flex justify-between items-center">
          <div>
            <h1 class="text-3xl font-bold text-gray-800">AI 旅行规划助手</h1>
            <p class="text-gray-600 mt-2">告诉我你的旅行想法，我来为你制定完美的行程</p>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-sm text-gray-500">欢迎，{{ user?.username }}</span>
            <el-button @click="handleLogout" type="text" class="text-gray-500">退出登录</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="container mx-auto px-4 py-8">
        <div class="max-w-4xl mx-auto">
          <!-- 输入区域 -->
          <div class="input-section bg-white rounded-lg shadow-lg p-6 mb-8">
            <h2 class="text-xl font-semibold text-gray-800 mb-4">描述你的旅行需求</h2>
            
            <!-- 文字输入框 -->
            <div class="mb-6">
              <el-input
                v-model="travelInput"
                type="textarea"
                :rows="6"
                placeholder="请详细描述你的旅行需求，例如：
• 目的地：想去哪里旅行？
• 时间：什么时候出发，计划几天？
• 预算：大概的预算范围
• 偏好：喜欢什么类型的活动（文化、美食、自然风光等）
• 人数：几个人一起旅行
• 其他特殊要求

示例：我想在下个月去日本旅行5天，预算1万元左右，喜欢体验当地文化和美食，两个人一起去..."
                class="planning-textarea"
                maxlength="1000"
                show-word-limit
              />
            </div>

            <!-- 操作按钮 -->
            <div class="flex justify-between items-center">
              <div class="text-sm text-gray-500">
                <i class="el-icon-info mr-1"></i>
                提示：描述越详细，AI 生成的行程越符合你的需求
              </div>
              <div class="space-x-3">
                <el-button @click="clearInput">清空</el-button>
                <el-button 
                  type="primary" 
                  @click="generatePlan"
                  :loading="isGenerating"
                  :disabled="!travelInput.trim()"
                >
                  <i class="el-icon-magic-stick mr-1"></i>
                  {{ isGenerating ? '正在生成...' : '生成旅行计划' }}
                </el-button>
              </div>
            </div>
          </div>

          <!-- 生成结果区域 -->
          <div v-if="generatedPlan" class="result-section bg-white rounded-lg shadow-lg p-6">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-xl font-semibold text-gray-800">为你生成的旅行计划</h2>
              <el-button type="success" size="small" @click="savePlan">
                <i class="el-icon-check mr-1"></i>
                保存计划
              </el-button>
            </div>
            
            <div class="plan-content">
              <div class="whitespace-pre-wrap text-gray-700 leading-relaxed">
                {{ generatedPlan }}
              </div>
            </div>
          </div>

          <!-- 历史记录区域 -->
          <div v-if="planHistory.length > 0" class="history-section bg-white rounded-lg shadow-lg p-6 mt-8">
            <h2 class="text-xl font-semibold text-gray-800 mb-4">最近的规划记录</h2>
            <div class="space-y-4">
              <div 
                v-for="(plan, index) in planHistory" 
                :key="index"
                class="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 cursor-pointer"
                @click="loadHistoryPlan(plan)"
              >
                <div class="flex justify-between items-start">
                  <div class="flex-1">
                    <p class="text-sm text-gray-600 mb-2">{{ plan.timestamp }}</p>
                    <p class="text-gray-800 line-clamp-2">{{ plan.input.substring(0, 100) }}...</p>
                  </div>
                  <el-button type="text" size="small" @click.stop="deleteHistoryPlan(index)">
                    <i class="el-icon-delete"></i>
                  </el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

// 响应式数据
const travelInput = ref('')
const generatedPlan = ref('')
const isGenerating = ref(false)
const planHistory = ref<Array<{input: string, result: string, timestamp: string}>>([])

// 计算属性
const user = computed(() => authStore.user)

// 生命周期
onMounted(() => {
  loadPlanHistory()
})

// 方法
const generatePlan = async () => {
  if (!travelInput.value.trim()) {
    ElMessage.warning('请先输入你的旅行需求')
    return
  }

  isGenerating.value = true
  
  try {
    // 模拟AI生成过程（后续可以接入真实的AI API）
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 模拟生成的旅行计划
    generatedPlan.value = generateMockPlan(travelInput.value)
    
    // 保存到历史记录
    const historyItem = {
      input: travelInput.value,
      result: generatedPlan.value,
      timestamp: new Date().toLocaleString()
    }
    planHistory.value.unshift(historyItem)
    savePlanHistory()
    
    ElMessage.success('旅行计划生成成功！')
  } catch (error) {
    console.error('生成计划失败:', error)
    ElMessage.error('生成计划失败，请稍后重试')
  } finally {
    isGenerating.value = false
  }
}

const generateMockPlan = (input: string): string => {
  return `基于你的需求："${input.substring(0, 50)}..."

🎯 旅行概览
• 推荐目的地：根据你的描述分析得出
• 建议行程：5-7天深度游
• 预算估算：根据你的预算进行合理分配

📅 详细行程安排

第1天：抵达与适应
• 上午：抵达目的地，办理入住
• 下午：市区漫步，熟悉环境
• 晚上：品尝当地特色美食

第2天：文化探索
• 上午：参观历史文化景点
• 下午：博物馆或艺术馆参观
• 晚上：观看当地表演或夜市

第3天：自然风光
• 全天：自然景区游览
• 推荐活动：徒步、摄影、观景

第4天：深度体验
• 上午：参与当地文化活动
• 下午：购物或手工体验
• 晚上：与当地人交流

第5天：返程准备
• 上午：最后的景点打卡
• 下午：购买纪念品，准备返程

💰 预算分配建议
• 交通：30%
• 住宿：25%
• 餐饮：20%
• 景点门票：15%
• 购物娱乐：10%

📝 贴心提醒
• 提前预订热门景点门票
• 准备好相关证件和保险
• 了解当地天气和文化习俗
• 保持联系方式畅通

这个计划是基于你的需求初步生成的，你可以根据实际情况进行调整。如需更详细的规划，请提供更多具体信息。`
}

const clearInput = () => {
  travelInput.value = ''
  generatedPlan.value = ''
}

const savePlan = async () => {
  try {
    // 这里可以调用后端API保存计划
    ElMessage.success('计划已保存到你的旅行列表')
  } catch (error) {
    ElMessage.error('保存失败，请稍后重试')
  }
}

const loadHistoryPlan = (plan: any) => {
  travelInput.value = plan.input
  generatedPlan.value = plan.result
}

const deleteHistoryPlan = async (index: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    planHistory.value.splice(index, 1)
    savePlanHistory()
    ElMessage.success('记录已删除')
  } catch {
    // 用户取消删除
  }
}

const loadPlanHistory = () => {
  const saved = localStorage.getItem('planHistory')
  if (saved) {
    try {
      planHistory.value = JSON.parse(saved)
    } catch (error) {
      console.error('加载历史记录失败:', error)
    }
  }
}

const savePlanHistory = () => {
  try {
    // 只保留最近10条记录
    const historyToSave = planHistory.value.slice(0, 10)
    localStorage.setItem('planHistory', JSON.stringify(historyToSave))
  } catch (error) {
    console.error('保存历史记录失败:', error)
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '确认退出', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await authStore.logout()
    router.push('/login')
  } catch {
    // 用户取消退出
  }
}
</script>

<style scoped>
.planning-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.header-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.main-content {
  flex: 1;
}

.planning-textarea :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 2px solid #e1e5e9;
  transition: all 0.3s ease;
}

.planning-textarea :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.plan-content {
  max-height: 600px;
  overflow-y: auto;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.container {
  max-width: 1200px;
}

@media (max-width: 768px) {
  .container {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  
  .header-section .flex {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
}
</style>