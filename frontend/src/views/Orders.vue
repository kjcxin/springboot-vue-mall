<template>
  <div v-loading="loading">
    <h2 class="page-title">我的订单</h2>

    <el-tabs v-model="activeStatus" @tab-change="load">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待支付" name="0" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="待收货" name="2" />
      <el-tab-pane label="已完成" name="3" />
      <el-tab-pane label="已取消" name="4" />
    </el-tabs>

    <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />

    <el-card v-for="order in orders" :key="order.id" class="order-card">
      <div class="order-header">
        <span class="order-no">订单号：{{ order.orderNo }}</span>
        <span class="order-status" :class="`status-${order.status}`">{{ statusText(order.status) }}</span>
      </div>
      <el-table :data="order.items" style="width: 100%">
        <el-table-column label="商品" min-width="240">
          <template #default="{ row }">{{ row.productImage }} {{ row.productName }}</template>
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
        <div class="order-info">
          <div class="order-time">{{ order.createTime }} 下单</div>
          <div class="order-receiver">收货：{{ order.receiverName }} {{ order.receiverPhone }}</div>
        </div>
        <div class="order-actions">
          <span class="total-price">¥{{ order.totalAmount }}</span>
          <el-button v-if="order.status === 0" size="small" type="primary" @click="doAction(order, 'pay')">去支付</el-button>
          <el-button v-if="order.status === 0" size="small" @click="doAction(order, 'cancel')">取消订单</el-button>
          <el-button v-if="order.status === 2" size="small" type="primary" @click="doAction(order, 'confirm')">确认收货</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '../api'

const orders = ref([])
const loading = ref(false)
const activeStatus = ref('all')

function statusText(s) {
  return { 0: '待支付', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }[s] || '未知'
}

async function load() {
  loading.value = true
  try {
    const status = activeStatus.value === 'all' ? undefined : Number(activeStatus.value)
    orders.value = await orderApi.list(status)
  } finally {
    loading.value = false
  }
}

async function doAction(order, action) {
  if (action === 'cancel') {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
  } else if (action === 'pay') {
    await ElMessageBox.confirm('模拟支付 ¥' + order.totalAmount + '，确定支付吗？', '提示', { type: 'info' })
  }
  if (action === 'pay') await orderApi.pay(order.id)
  else if (action === 'cancel') await orderApi.cancel(order.id)
  else if (action === 'confirm') await orderApi.confirm(order.id)
  ElMessage.success(action === 'pay' ? '支付成功' : action === 'cancel' ? '已取消' : '已确认收货')
  load()
}

onMounted(load)
</script>

<style scoped>
.page-title { margin-bottom: 16px; }
.order-card { margin-bottom: 16px; }
.order-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 12px; border-bottom: 1px solid #f0f0f0; margin-bottom: 12px; }
.order-no { color: #666; font-size: 14px; }
.order-status { font-weight: 600; }
.status-0 { color: #e6a23c; }
.status-1 { color: #409eff; }
.status-2 { color: #f56c6c; }
.status-3 { color: #67c23a; }
.status-4 { color: #909399; }
.order-footer { margin-top: 12px; display: flex; justify-content: space-between; align-items: center; }
.order-time, .order-receiver { color: #999; font-size: 13px; }
.order-actions { display: flex; align-items: center; gap: 8px; }
.total-price { font-size: 20px; font-weight: 700; color: #f56c6c; margin-right: 8px; }
</style>
