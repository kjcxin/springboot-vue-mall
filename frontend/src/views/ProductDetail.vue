<template>
  <div v-loading="loading">
    <el-card v-if="product">
      <div class="detail-body">
        <el-image :src="product.image" fit="cover" class="detail-img">
          <template #error><div class="img-fallback">🛍️</div></template>
        </el-image>
        <div class="detail-info">
          <h2 class="name">{{ product.name }}</h2>
          <div class="category">{{ product.category }} · 库存 {{ product.stock }}</div>
          <div class="price">¥{{ product.price }}</div>
          <div class="desc">{{ product.description }}</div>
          <div class="actions">
            <el-input-number v-model="quantity" :min="1" :max="product.stock" />
            <el-button type="primary" size="large" @click="handleAddCart">加入购物车</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi, cartApi } from '../api'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const product = ref(null)
const loading = ref(false)
const quantity = ref(1)

async function load() {
  loading.value = true
  try {
    product.value = await productApi.detail(route.params.id)
  } finally {
    loading.value = false
  }
}

async function handleAddCart() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  await cartApi.add({ productId: product.value.id, quantity: quantity.value })
  ElMessage.success('已加入购物车')
}

onMounted(load)
</script>

<style scoped>
.detail-body {
  display: flex;
  gap: 40px;
}
.detail-img {
  width: 400px;
  height: 400px;
  border-radius: 8px;
  flex-shrink: 0;
}
.img-fallback {
  width: 400px;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  font-size: 80px;
}
.detail-info {
  flex: 1;
}
.name {
  font-size: 24px;
  margin-bottom: 12px;
}
.category {
  color: #999;
  font-size: 14px;
  margin-bottom: 16px;
}
.price {
  font-size: 32px;
  font-weight: 700;
  color: #f56c6c;
  margin-bottom: 16px;
}
.desc {
  color: #666;
  line-height: 1.8;
  margin-bottom: 32px;
}
.actions {
  display: flex;
  gap: 16px;
  align-items: center;
}
@media (max-width: 768px) {
  .detail-body { flex-direction: column; }
  .detail-img, .img-fallback { width: 100%; height: 280px; }
}
</style>
