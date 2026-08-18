<template>
  <div>
    <div class="category-nav">
      <div class="category-chip" :class="{ active: query.categoryId === null }" @click="selectCategory(null)">全部</div>
      <div v-for="c in categories" :key="c.id" class="category-chip" :class="{ active: query.categoryId === c.id }" @click="selectCategory(c.id)">
        {{ c.name }}
      </div>
    </div>

    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索商品名称" clearable @keyup.enter="loadProducts(1)" style="width: 300px">
        <template #append><el-button @click="loadProducts(1)">搜索</el-button></template>
      </el-input>
      <el-select v-model="query.sort" style="width: 160px; margin-left: 12px" @change="loadProducts(1)">
        <el-option label="综合排序" value="" />
        <el-option label="销量优先" value="sales" />
        <el-option label="价格从低到高" value="price_asc" />
        <el-option label="价格从高到低" value="price_desc" />
      </el-select>
    </div>

    <div v-loading="loading" class="product-grid">
      <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover" @click="$router.push(`/product/${p.id}`)">
        <div class="product-img"><span class="product-emoji">{{ p.image }}</span></div>
        <div class="product-name">{{ p.name }}</div>
        <div class="product-meta">已售 {{ p.sales }}</div>
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
import { ref, reactive, onMounted } from 'vue'
import { productApi, categoryApi } from '../api'

const products = ref([])
const categories = ref([])
const total = ref(0)
const page = ref(1)
const size = 12
const loading = ref(false)
const query = reactive({ keyword: '', categoryId: null, sort: '' })

async function loadCategories() {
  categories.value = await categoryApi.tree()
}

async function loadProducts(p = 1) {
  page.value = p
  loading.value = true
  try {
    const data = await productApi.page({
      page: page.value,
      size,
      keyword: query.keyword || undefined,
      categoryId: query.categoryId || undefined,
      sort: query.sort || undefined
    })
    products.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function selectCategory(id) {
  query.categoryId = id
  loadProducts(1)
}

onMounted(() => {
  loadCategories()
  loadProducts(1)
})
</script>

<style scoped>
.category-nav {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}
.category-chip {
  padding: 6px 16px;
  background: #fff;
  border-radius: 16px;
  cursor: pointer;
  font-size: 14px;
  color: #555;
  border: 1px solid #eee;
}
.category-chip.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}
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
  height: 180px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef2ff, #dfe7ff);
}
.product-emoji {
  font-size: 60px;
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
.product-meta {
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
