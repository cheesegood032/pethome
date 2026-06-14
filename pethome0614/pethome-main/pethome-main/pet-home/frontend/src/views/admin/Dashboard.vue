<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon bg-primary">📦</div>
        <div class="stat-info">
          <div class="stat-value">{{ productCount }}</div>
          <div class="stat-label">商品总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-success">🛒</div>
        <div class="stat-info">
          <div class="stat-value">{{ orderCount }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-warning">👥</div>
        <div class="stat-info">
          <div class="stat-value">{{ userCount }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-danger">🏠</div>
        <div class="stat-info">
          <div class="stat-value">{{ fosterCount }}</div>
          <div class="stat-label">寄养订单</div>
        </div>
      </div>
    </div>

    <div class="section">
      <h3>待处理订单</h3>
      <div class="order-list">
        <div class="order-item" v-for="order in pendingOrders" :key="order.id">
          <div class="order-info">
            <span class="order-no">{{ order.orderNo }}</span>
            <span class="order-user">{{ order.username }}</span>
          </div>
          <div class="order-price">¥{{ order.totalPrice }}</div>
          <span class="order-status">{{ getOrderStatus(order.status) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getProductCount, getOrderCount, getUserCount, getFosterCount } from '@/api/admin'

export default {
  name: 'Dashboard',
  data() {
    return {
      productCount: 0,
      orderCount: 0,
      userCount: 0,
      fosterCount: 0,
      pendingOrders: []
    }
  },
  mounted() {
    this.fetchStats()
  },
  methods: {
    async fetchStats() {
      try {
        this.productCount = 10
        this.orderCount = 15
        this.userCount = 50
        this.fosterCount = 8

        this.pendingOrders = [
          { id: 1, orderNo: 'ORD202401010001', username: 'user1', totalPrice: 256.00, status: 1 },
          { id: 2, orderNo: 'ORD202401010002', username: 'user2', totalPrice: 128.00, status: 2 },
          { id: 3, orderNo: 'ORD202401010003', username: 'user3', totalPrice: 198.00, status: 1 }
        ]
      } catch (e) {
        console.error(e)
      }
    },
    getOrderStatus(status) {
      const map = {
        1: '未支付',
        2: '待发货',
        3: '已发货',
        4: '已签收'
      }
      return map[status] || '未知'
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.bg-primary { background: #e3f2fd; }
.bg-success { background: #e8f5e9; }
.bg-warning { background: #fff3e0; }
.bg-danger { background: #ffebee; }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.section h3 {
  font-size: 16px;
  margin-bottom: 16px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 8px;
}

.order-info {
  display: flex;
  gap: 20px;
}

.order-no {
  font-size: 14px;
  font-weight: bold;
}

.order-user {
  font-size: 14px;
  color: #999;
}

.order-price {
  font-size: 14px;
  color: #ff6b6b;
  font-weight: bold;
}

.order-status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 12px;
  background: #fff3e0;
  color: #ff9f43;
}
</style>
