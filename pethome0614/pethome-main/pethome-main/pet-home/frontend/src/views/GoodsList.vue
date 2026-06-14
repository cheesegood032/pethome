<template>
  <div class="goods-list">
    <div class="container">
      <div class="filter-bar">
        <el-input v-model="keyword" placeholder="搜索商品..." clearable style="width:260px" @keyup.enter.native="fetchList">
          <el-button slot="append" icon="el-icon-search" @click="fetchList"></el-button>
        </el-input>
        <el-select v-model="petType" placeholder="适用宠物" clearable @change="fetchList">
          <el-option label="全部" value=""></el-option>
          <el-option label="🐱 猫" value="猫"></el-option>
          <el-option label="🐶 狗" value="狗"></el-option>
          <el-option label="通用" value="通用"></el-option>
        </el-select>
        <el-select v-model="category" placeholder="分类" clearable @change="fetchList">
          <el-option label="全部" value=""></el-option>
          <el-option label="猫粮" value="猫粮"></el-option>
          <el-option label="狗粮" value="狗粮"></el-option>
          <el-option label="餐具水具" value="餐具水具"></el-option>
          <el-option label="家居笼具" value="家居笼具"></el-option>
          <el-option label="外出用品" value="外出用品"></el-option>
          <el-option label="窝垫床品" value="窝垫床品"></el-option>
          <el-option label="玩具" value="玩具"></el-option>
          <el-option label="清洁护理" value="清洁护理"></el-option>
        </el-select>
      </div>
      <div class="product-grid" v-loading="loading">
        <div class="product-card" v-for="p in list" :key="p.id" @click="$router.push('/goods/'+p.id)">
          <div class="card-img"><img :src="p.image || defaultImg" :alt="p.name"></div>
          <div class="card-body">
            <div class="card-name">{{ p.name }}</div>
            <div class="card-meta">
              <el-tag size="mini" type="info" v-if="p.category">{{ p.category }}</el-tag>
              <el-tag size="mini" v-if="p.pet_type">{{ p.pet_type }}</el-tag>
            </div>
            <div class="card-price">¥{{ p.price }}</div>
          </div>
        </div>
      </div>
      <div v-if="!loading && list.length===0" class="empty">
        <p>🐾 暂无符合条件的商品</p>
      </div>
      <el-pagination v-if="total>0" class="pagination" background layout="prev, pager, next"
        :total="total" :page-size="pageSize" :current-page.sync="currentPage" @current-change="fetchList">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { getProductList } from '@/api/product'

export default {
  name: 'GoodsList',
  data() {
    return {
      keyword: '', petType: '', category: '',
      list: [], total: 0, currentPage: 1, pageSize: 12,
      loading: false,
      defaultImg: 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200"><rect width="200" height="200" fill="%23f5f0eb"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23ccc" font-size="40">🐾</text></svg>'
    }
  },
  mounted() {
    const q = this.$route.query
    if (q.keyword) this.keyword = q.keyword
    if (q.pet_type) this.petType = q.pet_type
    if (q.category) this.category = q.category
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const params = { page: this.currentPage, pageSize: this.pageSize }
        if (this.keyword) params.keyword = this.keyword
        if (this.petType) params.pet_type = this.petType
        if (this.category) params.category = this.category
        const res = await getProductList(params)
        this.list = res.data.list || []
        this.total = res.data.total || 0
      } catch (e) { console.error(e) }
      this.loading = false
    }
  },
  watch: {
    '$route.query'() {
      const q = this.$route.query
      this.keyword = q.keyword || ''
      this.petType = q.pet_type || ''
      this.category = q.category || ''
      this.currentPage = 1
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }
.filter-bar { display: flex; gap: 16px; margin-bottom: 24px; align-items: center; flex-wrap: wrap; }
.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.product-card {
  background: #fff; border-radius: 14px; overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.06); cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
}
.product-card:hover { transform: translateY(-5px); box-shadow: 0 8px 24px rgba(255,159,67,0.18); }
.card-img { height: 180px; background: #f5f0eb; overflow: hidden; }
.card-img img { width: 100%; height: 100%; object-fit: cover; }
.card-body { padding: 14px 16px; }
.card-name { font-size: 15px; color: #333; font-weight: 600; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-meta { display: flex; gap: 6px; margin-bottom: 8px; }
.card-price { color: #ff6b6b; font-size: 20px; font-weight: 700; }
.empty { text-align: center; padding: 60px 0; color: #aaa; font-size: 16px; }
.pagination { text-align: center; margin-top: 32px; }
</style>
