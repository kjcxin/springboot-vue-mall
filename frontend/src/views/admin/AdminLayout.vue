<template>
  <el-container class="admin-container">
    <el-aside width="200px" class="admin-aside">
      <div class="admin-logo">🛒 商城管理后台</div>
      <el-menu :default-active="$route.path" router background-color="#001529" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/admin/dashboard">📊 数据看板</el-menu-item>
        <el-menu-item index="/admin/products">📦 商品管理</el-menu-item>
        <el-menu-item index="/admin/categories">🗂️ 分类管理</el-menu-item>
        <el-menu-item index="/admin/orders">📋 订单管理</el-menu-item>
        <el-menu-item index="/admin/users">👥 用户管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <span class="header-title">管理后台</span>
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
.admin-aside { background: #001529; }
.admin-logo { color: #fff; font-weight: 700; font-size: 15px; padding: 20px 16px; }
.admin-header { background: #fff; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08); }
.header-title { font-weight: 600; }
.header-actions { display: flex; gap: 8px; }
.admin-main { background: #f5f6f8; }
</style>
