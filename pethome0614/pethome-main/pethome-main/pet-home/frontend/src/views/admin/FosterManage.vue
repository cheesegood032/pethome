<template>
  <div class="admin-foster-orders">
    <div class="toolbar">
      <el-select v-model="filterStatus" placeholder="订单状态" clearable>
        <el-option label="全部" :value="null"></el-option>
        <el-option label="待审核" :value="1"></el-option>
        <el-option label="待缴费" :value="2"></el-option>
        <el-option label="寄养中" :value="3"></el-option>
        <el-option label="已完结" :value="4"></el-option>
        <el-option label="已取消" :value="5"></el-option>
      </el-select>
      <el-input v-model="searchKeyword" placeholder="搜索预约号/用户名" @keyup.enter.native="handleSearch" class="search-input"></el-input>
    </div>

    <el-table :data="orders" border class="order-table">
      <el-table-column prop="orderNo" label="预约号"></el-table-column>
      <el-table-column prop="username" label="用户"></el-table-column>
      <el-table-column prop="petName" label="宠物"></el-table-column>
      <el-table-column prop="species" label="物种"></el-table-column>
      <el-table-column prop="packageName" label="套餐"></el-table-column>
      <el-table-column prop="startDate" label="开始日期">
        <template slot-scope="scope">{{ formatDate(scope.row.startDate) }}</template>
      </el-table-column>
      <el-table-column prop="endDate" label="结束日期">
        <template slot-scope="scope">{{ formatDate(scope.row.endDate) }}</template>
      </el-table-column>
      <el-table-column prop="totalPrice" label="费用" width="100">
        <template slot-scope="scope">¥{{ scope.row.totalPrice }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <template v-if="scope.row.status === 1">
            <el-button type="success" size="small" @click="handleAudit(scope.row.id, true)">通过</el-button>
            <el-button type="danger" size="small" @click="handleReject(scope.row.id)">驳回</el-button>
          </template>
          <template v-else-if="scope.row.status === 3">
            <el-button type="success" size="small" @click="handleComplete(scope.row.id)">完成</el-button>
          </template>
          <el-button type="text" @click="handleDetail(scope.row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 寄养订单详情弹窗 -->
    <el-dialog title="寄养详情" :visible.sync="detailVisible" width="600px">
      <div v-if="currentOrder">
        <p><strong>预约号：</strong>{{ currentOrder.order_no }}</p>
        <p><strong>套餐：</strong>{{ currentOrder.package_name }}</p>
        <p><strong>日期：</strong>{{ currentOrder.start_date }} ~ {{ currentOrder.end_date }} ({{ currentOrder.total_days }}天)</p>
        <p><strong>总金额：</strong>¥{{ currentOrder.total_price }}</p>
        <p><strong>备注：</strong>{{ currentOrder.remark || '无' }}</p>
        <div v-if="currentOrder.pets && currentOrder.pets.length">
          <p><strong>宠物：</strong></p>
          <ul>
            <li v-for="pet in currentOrder.pets" :key="pet.id">{{ pet.species === '猫' ? '🐱' : '🐶' }} {{ pet.name }}</li>
          </ul>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAllFosterOrders, auditFosterOrder, completeFosterOrder, getFosterOrderDetail } from '@/api/foster'
import { Message } from 'element-ui'

export default {
  name: 'AdminFosterOrders',
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
        const res = await getAllFosterOrders(params)
        this.orders = res.data.list || res.data
      } catch (e) {
        console.error(e)
      }
    },
    handleSearch() {
      this.fetchOrders()
    },
    getStatusText(status) {
      const map = { 1: '待审核', 2: '待缴费', 3: '寄养中', 4: '已完结', 5: '已取消' }
      return map[status] || '未知'
    },
    getStatusType(status) {
      const map = { 1: 'warning', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }
      return map[status] || 'default'
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    async handleAudit(orderId, approved) {
      try {
        await auditFosterOrder({ orderId, approved })
        Message.success(approved ? '审核通过' : '已驳回')
        this.fetchOrders()
      } catch (e) {
        console.error(e)
      }
    },
    async handleReject(orderId) {
      this.$prompt('请输入驳回原因', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async ({ value }) => {
        try {
          await auditFosterOrder({ orderId, approved: false, rejectReason: value })
          Message.success('已驳回')
          this.fetchOrders()
        } catch (e) {
          console.error(e)
        }
      })
    },
    async handleComplete(orderId) {
      try {
        await completeFosterOrder(orderId)
        Message.success('已完成')
        this.fetchOrders()
      } catch (e) {
        console.error(e)
      }
    },
    async handleDetail(orderId) {
      try {
        const res = await getFosterOrderDetail(orderId)
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
.admin-foster-orders {
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
