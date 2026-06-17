<template>
  <div class="order-page">
    <div class="container">
      <div style="text-align: center; margin-bottom: 24px;">
        <el-radio-group v-model="orderType" @change="switchOrderType">
          <el-radio-button label="shop">商城订单</el-radio-button>
          <el-radio-button label="foster">寄养订单</el-radio-button>
        </el-radio-group>
      </div>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部" name="0"></el-tab-pane>
        <el-tab-pane label="未支付" name="1"></el-tab-pane>
        <el-tab-pane label="待处理" name="2"></el-tab-pane>
        <el-tab-pane label="配送中/待自提" name="3"></el-tab-pane>
        <el-tab-pane label="已完成" name="4"></el-tab-pane>
      </el-tabs>
      <div v-loading="loading">
        <div v-if="orders.length === 0 && !loading" class="empty">暂无订单</div>
        <el-card v-for="order in orders" :key="order.id" class="order-card" shadow="hover">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.order_no }}</span>
            <span class="order-time">{{ order.create_time }}</span>
            <el-tag :type="statusType(order.status)" size="small">{{ statusText(order.status) }}</el-tag>
          </div>
          <div class="order-items">
            <div class="item-row" v-for="item in (order.items || [])" :key="item.id">
              <span class="item-name">{{ item.product_name }}</span>
              <span class="item-spec" v-if="item.spec">{{ item.spec }}</span>
              <span class="item-price">¥{{ item.price }} × {{ item.quantity }}</span>
            </div>
          </div>
          <div class="order-footer">
            <div class="footer-left">
              <span>{{ order.delivery_method === 1 ? '🚚 送货上门' : '🏪 门店自提' }}</span>
            </div>
            <div class="footer-right">
              <span class="total">合计：<b>¥{{ order.total_price }}</b></span>
              <el-button v-if="order.status === 1" type="warning" size="small" @click="handlePay(order)">去支付</el-button>
              <el-button v-if="order.status === 3" type="success" size="small" @click="handleComplete(order)">确认完成</el-button>
            </div>
          </div>
        </el-card>
      </div>
      <el-pagination v-if="total > pageSize" class="pagination" background layout="prev, pager, next"
        :total="total" :page-size="pageSize" :current-page.sync="currentPage" @current-change="fetchOrders">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { getOrderList, payOrder, completeOrder } from '@/api/order'
import { mapActions } from 'vuex'

export default {
  name: 'OrderList',
  data() {
    return {
      orderType: 'shop', activeTab: '0', orders: [], total: 0, currentPage: 1, pageSize: 10, loading: false
    }
  },
  mounted() { this.fetchOrders() },
  methods: {
    ...mapActions(['updatePendingCount']),
    switchOrderType(val) {
      if (val === 'foster') {
        this.$router.push('/foster/order')
      }
    },
    async fetchOrders() {
      this.loading = true
      try {
        const params = { page: this.currentPage, pageSize: this.pageSize }
        if (this.activeTab !== '0') params.status = parseInt(this.activeTab)
        const res = await getOrderList(params)
        this.orders = res.data.list || []
        this.total = res.data.total || 0
      } catch (e) { console.error(e) }
      this.loading = false
    },
    handleTabClick() { this.currentPage = 1; this.fetchOrders() },
    async handlePay(order) {
      try {
        await payOrder(order.id)
        this.$message.success('支付成功！')
        this.fetchOrders()
        this.refreshBadge()
      } catch (e) { console.error(e) }
    },
    async handleComplete(order) {
      try {
        await completeOrder(order.id)
        this.$message.success('已确认完成！')
        this.fetchOrders()
        this.refreshBadge()
      } catch (e) { console.error(e) }
    },
    async refreshBadge() {
      try {
        const r1 = await getOrderList({ status: 1, page: 1, pageSize: 1 })
        const r3 = await getOrderList({ status: 3, page: 1, pageSize: 1 })
        this.updatePendingCount((r1.data.total || 0) + (r3.data.total || 0))
      } catch (e) { /* ignore */ }
    },
    statusText(s) {
      return { 1: '未支付', 2: '待处理', 3: '配送中/待自提', 4: '已完成', 9: '已取消' }[s] || '未知'
    },
    statusType(s) {
      return { 1: 'danger', 2: 'warning', 3: '', 4: 'success', 9: 'info' }[s] || 'info'
    }
  }
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
.page-title { font-size: 22px; color: #333; margin-bottom: 16px; }
.empty { text-align: center; padding: 60px; color: #aaa; }
.order-card { margin-bottom: 16px; border-radius: 14px; }
.order-header { display: flex; align-items: center; gap: 16px; margin-bottom: 12px; }
.order-no { font-weight: 600; color: #333; }
.order-time { color: #aaa; font-size: 13px; flex: 1; }
.order-items { border-top: 1px solid #f0f0f0; padding: 12px 0; }
.item-row { display: flex; gap: 16px; padding: 4px 0; font-size: 14px; color: #555; }
.item-name { flex: 1; }
.item-spec { color: #999; }
.item-price { color: #333; font-weight: 500; }
.order-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #f0f0f0; padding-top: 12px; }
.footer-left { color: #999; font-size: 13px; }
.footer-right { display: flex; align-items: center; gap: 16px; }
.total b { color: #ff6b6b; font-size: 18px; }
.pagination { text-align: center; margin-top: 24px; }
</style>
