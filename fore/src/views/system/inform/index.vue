<template>
  <div class="app-container">
    <el-tabs v-model="tabIndex" type="border-card">
      <el-tab-pane name="send">
        <span slot="label"><i class="el-icon-view" />  已创建</span>
        <inform-table :user-list="allUsers" />
      </el-tab-pane>
      <el-tab-pane v-if="permissions.tab_add" name="create">
        <span slot="label"><i class="el-icon-edit" />  新公告</span>
        <new-inform class="tab-content" @created="createdNewOne()" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import NewInform from './components/NewInform'
import InformTable from './components/InformTable'
export default {
  name: 'InformManagement',
  components: { NewInform, InformTable },
  data() {
    return {
      tabIndex: 'send',
      permissions: {},
      allUsers: []
    }
  },
  created() {
    this.$permission()
  },
  methods: {
    createdNewOne() {
      this.tabIndex = 'send'
    }
  }
}
</script>
<style scoped>
  .tab-content{
    padding:10px 30px;
  }
</style>
