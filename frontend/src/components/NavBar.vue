<template>
  <header class="navbar">
    <div class="navbar-inner">
      <div class="logo" @click="$router.push('/')">🛒 Mall 商城</div>
      <div class="nav-actions">
        <template v-if="userStore.isLoggedIn">
          <span class="welcome">{{ userStore.nickname || userStore.username }}</span>
          <el-button link @click="$router.push('/cart')">购物车</el-button>
          <el-button link @click="$router.push('/orders')">我的订单</el-button>
          <el-button link @click="handleLogout">退出登录</el-button>
        </template>
        <template v-else>
          <el-button link @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" size="small" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.navbar {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}
.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.logo {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
  cursor: pointer;
}
.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.welcome {
  color: #666;
  font-size: 14px;
}
</style>
