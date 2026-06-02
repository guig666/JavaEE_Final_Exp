import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '@/stores/admin'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 处理后端 .action 路径的重定向
    {
      path: '/manage/login.action',
      redirect: '/login'
    },
    {
      path: '/manage/manage.action',
      redirect: '/'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/',
      component: () => import('@/views/Layout.vue'),
      redirect: '/dashboard',
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

router.beforeEach((to, from, next) => {
  const adminStore = useAdminStore()
  // 确保每次跳转都校验最新的存储状态
  adminStore.loadFromStorage()
  
  if (to.path !== '/login' && !adminStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && adminStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router