import request from './request'

// 验证码 / 认证
export const captchaApi = {
  get: () => request.get('/captcha')
}
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data)
}

// 分类
export const categoryApi = {
  tree: () => request.get('/categories')
}

// 商品
export const productApi = {
  page: (params) => request.get('/products', { params }),
  detail: (id) => request.get(`/products/${id}`),
  reviews: (id) => request.get(`/products/${id}/reviews`)
}

// 购物车
export const cartApi = {
  list: () => request.get('/cart'),
  add: (data) => request.post('/cart', data),
  update: (productId, quantity) => request.put(`/cart/${productId}`, { quantity }),
  remove: (productId) => request.delete(`/cart/${productId}`)
}

// 订单
export const orderApi = {
  create: (data) => request.post('/orders', data),
  list: (status) => request.get('/orders', { params: status === null || status === undefined ? {} : { status } }),
  detail: (id) => request.get(`/orders/${id}`),
  pay: (id) => request.put(`/orders/${id}/pay`),
  cancel: (id) => request.put(`/orders/${id}/cancel`),
  confirm: (id) => request.put(`/orders/${id}/confirm`)
}

// 收货地址
export const addressApi = {
  list: () => request.get('/addresses'),
  create: (data) => request.post('/addresses', data),
  update: (id, data) => request.put(`/addresses/${id}`, data),
  setDefault: (id) => request.put(`/addresses/${id}/default`),
  remove: (id) => request.delete(`/addresses/${id}`)
}

// 收藏
export const favoriteApi = {
  list: () => request.get('/favorites'),
  check: (productId) => request.get(`/favorites/check/${productId}`),
  add: (productId) => request.post(`/favorites/${productId}`),
  remove: (productId) => request.delete(`/favorites/${productId}`)
}

// 评价
export const reviewApi = {
  create: (data) => request.post('/reviews', data)
}

// 用户
export const userApi = {
  profile: () => request.get('/user/profile'),
  updateProfile: (data) => request.put('/user/profile', data),
  changePassword: (data) => request.put('/user/password', data)
}

// 管理端
export const adminApi = {
  dashboard: () => request.get('/admin/dashboard'),
  products: (params) => request.get('/admin/products', { params }),
  productCreate: (data) => request.post('/admin/products', data),
  productUpdate: (id, data) => request.put(`/admin/products/${id}`, data),
  productStatus: (id, status) => request.put(`/admin/products/${id}/status?status=${status}`),
  productDelete: (id) => request.delete(`/admin/products/${id}`),
  categories: () => request.get('/admin/categories'),
  categoryCreate: (data) => request.post('/admin/categories', data),
  categoryUpdate: (id, data) => request.put(`/admin/categories/${id}`, data),
  categoryDelete: (id) => request.delete(`/admin/categories/${id}`),
  orders: (params) => request.get('/admin/orders', { params }),
  orderShip: (id) => request.put(`/admin/orders/${id}/ship`),
  orderClose: (id) => request.put(`/admin/orders/${id}/close`),
  users: (params) => request.get('/admin/users', { params }),
  userStatus: (id, status) => request.put(`/admin/users/${id}/status?status=${status}`)
}
