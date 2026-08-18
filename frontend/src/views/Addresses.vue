<template>
  <div>
    <div class="header">
      <h2>收货地址</h2>
      <el-button type="primary" @click="openDialog()">新增地址</el-button>
    </div>

    <div v-loading="loading" class="addr-grid">
      <el-empty v-if="!loading && list.length === 0" description="暂无地址" />
      <el-card v-for="a in list" :key="a.id" class="addr-card">
        <div class="addr-top">
          <span class="addr-name">{{ a.receiverName }} <span class="addr-phone">{{ a.receiverPhone }}</span></span>
          <el-tag v-if="a.isDefault === 1" size="small" type="success">默认</el-tag>
        </div>
        <div class="addr-text">{{ a.receiverAddress }}</div>
        <div class="addr-actions">
          <el-button v-if="a.isDefault !== 1" link type="primary" @click="setDefault(a)">设为默认</el-button>
          <el-button link @click="openDialog(a)">编辑</el-button>
          <el-button link type="danger" @click="remove(a)">删除</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑地址' : '新增地址'" width="480px">
      <el-form label-width="80px">
        <el-form-item label="收货人"><el-input v-model="form.receiverName" placeholder="请输入收货人" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.receiverPhone" placeholder="请输入联系电话" /></el-form-item>
        <el-form-item label="收货地址"><el-input v-model="form.receiverAddress" placeholder="请输入详细地址" /></el-form-item>
        <el-form-item label="设为默认"><el-switch v-model="form.isDefault" /></el-form-item>
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
import { addressApi } from '../api'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const form = reactive({ id: null, receiverName: '', receiverPhone: '', receiverAddress: '', isDefault: false })

async function load() {
  loading.value = true
  try {
    list.value = await addressApi.list()
  } finally {
    loading.value = false
  }
}

function openDialog(a) {
  if (a) {
    Object.assign(form, { id: a.id, receiverName: a.receiverName, receiverPhone: a.receiverPhone, receiverAddress: a.receiverAddress, isDefault: a.isDefault === 1 })
  } else {
    Object.assign(form, { id: null, receiverName: '', receiverPhone: '', receiverAddress: '', isDefault: false })
  }
  dialogVisible.value = true
}

async function save() {
  if (!form.receiverName || !form.receiverPhone || !form.receiverAddress) {
    ElMessage.warning('请填写完整信息')
    return
  }
  submitting.value = true
  try {
    const payload = { receiverName: form.receiverName, receiverPhone: form.receiverPhone, receiverAddress: form.receiverAddress, isDefault: form.isDefault ? 1 : 0 }
    if (form.id) await addressApi.update(form.id, payload)
    else await addressApi.create(payload)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } finally {
    submitting.value = false
  }
}

async function setDefault(a) {
  await addressApi.setDefault(a.id)
  load()
}

async function remove(a) {
  await ElMessageBox.confirm('确定删除该地址吗？', '提示', { type: 'warning' })
  await addressApi.remove(a.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.addr-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; min-height: 100px; }
.addr-card { }
.addr-top { display: flex; justify-content: space-between; align-items: center; }
.addr-name { font-weight: 600; }
.addr-phone { color: #999; font-weight: 400; margin-left: 8px; }
.addr-text { color: #666; margin: 8px 0; min-height: 40px; }
.addr-actions { display: flex; gap: 4px; }
@media (max-width: 900px) { .addr-grid { grid-template-columns: 1fr; } }
</style>
