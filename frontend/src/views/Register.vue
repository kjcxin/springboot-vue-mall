<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-brand">
        <div class="brand-logo">🛒</div>
        <h1 class="brand-title">加入 Mall 商城</h1>
        <p class="brand-sub">注册账号，享受一站式购物体验</p>
        <div class="brand-points">
          <div>✦ 多级分类 · 购物车 · 订单</div>
          <div>✦ 收藏 · 评价 · 收货地址</div>
          <div>✦ 后台管理系统</div>
        </div>
      </div>
      <div class="auth-form">
        <h2 class="form-title">创建账号</h2>
        <p class="form-sub">填写信息完成注册</p>
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="3-20 位用户名" size="large" />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" placeholder="选填" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="6-20 位密码" size="large" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirm">
            <el-input v-model="form.confirm" type="password" show-password placeholder="再次输入密码" size="large" />
          </el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleRegister">注 册</el-button>
          <div class="auth-footer">
            已有账号？<el-link type="primary" @click="$router.push('/login')">去登录</el-link>
          </div>
        </el-form>
      </div>
    </div>
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
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 140px);
  padding: 20px;
}
.auth-card {
  display: flex;
  width: 820px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-md);
  background: #fff;
}
.auth-brand {
  flex: 1;
  background: var(--brand-gradient);
  color: #fff;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.brand-logo { font-size: 52px; }
.brand-title { font-size: 28px; font-weight: 800; margin: 16px 0 8px; }
.brand-sub { font-size: 14px; opacity: 0.9; margin-bottom: 28px; }
.brand-points div { margin-bottom: 10px; font-size: 14px; opacity: 0.95; }
.auth-form {
  width: 400px;
  padding: 40px 36px;
}
.form-title { font-size: 24px; font-weight: 700; }
.form-sub { color: #999; font-size: 13px; margin: 6px 0 24px; }
.auth-footer { margin-top: 16px; text-align: center; font-size: 14px; color: #666; }
@media (max-width: 768px) {
  .auth-card { flex-direction: column; width: 100%; }
  .auth-brand { display: none; }
  .auth-form { width: 100%; }
}
</style>
