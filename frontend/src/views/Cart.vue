<template>
  <div v-loading="loading">
    <h2 class="page-title">购物车</h2>

    <el-empty v-if="!loading && items.length === 0" description="购物车还是空的，去逛逛吧">
      <el-button type="primary" @click="$router.push('/')">去购物</el-button>
    </el-empty>

    <el-card v-else>
      <el-table :data="items" style="width: 100%">
        <el-table-column label="商品" min-width="300">
          <template #default="{ row }">
            <div class="cart-product">
              <div class="cart-img">
                <span class="cart-emoji">{{ row.image }}</span>
              </div>
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
        <div class="total">合计：<span class="total-price">¥{{ totalAmount }}</span></div>
        <el-button type="primary" size="large" :disabled="items.length === 0" @click="dialogVisible = true">去结算</el-button>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="确认订单" width="480px">
      <el-form label-width="80px">
        <el-form-item label="收货人"><el-input v-model="form.receiverName" placeholder="请输入收货人" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.receiverPhone" placeholder="请输入联系电话" /></el-form-item>
        <el-form-item label="收货地址"><el-input v-model="form.receiverAddress" placeholder="请输入收货地址" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { cartApi, orderApi } from '../api'

const items = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const form = reactive({ receiverName: '', receiverPhone: '', receiverAddress: '' })

const totalAmount = computed(() =>
  items.value.reduce((sum, i) => sum + Number(i.totalPrice), 0).toFixed(2)
)

async function load() {
  loading.value = true
  try {
    items.value = await cartApi.list()
  } finally {
    loading.value = false
  }
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

async function handleSubmit() {
  if (!form.receiverName || !form.receiverPhone || !form.receiverAddress) {
    ElMessage.warning('请填写完整的收货信息')
    return
  }
  submitting.value = true
  try {
    await orderApi.create({ ...form })
    ElMessage.success('下单成功')
    dialogVisible.value = false
    form.receiverName = form.receiverPhone = form.receiverAddress = ''
    load()
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
.cart-footer {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 24px;
}
.total { font-size: 16px; color: #333; }
.total-price { font-size: 24px; font-weight: 700; color: #f56c6c; }
</style>
