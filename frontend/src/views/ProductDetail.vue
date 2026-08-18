<template>
  <div v-loading="loading">
    <el-card v-if="product" class="detail-card">
      <div class="detail-body">
        <div class="detail-img"><span class="detail-emoji">{{ product.image }}</span></div>
        <div class="detail-info">
          <h2 class="name">{{ product.name }}</h2>
          <div class="meta">已售 {{ product.sales }} · 库存 {{ product.stock }}</div>
          <div class="price">¥{{ product.price }}</div>
          <div class="desc">{{ product.description }}</div>
          <div class="actions">
            <el-input-number v-model="quantity" :min="1" :max="product.stock" />
            <el-button type="primary" size="large" @click="handleAddCart">加入购物车</el-button>
            <el-button size="large" :type="favorited ? 'warning' : 'default'" @click="toggleFavorite">
              {{ favorited ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="review-card">
      <template #header>
        <div class="review-header">
          <span>商品评价（{{ reviews.length }}）</span>
          <el-button v-if="userStore.isLoggedIn" type="primary" size="small" @click="reviewDialog = true">写评价</el-button>
        </div>
      </template>
      <el-empty v-if="reviews.length === 0" description="暂无评价" />
      <div v-for="r in reviews" :key="r.id" class="review-item">
        <div class="review-top">
          <span class="review-user">{{ r.username }}</span>
          <el-rate :model-value="r.rating" disabled />
        </div>
        <div class="review-content">{{ r.content }}</div>
        <div class="review-time">{{ r.createTime }}</div>
      </div>
    </el-card>

    <el-dialog v-model="reviewDialog" title="评价商品" width="480px">
      <el-form label-width="60px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="reviewForm.content" type="textarea" :rows="3" placeholder="说说你的使用感受" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi, cartApi, favoriteApi, reviewApi } from '../api'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const product = ref(null)
const reviews = ref([])
const loading = ref(false)
const quantity = ref(1)
const favorited = ref(false)
const reviewDialog = ref(false)
const submitting = ref(false)
const reviewForm = reactive({ rating: 5, content: '' })

async function load() {
  loading.value = true
  try {
    product.value = await productApi.detail(route.params.id)
    reviews.value = await productApi.reviews(route.params.id)
    if (userStore.isLoggedIn) {
      favorited.value = await favoriteApi.check(route.params.id)
    }
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

async function toggleFavorite() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  if (favorited.value) {
    await favoriteApi.remove(product.value.id)
    favorited.value = false
    ElMessage.success('已取消收藏')
  } else {
    await favoriteApi.add(product.value.id)
    favorited.value = true
    ElMessage.success('已收藏')
  }
}

async function submitReview() {
  submitting.value = true
  try {
    await reviewApi.create({ productId: product.value.id, rating: reviewForm.rating, content: reviewForm.content })
    ElMessage.success('评价成功')
    reviewDialog.value = false
    reviewForm.content = ''
    reviews.value = await productApi.reviews(route.params.id)
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.detail-card { margin-bottom: 16px; }
.detail-body { display: flex; gap: 40px; }
.detail-img {
  width: 400px;
  height: 400px;
  border-radius: 8px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef2ff, #dfe7ff);
}
.detail-emoji { font-size: 130px; line-height: 1; }
.detail-info { flex: 1; }
.name { font-size: 24px; margin-bottom: 12px; }
.meta { color: #999; font-size: 14px; margin-bottom: 16px; }
.price { font-size: 32px; font-weight: 700; color: #f56c6c; margin-bottom: 16px; }
.desc { color: #666; line-height: 1.8; margin-bottom: 32px; }
.actions { display: flex; gap: 16px; align-items: center; }
.review-header { display: flex; justify-content: space-between; align-items: center; }
.review-item { padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.review-item:last-child { border-bottom: none; }
.review-top { display: flex; align-items: center; gap: 12px; }
.review-user { font-weight: 600; }
.review-content { margin: 8px 0; color: #333; }
.review-time { font-size: 12px; color: #999; }
@media (max-width: 768px) {
  .detail-body { flex-direction: column; }
  .detail-img { width: 100%; height: 280px; }
}
</style>
