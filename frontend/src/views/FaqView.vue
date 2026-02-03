<script setup>
import { ref } from 'vue'
import { importFaq, searchFaq } from '../api/client'

const sourceType = ref('MARKDOWN')
const content = ref('# 退款\n退款需要提供订单号。\n\n# 发货\n一般48小时内发货。')
const importResult = ref('')
const query = ref('')
const results = ref([])

const doImport = async () => {
  importResult.value = ''
  try {
    const resp = await importFaq(sourceType.value, content.value)
    importResult.value = `已导入 ${resp.count} 条FAQ`
  } catch (error) {
    importResult.value = '导入失败'
  }
}

const doSearch = async () => {
  if (!query.value.trim()) return
  try {
    results.value = await searchFaq(query.value.trim(), 3)
  } catch (error) {
    results.value = ['检索失败']
  }
}
</script>

<template>
  <section class="card">
    <div class="card-title">FAQ 管理</div>
    <div class="form-row">
      <label>格式</label>
      <select v-model="sourceType">
        <option value="MARKDOWN">Markdown</option>
        <option value="CSV">CSV</option>
        <option value="TEXT">纯文本</option>
      </select>
      <button @click="doImport">导入 FAQ</button>
    </div>
    <textarea v-model="content" rows="8" class="textarea"></textarea>
    <div class="hint">{{ importResult }}</div>
  </section>

  <section class="card">
    <div class="card-title">FAQ 检索</div>
    <div class="form-row">
      <input v-model="query" type="text" placeholder="输入要检索的问题" />
      <button @click="doSearch">检索</button>
    </div>
    <ul class="list">
      <li v-for="(item, index) in results" :key="index">{{ item }}</li>
    </ul>
  </section>
</template>
