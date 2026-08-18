<template>
  <div v-loading="loading">
    <el-row :gutter="16">
      <el-col :span="6" v-for="c in topCards" :key="c.label">
        <div class="stat-card" :style="{ background: c.bg }">
          <div class="stat-icon">{{ c.icon }}</div>
          <div class="stat-info">
            <div class="stat-value">{{ c.value }}</div>
            <div class="stat-label">{{ c.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="8" v-for="c in subCards" :key="c.label">
        <div class="sub-card">
          <div class="sub-icon">{{ c.icon }}</div>
          <div class="sub-info">
            <div class="sub-value">{{ c.value }}</div>
            <div class="sub-label">{{ c.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header><b>最近订单</b></template>
      <el-table :data="data.recentOrders || []" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" min-width="200" />
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminApi } from '../../api'

const data = ref({})
const loading = ref(false)
const statusText = (s) => ({ 0: '待支付', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }[s] || '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'primary', 2: 'danger', 3: 'success', 4: 'info' }[s] || 'info')

const topCards = computed(() => [
  { label: '商品总数', icon: '📦', value: data.value.productCount, bg: 'linear-gradient(135deg,#667eea,#764ba2)' },
  { label: '用户总数', icon: '👥', value: data.value.userCount, bg: 'linear-gradient(135deg,#f093fb,#f5576c)' },
  { label: '订单总数', icon: '📋', value: data.value.orderCount, bg: 'linear-gradient(135deg,#4facfe,#00c6fb)' },
  { label: '销售额', icon: '💰', value: '¥' + (data.value.salesAmount ?? 0), bg: 'linear-gradient(135deg,#43e97b,#38f9d7)' }
])

const subCards = computed(() => [
  { label: '今日订单', icon: '🕐', value: data.value.todayOrders },
  { label: '待发货', icon: '🚚', value: data.value.pendingShip },
  { label: '分类数', icon: '🗂️', value: data.value.categoryCount }
])

async function load() {
  loading.value = true
  try {
    data.value = await adminApi.dashboard()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.stat-card {
  border-radius: 12px;
  padding: 22px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-sm);
}
.stat-icon { font-size: 40px; }
.stat-value { font-size: 26px; font-weight: 800; }
.stat-label { font-size: 13px; opacity: 0.9; margin-top: 2px; }
.sub-card {
  background: #fff;
  border-radius: 12px;
  padding: 18px 22px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: var(--shadow-sm);
}
.sub-icon { font-size: 30px; }
.sub-value { font-size: 22px; font-weight: 700; color: #333; }
.sub-label { font-size: 13px; color: #999; }
</style>
