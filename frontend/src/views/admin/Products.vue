<template>
  <div>
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="搜索商品名称" clearable style="width: 220px" @keyup.enter="load(1)" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px; margin-left: 8px" @change="load(1)">
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-button type="primary" style="margin-left: 8px" @click="load(1)">查询</el-button>
      <div class="spacer" />
      <el-button type="primary" @click="openDialog()">新增商品</el-button>
    </div>

    <el-table :data="list" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图标" width="70">
        <template #default="{ row }"><span style="font-size: 24px">{{ row.image }}</span></template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="160" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="210">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="query.size" :current-page="query.page" @current-change="load" />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品' : '新增商品'" width="520px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="图标"><el-input v-model="form.image" placeholder="emoji 图标（如 🎧）" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '../../api'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const categories = ref([])
const query = reactive({ page: 1, size: 10, keyword: '', status: null })
const form = reactive({ id: null, name: '', price: 0, stock: 0, categoryId: null, image: '🛍️', description: '' })

async function loadCategories() {
  categories.value = await adminApi.categories()
}

async function load(p = 1) {
  query.page = p
  loading.value = true
  try {
    const data = await adminApi.products({ page: query.page, size: query.size, keyword: query.keyword || undefined, status: query.status === null ? undefined : query.status })
    list.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    Object.assign(form, { id: row.id, name: row.name, price: Number(row.price), stock: row.stock, categoryId: row.categoryId, image: row.image, description: row.description })
  } else {
    Object.assign(form, { id: null, name: '', price: 0, stock: 0, categoryId: null, image: '🛍️', description: '' })
  }
  dialogVisible.value = true
}

async function save() {
  if (!form.name) {
    ElMessage.warning('请输入商品名称')
    return
  }
  submitting.value = true
  try {
    const payload = { name: form.name, price: form.price, stock: form.stock, categoryId: form.categoryId, image: form.image, description: form.description }
    if (form.id) await adminApi.productUpdate(form.id, payload)
    else await adminApi.productCreate(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(row) {
  await adminApi.productStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  load()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除商品「${row.name}」吗？`, '提示', { type: 'warning' })
  await adminApi.productDelete(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(() => {
  loadCategories()
  load(1)
})
</script>

<style scoped>
.toolbar { display: flex; align-items: center; margin-bottom: 16px; }
.spacer { flex: 1; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
