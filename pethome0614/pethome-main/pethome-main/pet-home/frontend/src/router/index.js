import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/Home.vue') },
      { path: 'goods', name: 'GoodsList', component: () => import('@/views/GoodsList.vue') },
      { path: 'goods/:id', name: 'GoodsDetail', component: () => import('@/views/GoodsDetail.vue') },
      { path: 'cart', name: 'Cart', component: () => import('@/views/Cart.vue') },
      { path: 'order', name: 'OrderList', component: () => import('@/views/OrderList.vue') },
      { path: 'foster', name: 'Foster', component: () => import('@/views/Foster.vue') },
      { path: 'foster/order', name: 'FosterOrder', component: () => import('@/views/FosterOrder.vue') },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue') }
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
      { path: 'goods', name: 'GoodsManage', component: () => import('@/views/admin/GoodsManage.vue') },
      { path: 'shop-order', name: 'ShopOrderManage', component: () => import('@/views/admin/ShopOrderManage.vue') },
      { path: 'users', name: 'UserManage', component: () => import('@/views/admin/UserManage.vue') },
      { path: 'foster', name: 'FosterManage', component: () => import('@/views/admin/FosterManage.vue') }
    ]
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLogin.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
