<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-brand">
        <div class="brand-logo">🛒</div>
        <h1 class="brand-title">Mall 商城</h1>
        <p class="brand-sub">功能完整的全栈电商系统</p>
        <div class="brand-points">
          <div>✦ 多级分类 · 购物车 · 订单</div>
          <div>✦ 收藏 · 评价 · 收货地址</div>
          <div>✦ 后台管理系统</div>
        </div>
      </div>
      <div class="auth-form">
        <h2 class="form-title">欢迎登录</h2>
        <p class="form-sub">登录后开启你的购物之旅</p>
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" size="large" @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item label="验证码" prop="captchaCode">
            <div class="captcha-row">
              <el-input v-model="form.captchaCode" placeholder="验证码" size="large" @keyup.enter="handleLogin" />
              <img :src="captchaImg" class="captcha-img" title="点击刷新" @click="loadCaptcha" />
            </div>
          </el-form-item>
          <div class="remember-row">
            <el-checkbox v-model="form.rememberMe">记住登录（7 天内免登录）</el-checkbox>
          </div>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">登 录</el-button>
          <div class="auth-footer">
            还没有账号？<el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi, captchaApi } from '../api'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const captchaImg = ref('')
const form = reactive({ username: '', password: '', captchaCode: '', captchaKey: '', rememberMe: false })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

async function loadCaptcha() {
  const data = await captchaApi.get()
  form.captchaKey = data.key
  captchaImg.value = data.img
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await authApi.login(form)
    userStore.setLogin(data)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  } catch (e) {
    loadCaptcha().catch(() => {})
  } finally {
    loading.value = false
  }
}

onMounted(loadCaptcha)
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
.brand-title { font-size: 30px; font-weight: 800; margin: 16px 0 8px; }
.brand-sub { font-size: 14px; opacity: 0.9; margin-bottom: 28px; }
.brand-points div { margin-bottom: 10px; font-size: 14px; opacity: 0.95; }
.auth-form {
  width: 400px;
  padding: 40px 36px;
}
.form-title { font-size: 24px; font-weight: 700; }
.form-sub { color: #999; font-size: 13px; margin: 6px 0 24px; }
.captcha-row { display: flex; gap: 10px; width: 100%; }
.captcha-img { height: 40px; width: 110px; border-radius: 6px; cursor: pointer; border: 1px solid #dcdfe6; }
.remember-row { margin-bottom: 16px; }
.auth-footer { margin-top: 16px; text-align: center; font-size: 14px; color: #666; }
@media (max-width: 768px) {
  .auth-card { flex-direction: column; width: 100%; }
  .auth-brand { display: none; }
  .auth-form { width: 100%; }
}
</style>
