<template>
  <div class="goods-detail" v-loading="loading">
    <div class="container" v-if="product">
      <div class="detail-card">
        <div class="detail-left">
          <div class="product-image">
            <img :src="product.image || defaultImg" :alt="product.name">
          </div>
        </div>
        <div class="detail-right">
          <h1 class="product-name">{{ product.name }}</h1>
          <div class="product-tags">
            <el-tag v-if="product.pet_type" size="small">{{ product.pet_type }}</el-tag>
            <el-tag v-if="product.category" size="small" type="info">{{ product.category }}</el-tag>
          </div>
          <div class="price-box">
            <span class="price-label">价格</span>
            <span class="price-value">¥{{ product.price }}</span>
          </div>
          <div class="info-row" v-if="product.spec">
            <span class="info-label">规格</span>
            <el-radio-group v-model="selectedSpec" size="small">
              <el-radio-button v-for="s in specList" :key="s" :label="s">{{ s }}</el-radio-button>
            </el-radio-group>
          </div>
          <div class="info-row">
            <span class="info-label">数量</span>
            <el-input-number v-model="quantity" :min="1" :max="product.stock" size="small"></el-input-number>
            <span class="stock-info">库存 {{ product.stock }} 件</span>
          </div>
          <div class="info-row" v-if="product.description">
            <span class="info-label">介绍</span>
            <p class="description">{{ product.description }}</p>
          </div>
          <div class="action-bar">
            <el-button type="warning" size="large" icon="el-icon-shopping-cart-2" @click="addToCart" :loading="adding">
              加入购物车
            </el-button>
          </div>
        </div>
      </div>

      <!-- 商品评论区 -->
      <div class="comment-section">
        <h2 class="section-title">
          <span class="title-icon">💬</span>
          商品评价
          <span class="comment-count" v-if="comments.length">({{ comments.length }})</span>
        </h2>
        <div v-if="commentsLoading" class="comment-loading">
          <i class="el-icon-loading"></i> 加载评论中...
        </div>
        <div v-else-if="comments.length === 0" class="comment-empty">
          <div class="empty-icon">📝</div>
          <p>暂无评价，快来抢先评价吧！</p>
        </div>
        <div v-else class="comment-list">
          <div class="comment-item" v-for="c in comments" :key="c.id">
            <div class="comment-header">
              <div class="comment-user">
                <div class="user-avatar">{{ (c.username || '用户').charAt(0) }}</div>
                <span class="user-name">{{ c.username || '匿名用户' }}</span>
              </div>
              <div class="comment-meta">
                <el-rate :value="c.rating" disabled show-score text-color="#ff9900" score-template="{value}分"></el-rate>
                <span class="comment-time">{{ formatTime(c.create_time) }}</span>
              </div>
            </div>
            <div class="comment-content">{{ c.content }}</div>
            <!-- 管理员回复 -->
            <div class="admin-reply" v-if="c.reply">
              <div class="reply-badge">
                <span class="reply-icon">🏪</span>
                <span class="reply-label">商家回复</span>
                <span class="reply-time">{{ formatTime(c.reply_time) }}</span>
              </div>
              <div class="reply-content">{{ c.reply }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getProductDetail } from '@/api/product'
import { addToCart } from '@/api/cart'
import { getCommentsByProduct } from '@/api/comment'
import { mapGetters } from 'vuex'

