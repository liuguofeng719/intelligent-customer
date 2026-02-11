<script setup>
// 对话澄清配置与统计页面
import { onMounted, ref } from 'vue'
import { fetchChatClarifyConfig, updateChatClarifyConfig, fetchChatClarifyStats } from '../api/client'

const loading = ref(false)
const message = ref('')
const form = ref({
  shortQuestionLength: 3,
  minQuestionLength: 6,
  enablePronounCheck: true,
  genericTemplate: '',
  pronounTemplate: '',
  orderTemplate: '',
  productTemplate: '',
  customerTemplate: ''
})
const stats = ref({ total: 0, today: 0, reasons: [] })

const loadConfig = async () => {
  // 拉取当前澄清配置
  const data = await fetchChatClarifyConfig()
  form.value = { ...form.value, ...data }
}

const loadStats = async () => {
  // 拉取澄清触发统计
  stats.value = await fetchChatClarifyStats()
}

const saveConfig = async () => {
  // 保存配置并刷新表单
  loading.value = true
  message.value = ''
  try {
    const payload = { ...form.value }
    const result = await updateChatClarifyConfig(payload)
    form.value = { ...form.value, ...result }
    message.value = '已保存澄清配置。'
  } catch (error) {
    message.value = '保存失败，请稍后重试。'
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    await loadConfig()
    await loadStats()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <section class="card">
    <div class="card-title">澄清追问配置</div>
    <div class="form-row">
      <label class="form-label">短问题阈值</label>
      <input v-model.number="form.shortQuestionLength" type="number" min="1" />
      <label class="form-label">通用阈值</label>
      <input v-model.number="form.minQuestionLength" type="number" min="1" />
      <label class="form-label">代词检查</label>
      <input v-model="form.enablePronounCheck" type="checkbox" />
    </div>
    <div class="form-row">
      <label class="form-label">通用模板</label>
      <input v-model="form.genericTemplate" type="text" />
    </div>
    <div class="form-row">
      <label class="form-label">代词模板</label>
      <input v-model="form.pronounTemplate" type="text" />
    </div>
    <div class="form-row">
      <label class="form-label">订单模板</label>
      <input v-model="form.orderTemplate" type="text" />
    </div>
    <div class="form-row">
      <label class="form-label">商品模板</label>
      <input v-model="form.productTemplate" type="text" />
    </div>
    <div class="form-row">
      <label class="form-label">客户模板</label>
      <input v-model="form.customerTemplate" type="text" />
    </div>
    <div class="form-row">
      <button :disabled="loading" @click="saveConfig">保存配置</button>
      <span class="hint">{{ message }}</span>
    </div>
  </section>

  <section class="card">
    <div class="card-title">澄清触发统计</div>
    <div class="grid">
      <div class="panel">
        <div class="panel-title">总次数</div>
        <div class="result">{{ stats.total }}</div>
      </div>
      <div class="panel">
        <div class="panel-title">今日次数</div>
        <div class="result">{{ stats.today }}</div>
      </div>
    </div>
    <div class="panel">
      <div class="panel-title">按原因统计</div>
      <ul class="list">
        <li v-for="item in stats.reasons" :key="item.reason">
          {{ item.label }}：{{ item.count }}
        </li>
      </ul>
    </div>
  </section>
</template>
