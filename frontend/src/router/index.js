import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/',
      name: 'map',
      component: () => import('@/views/Map.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/borrow',
      name: 'borrow',
      component: () => import('@/views/Borrow.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/return',
      name: 'return',
      component: () => import('@/views/Return.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('@/views/Orders.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/Profile.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  // 每次路由跳转前重新从存储中加载状态，确保是最新的
  userStore.loadFromStorage()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router
