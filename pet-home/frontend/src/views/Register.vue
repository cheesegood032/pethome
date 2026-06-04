<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <h2>注册宠物之家</h2>
        <p>加入我们，开始购物之旅</p>
      </div>
      <el-form :model="form" :rules="rules" ref="form" @keyup.enter.native="handleSubmit">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" prefix-icon="el-icon-phone"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="el-icon-lock"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="el-icon-lock"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="btn-submit" :loading="loading" @click="handleSubmit">注册</el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/user'
import { Message } from 'element-ui'

export default {
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: {
        username: '',
        phone: '',
        password: '',
        confirmPassword: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
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
          await register(this.form)
          Message.success('注册成功，请登录')
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
  background: linear-gradient(135deg, #ff9f43 0%, #ffb366 100%);
}

.register-card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  width: 400px;
  box-shadow: 0 10px 40px rgba(255, 159, 67, 0.3);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}

.register-header p {
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

.register-footer {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 14px;
}

.register-footer a {
  color: #ff9f43;
  margin-left: 4px;
}
</style>
