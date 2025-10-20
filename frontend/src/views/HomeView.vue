<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { 
  Location, 
  ArrowDown, 
  Plus, 
  Management, 
  MapLocation, 
  Wallet 
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const featuresSection = ref<HTMLElement>()

const startPlanning = () => {
  if (authStore.isAuthenticated) {
    router.push('/travel-plans/create')
  } else {
    router.push('/login')
  }
}

const scrollToFeatures = () => {
  featuresSection.value?.scrollIntoView({ behavior: 'smooth' })
}

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
      break
  }
}
</script>

<template>
  <div class="home-view">
    <!-- 导航栏 -->
    <nav class="navbar bg-white shadow-sm">
      <div class="container mx-auto px-4 py-3 flex justify-between items-center">
        <div class="flex items-center space-x-2">
          <el-icon class="text-2xl text-primary-600">
            <Location />
          </el-icon>
          <h1 class="text-xl font-bold text-gray-800">AI旅行规划师</h1>
        </div>
        
        <div class="flex items-center space-x-4">
          <template v-if="!authStore.isAuthenticated">
            <el-button type="primary" @click="$router.push('/login')">
              登录
            </el-button>
            <el-button @click="$router.push('/register')">
              注册
            </el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/dashboard')">
              进入控制台
            </el-button>
            <el-dropdown @command="handleUserCommand">
              <span class="el-dropdown-link flex items-center cursor-pointer">
                <el-avatar :size="32" :src="authStore.user?.avatar">
                  {{ authStore.user?.username?.charAt(0).toUpperCase() }}
                </el-avatar>
                <el-icon class="ml-1"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                  <el-dropdown-item command="settings">设置</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </nav>

    <!-- 英雄区域 -->
    <section class="hero bg-gradient-to-br from-primary-50 to-secondary-50">
      <div class="container mx-auto px-4 py-20 text-center">
        <h1 class="text-5xl font-bold text-gray-900 mb-6">
          智能旅行规划，让每次出行都完美
        </h1>
        <p class="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
          基于AI技术的个性化旅行规划助手，为您量身定制最佳旅行路线，发现隐藏的美景与体验
        </p>
        <div class="flex justify-center space-x-4">
          <el-button type="primary" size="large" @click="startPlanning" class="px-8 py-3">
            <el-icon class="mr-2"><Plus /></el-icon>
            开始规划旅行
          </el-button>
          <el-button size="large" @click="scrollToFeatures" class="px-8 py-3">
            了解更多
          </el-button>
        </div>
      </div>
    </section>

    <!-- 特色功能 -->
    <section ref="featuresSection" class="py-20 bg-white">
      <div class="container mx-auto px-4">
        <div class="text-center mb-16">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">为什么选择我们</h2>
          <p class="text-lg text-gray-600 max-w-2xl mx-auto">
            结合人工智能与丰富的旅行数据，为您提供最智能、最个性化的旅行规划服务
          </p>
        </div>
        
        <div class="grid md:grid-cols-3 gap-8">
          <div class="feature-card text-center p-8 rounded-xl bg-gradient-to-br from-blue-50 to-indigo-50 hover:shadow-lg">
            <div class="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <el-icon class="text-2xl text-primary-600"><Management /></el-icon>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">AI智能推荐</h3>
            <p class="text-gray-600">
              基于您的兴趣偏好和历史数据，AI为您推荐最适合的景点、餐厅和活动
            </p>
          </div>
          
          <div class="feature-card text-center p-8 rounded-xl bg-gradient-to-br from-green-50 to-emerald-50 hover:shadow-lg">
            <div class="w-16 h-16 bg-secondary-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <el-icon class="text-2xl text-secondary-600"><MapLocation /></el-icon>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">路线优化</h3>
            <p class="text-gray-600">
              智能规划最优路线，节省时间和交通成本，让您的旅行更加高效便捷
            </p>
          </div>
          
          <div class="feature-card text-center p-8 rounded-xl bg-gradient-to-br from-purple-50 to-pink-50 hover:shadow-lg">
            <div class="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <el-icon class="text-2xl text-purple-600"><Wallet /></el-icon>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">预算管理</h3>
            <p class="text-gray-600">
              智能预算分配和费用跟踪，帮您控制旅行成本，享受性价比最高的旅行体验
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- 使用步骤 -->
    <section class="py-20 bg-gray-50">
      <div class="container mx-auto px-4">
        <div class="text-center mb-16">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">简单三步，开启完美旅行</h2>
          <p class="text-lg text-gray-600">
            只需几分钟，就能获得专属的旅行规划方案
          </p>
        </div>
        
        <div class="grid md:grid-cols-3 gap-8">
          <div class="step text-center">
            <div class="w-12 h-12 bg-primary-600 text-white rounded-full flex items-center justify-center mx-auto mb-6 text-xl font-bold">
              1
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">填写旅行偏好</h3>
            <p class="text-gray-600">
              告诉我们您的目的地、时间、预算和兴趣偏好
            </p>
          </div>
          
          <div class="step text-center">
            <div class="w-12 h-12 bg-primary-600 text-white rounded-full flex items-center justify-center mx-auto mb-6 text-xl font-bold">
              2
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">AI生成方案</h3>
            <p class="text-gray-600">
              人工智能分析数据，为您生成个性化的旅行计划
            </p>
          </div>
          
          <div class="step text-center">
            <div class="w-12 h-12 bg-primary-600 text-white rounded-full flex items-center justify-center mx-auto mb-6 text-xl font-bold">
              3
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-4">开始精彩旅行</h3>
            <p class="text-gray-600">
              按照规划享受旅行，随时调整和优化您的行程
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- 数据统计 -->
    <section class="py-20 bg-primary-600 text-white">
      <div class="container mx-auto px-4">
        <div class="grid md:grid-cols-4 gap-8 text-center">
          <div class="stat">
            <div class="text-4xl font-bold mb-2">10,000+</div>
            <div class="text-primary-100">满意用户</div>
          </div>
          <div class="stat">
            <div class="text-4xl font-bold mb-2">50,000+</div>
            <div class="text-primary-100">成功规划</div>
          </div>
          <div class="stat">
            <div class="text-4xl font-bold mb-2">200+</div>
            <div class="text-primary-100">覆盖城市</div>
          </div>
          <div class="stat">
            <div class="text-4xl font-bold mb-2">98%</div>
            <div class="text-primary-100">推荐准确率</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 用户评价 -->
    <section class="py-20 bg-white">
      <div class="container mx-auto px-4">
        <div class="text-center mb-16">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">用户真实评价</h2>
          <p class="text-lg text-gray-600">
            看看其他旅行者如何评价我们的服务
          </p>
        </div>
        
        <div class="grid md:grid-cols-3 gap-8">
          <div class="bg-gray-50 p-8 rounded-xl">
            <div class="flex items-center mb-4">
              <el-avatar :size="48" src="/api/placeholder/48/48" class="mr-4" />
              <div>
                <div class="font-semibold text-gray-900">张小明</div>
                <div class="text-sm text-gray-500">商务人士</div>
              </div>
            </div>
            <p class="text-gray-600 italic">
              "AI推荐的路线非常合理，帮我节省了很多时间。特别是餐厅推荐，每一家都很棒！"
            </p>
          </div>
          
          <div class="bg-gray-50 p-8 rounded-xl">
            <div class="flex items-center mb-4">
              <el-avatar :size="48" src="/api/placeholder/48/48" class="mr-4" />
              <div>
                <div class="font-semibold text-gray-900">李小红</div>
                <div class="text-sm text-gray-500">自由职业者</div>
              </div>
            </div>
            <p class="text-gray-600 italic">
              "预算控制功能太实用了，让我在有限的预算内体验到了最丰富的旅行内容。"
            </p>
          </div>
          
          <div class="bg-gray-50 p-8 rounded-xl">
            <div class="flex items-center mb-4">
              <el-avatar :size="48" src="/api/placeholder/48/48" class="mr-4" />
              <div>
                <div class="font-semibold text-gray-900">王大伟</div>
                <div class="text-sm text-gray-500">摄影爱好者</div>
              </div>
            </div>
            <p class="text-gray-600 italic">
              "推荐的拍摄地点都很棒，AI真的了解我的喜好。界面也很简洁易用。"
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA区域 -->
    <section class="py-20 bg-gradient-to-r from-primary-600 to-secondary-600 text-white">
      <div class="container mx-auto px-4 text-center">
        <h2 class="text-3xl font-bold mb-4">准备开始您的智能旅行了吗？</h2>
        <p class="text-xl mb-8 text-primary-100">
          加入我们，体验AI驱动的个性化旅行规划服务
        </p>
        <el-button type="primary" size="large" @click="startPlanning" class="bg-white text-primary-600 hover:bg-gray-100 px-8 py-3">
          立即开始规划
        </el-button>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="bg-gray-900 text-white py-12">
      <div class="container mx-auto px-4">
        <div class="grid md:grid-cols-4 gap-8">
          <div>
            <div class="flex items-center space-x-2 mb-4">
              <el-icon class="text-2xl text-primary-400">
                <Location />
              </el-icon>
              <h3 class="text-xl font-bold">AI旅行规划师</h3>
            </div>
            <p class="text-gray-400">
              让每次旅行都成为难忘的回忆
            </p>
          </div>
          
          <div>
            <h4 class="font-semibold mb-4">产品功能</h4>
            <ul class="space-y-2 text-gray-400">
              <li>智能路线规划</li>
              <li>个性化推荐</li>
              <li>预算管理</li>
              <li>实时调整</li>
            </ul>
          </div>
          
          <div>
            <h4 class="font-semibold mb-4">支持</h4>
            <ul class="space-y-2 text-gray-400">
              <li>帮助中心</li>
              <li>联系我们</li>
              <li>用户反馈</li>
              <li>常见问题</li>
            </ul>
          </div>
          
          <div>
            <h4 class="font-semibold mb-4">关于我们</h4>
            <ul class="space-y-2 text-gray-400">
              <li>公司介绍</li>
              <li>隐私政策</li>
              <li>服务条款</li>
              <li>加入我们</li>
            </ul>
          </div>
        </div>
        
        <div class="border-t border-gray-800 mt-8 pt-8 text-center text-gray-400">
          <p>&copy; 2024 AI旅行规划师. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.hero {
  min-height: 60vh;
  display: flex;
  align-items: center;
}

.feature-card {
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
}

.step {
  position: relative;
}

.step:not(:last-child)::after {
  content: '';
  position: absolute;
  top: 24px;
  right: -50%;
  width: 100%;
  height: 2px;
  background: linear-gradient(to right, #3b82f6, transparent);
}

@media (max-width: 768px) {
  .step:not(:last-child)::after {
    display: none;
  }
}

.stat {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
