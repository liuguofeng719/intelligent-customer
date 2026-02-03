<script setup>
import { ref } from 'vue'
import { fetchCustomer, fetchOrder, fetchProduct } from '../api/client'

const customerId = ref('1')
const productId = ref('1')
const orderId = ref('1')

const customer = ref(null)
const product = ref(null)
const order = ref(null)

const loadCustomer = async () => {
  try {
    customer.value = await fetchCustomer(customerId.value)
  } catch (error) {
    customer.value = { error: '未找到客户' }
  }
}

const loadProduct = async () => {
  try {
    product.value = await fetchProduct(productId.value)
  } catch (error) {
    product.value = { error: '未找到产品' }
  }
}

const loadOrder = async () => {
  try {
    order.value = await fetchOrder(orderId.value)
  } catch (error) {
    order.value = { error: '未找到订单' }
  }
}
</script>

<template>
  <section class="card">
    <div class="card-title">业务查询</div>
    <div class="grid">
      <div class="panel">
        <div class="panel-title">客户</div>
        <div class="form-row">
          <input v-model="customerId" type="text" />
          <button @click="loadCustomer">查询</button>
        </div>
        <pre class="result">{{ customer }}</pre>
      </div>
      <div class="panel">
        <div class="panel-title">产品</div>
        <div class="form-row">
          <input v-model="productId" type="text" />
          <button @click="loadProduct">查询</button>
        </div>
        <pre class="result">{{ product }}</pre>
      </div>
      <div class="panel">
        <div class="panel-title">订单</div>
        <div class="form-row">
          <input v-model="orderId" type="text" />
          <button @click="loadOrder">查询</button>
        </div>
        <pre class="result">{{ order }}</pre>
      </div>
    </div>
  </section>
</template>
