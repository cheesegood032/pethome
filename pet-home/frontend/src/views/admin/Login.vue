<template>
  <div class="admin-login-page">
    <div class="login-card">
      <div class="login-header">
        <h2>管理员登录</h2>
        <p>欢迎回来，管理员！</p>
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
        <router-link to="/login">返回用户登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/admin'

export default {
  name: 'AdminLogin',
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
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return
        this.loading = true
        try {
          await login(this.form)
          this.$message.success('登录成功')
          this.$router.push('/admin')
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
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  width: 400px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
}

.login-footer a {
  color: #667eea;
  font-size: 14px;
}
</style>
