import request from './request'

export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data)
}

export const productApi = {
  page: (params) => request.get('/products', { params }),
  detail: (id) => request.get(`/products/${id}`)
}

export const cartApi = {
  list: () => request.get('/cart'),
  add: (data) => request.post('/cart', data),
  update: (productId, quantity) => request.put(`/cart/${productId}`, { quantity }),
  remove: (productId) => request.delete(`/cart/${productId}`)
}

export const orderApi = {
  create: (data) => request.post('/orders', data),
  list: () => request.get('/orders'),
  detail: (id) => request.get(`/orders/${id}`)
}
