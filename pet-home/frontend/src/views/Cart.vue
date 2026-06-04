<template>
  <div class="cart-page">
    <div class="container" v-loading="loading">
      <div v-if="!user" class="need-login">
        <p>请先登录</p>
        <router-link to="/login" class="el-button">去登录</router-link>
      </div>

      <div v-else-if="carts.length === 0" class="empty-cart">
        <div class="empty-icon">🛒</div>
        <p>购物车是空的</p>
        <router-link to="/" class="el-button">去购物</router-link>
      </div>

      <div v-else>
        <el-table :data="carts" border class="cart-table">
          <el-table-column prop="product.name" label="商品">
            <template slot-scope="scope">
              <div class="product-item">
                <img :src="scope.row.product.image" class="product-img" />
                <span class="product-name">{{ scope.row.product.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="spec" label="规格"></el-table-column>
          <el-table-column prop="product.price" label="单价">
            <template slot-scope="scope">
              ¥{{ scope.row.product.price }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" :max="scope.row.product.stock" @change="handleUpdateQuantity(scope.row)"></el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="小计">
            <template slot-scope="scope">
              ¥{{ (scope.row.product.price * scope.row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button type="text" @click="handleRemove(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="cart-footer">
          <div class="total-info">
            <span>共 {{ totalCount }} 件商品</span>
            <span class="total-price">合计: ¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" @click="handleCheckout">结算</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getCartList, updateCartQuantity, removeFromCart } from '@/api/cart'
import { Message } from 'element-ui'

export default {
  name: 'Cart',
  data() {
    return {
      carts: [],
      loading: false
    }
  },
  computed: {
    ...mapState(['user']),
    totalCount() {
      return this.carts.reduce((sum, item) => sum + item.quantity, 0)
    },
    totalPrice() {
      return this.carts.reduce((sum, item) => sum + (item.product.price * item.quantity), 0)
    }
  },
  mounted() {
    this.fetchCart()
  },
  methods: {
    async fetchCart() {
      if (!this.user) return
      this.loading = true
      try {
        const res = await getCartList()
        this.carts = res.data
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    async handleUpdateQuantity(row) {
      try {
        await updateCartQuantity({ cartId: row.id, quantity: row.quantity })
      } catch (e) {
        console.error(e)
      }
    },
    async handleRemove(id) {
      try {
        await removeFromCart(id)
        this.carts = this.carts.filter(item => item.id !== id)
        Message.success('删除成功')
      } catch (e) {
        console.error(e)
      }
    },
    handleCheckout() {
      this.$router.push('/orders')
    }
  }
}
</script>

<style scoped>
.cart-page {
  background: #faf8f5;
  min-height: calc(100vh - 140px);
  padding: 30px 0;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.need-login, .empty-cart {
  text-align: center;
  padding: 80px 0;
}

.need-login p, .empty-cart p {
  margin-bottom: 20px;
  color: #999;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.cart-table {
  background: white;
  border-radius: 12px;
  margin-bottom: 20px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
}

.product-name {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
}

.total-info {
  display: flex;
  gap: 30px;
}

.total-price {
  font-size: 20px;
  font-weight: bold;
  color: #ff6b6b;
}
</style>
