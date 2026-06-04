<template>
  <div class="personal-page">
    <div class="container" v-loading="loading">
      <div v-if="!user" class="need-login">
        <p>请先登录</p>
        <router-link to="/login" class="el-button">去登录</router-link>
      </div>

      <div v-else>
        <div class="personal-card">
          <div class="user-info">
            <div class="avatar">
              <span class="avatar-icon">🐾</span>
            </div>
            <div class="info">
              <h2>{{ user.username }}</h2>
              <p>{{ user.phone || '暂无手机号' }}</p>
            </div>
          </div>
        </div>

        <div class="nav-card">
          <div class="nav-item" @click="goToPets">
            <span class="nav-icon">🐱</span>
            <span>宠物档案</span>
          </div>
          <div class="nav-item" @click="goToOrders">
            <span class="nav-icon">📦</span>
            <span>用品订单</span>
          </div>
          <div class="nav-item" @click="goToFoster">
            <span class="nav-icon">🏠</span>
            <span>寄养预约</span>
          </div>
        </div>

        <div class="form-card">
          <h3>修改信息</h3>
          <el-form :model="form" label-width="80px">
            <el-form-item label="手机号">
              <el-input v-model="form.phone"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email"></el-input>
            </el-form-item>
            <el-form-item label="收货地址">
              <el-input v-model="form.address"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdate">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="form-card">
          <h3>修改密码</h3>
          <el-form :model="pwdForm" label-width="80px">
            <el-form-item label="原密码">
              <el-input v-model="pwdForm.oldPassword" type="password"></el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { updateUser, changePassword } from '@/api/user'
import { Message } from 'element-ui'

export default {
  name: 'Personal',
  data() {
    return {
      loading: false,
      form: {
        phone: '',
        email: '',
        address: ''
      },
      pwdForm: {
        oldPassword: '',
        newPassword: ''
      }
    }
  },
  computed: {
    ...mapState(['user'])
  },
  mounted() {
    if (this.user) {
      this.form.phone = this.user.phone || ''
      this.form.email = this.user.email || ''
      this.form.address = this.user.address || ''
    }
  },
  methods: {
    goToPets() {
      this.$router.push('/pets')
    },
    goToOrders() {
      this.$router.push('/orders')
    },
    goToFoster() {
      this.$router.push('/foster')
    },
    async handleUpdate() {
      try {
        await updateUser(this.form)
        Message.success('修改成功')
      } catch (e) {
        console.error(e)
      }
    },
    async handleChangePassword() {
      if (!this.pwdForm.oldPassword || !this.pwdForm.newPassword) {
        Message.error('请填写完整')
        return
      }
      try {
        await changePassword(this.pwdForm)
        Message.success('密码修改成功')
        this.pwdForm = { oldPassword: '', newPassword: '' }
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
.personal-page {
  background: #faf8f5;
  min-height: calc(100vh - 140px);
  padding: 30px 0;
}

.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 20px;
}

.need-login {
  text-align: center;
  padding: 80px 0;
}

.need-login p {
  margin-bottom: 20px;
  color: #999;
}

.personal-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #ff9f43 0%, #ffb366 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-icon {
  font-size: 40px;
}

.info h2 {
  font-size: 22px;
  margin-bottom: 4px;
}

.info p {
  color: #999;
}

.nav-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  display: flex;
  gap: 20px;
}

.nav-item {
  flex: 1;
  text-align: center;
  padding: 20px;
  background: #faf8f5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.nav-item:hover {
  background: #ffeaa7;
}

.nav-icon {
  display: block;
  font-size: 32px;
  margin-bottom: 8px;
}

.form-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
}

.form-card h3 {
  font-size: 16px;
  margin-bottom: 20px;
}
</style>
