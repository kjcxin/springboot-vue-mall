<template>
  <div v-loading="loading">
    <h2 class="page-title">购物车</h2>

    <el-empty v-if="!loading && items.length === 0" description="购物车还是空的">
      <el-button type="primary" @click="$router.push('/')">去购物</el-button>
    </el-empty>

    <el-card v-else>
      <el-table :data="items" style="width: 100%" @selection-change="onSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column label="商品" min-width="300">
          <template #default="{ row }">
            <div class="cart-product">
              <div class="cart-img"><span class="cart-emoji">{{ row.image }}</span></div>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="数量" width="180">
          <template #default="{ row }">
            <el-input-number :model-value="row.quantity" :min="1" size="small" @change="(v) => handleUpdate(row, v)" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleRemove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-footer">
        <div class="total">已选 <b>{{ selected.length }}</b> 件，合计：<span class="total-price">¥{{ totalAmount }}</span></div>
        <el-button type="primary" size="large" :disabled="selected.length === 0" @click="openCheckout">去结算</el-button>
      </div>
    </el-card>

    <el-dialog v-model="checkoutVisible" title="确认订单" width="560px">
      <div class="addr-title">收货地址</div>
      <div v-loading="addrLoading" class="addr-list">
        <div v-for="a in addresses" :key="a.id" class="addr-item" :class="{ active: selectedAddrId === a.id }" @click="selectedAddrId = a.id">
          <div class="addr-line">{{ a.receiverName }} <span class="addr-phone">{{ a.receiverPhone }}</span></div>
          <div class="addr-text">{{ a.receiverAddress }}</div>
          <el-tag v-if="a.isDefault === 1" size="small" type="success">默认</el-tag>
        </div>
        <el-empty v-if="!addrLoading && addresses.length === 0" description="还没有收货地址">
          <el-button type="primary" size="small" @click="$router.push('/addresses')">去添加地址</el-button>
        </el-empty>
      </div>
      <template #footer>
        <el-button @click="checkoutVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { cartApi, addressApi, orderApi } from '../api'

const router = useRouter()
const items = ref([])
const selected = ref([])
const loading = ref(false)
const checkoutVisible = ref(false)
const addresses = ref([])
const selectedAddrId = ref(null)
const addrLoading = ref(false)
const submitting = ref(false)

const totalAmount = computed(() =>
  selected.value.reduce((sum, i) => sum + Number(i.totalPrice), 0).toFixed(2)
)

async function load() {
  loading.value = true
  try {
    items.value = await cartApi.list()
  } finally {
    loading.value = false
  }
}

function onSelectionChange(rows) {
  selected.value = rows
}

async function handleUpdate(row, quantity) {
  await cartApi.update(row.productId, quantity)
  load()
}

async function handleRemove(row) {
  await cartApi.remove(row.productId)
  ElMessage.success('已删除')
  load()
}

async function openCheckout() {
  checkoutVisible.value = true
  addrLoading.value = true
  try {
    addresses.value = await addressApi.list()
    const def = addresses.value.find(a => a.isDefault === 1)
    selectedAddrId.value = def ? def.id : (addresses.value[0]?.id || null)
  } finally {
    addrLoading.value = false
  }
}

async function handleSubmit() {
  if (!selectedAddrId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    await orderApi.create({ addressId: selectedAddrId.value, productIds: selected.value.map(i => i.productId) })
    ElMessage.success('下单成功')
    checkoutVisible.value = false
    router.push('/orders')
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.page-title { margin-bottom: 16px; }
.cart-product { display: flex; align-items: center; gap: 12px; }
.cart-img { width: 60px; height: 60px; border-radius: 4px; flex-shrink: 0; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #eef2ff, #dfe7ff); }
.cart-emoji { font-size: 28px; line-height: 1; }
.cart-footer { margin-top: 20px; display: flex; align-items: center; justify-content: flex-end; gap: 24px; }
.total { font-size: 15px; color: #333; }
.total-price { font-size: 24px; font-weight: 700; color: #f56c6c; }
.addr-title { font-weight: 600; margin-bottom: 10px; }
.addr-list { max-height: 300px; overflow-y: auto; }
.addr-item { padding: 10px 12px; border: 1px solid #eee; border-radius: 6px; margin-bottom: 8px; cursor: pointer; }
.addr-item.active { border-color: #409eff; background: #ecf5ff; }
.addr-line { font-weight: 600; }
.addr-phone { color: #999; font-weight: 400; margin-left: 8px; }
.addr-text { color: #666; margin: 4px 0; }
</style>
