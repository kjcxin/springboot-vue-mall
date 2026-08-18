<template>
  <div class="app">
    <template v-if="!isAdminRoute">
      <NavBar />
      <main class="content">
        <router-view />
      </main>
      <footer class="footer">Mall 商城 · Spring Boot 3 + Vue 3 全栈练习项目</footer>
    </template>
    <router-view v-else />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'

const route = useRoute()
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
</script>

<style>
/* ============ 全局设计系统 ============ */
:root {
  --el-color-primary: #6366f1;
  --el-color-primary-light-3: #8184f4;
  --el-color-primary-light-5: #a5a7f7;
  --el-color-primary-light-7: #c7c9fa;
  --el-color-primary-light-8: #dbdcfb;
  --el-color-primary-light-9: #eef0fd;
  --el-color-primary-dark-2: #4f52d6;

  --brand-gradient: linear-gradient(135deg, #6366f1, #8b5cf6);
  --price-color: #f5222d;
  --bg: #f5f6fa;
  --card-radius: 12px;
  --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.06);
  --shadow-md: 0 6px 20px rgba(0, 0, 0, 0.08);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Microsoft YaHei", "Helvetica Neue", sans-serif;
  background: var(--bg);
  color: #1f2329;
  -webkit-font-smoothing: antialiased;
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 140px);
}

.footer {
  text-align: center;
  color: #b0b4bf;
  font-size: 12px;
  padding: 24px 0;
}

/* 统一卡片质感 */
.el-card {
  border-radius: var(--card-radius) !important;
  border: none !important;
  box-shadow: var(--shadow-sm) !important;
}

/* 统一标题 */
.page-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #1f2329;
}
</style>
