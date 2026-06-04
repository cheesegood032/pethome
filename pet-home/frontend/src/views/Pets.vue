<template>
  <div class="pets-page">
    <div class="container" v-loading="loading">
      <div v-if="!user" class="need-login">
        <p>请先登录</p>
        <router-link to="/login" class="el-button">去登录</router-link>
      </div>

      <div v-else>
        <div class="page-header">
          <h2>我的宠物</h2>
          <el-button type="primary" @click="goToAdd">添加宠物</el-button>
        </div>

        <div v-if="pets.length === 0" class="empty-pets">
          <div class="empty-icon">🐱</div>
          <p>还没有添加宠物</p>
          <el-button type="primary" @click="goToAdd">去添加</el-button>
        </div>

        <div class="pets-grid" v-else>
          <div class="pet-card" v-for="pet in pets" :key="pet.id">
            <div class="pet-image">
              <span class="pet-icon">🐾</span>
            </div>
            <div class="pet-info">
              <h3>{{ pet.name }}</h3>
              <div class="pet-meta">
                <span>{{ pet.species }}</span>
                <span>{{ pet.breed }}</span>
              </div>
              <div class="pet-details">
                <span>{{ pet.gender }} · {{ pet.age }}岁</span>
                <span v-if="pet.weight">{{ pet.weight }}kg</span>
              </div>
              <div class="pet-actions">
                <el-button type="text" @click="handleEdit(pet)">编辑</el-button>
                <el-button type="text" @click="handleDelete(pet.id)">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <el-dialog title="编辑宠物" :visible.sync="editDialog" width="400px">
          <el-form :model="editForm" label-width="80px">
            <el-form-item label="宠物名">
              <el-input v-model="editForm.name"></el-input>
            </el-form-item>
            <el-form-item label="物种">
              <el-select v-model="editForm.species">
                <el-option label="猫" value="猫"></el-option>
                <el-option label="狗" value="狗"></el-option>
                <el-option label="其他" value="其他"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="品种">
              <el-input v-model="editForm.breed"></el-input>
            </el-form-item>
            <el-form-item label="年龄">
              <el-input v-model="editForm.age" type="number"></el-input>
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="editForm.gender">
                <el-option label="公" value="公"></el-option>
                <el-option label="母" value="母"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="体重(kg)">
              <el-input v-model="editForm.weight"></el-input>
            </el-form-item>
            <el-form-item label="健康状况">
              <el-input v-model="editForm.healthStatus"></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="editDialog = false">取消</el-button>
            <el-button type="primary" @click="handleSaveEdit">保存</el-button>
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { getPetList, updatePet, deletePet } from '@/api/pet'
import { Message } from 'element-ui'

export default {
  name: 'Pets',
  data() {
    return {
      pets: [],
      loading: false,
      editDialog: false,
      editForm: {
        id: null,
        name: '',
        species: '',
        breed: '',
        age: null,
        gender: '',
        weight: null,
        healthStatus: ''
      }
    }
  },
  computed: {
    ...mapState(['user'])
  },
  mounted() {
    this.fetchPets()
  },
  methods: {
    async fetchPets() {
      if (!this.user) return
      this.loading = true
      try {
        const res = await getPetList()
        this.pets = res.data
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    goToAdd() {
      this.$router.push('/pets/add')
    },
    handleEdit(pet) {
      this.editForm = {
        id: pet.id,
        name: pet.name,
        species: pet.species,
        breed: pet.breed,
        age: pet.age,
        gender: pet.gender,
        weight: pet.weight,
        healthStatus: pet.healthStatus
      }
      this.editDialog = true
    },
    async handleSaveEdit() {
      try {
        await updatePet(this.editForm)
        Message.success('修改成功')
        this.editDialog = false
        this.fetchPets()
      } catch (e) {
        console.error(e)
      }
    },
    async handleDelete(id) {
      this.$confirm('确定删除该宠物吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async () => {
        try {
          await deletePet(id)
          Message.success('删除成功')
          this.fetchPets()
        } catch (e) {
          console.error(e)
        }
      })
    }
  }
}
</script>

<style scoped>
.pets-page {
  background: #faf8f5;
  min-height: calc(100vh - 140px);
  padding: 30px 0;
}

.container {
  max-width: 800px;
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 22px;
}

.empty-pets {
  text-align: center;
  padding: 80px 0;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.empty-pets p {
  margin-bottom: 20px;
  color: #999;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.pet-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
}

.pet-image {
  width: 120px;
  background: linear-gradient(135deg, #ff9f43 0%, #ffb366 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.pet-icon {
  font-size: 48px;
}

.pet-info {
  flex: 1;
  padding: 20px;
}

.pet-info h3 {
  font-size: 18px;
  margin-bottom: 8px;
}

.pet-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.pet-meta span {
  font-size: 12px;
  color: #999;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
}

.pet-details {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #666;
  margin-bottom: 12px;
}

.pet-actions {
  display: flex;
  gap: 16px;
}

.pet-actions .el-button {
  padding: 4px 0;
}
</style>