export default {
  name: 'GoodsDetail',
  data() {
    return {
      product: null,
      loading: false,
      adding: false,
      selectedSpec: '',
      quantity: 1,
      defaultImg: 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400"><rect width="400" height="400" fill="%23f5f0eb"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23ccc" font-size="60">🐾</text></svg>',
      // 评论相关
      comments: [],
      commentsLoading: false
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn']),
    specList() {
      if (!this.product || !this.product.spec) return []
      return this.product.spec.split(',').map(s => s.trim()).filter(s => s)
    }
  },
  mounted() {
    this.fetchDetail()
  },
  methods: {
    async fetchDetail() {
      this.loading = true
      try {
        const res = await getProductDetail(this.$route.params.id)
        this.product = res.data
        if (this.specList.length > 0) this.selectedSpec = this.specList[0]
        // 加载评论
        this.fetchComments()
      } catch (e) { console.error(e) }
      this.loading = false
    },
    async fetchComments() {
      this.commentsLoading = true
      try {
        const res = await getCommentsByProduct(this.$route.params.id)
        this.comments = res.data || []
      } catch (e) { console.error(e) }
      this.commentsLoading = false
    },
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      return date.toLocaleString('zh-CN')
    },
    async addToCart() {
      if (!this.isLoggedIn) {
        this.$confirm('请先登录后再操作', '提示', { confirmButtonText: '去登录', cancelButtonText: '取消', type: 'warning' })
          .then(() => this.$router.push('/login'))
          .catch(() => {})
        return
      }
      this.adding = true
      try {
        await addToCart({
          product_id: this.product.id,
          quantity: this.quantity,
          spec: this.selectedSpec
        })
        this.$message.success('已加入购物车 🛒')
      } catch (e) { console.error(e) }
      this.adding = false
    }
  }
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 32px 24px; }
.detail-card { display: flex; gap: 48px; background: #fff; border-radius: 18px; padding: 36px; box-shadow: 0 4px 20px rgba(0,0,0,0.06); }
.detail-left { flex-shrink: 0; }
.product-image { width: 420px; height: 420px; border-radius: 14px; overflow: hidden; background: #f5f0eb; }
.product-image img { width: 100%; height: 100%; object-fit: cover; }
.detail-right { flex: 1; }
.product-name { font-size: 24px; font-weight: 700; color: #333; margin-bottom: 12px; }
.product-tags { margin-bottom: 16px; display: flex; gap: 8px; }
.price-box { background: #fff8f0; border-radius: 10px; padding: 16px 20px; margin-bottom: 20px; }
.price-label { font-size: 14px; color: #999; margin-right: 12px; }
.price-value { font-size: 32px; font-weight: 700; color: #ff6b6b; }
.info-row { display: flex; align-items: flex-start; gap: 12px; margin-bottom: 18px; }
.info-label { font-size: 14px; color: #999; min-width: 40px; padding-top: 6px; }
.stock-info { font-size: 13px; color: #aaa; margin-left: 12px; line-height: 32px; }
.description { color: #666; font-size: 14px; line-height: 1.8; }
.action-bar { margin-top: 32px; }
.action-bar .el-button { height: 48px; font-size: 16px; padding: 0 40px; border-radius: 24px; }

/* ========== 评论区样式 ========== */
.comment-section {
  margin-top: 32px;
  background: #fff;
  border-radius: 18px;
  padding: 32px 36px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon { font-size: 24px; }

.comment-count {
  font-size: 14px;
  color: #999;
  font-weight: 400;
}

.comment-loading {
  text-align: center;
  padding: 40px;
  color: #aaa;
  font-size: 14px;
}

.comment-empty {
  text-align: center;
  padding: 48px 0;
  color: #bbb;
}

.empty-icon { font-size: 48px; margin-bottom: 12px; }
.comment-empty p { font-size: 14px; }

.comment-list { }

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #f5f5f5;
}

.comment-item:last-child { border-bottom: none; }

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.comment-time {
  font-size: 12px;
  color: #bbb;
}

.comment-content {
  font-size: 14px;
  color: #555;
  line-height: 1.8;
  padding-left: 46px;
}

/* 管理员回复 */
.admin-reply {
  margin-top: 12px;
  margin-left: 46px;
  background: #f8f9fb;
  border-radius: 10px;
  padding: 14px 18px;
  border-left: 3px solid #667eea;
}

.reply-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.reply-icon { font-size: 16px; }

.reply-label {
  font-size: 13px;
  font-weight: 600;
  color: #667eea;
}

.reply-time {
  font-size: 12px;
  color: #bbb;
  margin-left: 8px;
}

.reply-content {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
}
</style>
