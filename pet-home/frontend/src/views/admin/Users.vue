<template>
  <div class="admin-users">
    <div class="toolbar">
      <el-input v-model="searchKeyword" placeholder="搜索用户名/手机号" @keyup.enter.native="handleSearch" class="search-input"></el-input>
      <el-button type="primary" @click="handleAdd">添加用户</el-button>
    </div>

    <el-table :data="users" border class="user-table">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column prop="phone" label="手机号"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="address" label="地址"></el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180">
        <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getAllUsers, deleteUser } from '@/api/user'
import { Message } from 'element-ui'

export default {
  name: 'AdminUsers',
  data() {
    return {
      users: [],
      searchKeyword: ''
    }
  },
  mounted() {
    this.fetchUsers()
  },
  methods: {
    async fetchUsers() {
      try {
        const res = await getAllUsers()
        this.users = res.data
      } catch (e) {
        console.error(e)
      }
    },
    handleSearch() {
      this.fetchUsers()
    },
    handleAdd() {
      this.$message.info('添加用户功能')
    },
    handleEdit(row) {
      this.$message.info('编辑用户: ' + row.username)
    },
    async handleDelete(id) {
      this.$confirm('确定删除该用户吗？', '提示').then(async () => {
        try {
          await deleteUser(id)
          Message.success('删除成功')
          this.fetchUsers()
        } catch (e) {
          console.error(e)
        }
      })
    },
    formatTime(timeStr) {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.admin-users {
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
