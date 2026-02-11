<script setup>
// 对话页面：发送问题并展示客服回复
import { onMounted, ref } from 'vue'
import { chat, resetChatSession } from '../api/client'

const input = ref('')
const loading = ref(false)
const messages = ref([])
const sessionId = ref('')
const intentLabel = ref('通用')

const SESSION_KEY = 'chat_session_id'

onMounted(() => {
  sessionId.value = localStorage.getItem(SESSION_KEY) || ''
})

// 发送问题并追加消息
const send = async () => {
  const question = input.value.trim()
  if (!question || loading.value) return
  // 先显示用户输入，再请求后端
  messages.value.push({ role: 'user', content: question })
  input.value = ''
  loading.value = true
  try {
    const response = await chat(question, sessionId.value)
    if (response.sessionId) {
      sessionId.value = response.sessionId
      localStorage.setItem(SESSION_KEY, response.sessionId)
    }
    if (response.intent) {
      intentLabel.value = response.intent
    }
    messages.value.push({ role: 'assistant', content: response.answer || '' })
  } catch (error) {
    messages.value.push({ role: 'assistant', content: '系统繁忙，请稍后再试。' })
  } finally {
    loading.value = false
  }
}

const resetSession = async () => {
  if (loading.value) return
  // 清空历史并生成新会话
  loading.value = true
  try {
    const response = await resetChatSession(sessionId.value)
    sessionId.value = response.sessionId || ''
    intentLabel.value = response.intent || '通用'
    if (sessionId.value) {
      localStorage.setItem(SESSION_KEY, sessionId.value)
    } else {
      localStorage.removeItem(SESSION_KEY)
    }
    messages.value = []
  } catch (error) {
    messages.value.push({ role: 'assistant', content: '会话重置失败，请稍后再试。' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="card">
    <div class="card-title">智能客服对话</div>
    <div class="chat-summary">
      <div class="summary-item">
        <span class="label">会话</span>
        <span class="value">{{ sessionId ? sessionId.slice(0, 8) : '未建立' }}</span>
      </div>
      <div class="summary-item">
        <span class="label">意图</span>
        <span class="value">{{ intentLabel }}</span>
      </div>
    </div>
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
      <button class="secondary" @click="() => { if (confirm('确认开启新会话并清空历史？')) resetSession() }">新会话</button>
    </div>
  </section>
</template>
