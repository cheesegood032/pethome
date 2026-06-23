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
              <!-- 已完成订单显示评论按钮 -->
              <el-button
                v-if="order.status === 4"
                :type="isItemCommented(order.id, item.id) ? 'info' : 'warning'"
                size="mini"
                :disabled="isItemCommented(order.id, item.id)"
                @click="openCommentDialog(order, item)"
                class="comment-btn"
              >
                {{ isItemCommented(order.id, item.id) ? '已评价' : '去评价' }}
              </el-button>
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

      <!-- 评论弹窗 -->
      <el-dialog title="评价商品" :visible.sync="commentDialogVisible" width="500px" :close-on-click-modal="false">
        <div v-if="commentItem" class="comment-dialog-body">
          <div class="comment-product-name">{{ commentItem.product_name }}</div>
          <div class="comment-rating">
            <span class="rating-label">评分：</span>
            <el-rate v-model="commentForm.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"></el-rate>
          </div>
          <el-input
            type="textarea"
            v-model="commentForm.content"
            :rows="4"
            placeholder="请输入您的评价内容..."
            maxlength="500"
            show-word-limit
          ></el-input>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="commentDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitComment" :loading="submittingComment">提交评价</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { getOrderList, payOrder, completeOrder } from '@/api/order'
import { addComment, getCommentStatus } from '@/api/comment'
import { mapActions } from 'vuex'

export default {
  name: 'OrderList',
  data() {
    return {
      orderType: 'shop', activeTab: '0', orders: [], total: 0, currentPage: 1, pageSize: 10, loading: false,
      // 评论相关
      commentDialogVisible: false,
      commentItem: null,
      commentOrder: null,
      commentForm: { rating: 5, content: '' },
      submittingComment: false,
      // 每个订单已评论的 item id 集合: { orderId: [itemId1, itemId2, ...] }
      commentedMap: {}
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
        // 对已完成订单加载评论状态
        await this.loadCommentStatuses()
      } catch (e) { console.error(e) }
      this.loading = false
    },
    async loadCommentStatuses() {
      for (const order of this.orders) {
        if (order.status === 4) {
          try {
            const res = await getCommentStatus(order.id)
            this.$set(this.commentedMap, order.id, res.data || [])
          } catch (e) {
            console.error(e)
          }
        }
      }
    },
    isItemCommented(orderId, itemId) {
      const ids = this.commentedMap[orderId]
      return ids && ids.includes(itemId)
    },
    openCommentDialog(order, item) {
      this.commentOrder = order
      this.commentItem = item
      this.commentForm = { rating: 5, content: '' }
      this.commentDialogVisible = true
    },
    async submitComment() {
      if (!this.commentForm.content.trim()) {
        this.$message.warning('请输入评价内容')
        return
      }
      this.submittingComment = true
      try {
        await addComment({
          orderItemId: this.commentItem.id,
          productId: this.commentItem.product_id,
          rating: this.commentForm.rating,
          content: this.commentForm.content.trim()
        })
        this.$message.success('评价提交成功！')
        this.commentDialogVisible = false
        // 更新已评论状态
        const orderId = this.commentOrder.id
        if (!this.commentedMap[orderId]) {
          this.$set(this.commentedMap, orderId, [])
        }
        this.commentedMap[orderId].push(this.commentItem.id)
      } catch (e) { console.error(e) }
      this.submittingComment = false
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
.item-row { display: flex; gap: 16px; padding: 4px 0; font-size: 14px; color: #555; align-items: center; }
.item-name { flex: 1; }
.item-spec { color: #999; }
.item-price { color: #333; font-weight: 500; }
.comment-btn { margin-left: 8px; }
.order-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #f0f0f0; padding-top: 12px; }
.footer-left { color: #999; font-size: 13px; }
.footer-right { display: flex; align-items: center; gap: 16px; }
.total b { color: #ff6b6b; font-size: 18px; }
.pagination { text-align: center; margin-top: 24px; }

/* 评论弹窗样式 */
.comment-dialog-body { }
.comment-product-name { font-size: 16px; font-weight: 600; color: #333; margin-bottom: 16px; }
.comment-rating { display: flex; align-items: center; margin-bottom: 16px; }
.rating-label { font-size: 14px; color: #666; margin-right: 8px; }
</style>
