<template>
  <div class="app-container">
    <div class="filter-container">
      <el-button v-if="isDev()" icon="el-icon-plus" type="success" @click="newRecord()">新增[DEV]</el-button>
      <el-button icon="el-icon-refresh-right" type="warning" @click="flushCache()">刷新缓存</el-button>
    </div>
    <el-tree :data="routes" :props="treeProps" default-expand-all>
      <span slot-scope="{ node, data }" class="custom-tree-node">
        <span>
          <a style="margin-right:15px">{{ node.label }}</a>
          <a style="margin-right:30px">{{ data.path }}</a>
          <el-tooltip v-for="rid in data.role_ids" :key="rid" :content="getRolesById(rid).name||'unkown'" placement="top">
            <el-tag type="mini" style="margin-left:5px;padding:0 10px;">{{ getRolesById(rid).name_show||'未知角色' }}</el-tag>
          </el-tooltip>
        </span>
        <span>
          <el-button v-if="isDev()" size="mini" type="text" @click="updateRoute(node,data)">修改[DEV]</el-button>
          <el-button v-if="isDev()" size="mini" type="text" @click="deleteRecord(node,data)">删除[DEV]</el-button>
          <el-button size="mini" type="text" @click="updateRoles(node,data)">角色配置</el-button>
        </span>
      </span>
    </el-tree>

    <el-dialog :visible.sync="showDialog" :title="dialogStatus==='create'?'新增路由':'修改路由'">
      <el-form ref="recordForm" :model="temp" label-position="left" label-width="80px" :rules="rule">
        <el-row :gutter="60">
          <el-col :span="12">
            <el-form-item label="路由路径" prop="path">
              <el-input v-model="temp.path" placeholder="例如:/system/user" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="路由名称" prop="des">
              <el-input v-model="temp.des" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-show="dialogStatus==='create'" :gutter="60">
          <el-col :span="12">
            <el-form-item label="父级路由">
              <el-cascader v-model="temp.parentArr" :options="parentToSelet" clearable :props="{checkStrictly:true}" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
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

    <el-dialog :visible.sync="showRoleDialog" :title="`路由权限配置：${editingMenuPath}`" width="640px">
      <el-alert type="success" style="margin-bottom:20px">
        1. 拥有父路由权限的角色才有资格配置子路由权限。<br><br>2. 级联操作时，所有子路由会同目标路由保持相同的权限设置。
      </el-alert>
      <div style="text-align: center;">
        <el-transfer
          v-model="roleTemp"
          style="text-align: left; display: inline-block;"
          filterable
          :titles="['无权限', '有权限']"
          :button-texts="['到左边', '到右边']"
          :data="canBeSelected"
        />
      </div>
      <div style="text-align:center;margin-top:25px;">
        <el-form inline>
          <el-form-item label="级联修改">
            <el-switch v-model="cascade" style="margin-right:50px;" />
          </el-form-item>
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
import { queryAllRoutesWithRoles, updateRouteRoles, flushCache, addRoute, updateRoute, deleteRoute } from '@/api/system/route'
import { queryAllRoles } from '@/api/system/role'
export default {
  name: 'MenuPermission',
  data() {
    return {
      routes: [],
      treeProps: {
        children: 'children',
        label: 'label'
      },
      temp: {
        id: undefined,
        path: undefined,
        parent: undefined,
        parentArr: [],
        des: undefined
      },
      rule: {
        path: [{ required: true, message: '请输入路径', trigger: 'blur' }],
        des: [{ required: true, message: '请输入名称', trigger: 'blur' }]
      },
      showDialog: false,
      isSaving: false,
      dialogStatus: '',
      editingMenuPath: '',
      editingMenuId: undefined,
      allRoles: [],
      showRoleDialog: false,
      roleTemp: [],
      cascade: false,
      canBeSelected: [],
      isSavingRoles: false
    }
  },
  computed: {
    // 将树状菜单转换成级联选择器
    parentToSelet() {
      const parall = node => {
        const result = {}
        result.value = node.id
        result.label = node.des
        if (node.children && node.children.length) result.children = node.children.map(c => parall(c))
        return result
      }
      return this.routes.map(r => parall(r))
    }
  },
  created() {
    queryAllRoles().then(r => { this.allRoles = r.result }).catch(e => { this.allRoles = [] })
    this.query()
  },
  methods: {
    query() {
      queryAllRoutesWithRoles().then(r => { this.routes = r.result }).catch(() => { this.routes = [] })
    },
    flushCache() {
      flushCache().then(r => { this.$message.success('刷新成功') }).catch(e => { })
    },
    updateRoles(node, data) {
      this.showRoleDialog = true
      // 只有父节点的角色可选择
      const validRoles = node.level === 1 ? this.allRoles : this.allRoles.filter(e => node.parent.data.role_ids.includes(e.id))
      // 只有有效角色可选择
      this.canBeSelected = validRoles.filter(r => r.status === 1).map(r => { return { key: r.id, label: r.name + ' ' + r.name_show } })
      // dialog显示路径
      let menuPath = data.label
      let temp = node.parent
      // 到达顶层
      while (temp.level > 0) {
        menuPath = temp.data.label + ' / ' + menuPath
        temp = temp.parent
      }
      this.editingMenuPath = menuPath
      this.editingMenuId = data.id
      this.roleTemp = data.role_ids
    },
    getRolesById(id) { return this.$getById(this.allRoles, id) || {} },
    saveRoles() {
      this.isSavingRoles = true
      updateRouteRoles(this.editingMenuId, this.roleTemp, this.cascade)
        .then(r => {
          this.routes = r.result
          this.showRoleDialog = false
        })
        .catch(e => {}).finally(() => { this.isSavingRoles = false })
    },
    isDev() { return process.env.NODE_ENV === 'development' },
    newRecord() {
      this.showDialog = true
      this.temp.id = undefined
      this.dialogStatus = 'create'
    },
    saveRecord() {
      this.isSaving = true
      this.$refs['recordForm'].validate(v => {
        if (!v) {
          this.isSaving = false
          return false
        }
        // 父级路由级联选择器绑定的数据为数组，需要去数组最后一位作为最终父路由ID
        var parentArr = this.temp.parentArr
        if (parentArr && parentArr.length) this.temp.parent = parentArr[parentArr.length - 1]

        if (this.dialogStatus === 'create') {
          addRoute(this.temp).then(r => {
            this.$message.success('操作成功')
            this.showDialog = false
            this.query()
          }).catch(e => {}).finally(this.isSaving = false)
        } else if (this.dialogStatus === 'edit') {
          updateRoute(this.temp).then(r => {
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
    // 不能修改所在位置
    updateRoute(node, data) {
      this.showDialog = true
      this.dialogStatus = 'edit'
      this.temp.id = data.id
      this.temp.des = node.label
      this.temp.path = data.path
      this.temp.parent = undefined
      this.temp.parentArr = []
    },
    deleteRecord(node, data) {
      this.$confirm('是否删除该路由及其子路由？', '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteRoute(data.id).then(r => { this.$message.success('操作成功'); this.query() }).catch(e => {})
      }).catch(e => {})
    }
  }
}
</script>
<style scoped>
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
    margin-bottom: 4px;
  }
</style>
