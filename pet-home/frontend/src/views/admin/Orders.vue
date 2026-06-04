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
      <el-table-column prop="orderNo" label="订单号"></el-table-column>
      <el-table-column prop="username" label="用户"></el-table-column>
      <el-table-column prop="userPhone" label="手机号"></el-table-column>
      <el-table-column prop="totalPrice" label="金额" width="100">
        <template slot-scope="scope">¥{{ scope.row.totalPrice }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button v-if="scope.row.status === 2" type="primary" size="small" @click="handleShip(scope.row.id)">发货</el-button>
          <el-button type="text" @click="handleDetail(scope.row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getAdminOrderList, shipOrder } from '@/api/order'
import { Message } from 'element-ui'

export default {
  name: 'AdminOrders',
  data() {
    return {
      orders: [],
      filterStatus: null,
      searchKeyword: ''
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
        this.orders = res.data
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
    handleDetail(orderId) {
      this.$message.info('订单详情: ' + orderId)
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
