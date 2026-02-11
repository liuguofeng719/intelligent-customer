// API 基础地址，默认与前端同源
const API_BASE = import.meta.env.VITE_API_BASE || ''

// 读取 SSE/流式响应为完整字符串
async function readStream(response) {
  const reader = response.body?.getReader()
  if (!reader) {
    return ''
  }
  const decoder = new TextDecoder()
  let result = ''
  while (true) {
    const { value, done } = await reader.read()
    if (done) break
    result += decoder.decode(value, { stream: true })
  }
  return result
}

// 发送聊天请求并解析 SSE 数据
export async function chat(question, sessionId) {
  // 发送对话请求，返回会话与意图
  const resp = await fetch(`${API_BASE}/api/chat`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ question, sessionId })
  })
  if (!resp.ok) {
    throw new Error('聊天请求失败')
  }
  const raw = await readStream(resp)
  const lines = raw.split('\n')
  const dataLines = lines.filter((line) => line.startsWith('data:'))
  const payload = dataLines.length === 0 ? raw.trim() : dataLines.map((line) => line.replace(/^data:\s?/, '')).join('')
  if (!payload) {
    return { answer: '', intent: '' }
  }
  try {
    return JSON.parse(payload)
  } catch (error) {
    return { answer: payload, intent: '' }
  }
}

// 重置会话并返回新会话标识
export async function resetChatSession(sessionId) {
  // 重置会话，清空历史
  const resp = await fetch(`${API_BASE}/api/chat/session/reset`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ sessionId })
  })
  if (!resp.ok) {
    throw new Error('重置会话失败')
  }
  return resp.json()
}

// 获取澄清配置
export async function fetchChatClarifyConfig() {
  // 获取澄清配置
  const resp = await fetch(`${API_BASE}/api/chat/clarify/config`)
  if (!resp.ok) {
    throw new Error('获取澄清配置失败')
  }
  return resp.json()
}

// 更新澄清配置
export async function updateChatClarifyConfig(payload) {
  // 更新澄清配置
  const resp = await fetch(`${API_BASE}/api/chat/clarify/config`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
  if (!resp.ok) {
    throw new Error('更新澄清配置失败')
  }
  return resp.json()
}

// 获取澄清统计
export async function fetchChatClarifyStats() {
  // 获取澄清统计
  const resp = await fetch(`${API_BASE}/api/chat/clarify/stats`)
  if (!resp.ok) {
    throw new Error('获取澄清统计失败')
  }
  return resp.json()
}

// 导入 FAQ 内容
export async function importFaq(sourceType, content) {
  const resp = await fetch(`${API_BASE}/api/faq/import`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ sourceType, content })
  })
  if (!resp.ok) {
    throw new Error('导入失败')
  }
  return resp.json()
}

// 检索 FAQ 片段
export async function searchFaq(query, topK = 3) {
  const resp = await fetch(`${API_BASE}/api/faq/search?query=${encodeURIComponent(query)}&topK=${topK}`)
  if (!resp.ok) {
    throw new Error('检索失败')
  }
  return resp.json()
}

// 查询客户信息
export async function fetchCustomer(id) {
  const resp = await fetch(`${API_BASE}/api/customers/${id}`)
  if (!resp.ok) {
    throw new Error('未找到客户')
  }
  return resp.json()
}

// 查询产品信息
export async function fetchProduct(id) {
  const resp = await fetch(`${API_BASE}/api/products/${id}`)
  if (!resp.ok) {
    throw new Error('未找到产品')
  }
  return resp.json()
}

// 查询订单信息
export async function fetchOrder(id) {
  const resp = await fetch(`${API_BASE}/api/orders/${id}`)
  if (!resp.ok) {
    throw new Error('未找到订单')
  }
  return resp.json()
}
