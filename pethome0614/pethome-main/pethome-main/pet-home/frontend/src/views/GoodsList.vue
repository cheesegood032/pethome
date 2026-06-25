<template>
  <div class="goods-list">
    <div class="container">


      <!-- 筛选栏 -->
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
          <el-option label="主粮零食" value="主粮零食"></el-option>
          <el-option label="宠物窝笼" value="宠物窝笼"></el-option>
          <el-option label="洗护用品" value="洗护用品"></el-option>
          <el-option label="玩具" value="玩具"></el-option>
          <el-option label="服饰" value="服饰"></el-option>
          <el-option label="外出用品" value="外出用品"></el-option>
        </el-select>
        <el-select v-model="sortBy" placeholder="排序" @change="fetchList" style="width:120px">
          <el-option label="默认" value=""></el-option>
          <el-option label="价格↑" value="price_asc"></el-option>
          <el-option label="价格↓" value="price_desc"></el-option>
          <el-option label="销量↓" value="sales_desc"></el-option>
        </el-select>
      </div>

      <!-- 热销推荐 -->
      <div class="hot-section" v-if="hotProducts.length > 0">
        <h3 class="section-title">🔥 热销推荐</h3>
        <div class="hot-scroll">
          <div class="hot-track">
            <div class="product-card-h" v-for="p in hotProducts" :key="p.id" @click="goDetail(p.id)">
              <div class="card-img"><img :src="p.image || defaultImg" :alt="p.name"></div>
              <div class="card-body">
                <div class="card-name">{{ p.name }}</div>
                <div class="card-price">¥{{ p.price }}</div>
                <div class="card-sales">已售 {{ p.sales_count || 0 }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="section-header">
        <h3 class="section-title">🛍️ 全部商品</h3>
        <span class="result-count">共 {{ total }} 件商品</span>
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
import { getProductList, getHotProducts } from '@/api/product'

export default {
  name: 'GoodsList',
  data() {
    return {
      keyword: '', petType: '', category: '', sortBy: '',
      list: [], hotProducts: [], total: 0, currentPage: 1, pageSize: 12,
      allData: [], // 存储所有商品数据，用于前端排序
      loading: false,
      defaultImg: 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200"><rect width="200" height="200" fill="%23f5f0eb"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23ccc" font-size="40">🐾</text></svg>',
    }
  },
  mounted() {
    const q = this.$route.query
    if (q.keyword) this.keyword = q.keyword
    if (q.pet_type) this.petType = q.pet_type
    if (q.category) this.category = q.category
    this.fetchHot()
    this.fetchList()
  },
  methods: {
    // 成员A：负责获取热销商品推荐列表并在页面顶部展示
    async fetchHot() {
      try {
        const res = await getHotProducts()
        this.hotProducts = res.data || []
      } catch (e) { console.error(e) }
    },
    // 成员A：负责实现商品列表的获取、前端分页与多条件排序功能
    async fetchList() {
      this.loading = true
      try {
        // 获取所有商品（不分页，一次性获取全部）
        const params = { page: 1, pageSize: 999 }
        if (this.keyword) params.keyword = this.keyword
        if (this.petType) params.pet_type = this.petType
        if (this.category) params.category = this.category
        // 注意：这里不传 sort 参数
        const res = await getProductList(params)
        let data = res.data.list || []

        // ===== 前端排序 =====
        if (this.sortBy) {
          switch (this.sortBy) {
            case 'price_asc':
              data.sort((a, b) => a.price - b.price)
              break
            case 'price_desc':
              data.sort((a, b) => b.price - a.price)
              break
            case 'sales_desc':
              data.sort((a, b) => (b.sales_count || 0) - (a.sales_count || 0))
              break
            default:
              break
          }
        }

        // 前端分页
        this.total = data.length
        const start = (this.currentPage - 1) * this.pageSize
        const end = start + this.pageSize
        this.list = data.slice(start, end)
        this.allData = data // 保存全部数据

      } catch (e) { console.error(e) }
      this.loading = false
    },
    // 成员A：负责实现商品卡片点击跳转到商品详情页的功能
    goDetail(id) {
      this.$router.push(`/goods/${id}`)
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
    },
    // 监听排序变化，重新获取数据
    sortBy() {
      this.currentPage = 1
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.container { max-width: 1200px; margin: 0 auto; padding: 24px; }

.banner-section { margin-bottom: 24px; }
.banner-item {
  height: 100%;
  border-radius: 14px;
  padding: 30px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
}
.banner-content h2 { font-size: 24px; margin-bottom: 8px; }
.banner-content p { font-size: 16px; opacity: 0.9; }
.banner-icon { font-size: 64px; }

.filter-bar { display: flex; gap: 16px; margin-bottom: 24px; align-items: center; flex-wrap: wrap; }

.hot-section { margin-bottom: 32px; }
.hot-scroll { overflow-x: auto; padding-bottom: 8px; }
.hot-scroll::-webkit-scrollbar { height: 6px; }
.hot-scroll::-webkit-scrollbar-thumb { background: #ffd4a0; border-radius: 3px; }
.hot-track { display: flex; gap: 16px; min-width: max-content; }

.product-card-h {
  width: 200px; flex-shrink: 0; background: #fff; border-radius: 14px;
  overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.06);
  cursor: pointer; transition: transform 0.25s, box-shadow 0.25s;
}
.product-card-h:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(255,159,67,0.18); }

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}
.section-title { font-size: 20px; color: #333; font-weight: 700; }
.result-count { font-size: 14px; color: #aaa; }

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.product-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
}
.product-card:hover { transform: translateY(-5px); box-shadow: 0 8px 24px rgba(255,159,67,0.18); }
.card-img { height: 180px; background: #f5f0eb; overflow: hidden; }
.card-img img { width: 100%; height: 100%; object-fit: cover; }
.card-body { padding: 14px 16px; }
.card-name { font-size: 15px; color: #333; font-weight: 600; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-meta { display: flex; gap: 6px; margin-bottom: 8px; flex-wrap: wrap; }
.card-price { color: #ff6b6b; font-size: 20px; font-weight: 700; }
.card-sales { font-size: 12px; color: #aaa; margin-top: 2px; }

.empty { text-align: center; padding: 60px 0; color: #aaa; font-size: 16px; }
.pagination { text-align: center; margin-top: 32px; }

@media (max-width: 992px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 576px) {
  .product-grid { grid-template-columns: 1fr; }
  .banner-item { flex-direction: column; text-align: center; padding: 20px; }
  .banner-icon { font-size: 40px; margin-top: 10px; }
}
</style>