<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <div class="logo-big">🐶🐱</div>
        <h2>加入宠物之家</h2>
        <p>创建你的专属账户</p>
      </div>
      <el-form :model="form" :rules="rules" ref="regForm">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="el-icon-lock" show-password></el-input>
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" prefix-icon="el-icon-phone"></el-input>
        </el-form-item>
        <el-form-item prop="address">
          <el-input v-model="form.address" placeholder="收货地址（选填）" prefix-icon="el-icon-location"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="btn-submit" :loading="loading" @click="handleSubmit">注 册</el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/user'

export default {
  name: 'Register',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.form.password) callback(new Error('两次输入密码不一致'))
      else callback()
    }
    const validatePhone = (rule, value, callback) => {
      if (!/^1[3-9]\d{9}$/.test(value)) callback(new Error('手机号格式不正确'))
      else callback()
    }
    return {
      form: { username: '', password: '', confirmPassword: '', phone: '', address: '' },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度2-20个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirm, trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { validator: validatePhone, trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleSubmit() {
      this.$refs.regForm.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          await register({
            username: this.form.username,
            password: this.form.password,
            phone: this.form.phone,
            address: this.form.address
          })
          this.$message.success('注册成功，请登录')
          this.$router.push('/login')
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
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff9f43 0%, #ffb366 40%, #ffd4a0 100%);
}
.register-card {
  background: #fff;
  padding: 44px 40px 32px;
  border-radius: 20px;
  width: 440px;
  box-shadow: 0 16px 48px rgba(255,159,67,0.25);
}
.register-header { text-align: center; margin-bottom: 28px; }
.logo-big { font-size: 42px; margin-bottom: 10px; }
.register-header h2 { font-size: 22px; color: #333; margin-bottom: 6px; }
.register-header p { color: #aaa; font-size: 14px; }
.btn-submit {
  width: 100%; height: 46px; font-size: 16px;
  background: linear-gradient(135deg, #ff9f43, #ff8c1a);
  border: none; border-radius: 23px; letter-spacing: 4px;
}
.btn-submit:hover { background: linear-gradient(135deg, #ff8c1a, #ff7a00); }
.register-footer { text-align: center; margin-top: 18px; color: #999; font-size: 14px; }
.register-footer a { color: #ff9f43; margin-left: 4px; font-weight: 600; }
</style>
