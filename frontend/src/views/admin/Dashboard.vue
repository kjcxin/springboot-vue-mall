<template>
  <div v-loading="loading">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card class="stat-card"><div class="stat-label">商品总数</div><div class="stat-value">{{ data.productCount }}</div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card"><div class="stat-label">用户总数</div><div class="stat-value">{{ data.userCount }}</div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card"><div class="stat-label">订单总数</div><div class="stat-value">{{ data.orderCount }}</div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card"><div class="stat-label">销售额</div><div class="stat-value">¥{{ data.salesAmount }}</div></el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="8">
        <el-card class="stat-card"><div class="stat-label">今日订单</div><div class="stat-value">{{ data.todayOrders }}</div></el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card"><div class="stat-label">待发货</div><div class="stat-value">{{ data.pendingShip }}</div></el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card"><div class="stat-label">分类数</div><div class="stat-value">{{ data.categoryCount }}</div></el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>最近订单</template>
      <el-table :data="data.recentOrders || []" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" min-width="200" />
        <el-table-column prop="totalAmount" label="金额" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">{{ statusText(row.status) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '../../api'

const data = ref({})
const loading = ref(false)
const statusText = (s) => ({ 0: '待支付', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }[s] || '未知')

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
.stat-card { text-align: center; }
.stat-label { color: #999; font-size: 13px; }
.stat-value { font-size: 28px; font-weight: 700; margin-top: 8px; color: #333; }
</style>
