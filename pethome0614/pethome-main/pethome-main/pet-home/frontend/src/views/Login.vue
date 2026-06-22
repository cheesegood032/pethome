<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-big">🐾</div>
        <h2>欢迎回到宠物之家</h2>
        <p>登录后享受更多服务</p>
      </div>
      <el-form :model="form" :rules="rules" ref="loginForm" @keyup.enter.native="handleSubmit">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名 / 手机号" prefix-icon="el-icon-user" clearable></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="btn-submit" :loading="loading" @click="handleSubmit">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
      <div class="admin-link">
        <router-link to="/admin/login">管理员入口 →</router-link>
      </div>
    </div>
  </div>
</template>
<script>
import { login as loginApi } from '@/api/user'
import { mapActions } from 'vuex'
export default {
  name: 'Login',
  data() {
    return {
      form: { username: '', password: '' },
      rules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    ...mapActions(['login']),
    handleSubmit() {
      this.$refs.loginForm.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await loginApi(this.form)
          this.login(res.data)
          this.$message.success('登录成功，欢迎回来！')
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
  /* 替换背景图路径 public/images/login_bg.jpg */
  background-image: url("/images/login_bg.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}
.login-card {
  background: #fff;
  padding: 48px 40px 36px;
  border-radius: 20px;
  width: 420px;
  box-shadow: 0 16px 48px rgba(0,0,0,0.25);
}
.login-header { text-align: center; margin-bottom: 32px; }
.logo-big { font-size: 48px; margin-bottom: 12px; }
.login-header h2 { font-size: 24px; color: #333; margin-bottom: 6px; }
.login-header p { color: #aaa; font-size: 14px; }
.btn-submit {
  width: 100%;
  height: 46px;
  font-size: 16px;
  background: linear-gradient(135deg, #ff9f43, #ff8c1a);
  border: none;
  border-radius: 23px;
  letter-spacing: 4px;
}
.btn-submit:hover { background: linear-gradient(135deg, #ff8c1a, #ff7a00); }
.login-footer { text-align: center; margin-top: 20px; color: #999; font-size: 14px; }
.login-footer a { color: #ff9f43; margin-left: 4px; font-weight: 600; }
.admin-link { text-align: center; margin-top: 16px; }
.admin-link a { color: #bbb; font-size: 13px; }
.admin-link a:hover { color: #ff9f43; }
</style>