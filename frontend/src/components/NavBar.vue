<template>
  <header class="navbar">
    <div class="navbar-inner">
      <div class="logo" @click="$router.push('/')">🛒 Mall 商城</div>
      <div class="nav-links">
        <el-button link @click="$router.push('/')">首页</el-button>
        <el-button link @click="$router.push('/cart')">购物车</el-button>
        <el-button link @click="$router.push('/favorites')">收藏</el-button>
        <el-button link @click="$router.push('/orders')">我的订单</el-button>
      </div>
      <div class="nav-actions">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown @command="handleCommand">
            <span class="user-entry">
              <span class="avatar">{{ userStore.avatar || '🙂' }}</span>
              <span class="nickname">{{ userStore.displayName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="addresses">收货地址</el-dropdown-item>
                <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" command="admin" divided>后台管理</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
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

function handleCommand(cmd) {
  if (cmd === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  } else if (cmd === 'admin') {
    router.push('/admin')
  } else {
    router.push('/' + cmd)
  }
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
.nav-links {
  display: flex;
  gap: 8px;
}
.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-entry {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.avatar {
  font-size: 20px;
}
.nickname {
  color: #333;
  font-size: 14px;
}
</style>
