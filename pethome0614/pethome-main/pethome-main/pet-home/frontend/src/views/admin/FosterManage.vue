<template>
  <div class="admin-foster-orders">
    <div class="toolbar">
      <el-select v-model="filterStatus" placeholder="订单状态" clearable>
        <el-option label="全部" :value="null"></el-option>
        <el-option label="待审核" :value="1"></el-option>
        <el-option label="待缴费" :value="2"></el-option>
        <el-option label="寄养中" :value="3"></el-option>
        <el-option label="已入住" :value="4"></el-option>
        <el-option label="已完结" :value="5"></el-option>
        <el-option label="已驳回" :value="8"></el-option>
        <el-option label="已取消" :value="9"></el-option>
      </el-select>
      <el-input v-model="searchKeyword" placeholder="搜索用户名" @keyup.enter.native="handleSearch" class="search-input"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
    </div>

    <el-table :data="orders" border class="order-table">
      <el-table-column prop="order_no" label="预约号" min-width="180"></el-table-column>
      <el-table-column prop="username" label="用户" min-width="100"></el-table-column>
      <el-table-column label="宠物" min-width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.pets && scope.row.pets.length">
            <span v-for="(pet, idx) in scope.row.pets" :key="pet.id">
              {{ pet.species === '猫' ? '🐱' : '🐶' }}{{ pet.name }}<span v-if="idx < scope.row.pets.length - 1">、</span>
            </span>
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="package_name" label="套餐" min-width="120"></el-table-column>
      <el-table-column label="开始日期" min-width="110">
        <template slot-scope="scope">{{ formatDate(scope.row.start_date) }}</template>
      </el-table-column>
      <el-table-column label="结束日期" min-width="110">
        <template slot-scope="scope">{{ formatDate(scope.row.end_date) }}</template>
      </el-table-column>
      <el-table-column label="费用" width="100">
        <template slot-scope="scope">¥{{ scope.row.total_price }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template slot-scope="scope">
          <template v-if="scope.row.status === 1">
            <el-button type="success" size="small" @click="handleAudit(scope.row.id, true)">通过</el-button>
            <el-button type="danger" size="small" @click="handleReject(scope.row.id)">驳回</el-button>
          </template>
          <template v-if="scope.row.status === 3">
            <el-button type="warning" size="small" @click="handleCheckin(scope.row.id)">入住</el-button>
          </template>
          <template v-if="scope.row.status === 4">
            <el-button type="success" size="small" @click="handleComplete(scope.row.id)">完成</el-button>
          </template>
          <el-button type="text" @click="handleDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > pageSize">
      <el-pagination
        @current-change="handlePageChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
      ></el-pagination>
    </div>

    <!-- 寄养订单详情弹窗 -->
    <el-dialog title="寄养详情" :visible.sync="detailVisible" width="600px">
      <div v-if="currentOrder">
        <p><strong>预约号：</strong>{{ currentOrder.order_no }}</p>
        <p><strong>用户：</strong>{{ currentOrder.username }}</p>
        <p><strong>套餐：</strong>{{ currentOrder.package_name }}</p>
        <p><strong>日期：</strong>{{ formatDate(currentOrder.start_date) }} ~ {{ formatDate(currentOrder.end_date) }} ({{ currentOrder.total_days }}天)</p>
        <p><strong>总金额：</strong>¥{{ currentOrder.total_price }}</p>
        <p><strong>状态：</strong><el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag></p>
        <p><strong>备注：</strong>{{ currentOrder.remark || '无' }}</p>
        <p v-if="currentOrder.reject_reason"><strong>驳回原因：</strong>{{ currentOrder.reject_reason }}</p>
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
import { getAllFosterOrders, auditFosterOrder, completeFosterOrder, checkinFosterOrder } from '@/api/foster'
import { Message } from 'element-ui'

export default {
  name: 'AdminFosterOrders',
  data() {
    return {
      orders: [],
      filterStatus: null,
      searchKeyword: '',
      detailVisible: false,
      currentOrder: null,
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  mounted() {
    this.fetchOrders()
  },
  methods: {
    async fetchOrders() {
      try {
        const params = { page: this.currentPage, pageSize: this.pageSize }
        if (this.filterStatus !== null) params.status = this.filterStatus
        if (this.searchKeyword) params.username = this.searchKeyword
        const res = await getAllFosterOrders(params)
        const data = res.data
        this.orders = data.list || data || []
        this.total = data.total || 0
      } catch (e) {
        console.error(e)
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.fetchOrders()
    },
    handlePageChange(page) {
      this.currentPage = page
      this.fetchOrders()
    },
    getStatusText(status) {
      const map = { 1: '待审核', 2: '待缴费', 3: '寄养中', 4: '已入住', 5: '已完结', 8: '已驳回', 9: '已取消' }
      return map[status] || '未知'
    },
    getStatusType(status) {
      const map = { 1: 'warning', 2: 'primary', 3: 'success', 4: '', 5: 'info', 8: 'danger', 9: 'info' }
      return map[status] || 'default'
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      if (isNaN(date.getTime())) return dateStr
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
    async handleCheckin(orderId) {
      try {
        await checkinFosterOrder(orderId)
        Message.success('已入住')
        this.fetchOrders()
      } catch (e) {
        console.error(e)
      }
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
    handleDetail(order) {
      // 列表中已包含完整订单数据（含pets），直接展示，无需再请求
      this.currentOrder = order
      this.detailVisible = true
    }
  },
  watch: {
    filterStatus() {
      this.currentPage = 1
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
  align-items: center;
}

.search-input {
  width: 300px;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
