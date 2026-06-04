<template>
  <div class="home">
    <div class="container">
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索商品..." @keyup.enter.native="handleSearch">
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
      </div>

      <div class="filter-bar">
        <el-select v-model="filterCategory" placeholder="商品分类" clearable>
          <el-option label="全部" value=""></el-option>
          <el-option label="狗粮" value="狗粮"></el-option>
          <el-option label="猫粮" value="猫粮"></el-option>
          <el-option label="餐具水具" value="餐具水具"></el-option>
          <el-option label="家居笼具" value="家居笼具"></el-option>
          <el-option label="外出用品" value="外出用品"></el-option>
          <el-option label="窝垫床品" value="窝垫床品"></el-option>
          <el-option label="玩具" value="玩具"></el-option>
          <el-option label="清洁护理" value="清洁护理"></el-option>
        </el-select>
        <el-select v-model="filterPetType" placeholder="适用宠物" clearable>
          <el-option label="全部" value=""></el-option>
          <el-option label="猫" value="猫"></el-option>
          <el-option label="狗" value="狗"></el-option>
          <el-option label="通用" value="通用"></el-option>
        </el-select>
      </div>

      <div class="product-grid" v-loading="loading">
        <div class="product-card" v-for="product in products" :key="product.id" @click="goDetail(product.id)">
          <div class="product-image">
            <img :src="product.image || '/assets/images/product-default.jpg'" :alt="product.name">
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <div class="product-meta">
              <span class="product-category">{{ product.category }}</span>
              <span class="product-pet-type">{{ product.petType }}</span>
            </div>
            <div class="product-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price }}</span>
            </div>
            <div class="product-stock">库存: {{ product.stock }}</div>
          </div>
        </div>
      </div>

      <div v-if="!loading && products.length === 0" class="empty">
        <p>暂无商品</p>
      </div>

      <el-pagination
        v-if="total > 0"
        class="pagination"
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { getProductList, searchProducts } from '@/api/product'

export default {
  name: 'Home',
  data() {
    return {
      products: [],
      loading: false,
      searchKeyword: '',
      filterCategory: '',
      filterPetType: '',
      currentPage: 1,
      pageSize: 12,
      total: 0
    }
  },
  mounted() {
    this.fetchProducts()
  },
  methods: {
    async fetchProducts() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          limit: this.pageSize
        }
        if (this.searchKeyword) params.keyword = this.searchKeyword
        if (this.filterCategory) params.category = this.filterCategory
        if (this.filterPetType) params.petType = this.filterPetType

        const res = await getProductList(params)
        this.products = res.data.list
        this.total = res.data.total
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    handleSearch() {
      this.currentPage = 1
      this.fetchProducts()
    },
    handlePageChange(page) {
      this.currentPage = page
      this.fetchProducts()
    },
    goDetail(id) {
      this.$router.push(`/product/${id}`)
    }
  },
  watch: {
    filterCategory() {
      this.currentPage = 1
      this.fetchProducts()
    },
    filterPetType() {
      this.currentPage = 1
      this.fetchProducts()
    }
  }
}
</script>

<style scoped>
.home {
  background: #faf8f5;
  min-height: calc(100vh - 140px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-bar {
  padding: 24px 0;
}

.search-bar >>> .el-input__inner {
  border-radius: 25px;
  height: 45px;
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.filter-bar >>> .el-select {
  width: 150px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(255, 159, 67, 0.2);
}

.product-image {
  height: 180px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 15px;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.product-category, .product-pet-type {
  font-size: 12px;
  color: #999;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
}

.product-price {
  color: #ff6b6b;
  margin-bottom: 4px;
}

.price-symbol {
  font-size: 14px;
}

.price-value {
  font-size: 22px;
  font-weight: bold;
}

.product-stock {
  font-size: 12px;
  color: #999;
}

.empty {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.pagination {
  text-align: center;
  margin-top: 30px;
  padding-bottom: 30px;
}
</style>
