import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types'

// 创建axios实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 添加认证token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 添加请求时间戳，防止缓存
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }
    
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { data } = response
    
    // 如果是文件下载，直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    // 检查业务状态码
    if (data.success) {
      return data
    } else {
      // 业务错误处理
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error) => {
    console.error('响应错误:', error)
    
    // 网络错误处理
    if (!error.response) {
      ElMessage.error('网络连接失败，请检查网络设置')
      return Promise.reject(error)
    }
    
    const { status, data } = error.response
    
    switch (status) {
      case 401:
        // 未授权，清除token并跳转到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        ElMessage.error('登录已过期，请重新登录')
        // 这里可以添加路由跳转逻辑
        window.location.href = '/login'
        break
      case 403:
        ElMessage.error('没有权限访问该资源')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 500:
        ElMessage.error('服务器内部错误')
        break
      default:
        ElMessage.error(data?.message || `请求失败 (${status})`)
    }
    
    return Promise.reject(error)
  }
)

// 封装常用的请求方法
export const http = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.get(url, config)
  },
  
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.post(url, data, config)
  },
  
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.put(url, data, config)
  },
  
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.delete(url, config)
  },
  
  patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.patch(url, data, config)
  },
  
  // 文件上传
  upload<T = any>(url: string, formData: FormData, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.post(url, formData, {
      ...config,
      headers: {
        'Content-Type': 'multipart/form-data',
        ...config?.headers
      }
    })
  },
  
  // 文件下载
  download(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse> {
    return request.get(url, {
      ...config,
      responseType: 'blob'
    })
  }
}

export default request