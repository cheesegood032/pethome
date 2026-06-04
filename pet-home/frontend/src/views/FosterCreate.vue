<template>
  <div class="foster-create-page">
    <div class="container">
      <div class="form-card">
        <h2>预约寄养</h2>
        <el-form :model="form" label-width="100px">
          <el-form-item label="选择宠物" required>
            <el-select v-model="form.petId" @change="handlePetChange">
              <el-option v-for="pet in pets" :key="pet.id" :label="pet.name" :value="pet.id"></el-option>
            </el-select>
            <el-button type="text" @click="goToAddPet">添加宠物</el-button>
          </el-form-item>
          <el-form-item label="选择套餐" required>
            <el-select v-model="form.packageId" @change="handlePackageChange">
              <el-option v-for="pkg in packages" :key="pkg.id" :label="pkg.name" :value="pkg.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="开始日期" required>
            <el-date-picker v-model="form.startDate" type="date" placeholder="选择开始日期" @change="handleDateChange"></el-date-picker>
          </el-form-item>
          <el-form-item label="结束日期" required>
            <el-date-picker v-model="form.endDate" type="date" placeholder="选择结束日期" :picker-options="endDateOptions" @change="handleDateChange"></el-date-picker>
          </el-form-item>
          <el-form-item label="寄养天数">
            <el-input :value="totalDays" disabled></el-input>
          </el-form-item>
          <el-form-item label="预计费用">
            <el-input :value="'¥' + totalPrice" disabled></el-input>
          </el-form-item>
          <el-form-item label="备注">
            <el-input type="textarea" v-model="form.remark"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit">确认预约</el-button>
            <el-button @click="goBack">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getPetList } from '@/api/pet'
import { getPackageList, createFosterOrder } from '@/api/foster'
import { Message } from 'element-ui'

export default {
  name: 'FosterCreate',
  data() {
    return {
      pets: [],
      packages: [],
      form: {
        petId: null,
        packageId: null,
        startDate: null,
        endDate: null,
        remark: ''
      },
      totalDays: 0,
      totalPrice: 0
    }
  },
  computed: {
    endDateOptions() {
      return {
        disabledDate: (time) => {
          if (!this.form.startDate) return false
          return time.getTime() <= this.form.startDate.getTime()
        }
      }
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        const [petsRes, packagesRes] = await Promise.all([
          getPetList(),
          getPackageList()
        ])
        this.pets = petsRes.data
        this.packages = packagesRes.data
      } catch (e) {
        console.error(e)
      }
    },
    handlePetChange() {
      this.calculatePrice()
    },
    handlePackageChange() {
      this.calculatePrice()
    },
    handleDateChange() {
      this.calculatePrice()
    },
    calculatePrice() {
      if (!this.form.startDate || !this.form.endDate) {
        this.totalDays = 0
        this.totalPrice = 0
        return
      }

      const start = new Date(this.form.startDate)
      const end = new Date(this.form.endDate)
      const diff = end.getTime() - start.getTime()
      this.totalDays = Math.floor(diff / (1000 * 60 * 60 * 24)) + 1

      const pkg = this.packages.find(p => p.id === this.form.packageId)
      if (pkg) {
        this.totalPrice = (pkg.pricePerDay * this.totalDays).toFixed(2)
      }
    },
    goToAddPet() {
      this.$router.push('/pets/add')
    },
    async handleSubmit() {
      if (!this.form.petId || !this.form.packageId || !this.form.startDate || !this.form.endDate) {
        Message.error('请填写完整信息')
        return
      }
      try {
        const data = {
          petId: this.form.petId,
          packageId: this.form.packageId,
          startDate: this.formatDate(this.form.startDate),
          endDate: this.formatDate(this.form.endDate),
          remark: this.form.remark
        }
        await createFosterOrder(data)
        Message.success('预约成功，请等待审核')
        this.$router.push('/foster')
      } catch (e) {
        console.error(e)
      }
    },
    formatDate(date) {
      if (!date) return ''
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    goBack() {
      this.$router.push('/foster')
    }
  }
}
</script>

<style scoped>
.foster-create-page {
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
