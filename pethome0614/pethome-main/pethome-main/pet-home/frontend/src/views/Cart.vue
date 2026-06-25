<template>
  <div class="cart-page">
    <div class="container">
      <h2 class="page-title">🛒 我的购物车</h2>
      <div v-loading="loading">
        <el-table :data="cartList" style="width:100%" @selection-change="handleSelChange" ref="cartTable"
          empty-text="购物车空空如也，去商城逛逛吧~" class="cart-table">
          <el-table-column type="selection" width="50"></el-table-column>
          <el-table-column label="商品" min-width="260">
            <template slot-scope="{ row }">
              <div class="product-cell">
                <img :src="row.product_image || defaultImg" class="thumb">
                <span class="name">{{ row.product_name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="规格" prop="spec" width="100"></el-table-column>
          <el-table-column label="单价" width="100">
            <template slot-scope="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column label="数量" width="150">
            <template slot-scope="{ row }">
              <el-input-number v-model="row.quantity" :min="1" :max="row.stock || 999" size="mini"
                @change="updateQty(row)"></el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="小计" width="100">
            <template slot-scope="{ row }">
              <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="{ row }">
              <el-popconfirm title="确定删除？" @confirm="removeItem(row)">
                <el-button slot="reference" type="text" size="small" class="del-btn">删除</el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 结算区 -->
      <div class="checkout-bar" v-if="cartList.length > 0">
        <div class="checkout-left">
          <div class="delivery-section">
            <span class="label">配送方式：</span>
            <el-radio-group v-model="deliveryMethod" size="small">
              <el-radio :label="1">🚚 送货上门</el-radio>
              <el-radio :label="2">🏪 门店自提</el-radio>
            </el-radio-group>
          </div>
          <div v-if="deliveryMethod === 1" class="address-section">
            <span class="label">收货地址：</span>
            <el-input v-model="receiverAddress" placeholder="请输入收货地址" size="small" style="width:300px"></el-input>
          </div>
          <div v-if="deliveryMethod === 1" class="receiver-section">
            <el-input v-model="receiverName" placeholder="收货人" size="small" style="width:120px;margin-right:8px"></el-input>
            <el-input v-model="receiverPhone" placeholder="联系电话" size="small" style="width:160px"></el-input>
          </div>
          <div v-if="deliveryMethod === 2" class="store-section">
            <span class="label">自提门店：</span>
            <el-select v-model="selectedStoreId" placeholder="选择门店" size="small" style="width:300px">
              <el-option v-for="s in stores" :key="s.id" :label="s.name + ' - ' + s.address" :value="s.id"></el-option>
            </el-select>
          </div>
        </div>
        <div class="checkout-right">
          <div class="total-price">
            已选 <b>{{ selected.length }}</b> 件，合计：<span class="price">¥{{ totalPrice }}</span>
          </div>
          <el-button type="warning" :disabled="selected.length === 0" :loading="submitting" @click="submitOrder">
            提交订单
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCartList, updateCartQuantity, removeFromCart } from '@/api/cart'
import { createOrder } from '@/api/order'
import { getStoreList } from '@/api/store'
import { mapState } from 'vuex'

export default {
  name: 'Cart',
  data() {
    return {
      cartList: [], selected: [], loading: false, submitting: false,
      deliveryMethod: 1, receiverName: '', receiverPhone: '', receiverAddress: '',
      selectedStoreId: null, stores: [],
      defaultImg: 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="60" height="60"><rect width="60" height="60" fill="%23f5f0eb"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23ccc" font-size="16">🐾</text></svg>'
    }
  },
  computed: {
    ...mapState(['user']),
    totalPrice() {
      return this.selected.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
    }
  },
  mounted() {
    this.fetchCart()
    this.fetchStores()
    if (this.user) {
      this.receiverAddress = this.user.address || ''
      this.receiverName = this.user.username || ''
      this.receiverPhone = this.user.phone || ''
    }
  },
  methods: {
    // 成员A：负责实现获取当前登录用户购物车数据并在表格中展示的功能
    async fetchCart() {
      this.loading = true
      try {
        const res = await getCartList()
        this.cartList = res.data || []
      } catch (e) { console.error(e) }
      this.loading = false
    },
    async fetchStores() {
      try {
        const res = await getStoreList()
        this.stores = res.data || []
      } catch (e) { console.error(e) }
    },
    handleSelChange(val) { this.selected = val },
    // 成员A：负责实现购物车商品数量的修改并同步到后端数据库的功能
    async updateQty(row) {
      try { await updateCartQuantity({ id: row.id, quantity: row.quantity }) }
      catch (e) { console.error(e) }
    },
    // 成员A：负责实现从购物车中移除指定商品的功能
    async removeItem(row) {
      try {
        await removeFromCart(row.id)
        this.fetchCart()
        this.$message.success('已删除')
      } catch (e) { console.error(e) }
    },
    // 成员A：负责实现购物车结算功能，收集用户选择的商品及收货/自提信息并提交生成订单
    async submitOrder() {
      if (this.selected.length === 0) return
      if (this.deliveryMethod === 1 && !this.receiverAddress) {
        this.$message.warning('请填写收货地址')
        return
      }
      if (this.deliveryMethod === 2 && !this.selectedStoreId) {
        this.$message.warning('请选择自提门店')
        return
      }
      this.submitting = true
      try {
        await createOrder({
          cart_ids: this.selected.map(i => i.id),
          delivery_method: this.deliveryMethod,
          store_id: this.deliveryMethod === 2 ? this.selectedStoreId : null,
          receiver_name: this.receiverName,
          receiver_phone: this.receiverPhone,
          receiver_address: this.receiverAddress
        })
        this.$message.success('订单创建成功！')
        this.$router.push('/order')
      } catch (e) { console.error(e) }
      this.submitting = false
    }
  }
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
.page-title { font-size: 22px; color: #333; margin-bottom: 20px; }
.product-cell { display: flex; align-items: center; gap: 12px; }
.thumb { width: 56px; height: 56px; border-radius: 8px; object-fit: cover; background: #f5f0eb; }
.name { font-size: 14px; color: #333; }
.subtotal { color: #ff6b6b; font-weight: 600; }
.del-btn { color: #ff4757; }
.checkout-bar {
  margin-top: 24px; background: #fff; border-radius: 14px; padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  display: flex; justify-content: space-between; align-items: flex-end; flex-wrap: wrap; gap: 16px;
}
.checkout-left { display: flex; flex-direction: column; gap: 12px; }
.label { font-size: 14px; color: #666; margin-right: 8px; }
.checkout-right { text-align: right; }
.total-price { font-size: 14px; color: #666; margin-bottom: 12px; }
.price { font-size: 24px; color: #ff6b6b; font-weight: 700; }
.checkout-right .el-button { height: 44px; font-size: 16px; padding: 0 32px; border-radius: 22px; }
.cart-table >>> .el-table__row { height: 80px; }
</style>
