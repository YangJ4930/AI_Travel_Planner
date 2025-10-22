<template>
  <div class="login-view min-h-screen bg-gradient-to-br from-primary-50 to-secondary-50 flex items-center justify-center p-4">
    <div class="login-container bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
      <!-- Logo和标题 -->
      <div class="text-center mb-8">
        <div class="flex items-center justify-center space-x-2 mb-4">
          <el-icon class="text-3xl text-primary-600">
            <Location />
          </el-icon>
          <h1 class="text-2xl font-bold text-gray-800">AI旅行规划师</h1>
        </div>
        <p class="text-gray-600">登录您的账户</p>
      </div>

      <!-- 登录表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        @submit.prevent="handleLogin"
        class="space-y-6"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名或邮箱"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <div class="flex items-center justify-between">
          <el-checkbox v-model="loginForm.rememberMe">
            记住我
          </el-checkbox>
          <el-link type="primary" @click="showForgotPassword = true">
            忘记密码？
          </el-link>
        </div>

        <el-button
          type="primary"
          size="large"
          :loading="authStore.loading"
          @click="handleLogin"
          class="w-full"
        >
          登录
        </el-button>
      </el-form>



      <!-- 注册链接 -->
      <div class="text-center mt-6">
        <span class="text-gray-600">还没有账户？</span>
        <el-link type="primary" @click="$router.push('/register')" class="ml-1">
          立即注册
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

    <!-- 忘记密码对话框 -->
    <el-dialog
      v-model="showForgotPassword"
      title="重置密码"
      width="400px"
      :before-close="handleCloseForgotPassword"
    >
      <el-form
        ref="forgotPasswordFormRef"
        :model="forgotPasswordForm"
        :rules="forgotPasswordRules"
      >
        <el-form-item prop="email">
          <el-input
            v-model="forgotPasswordForm.email"
            placeholder="请输入您的邮箱地址"
            :prefix-icon="Message"
            clearable
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showForgotPassword = false">取消</el-button>
          <el-button 
            type="primary" 
            :loading="forgotPasswordLoading"
            @click="handleForgotPassword"
          >
            发送重置邮件
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { 
  Location, 
  User, 
  Lock, 
  Message, 
  ArrowLeft
} from '@element-plus/icons-vue'
import type { LoginRequest } from '@/types'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 表单引用
const loginFormRef = ref<FormInstance>()
const forgotPasswordFormRef = ref<FormInstance>()

// 登录表单数据
const loginForm = reactive<LoginRequest & { rememberMe: boolean }>({
  username: '',
  password: '',
  rememberMe: false
})

// 忘记密码表单数据
const forgotPasswordForm = reactive({
  email: ''
})

// 忘记密码对话框状态
const showForgotPassword = ref(false)
const forgotPasswordLoading = ref(false)

// 表单验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const forgotPasswordRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    const success = await authStore.login({
      username: loginForm.username,
      password: loginForm.password
    })

    if (success) {
      // 登录成功，重定向到规划页面
      const redirect = route.query.redirect as string || '/planning'
      router.push(redirect)
    }
  } catch (error) {
    console.error('登录失败:', error)
  }
}



// 处理忘记密码
const handleForgotPassword = async () => {
  if (!forgotPasswordFormRef.value) return
  
  try {
    const valid = await forgotPasswordFormRef.value.validate()
    if (!valid) return

    forgotPasswordLoading.value = true
    
    // TODO: 调用忘记密码API
    await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
    
    ElMessage.success('重置密码邮件已发送，请查收邮箱')
    showForgotPassword.value = false
    forgotPasswordForm.email = ''
  } catch (error) {
    console.error('发送重置邮件失败:', error)
    ElMessage.error('发送失败，请稍后重试')
  } finally {
    forgotPasswordLoading.value = false
  }
}

// 关闭忘记密码对话框
const handleCloseForgotPassword = () => {
  forgotPasswordForm.email = ''
  showForgotPassword.value = false
}

// 组件挂载时检查是否已登录
onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push('/travel-plans')
  }
})
</script>

<style scoped>
.login-container {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.login-view {
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