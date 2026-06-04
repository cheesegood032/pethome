<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <h2>登录宠物之家</h2>
        <p>欢迎回来！</p>
      </div>
      <el-form :model="form" :rules="rules" ref="form" @keyup.enter.native="handleSubmit">
        <el-form-item prop="loginName">
          <el-input v-model="form.loginName" placeholder="用户名/手机号" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="el-icon-lock"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="btn-submit" :loading="loading" @click="handleSubmit">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
      <div class="admin-link">
        <router-link to="/admin/login">管理员登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import { login } from '@/api/user'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        loginName: '',
        password: ''
      },
      rules: {
        loginName: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    ...mapActions(['login']),
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return
        this.loading = true
        try {
          const res = await login(this.form)
          this.login({ user: res.data.user, token: res.data.token })
          this.$message.success('登录成功')
          this.$router.push('/')
        } catch (e) {
          console.error(e)
        }
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff9f43 0%, #ffb366 100%);
}

.login-card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  width: 400px;
  box-shadow: 0 10px 40px rgba(255, 159, 67, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  color: #999;
  font-size: 14px;
}

.btn-submit {
  width: 100%;
  height: 45px;
  font-size: 16px;
  background: linear-gradient(135deg, #ff9f43 0%, #ff8c1a 100%);
  border: none;
}

.btn-submit:hover {
  background: linear-gradient(135deg, #ff8c1a 0%, #ff7a00 100%);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 14px;
}

.login-footer a {
  color: #ff9f43;
  margin-left: 4px;
}

.admin-link {
  text-align: center;
  margin-top: 16px;
}

.admin-link a {
  color: #999;
  font-size: 13px;
}
</style>
