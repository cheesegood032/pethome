<template>
  <div class="profile-page">
    <div class="container">
      <h2 class="page-title">👤 个人中心</h2>
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="info">
          <el-form :model="userForm" label-width="100px" class="info-form">
            <el-form-item label="用户名">
              <el-input :value="userForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userForm.phone"></el-input>
            </el-form-item>
            <el-form-item label="收货地址">
              <el-input v-model="userForm.address" type="textarea" :rows="2"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveInfo" :loading="saving">保存修改</el-button>
            </el-form-item>
          </el-form>
          <el-divider>修改密码</el-divider>
          <el-form :model="pwdForm" label-width="100px" class="info-form">
            <el-form-item label="旧密码">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="warning" @click="changePwd" :loading="changingPwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 我的宠物 -->
        <el-tab-pane label="我的宠物" name="pets">
          <el-button type="primary" size="small" icon="el-icon-plus" @click="openPetDialog(null)" style="margin-bottom:16px">
            添加宠物
          </el-button>
          <el-table :data="petList" style="width:100%" empty-text="还没有添加宠物哦~">
            <el-table-column prop="name" label="名称" width="120"></el-table-column>
            <el-table-column prop="species" label="物种" width="80"></el-table-column>
            <el-table-column prop="breed" label="品种" width="120"></el-table-column>
            <el-table-column prop="age" label="年龄" width="80"></el-table-column>
            <el-table-column prop="weight" label="体重(kg)" width="90"></el-table-column>
            <el-table-column prop="health_status" label="健康状况" width="100"></el-table-column>
            <el-table-column label="操作" width="160">
              <template slot-scope="{ row }">
                <el-button type="text" size="small" @click="openPetDialog(row)">编辑</el-button>
                <el-popconfirm title="确定删除该宠物？" @confirm="deletePet(row)">
                  <el-button slot="reference" type="text" size="small" style="color:#ff4757">删除</el-button>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <!-- 宠物弹窗 -->
      <el-dialog :title="editingPet ? '编辑宠物' : '添加宠物'" :visible.sync="petDialogVisible" width="500px">
        <el-form :model="petForm" label-width="90px">
          <el-form-item label="名称" required>
            <el-input v-model="petForm.name" placeholder="宠物名称"></el-input>
          </el-form-item>
          <el-form-item label="物种">
            <el-select v-model="petForm.species" placeholder="选择物种">
              <el-option label="猫" value="猫"></el-option>
              <el-option label="狗" value="狗"></el-option>
              <el-option label="其他" value="其他"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="品种">
            <el-input v-model="petForm.breed" placeholder="如：金毛、布偶"></el-input>
          </el-form-item>
          <el-form-item label="年龄">
            <el-input v-model="petForm.age" placeholder="如：2岁"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="petForm.gender">
              <el-radio label="公">公</el-radio>
              <el-radio label="母">母</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="体重(kg)">
            <el-input v-model="petForm.weight" placeholder="如：5.5"></el-input>
          </el-form-item>
          <el-form-item label="健康状况">
            <el-input v-model="petForm.health_status" placeholder="如：健康、已绝育"></el-input>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="petForm.remark" type="textarea" :rows="2"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="petDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePet" :loading="petSaving">保存</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { getUserInfo, updateUser, changePassword } from '@/api/user'
import { getPetList, addPet, updatePet, deletePet } from '@/api/pet'
import { mapActions } from 'vuex'

export default {
  name: 'Profile',
  data() {
    return {
      activeTab: 'info',
      userForm: { username: '', phone: '', address: '' },
      pwdForm: { oldPassword: '', newPassword: '' },
      saving: false, changingPwd: false,
      petList: [], petDialogVisible: false, editingPet: null,
      petForm: { name: '', species: '猫', breed: '', age: '', gender: '公', weight: '', health_status: '健康', remark: '' },
      petSaving: false
    }
  },
  mounted() {
    this.fetchUser()
    this.fetchPets()
  },
  methods: {
    ...mapActions(['login']),
    async fetchUser() {
      try {
        const res = await getUserInfo()
        this.userForm = { username: res.data.username, phone: res.data.phone || '', address: res.data.address || '' }
      } catch (e) { console.error(e) }
    },
    async saveInfo() {
      this.saving = true
      try {
        const res = await updateUser({ phone: this.userForm.phone, address: this.userForm.address })
        this.login(res.data)
        this.$message.success('保存成功')
      } catch (e) { console.error(e) }
      this.saving = false
    },
    async changePwd() {
      if (!this.pwdForm.oldPassword || !this.pwdForm.newPassword) {
        this.$message.warning('请填写旧密码和新密码')
        return
      }
      this.changingPwd = true
      try {
        await changePassword(this.pwdForm)
        this.$message.success('密码修改成功')
        this.pwdForm = { oldPassword: '', newPassword: '' }
      } catch (e) { console.error(e) }
      this.changingPwd = false
    },
    async fetchPets() {
      try {
        const res = await getPetList()
        this.petList = res.data || []
      } catch (e) { console.error(e) }
    },
    openPetDialog(pet) {
      this.editingPet = pet
      if (pet) {
        this.petForm = { ...pet }
      } else {
        this.petForm = { name: '', species: '猫', breed: '', age: '', gender: '公', weight: '', health_status: '健康', remark: '' }
      }
      this.petDialogVisible = true
    },
    async savePet() {
      if (!this.petForm.name) { this.$message.warning('请输入宠物名称'); return }
      this.petSaving = true
      try {
        if (this.editingPet) {
          await updatePet({ ...this.petForm, id: this.editingPet.id })
        } else {
          await addPet(this.petForm)
        }
        this.$message.success('保存成功')
        this.petDialogVisible = false
        this.fetchPets()
      } catch (e) { console.error(e) }
      this.petSaving = false
    },
    async deletePet(row) {
      try {
        await deletePet(row.id)
        this.$message.success('已删除')
        this.fetchPets()
      } catch (e) { console.error(e) }
    }
  }
}
</script>

<style scoped>
.container { max-width: 1000px; margin: 0 auto; padding: 24px; }
.page-title { font-size: 22px; color: #333; margin-bottom: 16px; }
.info-form { max-width: 500px; }
</style>
