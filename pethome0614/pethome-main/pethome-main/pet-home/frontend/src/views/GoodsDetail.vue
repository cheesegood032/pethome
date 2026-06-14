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
    </div>
  </div>
</template>

<script>
import { getProductDetail } from '@/api/product'
import { addToCart } from '@/api/cart'
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
      defaultImg: 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400"><rect width="400" height="400" fill="%23f5f0eb"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23ccc" font-size="60">🐾</text></svg>'
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
      } catch (e) { console.error(e) }
      this.loading = false
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
</style>
