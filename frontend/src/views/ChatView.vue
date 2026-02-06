<script setup>
// 对话页面：发送问题并展示客服回复
import { ref } from 'vue'
import { chat } from '../api/client'

const input = ref('')
const loading = ref(false)
const messages = ref([])

// 发送问题并追加消息
const send = async () => {
  const question = input.value.trim()
  if (!question || loading.value) return
  messages.value.push({ role: 'user', content: question })
  input.value = ''
  loading.value = true
  try {
    const answer = await chat(question)
    messages.value.push({ role: 'assistant', content: answer })
  } catch (error) {
    messages.value.push({ role: 'assistant', content: '系统繁忙，请稍后再试。' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="card">
    <div class="card-title">智能客服对话</div>
    <div class="chat-window">
      <div v-for="(msg, index) in messages" :key="index" class="chat-line" :class="msg.role">
        <span class="role">{{ msg.role === 'user' ? '我' : '客服' }}</span>
        <span class="content">{{ msg.content }}</span>
      </div>
      <div v-if="loading" class="chat-line assistant">
        <span class="role">客服</span>
        <span class="content">正在生成回复...</span>
      </div>
    </div>
    <div class="chat-input">
      <input v-model="input" type="text" placeholder="请输入问题，例如：订单什么时候发货？" />
      <button @click="send">发送</button>
    </div>
  </section>
</template>
