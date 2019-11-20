<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="queryCond.name" placeholder="名称" class="filter-item" @keyup.enter.native="query" />
      <el-input v-model="queryCond.account" placeholder="账号" class="filter-item" @keyup.enter.native="query" />
      <el-select v-model="queryCond.roles" placeholder="角色" class="filter-item" clearable multiple collapse-tags @change="query">
        <el-option v-for="role in allRoles" :key="role.id" :label="role.name_show" :value="role.id" />
      </el-select>
      <el-select v-model="queryCond.status" placeholder="状态" class="filter-item" clearable @change="query">
        <el-option v-show="queryCond.status||queryCond.status===0" key="" label="" value="" />
        <el-option v-for="(text,index) in status" :key="index" :label="text" :value="index" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="query">
        查询
      </el-button>
      <el-button v-if="permissions.button_add" class="filter-item" type="success" icon="el-icon-plus" @click="newRecord">
        新增
      </el-button>
    </div>
    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column type="index" label="序号" align="center" width="60" />
      <el-table-column :formatter="r=>r.name" label="昵称" align="center" min-width="140" />
      <el-table-column :formatter="r=>r.account" label="账号" align="center" min-width="140" />
      <el-table-column label="角色" min-width="200" align="center">
        <template slot-scope="{row}">
          <el-tag v-for="r in row.roles" :key="r" size="mini" style="margin:2px">{{ getRoleNameById(r) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column :formatter="r=>r.phone||'未设置'" label="联系方式" align="center" width="140" />
      <el-table-column :formatter="r=>dateFormat(r.last_login)" label="上次登录" align="center" width="170" />
      <el-table-column label="状态" align="center" width="70">
        <template slot-scope="{row}">
          <el-tag :type="statusType[row.status]||''" size="mini">{{ status[row.status]||'未知' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column width="360" label="操作" align="center">
        <template slot-scope="{row}">
          <el-button v-if="row.status===1&&permissions.button_role_setting" type="primary" size="mini" @click="updateRoles(row)">角色配置</el-button>
          <el-button v-if="row.status===1&&permissions.button_forbidden" type="warning" size="mini" @click="forbidden(row.account)">禁用</el-button>
          <el-button v-if="row.status===2&&permissions.button_lift" type="success" size="mini" @click="unforbidden(row.account)">解禁</el-button>
          <el-button v-if="row.status===1&&permissions.button_password_reset" type="danger" size="mini" @click="resetPsw(row.account)">重置密码</el-button>
          <el-button v-if="row.status!==0&&permissions.button_dimission" type="info" size="mini" @click="invalid(row.account)">离职</el-button>
        </template>
      </el-table-column>
    </el-table>

    <nav style="text-align: center; padding-top: 30px">
      <el-pagination
        :page-sizes="[10,20,30,50]"
        :total.sync="total"
        :current-page.sync="page.num"
        :page-size.sync="page.limit"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="query"
        @current-change="query"
      />
    </nav>

    <el-dialog :title="editStatus==='create'?'新增用户':('角色配置：'+temp.account)" :visible.sync="showDialog">
      <el-form ref="userForm" :model="temp" style="width:100%;" label-position="right" :rules="userRules" label-width="80px">
        <el-row v-if="editStatus==='create'">
          <el-col :span="12">
            <el-form-item label="账号" prop="account">
              <el-input v-model="temp.account" :disabled="editStatus!=='create'" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="初始密码">
              <el-input v-model="temp.password" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="editStatus==='create'">
          <el-col :span="12">
            <el-form-item label="昵称" prop="name">
              <el-input v-model="temp.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式">
              <el-input v-model="temp.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="用户角色" prop="roles">
          <el-select v-model="temp.roles" multiple clearable style="width:340px" placeholder="请选择用户角色">
            <el-option v-for="role in allRoles" :key="role.id" :label="role.name_show" :value="role.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button icon="el-icon-close" @click="showDialog=false">
          取消
        </el-button>
        <el-button type="primary" icon="el-icon-check" @click="saveUser()">
          保存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { queryAllRoles } from '@/api/system/role'
import { queryUser, forbiddenUser, unforbiddenUser, invalidUser, resetUserPsw, createUser, updateUserRoles, getUserByAccount } from '@/api/user'
import { parseTime } from '@/utils'
import { MessageBox } from 'element-ui'

export default {
  name: 'UserManagement',
  data() {
    var accountCheck = (rule, value, callback) => {
      getUserByAccount(value).then(r => {
        if (!/^[a-zA-Z][0-9a-zA-Z]*$/g.test(value)) {
          callback(new Error('账号由字母数字组成，且字母开头'))
        } else if (r.result) {
          callback(new Error('账号已存在'))
        } else callback()
      })
    }
    return {
      queryCond: { name: undefined, account: undefined, roles: [], status: 1 },
      page: { limit: 10, num: 1 },
      total: 0,
      listLoading: false,
      list: [],
      userRules: {
        account: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 2, max: 10, message: '账号长度2-10', trigger: 'blur' },
          { validator: accountCheck, trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 1, max: 6, message: '昵称长度1-6', trigger: 'blur' }
        ],
        roles: [
          { type: 'array', required: true, message: '请至少选择一个角色', trigger: 'blur' }
        ]
      },
      temp: { name: '', password: '111111', account: '', phone: '', roles: [] },
      showDialog: false,
      editStatus: '',
      allRoles: [],
      status: ['无效', '正常', '禁用'],
      statusType: ['info', 'success', 'warning', 'danger'],
      permissions: {}
    }
  },
  created() {
    this.$permission()
    queryAllRoles().then(r => { this.allRoles = r.result })
    this.query()
  },
  methods: {
    query() {
      this.listLoading = true
      queryUser(this.queryCond, this.page).then(r => {
        this.list = r.result.list
        this.total = r.result.total
        this.listLoading = false
      }).catch(e => { this.listLoading = false })
    },
    newRecord() {
      this.editStatus = 'create'
      this.showDialog = true
      this.temp.name = ''
      this.temp.account = ''
      this.temp.phone = ''
      this.temp.roles = []
    },
    dateFormat(date) {
      return date ? parseTime(date) : '未知'
    },
    forbidden(account) {
      MessageBox({ message: '禁用之后用户将不能登录系统，确认操作？', title: '禁用账号:' + account }).then(() => {
        forbiddenUser(account).then(r => {
          this.$message.success('操作成功')
          this.query()
        })
      }
      )
    },
    unforbidden(account) {
      unforbiddenUser(account).then(r => {
        this.$message.success('操作成功')
        this.query()
      })
    },
    invalid(account) {
      MessageBox({ message: '操作之后将不能恢复，确认操作？', title: '操作确认' }).then(() => {
        invalidUser(account).then(r => {
          this.$message.success('操作成功')
          this.query()
        })
      })
    },
    resetPsw(account) {
      this.$prompt('危险操作，用户密码将被重置为111111，请输入[确认重置]进行验证。', '重置密码:' + account, {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (value === '确认重置') {
          resetUserPsw(account).then(r => {
            this.$message.success('操作成功')
          })
        } else { this.$message.error('验证失败') }
      }).catch(() => { })
    },
    saveUser() {
      if (this.editStatus === 'create') {
        this.$refs['userForm'].validate((valid) => {
          if (valid) {
            MessageBox({ message: '确认添加该用户？', title: '确认' }).then(() => {
              createUser(this.temp).then(r => {
                this.$message.success('操作成功')
                this.showDialog = false
                this.query()
              })
            }).catch(e => {})
          } else {
            this.$message.error('格式错误')
            return false
          }
        })
      } else if (this.editStatus === 'edit') {
        if (!this.temp.roles || !this.temp.roles.length) {
          this.$message.error('请选择至少一个角色')
          return
        }
        updateUserRoles(this.temp.account, this.temp.roles).then(r => {
          this.$message.success('修改成功')
          this.showDialog = false
          this.query()
        }).catch(e => {})
      }
    },
    getRoleNameById(roleId) {
      return (this.$getById(this.allRoles, roleId) || {}).name_show
    },
    updateRoles(row) {
      this.showDialog = true
      this.editStatus = 'edit'
      this.temp.name = row.name
      this.temp.id = row.id
      this.temp.roles = row.roles
      this.temp.account = row.account
    }
  }
}
</script>
<style scoped>
.filter-item.el-input{
  margin-right: 10px;
  width: 180px;
}
</style>
