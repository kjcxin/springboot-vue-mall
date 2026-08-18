<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="openDialog()">新增分类</el-button>
    </div>

    <el-table :data="list" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="分类名称" min-width="160" />
      <el-table-column label="父分类" width="150">
        <template #default="{ row }">{{ parentName(row.parentId) }}</template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="440px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="form.parentId" placeholder="无（作为顶级分类）" clearable style="width: 100%">
            <el-option v-for="c in list" :key="c.id" :label="c.name" :value="c.id" :disabled="c.id === form.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
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
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const form = reactive({ id: null, name: '', parentId: null, sort: 0, status: 1 })

function parentName(parentId) {
  if (!parentId || parentId === 0) return '顶级'
  const p = list.value.find(c => c.id === parentId)
  return p ? p.name : '—'
}

async function load() {
  loading.value = true
  try {
    list.value = await adminApi.categories()
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    Object.assign(form, { id: row.id, name: row.name, parentId: row.parentId === 0 ? null : row.parentId, sort: row.sort, status: row.status })
  } else {
    Object.assign(form, { id: null, name: '', parentId: null, sort: 0, status: 1 })
  }
  dialogVisible.value = true
}

async function save() {
  if (!form.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  submitting.value = true
  try {
    const payload = { name: form.name, parentId: form.parentId || 0, sort: form.sort, status: form.status }
    if (form.id) await adminApi.categoryUpdate(form.id, payload)
    else await adminApi.categoryCreate(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
  await adminApi.categoryDelete(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
</style>
