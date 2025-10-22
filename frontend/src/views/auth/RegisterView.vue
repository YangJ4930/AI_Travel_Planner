<template>
  <div class="register-view min-h-screen bg-gradient-to-br from-primary-50 to-secondary-50 flex items-center justify-center p-4">
    <div class="register-container bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
      <!-- Logo和标题 -->
      <div class="text-center mb-8">
        <div class="flex items-center justify-center space-x-2 mb-4">
          <el-icon class="text-3xl text-primary-600">
            <Location />
          </el-icon>
          <h1 class="text-2xl font-bold text-gray-800">AI旅行规划师</h1>
        </div>
        <p class="text-gray-600">创建您的账户</p>
      </div>

      <!-- 注册表单 -->
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        @submit.prevent="handleRegister"
        class="space-y-6"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="用户名"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="邮箱地址"
            size="large"
            :prefix-icon="Message"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <!-- 服务条款 -->
        <el-form-item prop="agreeToTerms">
          <el-checkbox v-model="registerForm.agreeToTerms">
            我已阅读并同意
            <el-link type="primary" @click="showTermsDialog = true">服务条款</el-link>
            和
            <el-link type="primary" @click="showPrivacyDialog = true">隐私政策</el-link>
          </el-checkbox>
        </el-form-item>

        <el-button
          type="primary"
          size="large"
          :loading="authStore.loading"
          @click="handleRegister"
          class="w-full"
        >
          注册
        </el-button>
      </el-form>

      <!-- 登录链接 -->
      <div class="text-center mt-6">
        <span class="text-gray-600">已有账户？</span>
        <el-link type="primary" @click="$router.push('/login')" class="ml-1">
          立即登录
        </el-link>
      </div>

      <!-- 返回首页 -->
      <div class="text-center mt-4">
        <el-link @click="$router.push('/')" class="text-gray-500">
          <el-icon class="mr-1"><ArrowLeft /></el-icon>
          返回首页
        </el-link>
      </div>
    </div>

    <!-- 服务条款对话框 -->
    <el-dialog
      v-model="showTermsDialog"
      title="服务条款"
      width="600px"
      :before-close="() => showTermsDialog = false"
    >
      <div class="max-h-96 overflow-y-auto text-sm text-gray-600 space-y-4">
        <h4 class="font-semibold text-gray-800">1. 服务说明</h4>
        <p>AI旅行规划师是一个基于人工智能的旅行规划平台，为用户提供个性化的旅行建议和规划服务。</p>
        
        <h4 class="font-semibold text-gray-800">2. 用户责任</h4>
        <p>用户应确保提供的信息真实有效，并对自己的账户安全负责。</p>
        
        <h4 class="font-semibold text-gray-800">3. 服务限制</h4>
        <p>我们保留在必要时暂停或终止服务的权利，以维护平台的正常运行。</p>
        
        <h4 class="font-semibold text-gray-800">4. 免责声明</h4>
        <p>平台提供的旅行建议仅供参考，用户应根据实际情况做出决策。</p>
      </div>
      
      <template #footer>
        <el-button @click="showTermsDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 隐私政策对话框 -->
    <el-dialog
      v-model="showPrivacyDialog"
      title="隐私政策"
      width="600px"
      :before-close="() => showPrivacyDialog = false"
    >
      <div class="max-h-96 overflow-y-auto text-sm text-gray-600 space-y-4">
        <h4 class="font-semibold text-gray-800">1. 信息收集</h4>
        <p>我们收集您提供的个人信息，包括但不限于用户名、邮箱地址等。</p>
        
        <h4 class="font-semibold text-gray-800">2. 信息使用</h4>
        <p>收集的信息用于提供服务、改善用户体验和发送相关通知。</p>
        
        <h4 class="font-semibold text-gray-800">3. 信息保护</h4>
        <p>我们采用行业标准的安全措施保护您的个人信息。</p>
        
        <h4 class="font-semibold text-gray-800">4. 信息共享</h4>
        <p>除法律要求外，我们不会与第三方共享您的个人信息。</p>
      </div>
      
      <template #footer>
        <el-button @click="showPrivacyDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { 
  Location, 
  User, 
  Lock, 
  Message, 
  ArrowLeft
} from '@element-plus/icons-vue'
import type { RegisterRequest } from '@/types'

const router = useRouter()
const authStore = useAuthStore()

// 表单引用
const registerFormRef = ref<FormInstance>()

// 注册表单数据
const registerForm = reactive<RegisterRequest & { 
  confirmPassword: string
  agreeToTerms: boolean 
}>({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  agreeToTerms: false
})

// 对话框状态
const showTermsDialog = ref(false)
const showPrivacyDialog = ref(false)

// 确认密码验证器
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreeToTerms: [
    { 
      validator: (rule: any, value: boolean, callback: any) => {
        if (!value) {
          callback(new Error('请同意服务条款和隐私政策'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    const success = await authStore.register({
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password
    })

    if (success) {
      ElMessage.success('注册成功！请登录您的账户')
      router.push('/login')
    }
  } catch (error) {
    console.error('注册失败:', error)
  }
}

// 组件挂载时检查是否已登录
onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push('/travel-plans')
  }
})
</script>

<style scoped>
.register-container {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.register-view {
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(120, 200, 255, 0.3) 0%, transparent 50%);
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
}

:deep(.el-checkbox__label) {
  color: #6b7280;
}

.el-link {
  font-weight: 500;
}
</style>