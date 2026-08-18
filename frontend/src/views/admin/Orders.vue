<template>
  <div>
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="订单号/收货人" clearable style="width: 220px" @keyup.enter="load(1)" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 130px; margin-left: 8px" @change="load(1)">
        <el-option label="待支付" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="待收货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
      <el-button type="primary" style="margin-left: 8px" @click="load(1)">查询</el-button>
    </div>

    <el-table :data="list" v-loading="loading" style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" min-width="200" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column label="商品" min-width="180">
        <template #default="{ row }">{{ row.items.map(i => i.productName).join('、') }}</template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="170" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" link type="primary" @click="ship(row)">发货</el-button>
          <el-button v-if="row.status === 0 || row.status === 1" link type="danger" @click="close(row)">关闭</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="query.size" :current-page="query.page" @current-change="load" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../api'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '', status: null })

const statusText = (s) => ({ 0: '待支付', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }[s] || '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'primary', 2: 'danger', 3: 'success', 4: 'info' }[s] || 'info')

async function load(p = 1) {
  query.page = p
  loading.value = true
  try {
    const data = await adminApi.orders({ page: query.page, size: query.size, keyword: query.keyword || undefined, status: query.status === null ? undefined : query.status })
    list.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

async function ship(row) {
  await adminApi.orderShip(row.id)
  ElMessage.success('已发货')
  load()
}

async function close(row) {
  await ElMessageBox.confirm('确定关闭该订单吗？关闭后将恢复库存。', '提示', { type: 'warning' })
  await adminApi.orderClose(row.id)
  ElMessage.success('已关闭')
  load()
}

onMounted(() => load(1))
</script>

<style scoped>
.toolbar { display: flex; align-items: center; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
