const API_BASE = import.meta.env.VITE_API_BASE || ''

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

export async function chat(question) {
  const resp = await fetch(`${API_BASE}/api/chat`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ question })
  })
  if (!resp.ok) {
    throw new Error('聊天请求失败')
  }
  const raw = await readStream(resp)
  const lines = raw.split('\n')
  const dataLines = lines.filter((line) => line.startsWith('data:'))
  if (dataLines.length === 0) {
    return raw.trim()
  }
  return dataLines.map((line) => line.replace(/^data:\s?/, '')).join('')
}

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

export async function searchFaq(query, topK = 3) {
  const resp = await fetch(`${API_BASE}/api/faq/search?query=${encodeURIComponent(query)}&topK=${topK}`)
  if (!resp.ok) {
    throw new Error('检索失败')
  }
  return resp.json()
}

export async function fetchCustomer(id) {
  const resp = await fetch(`${API_BASE}/api/customers/${id}`)
  if (!resp.ok) {
    throw new Error('未找到客户')
  }
  return resp.json()
}

export async function fetchProduct(id) {
  const resp = await fetch(`${API_BASE}/api/products/${id}`)
  if (!resp.ok) {
    throw new Error('未找到产品')
  }
  return resp.json()
}

export async function fetchOrder(id) {
  const resp = await fetch(`${API_BASE}/api/orders/${id}`)
  if (!resp.ok) {
    throw new Error('未找到订单')
  }
  return resp.json()
}
