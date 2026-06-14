<template>
  <header class="header">
    <div class="header-inner">
      <div class="logo" @click="$router.push('/')">
        <span class="logo-icon">🐾</span>
        <span class="logo-text">宠物之家</span>
      </div>
      <nav class="nav">
        <router-link to="/" class="nav-item" exact>首页</router-link>
        <router-link to="/goods" class="nav-item">商城</router-link>
        <router-link to="/foster" class="nav-item">寄养预约</router-link>
        <router-link to="/cart" class="nav-item">购物车</router-link>
        <router-link to="/order" class="nav-item">我的订单</router-link>
        <router-link to="/profile" class="nav-item">个人中心</router-link>
      </nav>
      <div class="user-area">
        <template v-if="!isLoggedIn">
          <router-link to="/login" class="btn-login">登录</router-link>
          <span class="sep">/</span>
          <router-link to="/register" class="btn-register">注册</router-link>
        </template>
        <template v-else>
          <span class="username">
            {{ user.username }}
            <span v-if="pendingCount > 0" class="red-dot"></span>
          </span>
          <a @click="handleLogout" class="btn-logout">退出</a>
        </template>
      </div>
    </div>
  </header>
</template>

<script>
import { mapState, mapGetters, mapActions } from 'vuex'
import { logout } from '@/api/user'
import { getOrderList } from '@/api/order'

export default {
  name: 'NavBar',
  computed: {
    ...mapState(['user', 'pendingCount']),
    ...mapGetters(['isLoggedIn'])
  },
  watch: {
    '$route'() {
      if (this.isLoggedIn) this.fetchPendingCount()
    }
  },
  mounted() {
    if (this.isLoggedIn) this.fetchPendingCount()
  },
  methods: {
    ...mapActions(['logout', 'refreshBadge']),
    async handleLogout() {
      try { await logout() } catch (e) { /* ignore */ }
      this.logout()
      this.$router.push('/')
    },
    fetchPendingCount() {
      this.refreshBadge()
    }
  }
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #ff9f43 0%, #ffb366 50%, #ffc988 100%);
  box-shadow: 0 2px 16px rgba(255, 159, 67, 0.35);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  height: 64px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}

.logo-icon {
  font-size: 30px;
  filter: drop-shadow(1px 1px 2px rgba(0,0,0,0.15));
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  text-shadow: 1px 1px 3px rgba(0,0,0,0.12);
  letter-spacing: 2px;
}

.nav {
  display: flex;
  gap: 28px;
  margin-left: 48px;
  flex: 1;
}

.nav-item {
  color: rgba(255,255,255,0.9);
  font-size: 15px;
  padding: 6px 0;
  position: relative;
  transition: color 0.2s;
  white-space: nowrap;
}

.nav-item:hover {
  color: #fff;
}

.nav-item.router-link-active,
.nav-item.router-link-exact-active {
  color: #fff;
  font-weight: 600;
}

.nav-item.router-link-active::after,
.nav-item.router-link-exact-active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  right: 0;
  height: 3px;
  background: #fff;
  border-radius: 2px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-login, .btn-register {
  color: #fff;
  font-size: 14px;
  padding: 5px 14px;
  border-radius: 20px;
  transition: background 0.2s;
}

.btn-login { background: rgba(255,255,255,0.2); }
.btn-login:hover { background: rgba(255,255,255,0.35); }
.btn-register { background: #fff; color: #ff9f43; font-weight: 600; }
.btn-register:hover { background: #fff3e6; }

.sep { color: rgba(255,255,255,0.6); font-size: 14px; }

.username {
  color: #fff;
  font-size: 14px;
  position: relative;
}

.red-dot {
  position: absolute;
  top: -4px;
  right: -10px;
  width: 8px;
  height: 8px;
  background: #ff4757;
  border-radius: 50%;
  border: 2px solid #ff9f43;
}

.btn-logout {
  color: rgba(255,255,255,0.8);
  font-size: 13px;
  cursor: pointer;
  margin-left: 4px;
}
.btn-logout:hover { color: #fff; }
</style>
