<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="logo">
        <span class="logo-icon">🐾</span>
        <span>宠物之家</span>
      </div>
      <nav class="nav">
        <router-link to="/admin" class="nav-item">
          <i class="el-icon-layout"></i>
          <span>控制台</span>
        </router-link>
        <router-link to="/admin/goods" class="nav-item">
          <i class="el-icon-goods"></i>
          <span>商品管理</span>
        </router-link>
        <router-link to="/admin/shop-order" class="nav-item">
          <i class="el-icon-document"></i>
          <span>订单管理</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item">
          <i class="el-icon-user"></i>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/foster" class="nav-item">
          <i class="el-icon-home"></i>
          <span>寄养订单</span>
        </router-link>
      </nav>
    </aside>
    <main class="main-content">
      <header class="top-bar">
        <div class="title">{{ pageTitle }}</div>
        <div class="actions">
          <span class="welcome">欢迎，管理员</span>
          <el-button type="text" @click="handleLogout">退出</el-button>
        </div>
      </header>
      <div class="content">
        <router-view/>
      </div>
    </main>
  </div>
</template>

<script>
import { logout } from '@/api/admin'

export default {
  name: 'AdminLayout',
  computed: {
    pageTitle() {
      const titles = {
        '/admin': '控制台',
        '/admin/goods': '商品管理',
        '/admin/shop-order': '订单管理',
        '/admin/users': '用户管理',
        '/admin/foster': '寄养订单'
      }
      return titles[this.$route.path] || '控制台'
    }
  },
  methods: {
    async handleLogout() {
      try {
        await logout()
        localStorage.removeItem('admin')
        this.$router.push('/admin/login')
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 200px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  padding: 20px 0;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: white;
  font-size: 18px;
  font-weight: bold;
  padding: 0 20px 20px;
  border-bottom: 1px solid rgba(255,255,255,0.2);
}

.logo-icon {
  font-size: 24px;
}

.nav {
  padding-top: 20px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: rgba(255,255,255,0.8);
  transition: all 0.3s;
}

.nav-item:hover,
.nav-item.router-link-active {
  background: rgba(255,255,255,0.2);
  color: white;
}

.nav-item i {
  font-size: 18px;
}

.main-content {
  flex: 1;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.top-bar {
  background: white;
  padding: 0 30px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.top-bar .title {
  font-size: 18px;
  font-weight: bold;
}

.actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome {
  color: #666;
}

.content {
  flex: 1;
  padding: 20px 30px;
}
</style>
