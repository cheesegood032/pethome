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
      <el-table-column prop="pet_type" label="适用宠物"></el-table-column>
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

    <!-- 商品表单弹窗 -->
    <el-dialog :title="form.id ? '编辑商品' : '添加商品'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" ref="productForm" label-width="80px">
        <el-form-item label="商品名称" prop="name" :rules="[{ required: true, message: '请输入商品名称' }]">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="主粮零食" value="主粮零食"></el-option>
            <el-option label="宠物窝笼" value="宠物窝笼"></el-option>
            <el-option label="洗护用品" value="洗护用品"></el-option>
            <el-option label="玩具" value="玩具"></el-option>
            <el-option label="服饰" value="服饰"></el-option>
            <el-option label="外出用品" value="外出用品"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="适用宠物" prop="pet_type">
          <el-select v-model="form.pet_type" placeholder="请选择">
            <el-option label="猫" value="猫"></el-option>
            <el-option label="狗" value="狗"></el-option>
            <el-option label="通用" value="通用"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :precision="2" :step="1" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="1"></el-input-number>
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="form.spec"></el-input>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input type="textarea" v-model="form.description"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAllProducts, deleteProduct, updateProduct, addProduct } from '@/api/product'
import { Message } from 'element-ui'

export default {
  name: 'AdminProducts',
  data() {
    return {
      products: [],
      searchKeyword: '',
      dialogVisible: false,
      form: {
        id: null,
        name: '',
        category: '',
        pet_type: '',
        price: 0,
        stock: 0,
        spec: '',
        description: ''
      }
    }
  },
  mounted() {
    this.fetchProducts()
  },
  methods: {
    async fetchProducts() {
      try {
        const params = {}
        if (this.searchKeyword) params.keyword = this.searchKeyword
        const res = await getAllProducts(params)
        this.products = res.data.list || res.data
      } catch (e) {
        console.error(e)
      }
    },
    handleSearch() {
      this.fetchProducts()
    },
    handleAdd() {
      this.form = { id: null, name: '', category: '', pet_type: '', price: 0, stock: 0, spec: '', description: '' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogVisible = true
    },
    submitForm() {
      this.$refs.productForm.validate(async valid => {
        if (!valid) return
        try {
          if (this.form.id) {
            await updateProduct(this.form)
            Message.success('更新成功')
          } else {
            await addProduct(this.form)
            Message.success('添加成功')
          }
          this.dialogVisible = false
          this.fetchProducts()
        } catch (e) {
          console.error(e)
        }
      })
    },
    async handleToggleStatus(row) {
      try {
        await updateProduct({ id: row.id, status: row.status === 1 ? 0 : 1 })
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
