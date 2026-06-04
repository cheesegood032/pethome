<template>
  <div class="layout">
    <header class="header">
      <div class="container">
        <div class="logo">
          <router-link to="/">
            <span class="logo-icon">🐾</span>
            <span class="logo-text">宠物之家</span>
          </router-link>
        </div>
        <nav class="nav">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/cart" class="nav-item">
            购物车
            <span v-if="cartCount > 0" class="badge">{{ cartCount }}</span>
          </router-link>
          <router-link to="/orders" class="nav-item">用品订单</router-link>
          <router-link to="/foster" class="nav-item">寄养预约</router-link>
          <router-link to="/personal" class="nav-item">个人中心</router-link>
        </nav>
        <div class="user-area">
          <template v-if="!user">
            <router-link to="/login" class="btn-login">登录</router-link>
            <router-link to="/register" class="btn-register">注册</router-link>
          </template>
          <template v-else>
            <span class="username">{{ user.username }}</span>
            <a @click="handleLogout" class="btn-logout">退出</a>
          </template>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view/>
    </main>
    <footer class="footer">
      <div class="container">
        <p>© 2024 宠物之家 - 宠物用品购物与寄养预约平台</p>
      </div>
    </footer>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import { logout } from '@/api/user'

export default {
  name: 'Layout',
  computed: {
    ...mapState(['user', 'cartCount'])
  },
  methods: {
    ...mapActions(['logout']),
    async handleLogout() {
      await logout()
      this.logout()
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #ff9f43 0%, #ffb366 100%);
  padding: 0;
  box-shadow: 0 2px 10px rgba(255, 159, 67, 0.3);
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 64px;
}

.logo a {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 22px;
  font-weight: bold;
  color: white;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
}

.nav {
  display: flex;
  gap: 32px;
  margin-left: 48px;
  flex: 1;
}

.nav-item {
  color: white;
  font-size: 15px;
  position: relative;
  padding: 8px 0;
}

.nav-item.router-link-active {
  font-weight: bold;
}

.nav-item.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: white;
  border-radius: 2px;
}

.badge {
  position: absolute;
  top: -6px;
  right: -14px;
  background: #ff4757;
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-login, .btn-register {
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
}

.btn-login {
  background: rgba(255,255,255,0.2);
}

.btn-register {
  background: white;
  color: #ff9f43;
}

.username {
  color: white;
  font-size: 14px;
}

.btn-logout {
  color: rgba(255,255,255,0.8);
  font-size: 14px;
  cursor: pointer;
}

.btn-logout:hover {
  color: white;
}

.main {
  flex: 1;
  padding: 24px 0;
}

.footer {
  background: #2d3436;
  color: #b2bec3;
  text-align: center;
  padding: 20px 0;
  font-size: 14px;
}
</style>
