<template>
  <div class="app">
    <NavBar />
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: -apple-system, "PingFang SC", "Microsoft YaHei", sans-serif;
  background: #f5f6f8;
}
.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>
