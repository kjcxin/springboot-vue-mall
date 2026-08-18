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
        <el-form-item label="验证码" prop="captchaCode">
          <div class="captcha-row">
            <el-input v-model="form.captchaCode" placeholder="验证码" @keyup.enter="handleLogin" />
            <img :src="captchaImg" class="captcha-img" title="点击刷新" @click="loadCaptcha" />
          </div>
        </el-form-item>
        <div class="remember-row">
          <el-checkbox v-model="form.rememberMe">记住登录（7 天内免登录）</el-checkbox>
        </div>
        <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">登录</el-button>
        <div class="auth-footer">
          还没有账号？<el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </div>
      </el-form>
    </el-card>
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
.auth-wrapper {
  display: flex;
  justify-content: center;
  padding-top: 40px;
}
.auth-card {
  width: 400px;
}
.auth-title {
  text-align: center;
  margin-bottom: 20px;
}
.captcha-row {
  display: flex;
  gap: 10px;
  width: 100%;
}
.captcha-img {
  height: 32px;
  width: 110px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
}
.remember-row {
  margin-bottom: 16px;
}
.auth-footer {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
  color: #666;
}
</style>
