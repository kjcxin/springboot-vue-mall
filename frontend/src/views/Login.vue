<template>
  <div class="auth-wrapper">
    <el-card class="auth-card">
      <h2 class="auth-title">登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">登录</el-button>
        <div class="auth-footer">
          还没有账号？<el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await authApi.login(form)
    userStore.setLogin(data)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-wrapper {
  display: flex;
  justify-content: center;
  padding-top: 60px;
}
.auth-card {
  width: 400px;
}
.auth-title {
  text-align: center;
  margin-bottom: 20px;
}
.auth-footer {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
  color: #666;
}
</style>
