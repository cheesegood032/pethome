import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home.vue') },
      { path: 'product/:id', name: 'ProductDetail', component: () => import('@/views/ProductDetail.vue') },
      { path: 'cart', name: 'Cart', component: () => import('@/views/Cart.vue') },
      { path: 'orders', name: 'Orders', component: () => import('@/views/Orders.vue') },
      { path: 'foster', name: 'Foster', component: () => import('@/views/Foster.vue') },
      { path: 'foster/create', name: 'FosterCreate', component: () => import('@/views/FosterCreate.vue') },
      { path: 'personal', name: 'Personal', component: () => import('@/views/Personal.vue') },
      { path: 'pets', name: 'Pets', component: () => import('@/views/Pets.vue') },
      { path: 'pets/add', name: 'PetAdd', component: () => import('@/views/PetAdd.vue') }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    children: [
      { path: '', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'products', name: 'AdminProducts', component: () => import('@/views/admin/Products.vue') },
      { path: 'orders', name: 'AdminOrders', component: () => import('@/views/admin/Orders.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/Users.vue') },
      { path: 'foster-orders', name: 'AdminFosterOrders', component: () => import('@/views/admin/FosterOrders.vue') }
    ]
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
