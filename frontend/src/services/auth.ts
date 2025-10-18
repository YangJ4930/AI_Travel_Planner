import axios from 'axios'
import type { ApiResponse, LoginRequest, RegisterRequest, User, LoginResponse } from '@/types'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

// 创建axios实例
const authAxios = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加token
authAxios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.satoken = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
// 响应拦截器
authAxios.interceptors.response.use(
  (response) => {
    // 直接返回响应数据，不做额外处理
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token过期或无效，清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // 可以在这里触发登出逻辑或跳转到登录页
    }
    
    // 如果后端返回了错误响应，尝试从中提取错误信息
    if (error.response?.data) {
      const errorData = error.response.data
      if (errorData.code !== 0 && errorData.message) {
        return Promise.reject(new Error(errorData.message))
      }
    }
    
    return Promise.reject(error)
  }
)

export const authApi = {
  // 用户登录 - 适配后端 /user/login
  async login(credentials: LoginRequest): Promise<ApiResponse<LoginResponse>> {
    try {
      const response = await authAxios.post('/user/login', credentials)
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '登录失败')
    }
  },

  // 用户注册 - 适配后端 /user/register
  async register(userData: RegisterRequest): Promise<ApiResponse<User>> {
    try {
      const response = await authAxios.post('/user/register', userData)
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '注册失败')
    }
  },

  // 用户登出 - 适配后端 /user/logout
  async logout(): Promise<ApiResponse<void>> {
    try {
      const response = await authAxios.post('/user/logout')
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '登出失败')
    }
  },

  // 获取用户信息 - 适配后端 /user
  async getProfile(): Promise<ApiResponse<User>> {
    try {
      const response = await authAxios.get('/user')
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '获取用户信息失败')
    }
  },

  // 更新用户信息
  async updateProfile(userData: Partial<User>): Promise<ApiResponse<User>> {
    try {
      const response = await authAxios.put('/user/profile', userData)
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '更新用户信息失败')
    }
  },

  // 修改密码
  async changePassword(data: { oldPassword: string; newPassword: string }): Promise<ApiResponse<void>> {
    try {
      const response = await authAxios.put('/user/password', data)
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '修改密码失败')
    }
  },

  // 忘记密码
  async forgotPassword(email: string): Promise<ApiResponse<void>> {
    try {
      const response = await authAxios.post('/user/forgot-password', { email })
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '发送重置邮件失败')
    }
  },

  // 重置密码
  async resetPassword(data: { token: string; newPassword: string }): Promise<ApiResponse<void>> {
    try {
      const response = await authAxios.post('/user/reset-password', data)
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '重置密码失败')
    }
  },

  // 验证token
  async verifyToken(): Promise<ApiResponse<User>> {
    try {
      const response = await authAxios.get('/user/verify')
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Token验证失败')
    }
  },

  // 刷新token
  async refreshToken(): Promise<ApiResponse<{ token: string }>> {
    try {
      const response = await authAxios.post('/user/refresh')
      return response.data
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '刷新Token失败')
    }
  }
}