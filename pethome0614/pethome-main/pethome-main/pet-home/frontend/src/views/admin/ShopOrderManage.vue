<template>
  <div class="admin-orders">
    <div class="toolbar">
      <el-select v-model="filterStatus" placeholder="订单状态" clearable>
        <el-option label="全部" :value="null"></el-option>
        <el-option label="未支付" :value="1"></el-option>
        <el-option label="待发货" :value="2"></el-option>
        <el-option label="已发货" :value="3"></el-option>
        <el-option label="已签收" :value="4"></el-option>
      </el-select>
      <el-input v-model="searchKeyword" placeholder="搜索订单号/用户名" @keyup.enter.native="handleSearch" class="search-input"></el-input>
    </div>

    <el-table :data="orders" border class="order-table">
      <el-table-column prop="order_no" label="订单号"></el-table-column>
      <el-table-column prop="username" label="用户"></el-table-column>
      <el-table-column prop="receiver_phone" label="手机号"></el-table-column>
      <el-table-column prop="total_price" label="金额" width="100">
        <template slot-scope="scope">¥{{ scope.row.total_price }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="create_time" label="创建时间" width="180">
        <template slot-scope="scope">{{ formatTime(scope.row.create_time) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status === 2" type="primary" size="small" @click="handleShip(scope.row.id)">发货</el-button>
          <el-button type="text" @click="handleDetail(scope.row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" :visible.sync="detailVisible" width="600px">
      <div v-if="currentOrder">
        <p><strong>订单号：</strong>{{ currentOrder.order_no }}</p>
        <p><strong>收货人：</strong>{{ currentOrder.receiver_name }} ({{ currentOrder.receiver_phone }})</p>
        <p><strong>地址：</strong>{{ currentOrder.receiver_address }}</p>
        <p><strong>总金额：</strong>¥{{ currentOrder.total_price }}</p>
        <p><strong>备注：</strong>{{ currentOrder.remark || '无' }}</p>
        <el-table :data="currentOrder.items" border size="small" style="margin-top: 15px;">
          <el-table-column prop="product_name" label="商品名称"></el-table-column>
          <el-table-column prop="spec" label="规格" width="100"></el-table-column>
          <el-table-column prop="price" label="单价" width="80"></el-table-column>
          <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAdminOrderList, shipOrder, getOrderDetail } from '@/api/order'
import { Message } from 'element-ui'

export default {
  name: 'AdminOrders',
  data() {
    return {
      orders: [],
      filterStatus: null,
      searchKeyword: '',
      detailVisible: false,
      currentOrder: null
    }
  },
  mounted() {
    this.fetchOrders()
  },
  methods: {
    async fetchOrders() {
      try {
        const params = {}
        if (this.filterStatus !== null) params.status = this.filterStatus
        const res = await getAdminOrderList(params)
        this.orders = res.data.list || res.data
      } catch (e) {
        console.error(e)
      }
    },
    handleSearch() {
      this.fetchOrders()
    },
    getStatusText(status) {
      const map = { 1: '未支付', 2: '待发货', 3: '已发货', 4: '已签收' }
      return map[status] || '未知'
    },
    getStatusType(status) {
      const map = { 1: 'warning', 2: 'primary', 3: 'success', 4: 'info' }
      return map[status] || 'default'
    },
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      return date.toLocaleString('zh-CN')
    },
    async handleShip(orderId) {
      try {
        await shipOrder(orderId)
        Message.success('发货成功')
        this.fetchOrders()
      } catch (e) {
        console.error(e)
      }
    },
    async handleDetail(orderId) {
      try {
        const res = await getOrderDetail(orderId)
        this.currentOrder = res.data
        this.detailVisible = true
      } catch (e) {
        console.error(e)
      }
    }
  },
  watch: {
    filterStatus() {
      this.fetchOrders()
    }
  }
}
</script>

<style scoped>
.admin-orders {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}
</style>
