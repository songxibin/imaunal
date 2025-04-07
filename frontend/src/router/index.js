import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'documents',
        name: 'Documents',
        component: () => import('@/views/Documents.vue')
      },
      {
        path: 'documents/:id',
        name: 'DocumentDetail',
        component: () => import('@/views/DocumentDetail.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue')
      }
    ]
  },
  {
    path: '/public',
    component: () => import('@/views/PublicLayout.vue'),
    meta: { requiresAuth: false },
    children: [
      {
        path: 'search',
        name: 'PublicSearch',
        component: () => import('@/views/PublicSearch.vue')
      },
      {
        path: 'documents/:id',
        name: 'PublicDocumentDetail',
        component: () => import('@/views/PublicDocumentDetail.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  console.log('Navigation guard - to:', to.path, 'isAuthenticated:', authStore.isAuthenticated)
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    console.log('Redirecting to login - requires auth but not authenticated')
    next('/login')
  } else {
    console.log('Proceeding with navigation')
    next()
  }
})

export default router 