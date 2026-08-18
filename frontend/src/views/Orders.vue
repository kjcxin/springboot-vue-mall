<template>
  <div v-loading="loading">
    <h2 class="page-title">我的订单</h2>

    <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />

    <el-card v-for="order in orders" :key="order.id" class="order-card">
      <div class="order-header">
        <span class="order-no">订单号：{{ order.orderNo }}</span>
        <span class="order-status" :class="`status-${order.status}`">{{ statusText(order.status) }}</span>
      </div>
      <el-table :data="order.items" style="width: 100%">
        <el-table-column label="商品" min-width="240">
          <template #default="{ row }">{{ row.productName }}</template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">¥{{ row.productPrice }}</template>
        </el-table-column>
        <el-table-column label="数量" width="100">
          <template #default="{ row }">×{{ row.quantity }}</template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
      </el-table>
      <div class="order-footer">
        <span class="order-time">{{ order.createTime }}</span>
        <span class="order-total">合计：<span class="total-price">¥{{ order.totalAmount }}</span></span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '../api'

const orders = ref([])
const loading = ref(false)

function statusText(status) {
  return { 0: '待支付', 1: '已支付', 2: '已取消' }[status] || '未知'
}

async function load() {
  loading.value = true
  try {
    orders.value = await orderApi.list()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.page-title { margin-bottom: 16px; }
.order-card { margin-bottom: 16px; }
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 12px;
}
.order-no { color: #666; font-size: 14px; }
.order-status { font-weight: 600; }
.status-0 { color: #e6a23c; }
.status-1 { color: #67c23a; }
.status-2 { color: #909399; }
.order-footer {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-time { color: #999; font-size: 13px; }
.order-total { font-size: 15px; color: #333; }
.total-price { font-size: 20px; font-weight: 700; color: #f56c6c; }
</style>
