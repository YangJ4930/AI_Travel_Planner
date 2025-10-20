import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { requiresAuth: false, hideForAuth: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/auth/RegisterView.vue'),
      meta: { requiresAuth: false, hideForAuth: true }
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/planning',
      name: 'planning',
      component: () => import('@/views/PlanningView.vue'),
      meta: { requiresAuth: true }
    },

    // 新的旅行计划路由
    {
      path: '/travel-plans',
      name: 'travel-plans',
      component: () => import('@/views/TravelPlanList.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/travel-plans/create',
      name: 'travel-plan-create',
      component: () => import('@/views/TravelPlanCreate.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/travel-plans/:id',
      name: 'travel-plan-detail',
      component: () => import('@/views/TravelPlanDetail.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      redirect: '/'
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }
  
  // 已登录用户访问登录/注册页面时重定向到仪表板
  if (to.meta.hideForAuth && authStore.isAuthenticated) {
    next({ name: 'dashboard' })
    return
  }
  
  next()
})

export default router
