<template>
  <div>
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="用户名/昵称" clearable style="width: 220px" @keyup.enter="load(1)" />
      <el-button type="primary" style="margin-left: 8px" @click="load(1)">查询</el-button>
    </div>

    <el-table :data="list" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="头像" width="70">
        <template #default="{ row }"><span style="font-size: 22px">{{ row.avatar || '🙂' }}</span></template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" min-width="140" />
      <el-table-column prop="nickname" label="昵称" min-width="120" />
      <el-table-column label="角色" width="90">
        <template #default="{ row }">
          <el-tag :type="row.role === 1 ? 'warning' : 'info'">{{ row.role === 1 ? '管理员' : '普通用户' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="170" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button v-if="row.role !== 1" link :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
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
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '' })

async function load(p = 1) {
  query.page = p
  loading.value = true
  try {
    const data = await adminApi.users({ page: query.page, size: query.size, keyword: query.keyword || undefined })
    list.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row) {
  await adminApi.userStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  load()
}

onMounted(() => load(1))
</script>

<style scoped>
.toolbar { display: flex; align-items: center; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
