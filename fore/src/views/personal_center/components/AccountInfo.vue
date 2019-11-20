<template>
  <div>
    <el-form ref="recordForm" label-position="right" label-width="90px" style="margin:0 20px" :model="userTemp" :rules="rules">
      <el-form-item label="账号">
        <el-input v-model="userTemp.account" disabled />
      </el-form-item>
      <el-form-item label="昵称" prop="name">
        <el-input v-model="userTemp.name" />
      </el-form-item>
      <el-form-item label="电话" prop="phone" autocomplete="off">
        <el-input v-model="userTemp.phone" />
      </el-form-item>
      <el-form-item label="新密码" prop="new_password">
        <el-input v-model="userTemp.new_password" placeholder="如需修改密码请在此处填写" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码确认" prop="new_password1">
        <el-input v-model="userTemp.new_password1" placeholder="确认你的新密码" type="password" show-password />
      </el-form-item>
      <el-form-item label="当前密码" prop="password">
        <el-input v-model="userTemp.password" placeholder="修改个人信息需要输入用户密码" type="password" show-password />
      </el-form-item>
      <el-button style="float:right" type="primary" icon="el-icon-check" @click="saveRecord">保存</el-button>
    </el-form>
  </div>
</template>
<script>
import { updateUserInfo } from '@/api/user'
export default {
  name: 'AccountInfo',
  data() {
    var _this = this
    var newPasswordCheck = (rule, val, callback) => {
      var p0 = _this.userTemp.new_password
      var p1 = val
      if (p1 && !p0) callback(new Error('你未输入新密码'))
      else if (p1 !== p0) callback(new Error('两次密码不一致'))
      else callback()
    }
    return {
      showUserInfo: false,
      userTemp: { name: undefined, account: undefined, password: undefined, new_password: undefined, new_password1: undefined, phone: undefined },
      rules: {
        name: [{ required: true, message: '昵称不能为空', trigger: 'blur' }, { min: 2, max: 10, message: '长度为2-10', trigger: 'change' }],
        password: [{ required: true, message: '必须输入密码才可修改账号信息', trigger: 'blur' }],
        new_password: [{ min: 6, max: 16, message: '密码长度为6-16', trigger: 'blur' }],
        new_password1: [{ min: 6, max: 16, message: '密码长度为6-16', trigger: 'blur' }, { validator: newPasswordCheck, trigger: 'blur' }],
        phone: [{ min: 7, max: 11, message: '长度为7-11', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.initUserInfo()
  },
  methods: {
    initUserInfo() {
      this.userTemp.name = this.$store.getters.name
      this.userTemp.account = this.$store.getters.account
      this.userTemp.phone = this.$store.getters.phone
    },
    show() {
      this.showUserInfo = true
    },
    saveRecord() {
      this.$refs.recordForm.validate(valid => {
        if (valid) {
          updateUserInfo(this.userTemp).then(r => {
            this.$message.success('修改成功，重新登录即可生效')
            this.showUserInfo = false
          })
        } else {
          this.$message.error('提交参数错误，请检查')
          return false
        }
      })
    }
  }
}
</script>
