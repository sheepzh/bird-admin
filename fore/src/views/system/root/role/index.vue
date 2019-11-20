<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="queryCond.name" placeholder="角色代码" class="filter-item" clearable @keyup.native.enter="query()" @clear="query()" />
      <el-input v-model="queryCond.name_show" placeholder="角色名称" class="filter-item" clearable @keyup.native.enter="query()" @clear="query()" />
      <el-select v-model="queryCond.status" placeholder="状态" class="filter-item" clearable @change="query()">
        <!-- 取消选项 -->
        <el-option v-if="queryCond.status" :value="undefined" />
        <el-option v-for="(s,i) in statusLabel" :key="i" :label="s" :value="i" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" class="filter-item" :loading="listLoading" @click="query()">查询</el-button>
      <el-button type="success" icon="el-icon-plus" class="filter-item" @click="newRecord()">新增</el-button>
    </div>
    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column type="index" label="序号" align="center" width="70px" />
      <el-table-column label="代码" :formatter="r=>r.name" align="center" min-width="140" />
      <el-table-column label="名称" :formatter="r=>r.name_show" align="center" min-width="180" />
      <el-table-column label="状态" align="center" min-width="80">
        <template slot-scope="{row}">
          <el-tag :type="statusType[row.status]||'info'" size="mini">{{ statusLabel[row.status]||'未知' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="角色描述" align="center" min-width="300" :formatter="r=>r.des" />
      <el-table-column label="操作" align="center" min-width="200">
        <template slot-scope="{row}">
          <el-button size="mini" type="primary" @click="()=>edit(row)">修改</el-button>
          <el-button v-if="row.status===1" size="mini" type="danger" @click="ban(row.id)">禁用</el-button>
          <el-button v-if="row.status===0" size="mini" type="warning" @click="lift(row.id)">解禁</el-button>
          <el-button v-if="isDev()" size="mini" type="danger" @click="deleteRole(row.id)">删除[DEV]</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :visible.sync="showDialog" :title="dialogStatus==='create'?'新增角色':'修改角色'">
      <el-form ref="recordForm" :model="temp" label-position="left" label-width="80px" :rules="rule">
        <el-row :gutter="60">
          <el-col :span="12">
            <el-form-item label="角色代码" prop="name">
              <el-input v-model="temp.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色名称" prop="name_show">
              <el-input v-model="temp.name_show" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="dialogStatus==='edit'" :gutter="60">
          <el-col :span="12">
            <el-form-item label="角色状态">
              <el-input disabled :value="statusLabel[temp.status]||'未知'" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述信息">
          <el-input v-model="temp.des" type="textarea" :rows="2" placeholder="100字以内" maxlength="100" show-word-limit resize="none" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button icon="el-icon-close" @click="showDialog = false; dialogStatus = ''">
          {{ dialogStatus==='create'?'取消':'关闭' }}
        </el-button>
        <el-button type="primary" icon="el-icon-check" :loading.sync="isSaving" @click="saveRecord()">
          保存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { queryAllRoles, addRole, updateRole, banRole, liftRole, deleteRole } from '@/api/system/role'
export default {
  name: 'RoleManagement',
  data() {
    return {
      list: [],
      listLoading: false,
      queryCond: {
        name: undefined,
        name_show: undefined,
        status: 1
      },
      statusLabel: ['禁用', '正常'],
      statusType: ['danger', 'primary'],
      showDialog: false,
      dialogStatus: 'create',
      temp: { id: '', name: '', name_show: '', des: '', status: '' },
      isSaving: false,
      rule: {
        name: [{ required: true, message: '未输入代码', trigger: 'blur' }],
        name_show: [{ required: true, message: '未输入名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.query()
  },
  methods: {
    query() {
      this.listLoading = true
      queryAllRoles(this.queryCond).then(r => { this.list = r.result }).catch(e => { this.list = [] }).finally(() => { this.listLoading = false })
    },
    newRecord() {
      this.showDialog = true
      this.dialogStatus = 'create'
      this.temp = { id: '', name: '', name_show: '', des: '', status: '' }
      this.isSaving = false
    },
    edit(target) {
      this.showDialog = true
      this.dialogStatus = 'edit'
      this.temp = Object.assign(target)
      this.isSaving = false
    },
    saveRecord() {
      this.isSaving = true
      this.$refs['recordForm'].validate(v => {
        if (!v) {
          this.isSaving = false
          return false
        }
        if (this.dialogStatus === 'create') {
          addRole(this.temp).then(r => {
            this.$message.success('操作成功')
            this.showDialog = false
            this.query()
          }).catch(e => {}).finally(this.isSaving = false)
        } else if (this.dialogStatus === 'edit') {
          updateRole(this.temp).then(r => {
            this.$message.success('操作成功')
            this.showDialog = false
            this.query()
          }).catch(e => {}).finally(this.isSaving = false)
        } else {
          this.isSaving = false
          this.$message.error('状态错误')
        }
      })
    },
    ban(id) {
      this.$confirm('禁用该角色后，无法对其配置权限！确认禁用？', '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(r => {
        banRole(id).then(r => { this.$message.success('操作成功'); this.query() }).catch(e => {})
      }).catch(e => {})
    },
    lift(id) {
      this.$confirm('确认解禁？', '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(r => {
        liftRole(id).then(r => { this.$message.success('操作成功'); this.query() }).catch(e => {})
      }).catch(e => {})
    },
    isDev() { return process.env.NODE_ENV === 'development' },
    deleteRole(id) {
      this.$confirm('确认删除该角色？', '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteRole(id).then(r => { this.$message.success('操作成功'); this.query() }).catch(e => {})
      }).catch(e => {})
    }
  }
}
</script>
