import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '@/stores/admin'

// 路由配置
const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 处理后端 .action 路径的重定向
    {
      path: '/manage/login.action',  // 旧的登录路径
      redirect: '/login'  // 重定向到新路径
    },
    {
      path: '/manage/manage.action',  // 旧的管理首页路径
      redirect: '/'  // 重定向到根路径
    },
    {
      path: '/login',  // 登录页路径
      name: 'login',  // 登录页名称
      component: () => import('@/views/Login.vue')  // 登录页组件
    },
    {
      path: '/',
      component: () => import('@/views/Layout.vue'),  // 布局组件
      redirect: '/location',  // 默认重定向到位置页
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/Dashboard.vue')
        },
        {
          path: 'location',
          name: 'location',
          component: () => import('@/views/Location.vue')
        },
        {
          path: 'powerbank',
          name: 'powerbank',
          component: () => import('@/views/Powerbank.vue')
        },
        {
          path: 'user',
          name: 'user',
          component: () => import('@/views/UserManage.vue')
        },
        {
          path: 'order',
          name: 'order',
          component: () => import('@/views/OrderManage.vue')
        }
      ]
    }
  ]
})

// 登录守卫
router.beforeEach((to, from, next) => {
  // 获取 Pinia 状态管理实例
  const adminStore = useAdminStore()
  // 确保每次跳转都校验最新的存储状态
  adminStore.loadFromStorage()
  
  // 1. 未登录访问非登录页，重定向到登录页
  if (to.path !== '/login' && !adminStore.isLoggedIn) {
    next('/login')
  
  // 2. 已登录访问登录页，重定向到首页 
  } else if (to.path === '/login' && adminStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router