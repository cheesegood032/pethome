<template>
  <div class="home">
    <div class="container">
      <!-- 搜索栏 -->
      <div class="search-section">
        <div class="search-bar">
          <el-input v-model="keyword" placeholder="搜索宠物用品..." class="search-input" @keyup.enter.native="handleSearch">
            <el-select v-model="petType" slot="prepend" placeholder="宠物" clearable style="width:100px">
              <el-option label="全部" value=""></el-option>
              <el-option label="🐱 猫" value="猫"></el-option>
              <el-option label="🐶 狗" value="狗"></el-option>
              <el-option label="通用" value="通用"></el-option>
            </el-select>
            <el-select v-model="category" slot="prepend" placeholder="分类" clearable style="width:120px">
              <el-option label="全部" value=""></el-option>
              <el-option v-for="c in categories" :key="c.name" :label="c.name" :value="c.name"></el-option>
            </el-select>
            <el-button slot="append" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          </el-input>
        </div>
      </div>

      <!-- 分类卡片 -->
      <div class="section">
        <h2 class="section-title">🎯 商品分类</h2>
        <div class="category-grid">
          <div class="category-card" v-for="c in categories" :key="c.name" @click="goCategory(c.name)">
            <span class="category-icon">{{ c.icon }}</span>
            <span class="category-name">{{ c.name }}</span>
          </div>
        </div>
      </div>

      <!-- 热销好物 -->
      <div class="section">
        <h2 class="section-title">🔥 热销好物</h2>
        <div class="hot-scroll" v-loading="hotLoading">
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

      <!-- 新品上架 -->
      <div class="section">
        <h2 class="section-title">✨ 新品上架</h2>
        <div class="product-grid" v-loading="newLoading">
          <div class="product-card" v-for="p in newProducts" :key="p.id" @click="goDetail(p.id)">
            <div class="card-img"><img :src="p.image || defaultImg" :alt="p.name"></div>
            <div class="card-body">
              <div class="card-name">{{ p.name }}</div>
              <div class="card-meta">
                <el-tag size="mini" type="info">{{ p.category }}</el-tag>
                <el-tag size="mini" v-if="p.pet_type">{{ p.pet_type }}</el-tag>
              </div>
              <div class="card-price">¥{{ p.price }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getHotProducts, getNewProducts } from '@/api/product'

export default {
  name: 'Home',
  data() {
    return {
      keyword: '',
      petType: '',
      category: '',
      hotProducts: [],
      newProducts: [],
      hotLoading: false,
      newLoading: false,
      defaultImg: 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200"><rect width="200" height="200" fill="%23f5f0eb"/><text x="50%" y="50%" text-anchor="middle" dy=".3em" fill="%23ccc" font-size="40">🐾</text></svg>',
      categories: [
        { name: '猫粮', icon: '🐱' },
        { name: '狗粮', icon: '🐶' },
        { name: '玩具', icon: '🎾' },
        { name: '清洁护理', icon: '🧴' },
        { name: '餐具水具', icon: '🍽️' }
      ]
    }
  },
  mounted() {
    this.fetchHot()
    this.fetchNew()
  },
  methods: {
    async fetchHot() {
      this.hotLoading = true
      try {
        const res = await getHotProducts()
        this.hotProducts = res.data || []
      } catch (e) { console.error(e) }
      this.hotLoading = false
    },
    async fetchNew() {
      this.newLoading = true
      try {
        const res = await getNewProducts()
        this.newProducts = res.data || []
      } catch (e) { console.error(e) }
      this.newLoading = false
    },
    handleSearch() {
      const query = {}
      if (this.keyword) query.keyword = this.keyword
      if (this.petType) query.pet_type = this.petType
      if (this.category) query.category = this.category
      this.$router.push({ path: '/goods', query })
    },
    goCategory(name) {
      this.$router.push({ path: '/goods', query: { category: name } })
    },
    goDetail(id) {
      this.$router.push(`/goods/${id}`)
    }
  }
}
</script>

<style scoped>
.home { padding-bottom: 40px; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 24px; }

.search-section { padding: 28px 0 16px; }
.search-input >>> .el-input__inner { border-radius: 24px; height: 48px; }

.section { margin-top: 36px; }
.section-title { font-size: 20px; color: #333; margin-bottom: 18px; font-weight: 700; }

.category-grid { display: flex; gap: 16px; }
.category-card {
  flex: 1; display: flex; flex-direction: column; align-items: center;
  gap: 10px; padding: 24px 16px; background: #fff; border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05); cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
}
.category-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(255,159,67,0.18); }
.category-icon { font-size: 36px; }
.category-name { font-size: 14px; color: #555; font-weight: 600; }

.hot-scroll { overflow-x: auto; padding-bottom: 8px; }
.hot-scroll::-webkit-scrollbar { height: 6px; }
.hot-scroll::-webkit-scrollbar-thumb { background: #ffd4a0; border-radius: 3px; }
.hot-track { display: flex; gap: 16px; min-width: max-content; }

.product-card-h {
  width: 220px; flex-shrink: 0; background: #fff; border-radius: 14px;
  overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.06);
  cursor: pointer; transition: transform 0.25s, box-shadow 0.25s;
}
.product-card-h:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(255,159,67,0.18); }

.product-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.product-card {
  background: #fff; border-radius: 14px; overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.06); cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
}
.product-card:hover { transform: translateY(-5px); box-shadow: 0 8px 24px rgba(255,159,67,0.18); }

.card-img { height: 180px; background: #f5f0eb; display: flex; align-items: center; justify-content: center; overflow: hidden; }
.card-img img { width: 100%; height: 100%; object-fit: cover; }
.card-body { padding: 14px 16px; }
.card-name { font-size: 15px; color: #333; font-weight: 600; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-meta { display: flex; gap: 6px; margin-bottom: 8px; }
.card-price { color: #ff6b6b; font-size: 20px; font-weight: 700; }
.card-sales { font-size: 12px; color: #aaa; margin-top: 2px; }
</style>
