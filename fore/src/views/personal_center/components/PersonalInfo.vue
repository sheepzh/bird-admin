<template>
  <div>
    <span>{{ name }}</span>
    <el-divider />
    <div>
      <el-tag v-for="(r,i) in roles" :key="i" size="small" style="margin:10px 8px">{{ r }}</el-tag>
    </div>
  </div>
</template>
<script>
import { queryAllRoles } from '@/api/system/role'
export default {
  name: 'PersonalInfo',
  data() {
    return {
      roleNameMap: {}
    }
  },
  computed: {
    name() { return this.$store.getters.name },
    roles() {
      return this.$store.getters.roles
        .map(r => this.roleNameMap[r])
        .filter(r => r)
    }
  },
  created() {
    queryAllRoles().then(r => {
      this.roleNameMap = {}
      r.result.forEach(role => {
        this.roleNameMap[role.name] = role.name_show
      })
    }).catch(e => {})
  }
}
</script>
