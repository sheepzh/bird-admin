<template>
  <div class="app-container">
    <div class="filter-container">
      <el-autocomplete v-model="queryCond.module" :fetch-suggestions="moduleFetch" clearable placeholder="所在组件" class="filter-item" @select="query" @clear="query" @keyup.enter.native="query" />
      <el-input v-model="queryCond.name" placeholder="权限代码" class="filter-item" @keyup.enter.native="query"/>
      <el-input v-model="queryCond.des" placeholder="描述信息" class="filter-item" @keyup.enter.native="query"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" :loading="listLoading" @click="query()">查询</el-button>
      <!-- 开发模式下允许新增 -->
      <el-button v-if="isDev()" class="filter-item" type="success" icon="el-icon-plus" @click="newRecord()">新增[DEV]</el-button>
      <el-button class="filter-item" icon="el-icon-refresh-right" type="warning" @click="flushCache()">刷新缓存</el-button>
    </div>
    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column type="index" label="序号" width="70px" align="center" />
      <el-table-column label="组件名称" align="center" :formatter="r=>r.module" min-width="150" />
      <el-table-column label="权限代码" align="center" :formatter="r=>r.name" min-width="130" />
      <el-table-column label="角色" align="center" min-width="240">
        <template slot-scope="{row}">
          <el-tooltip v-for="rid in row.role_ids" :key="rid" :content="getRolesById(rid).name||'unkown'" placement="top">
            <el-tag size="mini" style="margin:3px 6px;">{{ getRolesById(rid).name_show||'未知' }}</el-tag>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center" :formatter="r=>r.des" min-width="180" />
      <el-table-column label="操作" align="center" min-width="240">
        <template slot-scope="{row}">
          <el-button size="mini" type="primary" @click="setRoles(row)">角色配置</el-button>
          <el-button v-if="isDev()" size="mini" type="warning" @click="updatePermission(row)">修改[DEV]</el-button>
          <el-button v-if="isDev()" size="mini" type="danger" @click="deletePermission(row.id)">删除[DEV]</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :visible.sync="showDialog" :title="dialogStatus==='create'?'新增权限':'修改权限'">
      <el-form ref="recordForm" label-position="left" label-width="80px" :rules="rule" :model="temp">
        <el-row :gutter="60">
          <el-col :span="12">
            <el-form-item label="组件名称" prop="module">
              <el-autocomplete v-model="temp.module" :fetch-suggestions="moduleFetch" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限代码" prop="name">
              <el-input v-model="temp.name" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述信息" prop="des">
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

    <el-dialog :visible.sync="showRoleDialog" :title="`权限角色配置：${temp.module} / ${temp.name}`" width="640px">
      <div style="text-align: center;">
        <el-transfer
          v-model="temp.role_ids"
          style="text-align: left; display: inline-block;"
          filterable
          :titles="['无权限', '有权限']"
          :button-texts="['到左边', '到右边']"
          :data="canBeSeleted"
        />
      </div>
      <div style="text-align:center;margin-top:25px;">
        <el-form inline>
          <el-form-item>
            <el-button type="primary" icon="el-icon-check" :loading.sync="isSavingRoles" @click="saveRoles()">
              保存
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { queryPermissions, addPermission, updatePermission, deletePermission, updatePermissionRoles, flushPermissionCache } from '@/api/system/permission'
import { queryAllRoles } from '@/api/system/role'
export default {
  name: 'NodePermission',
  data() {
    return {
      queryCond: {
        module: '',
        name: '',
        des: ''
      },
      listLoading: false,
      list: [],
      allModules: [],
      allRoles: [],
      temp: {
        id: undefined,
        module: undefined,
        name: undefined,
        des: undefined,
        role_ids: []
      },
      showDialog: false,
      dialogStatus: '',
      isSaving: false,
      rule: {
        module: [{ required: true, message: '未输入组件名称', trigger: 'blur' }],
        name: [{ required: true, message: '未输入权限代码', trigger: 'blur' }],
        des: [{ required: true, message: '未输入功能描述', trigger: 'blur' }]
      },
      showRoleDialog: false,
      isSavingRoles: false
    }
  },
  computed: {
    canBeSeleted() { return this.allRoles.filter(r => r.status).map(r => { return { key: r.id, label: r.name + ' ' + r.name_show } }) }
  },
  created() {
    queryAllRoles().then(r => { this.allRoles = r.result })
    this.query(true)
  },
  methods: {
    query(mapModules) {
      this.listLoading = true
      queryPermissions(this.queryCond)
        .then(r => {
          this.list = r.result
          if (mapModules) {
            this.allModules = Array.from(new Set(this.list.map(p => p.module))).map(m => { return { value: m } })
          }
        }).catch(e => {}).finally(() => { this.listLoading = false })
    },
    getRolesById(id) { return this.$getById(this.allRoles, id) || {} },
    isDev() { return process.env.NODE_ENV === 'development' },
    newRecord() {
      this.showDialog = true
      this.dialogStatus = 'create'
      this.temp = { id: undefined, name: undefined, des: undefined }
      this.isSaving = false
    },
    updatePermission(row) {
      this.temp = { ...row }
      this.showDialog = true
      this.dialogStatus = 'edit'
    },
    saveRecord() {
      this.isSaving = true
      this.$refs['recordForm'].validate(v => {
        if (!v) {
          this.isSaving = false
          return false
        }
        if (this.dialogStatus === 'create') {
          addPermission(this.temp).then(r => {
            this.$message.success('操作成功')
            this.showDialog = false
            this.query()
          }).catch(e => {}).finally(this.isSaving = false)
        } else if (this.dialogStatus === 'edit') {
          updatePermission(this.temp).then(r => {
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
    deletePermission(id) {
      this.$confirm('是否删除该权限点', '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deletePermission(id).then(r => {
          this.$message.success('操作成功')
          this.query()
        }).catch(e => {})
      }).catch(e => {})
    },
    setRoles(row) {
      this.showRoleDialog = true
      Object.assign(this.temp, row)
    },
    saveRoles() {
      this.isSavingRoles = true
      updatePermissionRoles(this.temp.id, this.temp.role_ids).then(r => {
        this.$message.success('操作成功')
        this.showRoleDialog = false
        this.query()
      }).catch(e => {}).finally(() => { this.isSavingRoles = false })
    },
    flushCache() { flushPermissionCache().then(r => { this.$message.success('操作成功') }).catch(e => {}) },
    moduleFetch(str, cb) {
      var allModules = this.allModules
      var results = str ? allModules.filter(m => m.value.indexOf(str) >= 0) : allModules
      cb(results)
    }
  }
}
</script>
