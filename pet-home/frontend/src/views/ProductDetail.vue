<template>
  <div class="product-detail">
    <div class="container" v-loading="loading">
      <div class="detail-card" v-if="product">
        <div class="product-image">
          <img :src="product.image || '/assets/images/product-default.jpg'" :alt="product.name">
        </div>
        <div class="product-info">
          <h1 class="product-name">{{ product.name }}</h1>
          <div class="product-meta">
            <el-tag size="small">{{ product.category }}</el-tag>
            <el-tag size="small" type="info">{{ product.petType }}</el-tag>
          </div>
          <div class="product-price">
            <span class="price-symbol">¥</span>
            <span class="price-value">{{ product.price }}</span>
          </div>
          <div class="product-spec" v-if="product.spec">
            <span class="spec-label">规格:</span>
            <span class="spec-value">{{ product.spec }}</span>
          </div>
          <div class="product-stock">
            <span class="stock-label">库存:</span>
            <span class="stock-value">{{ product.stock }}</span>
          </div>
          <div class="product-desc">
            <h3>商品描述</h3>
            <p>{{ product.description || '暂无描述' }}</p>
          </div>
          <div class="product-actions">
            <el-input-number v-model="quantity" :min="1" :max="product.stock" size="large"></el-input-number>
            <el-button type="primary" size="large" @click="handleAddToCart" :disabled="!user">加入购物车</el-button>
          </div>
          <div v-if="!user" class="login-tip">
            <router-link to="/login">登录</router-link>后可加入购物车
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getProductDetail } from '@/api/product'
import { addToCart } from '@/api/cart'
import { Message } from 'element-ui'

export default {
  name: 'ProductDetail',
  data() {
    return {
      product: null,
      loading: false,
      quantity: 1
    }
  },
  computed: {
    ...mapState(['user'])
  },
  mounted() {
    this.fetchProduct()
  },
  methods: {
    async fetchProduct() {
      this.loading = true
      try {
        const res = await getProductDetail(this.$route.params.id)
        this.product = res.data
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    async handleAddToCart() {
      if (!this.user) {
        this.$router.push('/login')
        return
      }
      try {
        await addToCart({
          productId: this.product.id,
          quantity: this.quantity,
          spec: this.product.spec
        })
        Message.success('添加成功')
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.product-detail {
  background: #faf8f5;
  min-height: calc(100vh - 140px);
  padding: 30px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.detail-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  display: flex;
  gap: 40px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}

.product-image {
  width: 400px;
  height: 400px;
  background: #f5f5f5;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 26px;
  color: #333;
  margin-bottom: 16px;
}

.product-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.product-price {
  color: #ff6b6b;
  margin-bottom: 20px;
}

.price-symbol {
  font-size: 20px;
}

.price-value {
  font-size: 36px;
  font-weight: bold;
}

.product-spec, .product-stock {
  margin-bottom: 12px;
  font-size: 14px;
}

.spec-label, .stock-label {
  color: #999;
  margin-right: 8px;
}

.spec-value, .stock-value {
  color: #333;
}

.product-desc {
  margin: 24px 0;
  padding: 20px;
  background: #faf8f5;
  border-radius: 8px;
}

.product-desc h3 {
  font-size: 16px;
  margin-bottom: 10px;
}

.product-desc p {
  color: #666;
  line-height: 1.6;
}

.product-actions {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-top: 24px;
}

.login-tip {
  margin-top: 16px;
  color: #999;
  font-size: 14px;
}

.login-tip a {
  color: #ff9f43;
}
</style>
