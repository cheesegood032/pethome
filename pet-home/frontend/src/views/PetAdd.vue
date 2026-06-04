<template>
  <div class="pet-add-page">
    <div class="container">
      <div class="form-card">
        <h2>添加宠物</h2>
        <el-form :model="form" label-width="80px">
          <el-form-item label="宠物名" required>
            <el-input v-model="form.name"></el-input>
          </el-form-item>
          <el-form-item label="物种" required>
            <el-select v-model="form.species">
              <el-option label="猫" value="猫"></el-option>
              <el-option label="狗" value="狗"></el-option>
              <el-option label="其他" value="其他"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="品种">
            <el-input v-model="form.breed"></el-input>
          </el-form-item>
          <el-form-item label="年龄">
            <el-input v-model="form.age" type="number"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender">
              <el-option label="公" value="公"></el-option>
              <el-option label="母" value="母"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="体重(kg)">
            <el-input v-model="form.weight"></el-input>
          </el-form-item>
          <el-form-item label="健康状况">
            <el-input v-model="form.healthStatus"></el-input>
          </el-form-item>
          <el-form-item label="备注">
            <el-input type="textarea" v-model="form.remark"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit">添加</el-button>
            <el-button @click="goBack">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { addPet } from '@/api/pet'
import { Message } from 'element-ui'

export default {
  name: 'PetAdd',
  data() {
    return {
      form: {
        name: '',
        species: '',
        breed: '',
        age: null,
        gender: '',
        weight: null,
        healthStatus: '',
        remark: ''
      }
    }
  },
  methods: {
    async handleSubmit() {
      if (!this.form.name || !this.form.species) {
        Message.error('请填写必填项')
        return
      }
      try {
        await addPet(this.form)
        Message.success('添加成功')
        this.$router.push('/pets')
      } catch (e) {
        console.error(e)
      }
    },
    goBack() {
      this.$router.push('/pets')
    }
  }
}
</script>

<style scoped>
.pet-add-page {
  background: #faf8f5;
  min-height: calc(100vh - 140px);
  padding: 30px 0;
}

.container {
  max-width: 500px;
  margin: 0 auto;
  padding: 0 20px;
}

.form-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
}

.form-card h2 {
  font-size: 22px;
  margin-bottom: 24px;
}
</style>
