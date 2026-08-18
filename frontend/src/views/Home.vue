<template>
  <div>
    <!-- 分类侧栏 + 轮播 -->
    <div class="home-top">
      <div class="category-sidebar">
        <div v-for="c in categories" :key="c.id" class="sidebar-item"
          :class="{ active: query.categoryId === c.id }"
          @mouseenter="hoverCategory = c.id" @mouseleave="hoverCategory = null"
          @click="selectCategory(c.id)">
          <span class="si-name">{{ c.name }}</span>
          <span class="si-arrow">›</span>
          <div v-if="hoverCategory === c.id && c.children && c.children.length" class="sub-panel">
            <div v-for="sub in c.children" :key="sub.id" class="sub-item" @click.stop="selectCategory(sub.id)">
              {{ sub.name }}
            </div>
          </div>
        </div>
      </div>

      <div class="banner">
        <el-carousel height="360px" :interval="4000" arrow="always">
          <el-carousel-item v-for="b in banners" :key="b.title">
            <div class="banner-slide" :style="{ background: b.bg }">
              <div class="banner-text">
                <h2>{{ b.title }}</h2>
                <p>{{ b.sub }}</p>
              </div>
              <span class="banner-emoji">{{ b.emoji }}</span>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>

    <!-- 热销推荐 -->
    <div class="section-header">
      <span class="section-title">🔥 热销推荐</span>
    </div>
    <div v-loading="hotLoading" class="product-grid">
      <el-card v-for="p in hotProducts" :key="p.id" class="product-card" shadow="hover" @click="$router.push(`/product/${p.id}`)">
        <div class="product-img"><span class="product-emoji">{{ p.image }}</span><span class="badge">热卖</span></div>
        <div class="product-name">{{ p.name }}</div>
        <div class="product-bottom">
          <span class="product-price"><small>¥</small>{{ p.price }}</span>
          <span class="product-sales">已售 {{ p.sales }}</span>
        </div>
      </el-card>
    </div>

    <!-- 全部商品 -->
    <div class="section-header">
      <span class="section-title">🛍️ 全部商品</span>
      <div class="sort-options">
        <div class="sort-item" :class="{ active: query.sort === '' }" @click="setSort('')">综合</div>
        <div class="sort-item" :class="{ active: query.sort === 'sales' }" @click="setSort('sales')">销量</div>
        <div class="sort-item" :class="{ active: query.sort === 'price_asc' }" @click="setSort('price_asc')">价格 ↑</div>
        <div class="sort-item" :class="{ active: query.sort === 'price_desc' }" @click="setSort('price_desc')">价格 ↓</div>
      </div>
    </div>
    <div v-loading="loading" class="product-grid">
      <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover" @click="$router.push(`/product/${p.id}`)">
        <div class="product-img"><span class="product-emoji">{{ p.image }}</span></div>
        <div class="product-name">{{ p.name }}</div>
        <div class="product-bottom">
          <span class="product-price"><small>¥</small>{{ p.price }}</span>
          <span class="product-sales">已售 {{ p.sales }}</span>
        </div>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { productApi, categoryApi } from '../api'

const route = useRoute()
const products = ref([])
const hotProducts = ref([])
const categories = ref([])
const total = ref(0)
const page = ref(1)
const size = 12
const loading = ref(false)
const hotLoading = ref(false)
const hoverCategory = ref(null)
const query = reactive({ keyword: '', categoryId: null, sort: '' })

const banners = [
  { title: '数码狂欢季', sub: '手机 · 电脑配件 · 影音 好物集结', emoji: '📱', bg: 'linear-gradient(135deg, #667eea, #764ba2)' },
  { title: '图书特惠', sub: '经典技术书籍，自我提升正当时', emoji: '📚', bg: 'linear-gradient(135deg, #f093fb, #f5576c)' },
  { title: '居家好物', sub: '提升生活品质的每一件小物', emoji: '🛋️', bg: 'linear-gradient(135deg, #4facfe, #00c6fb)' }
]

async function loadCategories() {
  categories.value = await categoryApi.tree()
}

async function loadHotProducts() {
  hotLoading.value = true
  try {
    const data = await productApi.page({ page: 1, size: 8, sort: 'sales' })
    hotProducts.value = data.records
  } finally {
    hotLoading.value = false
  }
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

function setSort(sort) {
  query.sort = sort
  loadProducts(1)
}

watch(() => route.query.keyword, (kw) => {
  query.keyword = kw || ''
  loadProducts(1)
})

onMounted(() => {
  if (route.query.keyword) query.keyword = route.query.keyword
  loadCategories()
  loadHotProducts()
  loadProducts(1)
})
</script>

<style scoped>
.home-top {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
.category-sidebar {
  width: 200px;
  background: #fff;
  border-radius: 12px;
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  flex-shrink: 0;
  z-index: 10;
}
.sidebar-item {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 13px 16px;
  cursor: pointer;
  font-size: 14px;
  color: #444;
  transition: background 0.15s;
}
.sidebar-item:hover { background: var(--el-color-primary-light-9); color: var(--el-color-primary); }
.sidebar-item.active { background: var(--el-color-primary-light-9); color: var(--el-color-primary); font-weight: 600; }
.si-arrow { color: #ccc; }
.sub-panel {
  position: absolute;
  left: 100%;
  top: 0;
  min-width: 200px;
  background: #fff;
  border-radius: 8px;
  box-shadow: var(--shadow-md);
  padding: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.sub-item {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  color: #555;
  white-space: nowrap;
}
.sub-item:hover { background: var(--el-color-primary-light-9); color: var(--el-color-primary); }
.banner { flex: 1; min-width: 0; }
.banner-slide {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 60px;
  color: #fff;
  border-radius: 12px;
}
.banner-text h2 { font-size: 34px; font-weight: 800; margin-bottom: 10px; }
.banner-text p { font-size: 16px; opacity: 0.92; }
.banner-emoji { font-size: 110px; }

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 26px 0 16px;
}
.section-title { font-size: 20px; font-weight: 700; color: #1f2329; }
.sort-options {
  display: flex;
  gap: 20px;
  background: #fff;
  padding: 8px 16px;
  border-radius: 8px;
  box-shadow: var(--shadow-sm);
}
.sort-item { cursor: pointer; font-size: 14px; color: #666; transition: color 0.2s; }
.sort-item:hover { color: var(--el-color-primary); }
.sort-item.active { color: var(--el-color-primary); font-weight: 700; }

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  min-height: 100px;
}
.product-card { cursor: pointer; transition: transform 0.2s, box-shadow 0.2s; }
.product-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-md) !important; }
.product-img {
  position: relative;
  width: 100%;
  height: 170px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef0fd, #f3e8ff);
}
.product-emoji { font-size: 60px; line-height: 1; transition: transform 0.2s; }
.product-card:hover .product-emoji { transform: scale(1.1); }
.badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: linear-gradient(135deg, #ff6b6b, #ff8e53);
  color: #fff;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
}
.product-name {
  margin-top: 12px;
  font-size: 15px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-bottom { margin-top: 8px; display: flex; align-items: baseline; justify-content: space-between; }
.product-price { font-size: 20px; font-weight: 800; color: var(--price-color); }
.product-price small { font-size: 13px; }
.product-sales { font-size: 12px; color: #999; }
.pagination { margin-top: 24px; display: flex; justify-content: center; }
@media (max-width: 768px) {
  .category-sidebar { display: none; }
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
