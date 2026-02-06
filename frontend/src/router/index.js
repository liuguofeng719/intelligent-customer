// 路由配置：页面入口与导航映射
import { createRouter, createWebHistory } from 'vue-router'
import ChatView from '../views/ChatView.vue'
import FaqView from '../views/FaqView.vue'
import BusinessView from '../views/BusinessView.vue'

const router = createRouter({
  history: createWebHistory(),
  // 仅包含三个核心页面：对话、FAQ 管理、业务查询
  routes: [
    { path: '/', name: 'chat', component: ChatView },
    { path: '/faq', name: 'faq', component: FaqView },
    { path: '/business', name: 'business', component: BusinessView }
  ]
})

export default router
