<template>
  <div>
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索商品名称" clearable @keyup.enter="handleSearch" style="width: 320px">
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
      <el-select v-model="category" placeholder="全部分类" clearable @change="handleSearch" style="width: 140px; margin-left: 12px">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
    </div>

    <div v-loading="loading" class="product-grid">
      <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover" @click="goDetail(p.id)">
        <div class="product-img">
          <span class="product-emoji">{{ p.image }}</span>
        </div>
        <div class="product-name">{{ p.name }}</div>
        <div class="product-category">{{ p.category }}</div>
        <div class="product-price">¥{{ p.price }}</div>
      </el-card>
      <el-empty v-if="!loading && products.length === 0" description="暂无商品" style="grid-column: 1 / -1" />
    </div>

    <div v-if="total > size" class="pagination">
      <el-pagination background layout="prev, pager, next, total" :total="total" :page-size="size"
        :current-page="page" @current-change="loadProducts" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '../api'

const router = useRouter()
const products = ref([])
const total = ref(0)
const page = ref(1)
const size = 12
const keyword = ref('')
const category = ref('')
const loading = ref(false)
const categories = ['数码', '服饰', '图书', '食品', '家居']

async function loadProducts(p = 1) {
  page.value = p
  loading.value = true
  try {
    const data = await productApi.page({ page: page.value, size, keyword: keyword.value, category: category.value })
    products.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  loadProducts(1)
}

function goDetail(id) {
  router.push(`/product/${id}`)
}

onMounted(() => loadProducts(1))
</script>

<style scoped>
.search-bar {
  display: flex;
  margin-bottom: 20px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  min-height: 200px;
}
.product-card {
  cursor: pointer;
}
.product-img {
  width: 100%;
  height: 200px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef2ff, #dfe7ff);
}
.product-emoji {
  font-size: 64px;
  line-height: 1;
}
.product-name {
  margin-top: 10px;
  font-size: 15px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-category {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}
.product-price {
  margin-top: 6px;
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}
.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
@media (max-width: 768px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
