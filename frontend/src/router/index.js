import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HubView.vue')
  },
  {
    path: '/farma',
    name: 'Farma',
    component: () => import('../views/MainView.vue')
  },
  {
    path: '/zahrady',
    name: 'Zahrady',
    component: () => import('../views/ZahradyView.vue')
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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
