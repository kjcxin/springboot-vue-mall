<template>
  <div v-loading="loading">
    <h2 class="page-title">我的收藏</h2>

    <el-empty v-if="!loading && list.length === 0" description="暂无收藏">
      <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
    </el-empty>

    <div class="fav-grid">
      <el-card v-for="f in list" :key="f.id" class="fav-card" shadow="hover">
        <div class="fav-body" @click="$router.push(`/product/${f.productId}`)">
          <div class="fav-img"><span class="fav-emoji">{{ f.product?.image }}</span></div>
          <div class="fav-name">{{ f.product?.name }}</div>
          <div class="fav-price">¥{{ f.product?.price }}</div>
        </div>
        <div class="fav-footer">
          <el-button link type="danger" @click="remove(f)">取消收藏</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { favoriteApi } from '../api'

const list = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    list.value = await favoriteApi.list()
  } finally {
    loading.value = false
  }
}

async function remove(f) {
  await favoriteApi.remove(f.productId)
  ElMessage.success('已取消收藏')
  load()
}

onMounted(load)
</script>

<style scoped>
.page-title { margin-bottom: 16px; }
.fav-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; min-height: 100px; }
.fav-body { cursor: pointer; }
.fav-img { height: 160px; border-radius: 4px; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #eef2ff, #dfe7ff); }
.fav-emoji { font-size: 54px; line-height: 1; }
.fav-name { margin-top: 10px; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.fav-price { margin-top: 6px; font-size: 18px; font-weight: 700; color: #f56c6c; }
.fav-footer { margin-top: 8px; text-align: right; }
@media (max-width: 768px) { .fav-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
