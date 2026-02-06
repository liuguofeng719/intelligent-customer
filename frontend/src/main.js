// 应用入口样式
import './assets/main.css'

// 创建应用实例并挂载路由
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 挂载到根节点
createApp(App).use(router).mount('#app')
