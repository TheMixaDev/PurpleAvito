import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: () => import('../views/MainView.vue')
    },
    {
      path: '/setup',
      name: 'setup',
      component: () => import('../views/SetupView.vue')
    },
    {
      path: '/create',
      name: 'panel',
      component: () => import('../views/PanelView.vue')
    },
    {
      path: '/matrix',
      name: 'matrix',
      component: () => import('../views/MatrixView.vue')
    }
  ]
})

export default router
