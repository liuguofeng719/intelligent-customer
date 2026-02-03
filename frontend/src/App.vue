<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="layout">
    <header class="app-header">
      <div class="brand">
        <div class="brand-title">智能客服演示</div>
        <div class="brand-subtitle">Spring Boot + LangChain4j + Ollama</div>
      </div>
      <nav class="nav">
        <RouterLink class="nav-link" to="/">聊天</RouterLink>
        <RouterLink class="nav-link" to="/faq">FAQ 管理</RouterLink>
        <RouterLink class="nav-link" to="/business">业务查询</RouterLink>
      </nav>
    </header>
    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>
