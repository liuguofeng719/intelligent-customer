import { createRouter, createWebHistory } from 'vue-router'
import ChatView from '../views/ChatView.vue'
import FaqView from '../views/FaqView.vue'
import BusinessView from '../views/BusinessView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'chat', component: ChatView },
    { path: '/faq', name: 'faq', component: FaqView },
    { path: '/business', name: 'business', component: BusinessView }
  ]
})

export default router
