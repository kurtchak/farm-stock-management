import { createRouter, createWebHistory } from 'vue-router'
import authService from '../services/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HubView.vue')
  },
  {
    path: '/farm',
    name: 'Farm',
    component: () => import('../views/MainView.vue')
  },
  {
    path: '/gardens',
    name: 'Gardens',
    component: () => import('../views/GardenView.vue')
  },
  {
    path: '/test-qr',
    name: 'test-qr',
    component: () => import('../views/TestQRView.vue')
  },
  {
    path: '/scan',
    name: 'scan',
    component: () => import('../views/ScanView.vue')
  },
  {
    path: '/adjust-quantity/:stockId',
    name: 'adjust-quantity',
    component: () => import('../views/AdjustQuantityView.vue')
  },
  {
    path: '/create-item',
    name: 'create-item',
    component: () => import('../views/CreateItemView.vue')
  },
  {
    path: '/stocks',
    name: 'stocks',
    component: () => import('../views/StockListView.vue')
  },
  {
    path: '/deleted-stocks',
    name: 'deleted-stocks',
    component: () => import('../views/DeletedStocksView.vue')
  },
  {
    path: '/settings',
    name: 'settings',
    component: () => import('../views/SettingsView.vue')
  },
  {
    path: '/history',
    name: 'history',
    component: () => import('../views/HistoryView.vue')
  },
  {
    path: '/gardens/items',
    name: 'GardenItems',
    component: () => import('../views/GardenItemsView.vue')
  },
  {
    path: '/gardens/items/create',
    name: 'GardenItemCreate',
    component: () => import('../views/GardenItemCreateView.vue')
  },
  {
    path: '/gardens/items/:id/adjust',
    name: 'GardenItemAdjust',
    component: () => import('../views/GardenItemAdjustView.vue')
  },
  {
    path: '/gardens/sets',
    name: 'GardenSets',
    component: () => import('../views/GardenSetsView.vue')
  },
  {
    path: '/gardens/sets/new',
    name: 'GardenSetCreate',
    component: () => import('../views/GardenSetFormView.vue')
  },
  {
    path: '/gardens/sets/:id/edit',
    name: 'GardenSetEdit',
    component: () => import('../views/GardenSetFormView.vue')
  },
  {
    path: '/gardens/history',
    name: 'GardenHistory',
    component: () => import('../views/GardenHistoryView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const isAuthenticated = authService.isAuthenticated()

  // If trying to access login page while authenticated, redirect to farm
  if (to.path === '/login' && isAuthenticated) {
    next('/farm')
    return
  }

  // If trying to access protected route while not authenticated, redirect to login
  if (to.path !== '/login' && !isAuthenticated) {
    next('/login')
    return
  }

  // Otherwise, allow navigation
  next()
})

export default router
