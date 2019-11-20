<template>
  <div class="app-container" style="background: #f0f2f5;">
    <el-row :gutter="20">
      <el-col :span="17">
        <inform-list class="dashboard-component" style="padding-bottom:45px" />
      </el-col>
      <el-col :span="7">
        <todo-list class="dashboard-component" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import InformList from './components/InformList'
import TodoList from './components/TodoList'
export default {
  name: 'Dashboard',
  components: { InformList, TodoList }
}
</script>
<style scoped>
.dashboard-component {
  background: #fff;
  padding: 16px 16px 30px;
}
</style>
