import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/MainView.vue')
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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
