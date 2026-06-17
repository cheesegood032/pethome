<template>
  <div class="layout">
    <NavBar />
    <main class="main-content">
      <router-view />
    </main>
    <footer class="footer">
      <p>© 2026 宠物之家 — 宠物用品购物与寄养预约平台</p>
    </footer>
  </div>
</template>

<script>
import NavBar from '@/components/NavBar.vue'

export default {
  name: 'Layout',
  components: { NavBar }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #faf8f5;
}



.main-content {
  flex: 1;
  min-height: calc(100vh - 140px);
}

.footer {
  background: #2d3436;
  color: #b2bec3;
  text-align: center;
  padding: 20px 0;
  font-size: 14px;
}
</style>
