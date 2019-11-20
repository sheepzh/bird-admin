<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <personal-info />
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <el-tabs style="height:auto">
            <el-tab-pane label="操作日志">
              <operation-log />
            </el-tab-pane>
            <el-tab-pane label="账户信息">
              <account-info />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import OperationLog from './components/OperationLog'
import AccountInfo from './components/AccountInfo'
import PersonalInfo from './components/PersonalInfo'
export default {
  name: 'PersonalCenter',
  components: { OperationLog, AccountInfo, PersonalInfo }
}
</script>
<style scoped>
.log-scroll ::-webkit-scrollbar {
  /*隐藏滚轮*/
  display: none;
}
</style>
