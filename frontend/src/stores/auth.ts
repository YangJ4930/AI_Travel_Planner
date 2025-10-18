import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/services/auth'
import type { User, LoginRequest, RegisterRequest, LoginResponse } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const loading = ref(false)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const userInfo = computed(() => user.value)

  // 登录
  const login = async (credentials: LoginRequest) => {
    try {
      loading.value = true
      const response = await authApi.login(credentials)
      
      // 根据后端返回的code判断成功与否，0表示成功
      if (response.code === 0) {
        // 从data中获取token信息
        const tokenData = response.data
        if (tokenData && tokenData.tokenContent) {
          token.value = tokenData.tokenContent
          localStorage.setItem('token', tokenData.tokenContent)
          
          // 获取用户信息
          await fetchUserInfo()
          
          ElMessage.success('登录成功')
          return true
        } else {
          ElMessage.error('登录失败：未获取到有效token')
          return false
        }
      } else {
        ElMessage.error(response.message || '登录失败')
        return false
      }
    } catch (error: any) {
      console.error('登录失败:', error)
      ElMessage.error(error.message || '登录失败，请稍后重试')
      return false
    } finally {
      loading.value = false
    }
  }

  // 注册
  const register = async (userData: RegisterRequest) => {
    try {
      loading.value = true
      const response = await authApi.register(userData)
      
      // 根据后端返回的code判断成功与否，0表示成功
      if (response.code === 0) {
        ElMessage.success('注册成功，请登录')
        return true
      } else {
        ElMessage.error(response.message || '注册失败')
        return false
      }
    } catch (error: any) {
      console.error('注册失败:', error)
      ElMessage.error(error.message || '注册失败，请稍后重试')
      return false
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = async () => {
    try {
      // 调用后端登出接口
      await authApi.logout()
    } catch (error) {
      console.error('登出请求失败:', error)
    } finally {
      // 清除本地状态
      token.value = null
      user.value = null
      
      // 清除持久化存储
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      
      ElMessage.success('已退出登录')
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const response = await authApi.getProfile()
      
      if (response.code === 0 && response.data) {
        user.value = response.data
        localStorage.setItem('user', JSON.stringify(response.data))
        return response.data
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 如果获取用户信息失败，可能token已过期，执行登出
      await logout()
    }
  }

  // 更新用户信息
  const updateProfile = async (profileData: Partial<User>) => {
    try {
      loading.value = true
      const response = await authApi.updateProfile(profileData)
      
      if (response.code === 0 && response.data) {
        user.value = response.data
        localStorage.setItem('user', JSON.stringify(response.data))
        ElMessage.success('个人信息更新成功')
        return true
      } else {
        ElMessage.error(response.message || '更新失败')
        return false
      }
    } catch (error: any) {
      console.error('更新个人信息失败:', error)
      ElMessage.error(error.message || '更新失败，请稍后重试')
      return false
    } finally {
      loading.value = false
    }
  }

  // 修改密码
  const changePassword = async (passwordData: { oldPassword: string; newPassword: string }) => {
    try {
      loading.value = true
      const response = await authApi.changePassword(passwordData)
      
      if (response.code === 0) {
        ElMessage.success('密码修改成功')
        return true
      } else {
        ElMessage.error(response.message || '密码修改失败')
        return false
      }
    } catch (error: any) {
      console.error('修改密码失败:', error)
      ElMessage.error(error.message || '密码修改失败，请检查原密码是否正确')
      return false
    } finally {
      loading.value = false
    }
  }

  // 检查认证状态
  const checkAuth = async () => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    
    if (savedToken && savedUser) {
      token.value = savedToken
      user.value = JSON.parse(savedUser)
      
      // 验证token是否有效
      try {
        await fetchUserInfo()
      } catch (error) {
        // token无效，清除状态
        await logout()
      }
    }
  }

  // 刷新token
  const refreshToken = async () => {
    try {
      const response = await authApi.refreshToken()
      
      if (response.success && response.data) {
        const newToken = response.data.token
        token.value = newToken
        localStorage.setItem('token', newToken)
        return true
      }
      return false
    } catch (error) {
      console.error('刷新token失败:', error)
      await logout()
      return false
    }
  }

  return {
    // 状态
    user: userInfo,
    token,
    loading,
    
    // 计算属性
    isAuthenticated,
    
    // 方法
    login,
    register,
    logout,
    fetchUserInfo,
    updateProfile,
    changePassword,
    checkAuth,
    refreshToken
  }
})