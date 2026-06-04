<template>
  <div class="orders-page">
    <div class="container" v-loading="loading">
      <div v-if="!user" class="need-login">
        <p>请先登录</p>
        <router-link to="/login" class="el-button">去登录</router-link>
      </div>

      <div v-else>
        <el-tabs v-model="activeTab" class="order-tabs">
          <el-tab-pane label="全部" name="all">
            <div class="order-list">
              <div class="order-card" v-for="order in allOrders" :key="order.id">
                <div class="order-header">
                  <span class="order-no">订单号: {{ order.orderNo }}</span>
                  <span class="order-status" :class="'status-' + order.status">{{ getStatusText(order.status) }}</span>
                </div>
                <div class="order-items">
                  <div class="order-item" v-for="item in order.items" :key="item.id">
                    <div class="item-info">
                      <span class="item-name">{{ item.productName }}</span>
                      <span class="item-spec">{{ item.spec }}</span>
                    </div>
                    <div class="item-price">¥{{ item.price }}</div>
                    <div class="item-quantity">x{{ item.quantity }}</div>
                  </div>
                </div>
                <div class="order-footer">
                  <span class="order-total">合计: ¥{{ order.totalPrice }}</span>
                  <div class="order-actions">
                    <el-button v-if="order.status === 1" type="primary" @click="handlePay(order.id)">支付</el-button>
                    <el-button v-if="order.status === 3" type="success" @click="handleReceive(order.id)">确认收货</el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="未支付" name="unpaid">
            <div class="order-list">
              <div class="order-card" v-for="order in unpaidOrders" :key="order.id">
                <div class="order-header">
                  <span class="order-no">订单号: {{ order.orderNo }}</span>
                  <span class="order-status status-1">{{ getStatusText(order.status) }}</span>
                </div>
                <div class="order-items">
                  <div class="order-item" v-for="item in order.items" :key="item.id">
                    <div class="item-info">
                      <span class="item-name">{{ item.productName }}</span>
                      <span class="item-spec">{{ item.spec }}</span>
                    </div>
                    <div class="item-price">¥{{ item.price }}</div>
                    <div class="item-quantity">x{{ item.quantity }}</div>
                  </div>
                </div>
                <div class="order-footer">
                  <span class="order-total">合计: ¥{{ order.totalPrice }}</span>
                  <el-button type="primary" @click="handlePay(order.id)">支付</el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="待发货" name="pending">
            <div class="order-list">
              <div class="order-card" v-for="order in pendingOrders" :key="order.id">
                <div class="order-header">
                  <span class="order-no">订单号: {{ order.orderNo }}</span>
                  <span class="order-status status-2">{{ getStatusText(order.status) }}</span>
                </div>
                <div class="order-items">
                  <div class="order-item" v-for="item in order.items" :key="item.id">
                    <div class="item-info">
                      <span class="item-name">{{ item.productName }}</span>
                      <span class="item-spec">{{ item.spec }}</span>
                    </div>
                    <div class="item-price">¥{{ item.price }}</div>
                    <div class="item-quantity">x{{ item.quantity }}</div>
                  </div>
                </div>
                <div class="order-footer">
                  <span class="order-total">合计: ¥{{ order.totalPrice }}</span>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="已发货" name="shipped">
            <div class="order-list">
              <div class="order-card" v-for="order in shippedOrders" :key="order.id">
                <div class="order-header">
                  <span class="order-no">订单号: {{ order.orderNo }}</span>
                  <span class="order-status status-3">{{ getStatusText(order.status) }}</span>
                </div>
                <div class="order-items">
                  <div class="order-item" v-for="item in order.items" :key="item.id">
                    <div class="item-info">
                      <span class="item-name">{{ item.productName }}</span>
                      <span class="item-spec">{{ item.spec }}</span>
                    </div>
                    <div class="item-price">¥{{ item.price }}</div>
                    <div class="item-quantity">x{{ item.quantity }}</div>
                  </div>
                </div>
                <div class="order-footer">
                  <span class="order-total">合计: ¥{{ order.totalPrice }}</span>
                  <el-button type="success" @click="handleReceive(order.id)">确认收货</el-button>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="已签收" name="received">
            <div class="order-list">
              <div class="order-card" v-for="order in receivedOrders" :key="order.id">
                <div class="order-header">
                  <span class="order-no">订单号: {{ order.orderNo }}</span>
                  <span class="order-status status-4">{{ getStatusText(order.status) }}</span>
                </div>
                <div class="order-items">
                  <div class="order-item" v-for="item in order.items" :key="item.id">
                    <div class="item-info">
                      <span class="item-name">{{ item.productName }}</span>
                      <span class="item-spec">{{ item.spec }}</span>
                    </div>
                    <div class="item-price">¥{{ item.price }}</div>
                    <div class="item-quantity">x{{ item.quantity }}</div>
                  </div>
                </div>
                <div class="order-footer">
                  <span class="order-total">合计: ¥{{ order.totalPrice }}</span>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getOrderList, payOrder, receiveOrder } from '@/api/order'
import { Message } from 'element-ui'

export default {
  name: 'Orders',
  data() {
    return {
      orders: [],
      loading: false,
      activeTab: 'all'
    }
  },
  computed: {
    ...mapState(['user']),
    allOrders() { return this.orders },
    unpaidOrders() { return this.orders.filter(o => o.status === 1) },
    pendingOrders() { return this.orders.filter(o => o.status === 2) },
    shippedOrders() { return this.orders.filter(o => o.status === 3) },
    receivedOrders() { return this.orders.filter(o => o.status === 4) }
  },
  mounted() {
    this.fetchOrders()
  },
  methods: {
    async fetchOrders() {
      if (!this.user) return
      this.loading = true
      try {
        const res = await getOrderList()
        this.orders = res.data
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    getStatusText(status) {
      const map = {
        1: '未支付',
        2: '待发货',
        3: '已发货',
        4: '已签收'
      }
      return map[status] || '未知'
    },
    async handlePay(orderId) {
      try {
        await payOrder(orderId)
        Message.success('支付成功')
        this.fetchOrders()
      } catch (e) {
        console.error(e)
      }
    },
    async handleReceive(orderId) {
      try {
        await receiveOrder(orderId)
        Message.success('确认收货成功')
        this.fetchOrders()
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.orders-page {
  background: #faf8f5;
  min-height: calc(100vh - 140px);
  padding: 30px 0;
}

.container {
  max-width: 1000px;
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

.order-tabs >>> .el-tabs__header {
  background: white;
  padding: 16px 20px;
  border-radius: 12px;
  margin-bottom: 20px;
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

.order-items {
  margin-bottom: 16px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
}

.item-info {
  flex: 1;
}

.item-name {
  display: block;
  font-size: 14px;
  color: #333;
}

.item-spec {
  display: block;
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.item-price {
  font-size: 14px;
  color: #ff6b6b;
  margin-right: 20px;
}

.item-quantity {
  font-size: 14px;
  color: #999;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.order-total {
  font-size: 16px;
  font-weight: bold;
  color: #ff6b6b;
}
</style>
