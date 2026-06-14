<template>
  <div class="admin-products">
    <div class="toolbar">
      <el-input v-model="searchKeyword" placeholder="搜索商品" @keyup.enter.native="handleSearch" class="search-input"></el-input>
      <el-button type="primary" @click="handleAdd">添加商品</el-button>
    </div>

    <el-table :data="products" border class="product-table">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="商品名称"></el-table-column>
      <el-table-column prop="category" label="分类"></el-table-column>
      <el-table-column prop="petType" label="适用宠物"></el-table-column>
      <el-table-column prop="price" label="价格" width="100">
        <template slot-scope="scope">¥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" @click="handleToggleStatus(scope.row)">
            {{ scope.row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button type="text" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getAllProducts, deleteProduct, updateProduct } from '@/api/product'
import { Message } from 'element-ui'

export default {
  name: 'AdminProducts',
  data() {
    return {
      products: [],
      searchKeyword: ''
    }
  },
  mounted() {
    this.fetchProducts()
  },
  methods: {
    async fetchProducts() {
      try {
        const res = await getAllProducts()
        this.products = res.data
      } catch (e) {
        console.error(e)
      }
    },
    handleSearch() {
      this.fetchProducts()
    },
    handleAdd() {
      this.$message.info('添加商品功能')
    },
    handleEdit(row) {
      this.$message.info('编辑商品: ' + row.name)
    },
    async handleToggleStatus(row) {
      try {
        await updateProduct({ ...row, status: row.status === 1 ? 0 : 1 })
        Message.success('状态更新成功')
        this.fetchProducts()
      } catch (e) {
        console.error(e)
      }
    },
    async handleDelete(id) {
      this.$confirm('确定删除该商品吗？', '提示').then(async () => {
        try {
          await deleteProduct(id)
          Message.success('删除成功')
          this.fetchProducts()
        } catch (e) {
          console.error(e)
        }
      })
    }
  }
}
</script>

<style scoped>
.admin-products {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}
</style>
