<template>
  <div v-loading="loading">
    <h2 class="page-title">个人中心</h2>

    <el-card class="profile-card">
      <div class="profile-header">
        <div class="big-avatar">{{ user.avatar || '🙂' }}</div>
        <div>
          <div class="profile-name">{{ user.nickname || user.username }}</div>
          <div class="profile-sub">用户名：{{ user.username }}</div>
        </div>
      </div>
      <el-form label-width="80px" class="profile-form">
        <el-form-item label="头像"><el-input v-model="form.avatar" placeholder="输入 emoji 作为头像（如 🐱）" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-button type="primary" :loading="saving" @click="saveProfile">保存资料</el-button>
      </el-form>
    </el-card>

    <el-card class="profile-card">
      <template #header>修改密码</template>
      <el-form label-width="80px">
        <el-form-item label="原密码"><el-input v-model="pwd.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwd.newPassword" type="password" show-password /></el-form-item>
        <el-button type="primary" :loading="pwdSaving" @click="changePwd">修改密码</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '../api'

const user = ref({})
const loading = ref(false)
const saving = ref(false)
const pwdSaving = ref(false)
const form = reactive({ avatar: '', nickname: '', phone: '' })
const pwd = reactive({ oldPassword: '', newPassword: '' })

async function load() {
  loading.value = true
  try {
    user.value = await userApi.profile()
    Object.assign(form, { avatar: user.value.avatar, nickname: user.value.nickname, phone: user.value.phone })
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  saving.value = true
  try {
    await userApi.updateProfile(form)
    ElMessage.success('保存成功')
    load()
  } finally {
    saving.value = false
  }
}

async function changePwd() {
  if (!pwd.oldPassword || !pwd.newPassword) {
    ElMessage.warning('请填写完整')
    return
  }
  pwdSaving.value = true
  try {
    await userApi.changePassword(pwd)
    ElMessage.success('密码修改成功，请重新登录')
    pwd.oldPassword = pwd.newPassword = ''
  } finally {
    pwdSaving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.page-title { margin-bottom: 16px; }
.profile-card { max-width: 640px; margin-bottom: 16px; }
.profile-header { display: flex; align-items: center; gap: 20px; padding-bottom: 20px; margin-bottom: 20px; border-bottom: 1px solid #f0f0f0; }
.big-avatar { font-size: 56px; width: 80px; height: 80px; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #eef2ff, #dfe7ff); border-radius: 12px; }
.profile-name { font-size: 20px; font-weight: 600; }
.profile-sub { color: #999; font-size: 13px; margin-top: 4px; }
</style>
