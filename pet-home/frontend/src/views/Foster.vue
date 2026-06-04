<template>
  <div class="foster-page">
    <div class="container" v-loading="loading">
      <div v-if="!user" class="need-login">
        <p>请先登录</p>
        <router-link to="/login" class="el-button">去登录</router-link>
      </div>

      <div v-else>
        <div class="page-header">
          <h2>寄养预约</h2>
          <el-button type="primary" @click="goToCreate">预约寄养</el-button>
        </div>

        <div v-if="orders.length === 0" class="empty-orders">
          <div class="empty-icon">🏠</div>
          <p>还没有预约记录</p>
          <el-button type="primary" @click="goToCreate">去预约</el-button>
        </div>

        <div class="order-list" v-else>
          <div class="order-card" v-for="order in orders" :key="order.id">
            <div class="order-header">
              <span class="order-no">预约号: {{ order.orderNo }}</span>
              <span class="order-status" :class="'status-' + order.status">{{ getStatusText(order.status) }}</span>
            </div>
            <div class="order-content">
              <div class="foster-info">
                <div class="info-item">
                  <span class="label">宠物:</span>
                  <span>{{ order.petName }} ({{ order.species }})</span>
                </div>
                <div class="info-item">
                  <span class="label">套餐:</span>
                  <span>{{ order.packageName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">时间:</span>
                  <span>{{ formatDate(order.startDate) }} ~ {{ formatDate(order.endDate) }} ({{ order.totalDays }}天)</span>
                </div>
                <div class="info-item">
                  <span class="label">费用:</span>
                  <span class="price">¥{{ order.totalPrice }}</span>
                </div>
                <div class="info-item" v-if="order.remark">
                  <span class="label">备注:</span>
                  <span>{{ order.remark }}</span>
                </div>
                <div class="info-item" v-if="order.rejectReason">
                  <span class="label">驳回原因:</span>
                  <span class="reject">{{ order.rejectReason }}</span>
                </div>
              </div>
            </div>
            <div class="order-actions">
              <el-button v-if="order.status === 1" type="danger" @click="handleCancel(order.id)">取消预约</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getFosterOrderList, cancelFosterOrder } from '@/api/foster'
import { Message } from 'element-ui'

export default {
  name: 'Foster',
  data() {
    return {
      orders: [],
      loading: false
    }
  },
  computed: {
    ...mapState(['user'])
  },
  mounted() {
    this.fetchOrders()
  },
  methods: {
    async fetchOrders() {
      if (!this.user) return
      this.loading = true
      try {
        const res = await getFosterOrderList()
        this.orders = res.data
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    getStatusText(status) {
      const map = {
        1: '待审核',
        2: '待缴费',
        3: '寄养中',
        4: '已完结',
        5: '已取消'
      }
      return map[status] || '未知'
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    goToCreate() {
      this.$router.push('/foster/create')
    },
    async handleCancel(orderId) {
      this.$confirm('确定取消该预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        try {
          await cancelFosterOrder(orderId)
          Message.success('取消成功')
          this.fetchOrders()
        } catch (e) {
          console.error(e)
        }
      })
    }
  }
}
</script>

<style scoped>
.foster-page {
  background: #faf8f5;
  min-height: calc(100vh - 140px);
  padding: 30px 0;
}

.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 20px;
}

.need-login {
  text-align: center;
  padding: 80px 0;
}

.need-login p {
  margin-bottom: 20px;
  color: #999;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 22px;
}

.empty-orders {
  text-align: center;
  padding: 80px 0;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.empty-orders p {
  margin-bottom: 20px;
  color: #999;
}

.order-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 14px;
  color: #666;
}

.order-status {
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 12px;
}

.status-1 { background: #fff3e0; color: #ff9f43; }
.status-2 { background: #e3f2fd; color: #1976d2; }
.status-3 { background: #e8f5e9; color: #388e3c; }
.status-4 { background: #f5f5f5; color: #999; }
.status-5 { background: #ffebee; color: #e53935; }

.order-content {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  margin-bottom: 8px;
}

.info-item .label {
  color: #999;
  width: 60px;
}

.info-item .price {
  color: #ff6b6b;
  font-weight: bold;
}

.info-item .reject {
  color: #e53935;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
}
</style>
