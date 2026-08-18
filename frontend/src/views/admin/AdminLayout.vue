<template>
  <el-container class="admin-container">
    <el-aside width="220px" class="admin-aside">
      <div class="admin-logo"><span class="logo-icon">🛒</span> 商城管理后台</div>
      <el-menu :default-active="$route.path" router class="admin-menu">
        <el-menu-item index="/admin/dashboard"><span class="mi">📊</span> 数据看板</el-menu-item>
        <el-menu-item index="/admin/products"><span class="mi">📦</span> 商品管理</el-menu-item>
        <el-menu-item index="/admin/categories"><span class="mi">🗂️</span> 分类管理</el-menu-item>
        <el-menu-item index="/admin/orders"><span class="mi">📋</span> 订单管理</el-menu-item>
        <el-menu-item index="/admin/users"><span class="mi">👥</span> 用户管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <span class="header-title">商城管理后台</span>
        <div class="header-actions">
          <el-button link @click="$router.push('/')">返回前台</el-button>
          <el-button link type="danger" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

function logout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-container { min-height: 100vh; }
.admin-aside {
  background: #fff;
  border-right: 1px solid #eef0f3;
}
.admin-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 17px;
  color: #333;
  gap: 6px;
}
.logo-icon { font-size: 22px; }
.admin-menu { border-right: none; }
.admin-menu .mi { margin-right: 6px; }
.admin-menu :deep(.el-menu-item.is-active) {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  border-right: 3px solid var(--el-color-primary);
}
.admin-header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-sm);
  border-bottom: 1px solid #eef0f3;
}
.header-title { font-weight: 600; font-size: 15px; }
.header-actions { display: flex; gap: 8px; }
.admin-main { background: #f5f6fa; }
</style>
