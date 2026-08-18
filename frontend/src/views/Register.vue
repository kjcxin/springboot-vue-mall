<template>
  <div class="auth-wrapper">
    <el-card class="auth-card">
      <h2 class="auth-title">注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="3-20 位用户名" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="选填" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="6-20 位密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirm">
          <el-input v-model="form.confirm" type="password" show-password placeholder="再次输入密码" />
        </el-form-item>
        <el-button type="primary" style="width: 100%" :loading="loading" @click="handleRegister">注册</el-button>
        <div class="auth-footer">
          已有账号？<el-link type="primary" @click="$router.push('/login')">去登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', nickname: '', password: '', confirm: '' })

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度需在 3-20 之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在 6-20 之间', trigger: 'blur' }
  ],
  confirm: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        value === form.password ? callback() : callback(new Error('两次输入的密码不一致'))
      },
      trigger: 'blur'
    }
  ]
}

async function handleRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    await authApi.register({ username: form.username, nickname: form.nickname, password: form.password })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
