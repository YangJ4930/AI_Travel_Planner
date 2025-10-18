// 用户相关类型 - 适配后端UserInfoVO
export interface User {
  userId: number  // 改为userId，匹配后端UserInfoVO
  username: string
  email?: string  // 后端UserInfoVO中没有，设为可选
  firstName?: string
  lastName?: string
  phoneNumber?: string
  role?: string
  isActive?: boolean
  avatar?: string
  createdAt?: string  // 后端UserInfoVO中没有，设为可选
  updatedAt?: string  // 后端UserInfoVO中没有，设为可选
}

export interface UserPreference {
  id: number
  userId: number
  travelStyle: string
  budgetLevel: string
  accommodationType: string
  transportationPreference: string
  foodPreference: string[]
  interests: string[]
}

// 登录请求参数 - 适配后端UserInfoParam
export interface LoginRequest {
  username: string  // 改为username，适配后端
  password: string
}

// 登录响应 - 适配后端UserLoginVO
export interface LoginResponse {
  token: string
  user: User
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  confirmPassword: string
  firstName?: string
  lastName?: string
  phoneNumber?: string
}

// 旅行计划相关类型
export interface TravelPlan {
  id: number
  userId: number
  title: string
  destination: string
  startDate: string
  endDate: string
  totalBudget: number
  travelerCount: number
  status: 'draft' | 'confirmed' | 'completed'
  aiGeneratedContent?: any
  createdAt: string
  updatedAt: string
  itineraryItems?: ItineraryItem[]
}

export interface ItineraryItem {
  id: number
  travelPlanId: number
  dayNumber: number
  startTime?: string
  endTime?: string
  activityType: string
  title: string
  description?: string
  locationName?: string
  latitude?: number
  longitude?: number
  estimatedCost?: number
  bookingInfo?: any
}

export interface CreatePlanRequest {
  title: string
  destination: string
  startDate: string
  endDate: string
  totalBudget: number
  travelerCount: number
  preferences?: string
}

// 费用记录相关类型
export interface ExpenseRecord {
  id: number
  travelPlanId: number
  category: string
  amount: number
  currency: string
  description?: string
  expenseDate: string
  location?: string
  receiptUrl?: string
  createdAt: string
}

export interface ExpenseCategory {
  name: string
  icon: string
  color: string
}

// 地图相关类型
export interface Location {
  name: string
  address: string
  latitude: number
  longitude: number
  type?: string
}

export interface RouteInfo {
  distance: number
  duration: number
  steps: RouteStep[]
}

export interface RouteStep {
  instruction: string
  distance: number
  duration: number
  coordinates: [number, number][]
}

// 语音相关类型
export interface VoiceRequest {
  audioData: string
  format: string
}

export interface VoiceResponse {
  text: string
  confidence: number
}

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string | null
  data?: T
}

export interface PaginatedResponse<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

// 通用类型
export interface SelectOption {
  label: string
  value: string | number
}

export interface MenuItem {
  name: string
  path: string
  icon?: string
  children?: MenuItem[]
}

// 地图配置类型
export interface MapConfig {
  center: [number, number]
  zoom: number
  apiKey: string
}

// 语音配置类型
export interface VoiceConfig {
  appId: string
  apiKey: string
  apiSecret: string
}

// AI服务配置类型
export interface AIConfig {
  provider: string
  apiKey: string
  model: string
}

// 表单验证规则类型
export interface ValidationRule {
  required?: boolean
  message: string
  trigger?: string
  min?: number
  max?: number
  pattern?: RegExp
  validator?: (rule: any, value: any, callback: any) => void
}

// 主题配置类型
export interface ThemeConfig {
  primaryColor: string
  secondaryColor: string
  darkMode: boolean
}

// 应用状态类型
export interface AppState {
  loading: boolean
  user: User | null
  theme: ThemeConfig
  language: string
}

// 错误类型
export interface AppError {
  code: string
  message: string
  details?: any
}