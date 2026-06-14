<template>
  <div class="foster-order-page">
    <div class="container">
      <h2 class="page-title">🏡 寄养订单</h2>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="待审核" name="1"></el-tab-pane>
        <el-tab-pane label="待支付" name="2"></el-tab-pane>
        <el-tab-pane label="待入住" name="3"></el-tab-pane>
        <el-tab-pane label="寄养中" name="4"></el-tab-pane>
        <el-tab-pane label="已完成" name="5"></el-tab-pane>
      </el-tabs>
      <div v-loading="loading">
        <div v-if="orders.length === 0 && !loading" class="empty">暂无寄养订单</div>
        <el-card v-for="order in orders" :key="order.id" class="order-card" shadow="hover">
          <div class="order-header">
            <span class="order-no">单号：{{ order.order_no }}</span>
            <el-tag :type="statusType(order.status)" size="small">{{ statusText(order.status) }}</el-tag>
          </div>
          <div class="order-body">
            <div class="info-grid">
              <div class="info-item"><span class="label">套餐：</span>{{ order.package_name }}</div>
              <div class="info-item"><span class="label">日期：</span>{{ order.start_date }} ~ {{ order.end_date }}</div>
              <div class="info-item"><span class="label">天数：</span>{{ order.total_days }}天</div>
              <div class="info-item"><span class="label">总价：</span><b class="price">¥{{ order.total_price }}</b></div>
            </div>
            <div class="pet-list" v-if="order.pets && order.pets.length">
              <span class="label">宠物：</span>
              <el-tag v-for="pet in order.pets" :key="pet.id" size="small" style="margin:2px">
                {{ pet.species === '猫' ? '🐱' : '🐶' }} {{ pet.name }}
              </el-tag>
            </div>
            <div v-if="order.reject_reason" class="reject-reason">
              <el-alert :title="'驳回原因：' + order.reject_reason" type="error" :closable="false" show-icon></el-alert>
            </div>
          </div>
          <div class="order-footer">
            <el-button v-if="order.status === 1" type="info" size="small" @click="handleCancel(order)">取消预约</el-button>
            <el-button v-if="order.status === 2" type="warning" size="small" @click="handlePay(order)">去支付</el-button>
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
import { getFosterOrderList, cancelFosterOrder, payFosterOrder } from '@/api/foster'

export default {
  name: 'FosterOrder',
  data() {
    return {
      activeTab: '1', orders: [], total: 0, currentPage: 1, pageSize: 10, loading: false
    }
  },
  mounted() { this.fetchOrders() },
  methods: {
    async fetchOrders() {
      this.loading = true
      try {
        const params = { page: this.currentPage, pageSize: this.pageSize, status: parseInt(this.activeTab) }
        const res = await getFosterOrderList(params)
        this.orders = res.data.list || []
        this.total = res.data.total || 0
      } catch (e) { console.error(e) }
      this.loading = false
    },
    handleTabClick() { this.currentPage = 1; this.fetchOrders() },
    async handleCancel(order) {
      try {
        await this.$confirm('确定取消此预约？', '提示')
        await cancelFosterOrder(order.id)
        this.$message.success('已取消')
        this.fetchOrders()
      } catch (e) { /* cancel or error */ }
    },
    async handlePay(order) {
      try {
        await payFosterOrder(order.id)
        this.$message.success('支付成功！')
        this.fetchOrders()
      } catch (e) { console.error(e) }
    },
    statusText(s) {
      return { 1: '待审核', 2: '待支付', 3: '待入住', 4: '寄养中', 5: '已完成', 8: '已驳回', 9: '已取消' }[s] || '未知'
    },
    statusType(s) {
      return { 1: 'warning', 2: 'danger', 3: '', 4: 'success', 5: 'success', 8: 'danger', 9: 'info' }[s] || 'info'
    }
  }
}
</script>

<style scoped>
.container { max-width: 1000px; margin: 0 auto; padding: 24px; }
.page-title { font-size: 22px; color: #333; margin-bottom: 16px; }
.empty { text-align: center; padding: 60px; color: #aaa; }
.order-card { margin-bottom: 16px; border-radius: 14px; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.order-no { font-weight: 600; color: #333; }
.order-body { border-top: 1px solid #f0f0f0; padding-top: 12px; }
.info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px; margin-bottom: 10px; }
.info-item { font-size: 14px; color: #555; }
.label { color: #999; }
.price { color: #ff6b6b; font-size: 18px; }
.pet-list { margin-top: 8px; }
.reject-reason { margin-top: 10px; }
.order-footer { border-top: 1px solid #f0f0f0; padding-top: 12px; text-align: right; margin-top: 10px; }
.pagination { text-align: center; margin-top: 24px; }
</style>
