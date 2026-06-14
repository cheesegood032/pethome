<template>
  <div class="foster-page">
    <div class="container">
      <h2 class="page-title">🏡 寄养预约</h2>
      <el-steps :active="currentStep" align-center finish-status="success" class="steps">
        <el-step title="选择宠物"></el-step>
        <el-step title="选择套餐"></el-step>
        <el-step title="选择日期"></el-step>
      </el-steps>

      <div class="step-content">
        <!-- 步骤一：选择宠物 -->
        <div v-show="currentStep === 0" class="step-panel">
          <div v-if="pets.length === 0" class="empty-tip">
            <p>您还没有添加宠物档案</p>
            <el-button type="primary" size="small" @click="$router.push('/profile')">去添加宠物</el-button>
          </div>
          <el-checkbox-group v-model="selectedPetIds" v-else>
            <div class="pet-grid">
              <div class="pet-card" v-for="pet in pets" :key="pet.id">
                <el-checkbox :label="pet.id">
                  <div class="pet-info">
                    <span class="pet-icon">{{ pet.species === '猫' ? '🐱' : '🐶' }}</span>
                    <div>
                      <div class="pet-name">{{ pet.name }}</div>
                      <div class="pet-detail">{{ pet.breed }} · {{ pet.age }} · {{ pet.weight }}kg</div>
                    </div>
                  </div>
                </el-checkbox>
              </div>
            </div>
          </el-checkbox-group>
        </div>

        <!-- 步骤二：选择套餐 -->
        <div v-show="currentStep === 1" class="step-panel">
          <el-radio-group v-model="selectedPackageId">
            <div class="package-grid">
              <div class="package-card" v-for="pkg in packages" :key="pkg.id">
                <el-radio :label="pkg.id">
                  <div class="pkg-info">
                    <div class="pkg-name">{{ pkg.name }}</div>
                    <div class="pkg-desc">{{ pkg.description }}</div>
                    <div class="pkg-price">¥{{ pkg.price_per_day }}/天</div>
                    <div class="pkg-services" v-if="pkg.services">
                      <el-tag size="mini" v-for="s in pkg.services.split(',').filter(x=>x)" :key="s" type="info" style="margin:2px">{{ s }}</el-tag>
                    </div>
                  </div>
                </el-radio>
              </div>
            </div>
          </el-radio-group>
        </div>

        <!-- 步骤三：选择日期 -->
        <div v-show="currentStep === 2" class="step-panel">
          <div class="date-section">
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
              start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd"
              :picker-options="pickerOptions">
            </el-date-picker>
          </div>
          <el-input v-model="remark" type="textarea" :rows="3" placeholder="备注（可选）" style="max-width:500px;margin-top:16px"></el-input>
        </div>
      </div>

      <!-- 右侧价格计算 & 操作按钮 -->
      <div class="bottom-bar">
        <div class="price-summary" v-if="currentStep === 2 && totalPrice > 0">
          <span>{{ selectedPetIds.length }}只宠物 × {{ totalDays }}天 × ¥{{ selectedPackage ? selectedPackage.price_per_day : 0 }}/天</span>
          <span class="total-price">= ¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <div class="btn-group">
          <el-button v-if="currentStep > 0" @click="currentStep--">上一步</el-button>
          <el-button v-if="currentStep < 2" type="primary" @click="nextStep">下一步</el-button>
          <el-button v-if="currentStep === 2" type="warning" :loading="submitting" @click="submitFoster">提交预约</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getPetList } from '@/api/pet'
import { getPackageList, createFosterOrder } from '@/api/foster'

export default {
  name: 'Foster',
  data() {
    return {
      currentStep: 0,
      pets: [], packages: [],
      selectedPetIds: [], selectedPackageId: null,
      dateRange: null, remark: '',
      submitting: false,
      pickerOptions: { disabledDate: date => date < new Date(Date.now() - 86400000) }
    }
  },
  computed: {
    selectedPackage() {
      return this.packages.find(p => p.id === this.selectedPackageId) || null
    },
    totalDays() {
      if (!this.dateRange || this.dateRange.length < 2) return 0
      const d1 = new Date(this.dateRange[0])
      const d2 = new Date(this.dateRange[1])
      return Math.max(1, Math.round((d2 - d1) / 86400000))
    },
    totalPrice() {
      if (!this.selectedPackage) return 0
      return this.selectedPackage.price_per_day * this.totalDays * this.selectedPetIds.length
    }
  },
  mounted() {
    this.fetchPets()
    this.fetchPackages()
  },
  methods: {
    async fetchPets() {
      try { const res = await getPetList(); this.pets = res.data || [] } catch (e) { console.error(e) }
    },
    async fetchPackages() {
      try { const res = await getPackageList(); this.packages = res.data || [] } catch (e) { console.error(e) }
    },
    nextStep() {
      if (this.currentStep === 0 && this.selectedPetIds.length === 0) {
        this.$message.warning('请至少选择一只宠物'); return
      }
      if (this.currentStep === 1 && !this.selectedPackageId) {
        this.$message.warning('请选择寄养套餐'); return
      }
      this.currentStep++
    },
    async submitFoster() {
      if (!this.dateRange || this.dateRange.length < 2) {
        this.$message.warning('请选择日期范围'); return
      }
      this.submitting = true
      try {
        await createFosterOrder({
          pet_ids: this.selectedPetIds,
          package_id: this.selectedPackageId,
          start_date: this.dateRange[0],
          end_date: this.dateRange[1],
          total_days: this.totalDays,
          total_price: this.totalPrice,
          remark: this.remark
        })
        this.$message.success('预约提交成功！')
        this.$router.push('/foster/order')
      } catch (e) { console.error(e) }
      this.submitting = false
    }
  }
}
</script>

<style scoped>
.container { max-width: 1000px; margin: 0 auto; padding: 24px; }
.page-title { font-size: 22px; color: #333; margin-bottom: 20px; }
.steps { margin-bottom: 32px; }
.step-panel { min-height: 200px; }
.empty-tip { text-align: center; padding: 40px; color: #aaa; }
.pet-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.pet-card { background: #fff; border-radius: 12px; padding: 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.pet-info { display: flex; align-items: center; gap: 12px; }
.pet-icon { font-size: 32px; }
.pet-name { font-weight: 600; color: #333; }
.pet-detail { font-size: 13px; color: #999; }
.package-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.package-card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.pkg-info { padding-left: 8px; }
.pkg-name { font-size: 16px; font-weight: 600; color: #333; margin-bottom: 6px; }
.pkg-desc { font-size: 13px; color: #999; margin-bottom: 8px; }
.pkg-price { font-size: 20px; color: #ff6b6b; font-weight: 700; margin-bottom: 8px; }
.date-section { margin-bottom: 16px; }
.bottom-bar { margin-top: 32px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px; }
.price-summary { font-size: 14px; color: #666; }
.total-price { font-size: 24px; color: #ff6b6b; font-weight: 700; margin-left: 8px; }
.btn-group { display: flex; gap: 12px; }
</style>
