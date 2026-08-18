<template>
  <header class="navbar">
    <div class="navbar-inner">
      <div class="logo" @click="$router.push('/')">
        <span class="logo-icon">🛒</span>
        <span class="logo-text">Mall 商城</span>
      </div>

      <div class="search-box">
        <el-input v-model="searchKeyword" placeholder="搜索商品名称" clearable @keyup.enter="handleSearch">
          <template #append>
            <el-button :icon="Search" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <div class="nav-actions">
        <template v-if="userStore.isLoggedIn">
          <el-badge :value="0" hidden class="nav-badge">
            <el-button link class="nav-icon-btn" @click="$router.push('/cart')">🛍️ 购物车</el-button>
          </el-badge>
          <el-button link class="nav-icon-btn" @click="$router.push('/favorites')">❤️ 收藏</el-button>
          <el-button link class="nav-icon-btn" @click="$router.push('/orders')">📋 订单</el-button>
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
          <el-button type="primary" round @click="$router.push('/register')">免费注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')

function handleSearch() {
  router.push({ path: '/', query: searchKeyword.value ? { keyword: searchKeyword.value } : {} })
}

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
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 100;
}
.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  gap: 24px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}
.logo-icon { font-size: 26px; }
.logo-text {
  font-size: 21px;
  font-weight: 800;
  background: var(--brand-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.search-box {
  flex: 1;
  max-width: 420px;
}
.nav-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-shrink: 0;
}
.nav-icon-btn { color: #555; font-size: 14px; }
.user-entry {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.avatar {
  font-size: 20px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef0fd, #f3e8ff);
  border-radius: 50%;
}
.nickname { color: #333; font-size: 14px; }
@media (max-width: 768px) {
  .search-box { display: none; }
}
</style>
