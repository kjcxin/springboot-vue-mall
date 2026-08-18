import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/Home.vue') },
  { path: '/product/:id', name: 'product-detail', component: () => import('../views/ProductDetail.vue') },
  { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'register', component: () => import('../views/Register.vue') },
  { path: '/cart', name: 'cart', component: () => import('../views/Cart.vue'), meta: { requiresAuth: true } },
  { path: '/orders', name: 'orders', component: () => import('../views/Orders.vue'), meta: { requiresAuth: true } },
  { path: '/addresses', name: 'addresses', component: () => import('../views/Addresses.vue'), meta: { requiresAuth: true } },
  { path: '/favorites', name: 'favorites', component: () => import('../views/Favorites.vue'), meta: { requiresAuth: true } },
  { path: '/profile', name: 'profile', component: () => import('../views/Profile.vue'), meta: { requiresAuth: true } },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'admin-dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'products', name: 'admin-products', component: () => import('../views/admin/Products.vue') },
      { path: 'categories', name: 'admin-categories', component: () => import('../views/admin/Categories.vue') },
      { path: 'orders', name: 'admin-orders', component: () => import('../views/admin/Orders.vue') },
      { path: 'users', name: 'admin-users', component: () => import('../views/admin/Users.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router
