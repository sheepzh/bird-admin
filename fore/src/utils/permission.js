import { queryPermissionByComponent } from '@/api/system/permission'
import store from '@/store'

export default function() {
  queryPermissionByComponent(this).then(r => {
    const roles = store.getters && store.getters.roles
    if (!this.$options.data().permissions) { console.error('permissions参数未在data中进行定义'); return }
    for (const key in r.result) {
      this.$set(this.permissions, key, hasPermission(roles, r.result[key]))
    }
  }).catch(e => { console.error(`获取权限错误:${this.$options.name}`, e) })
}

function hasPermission(roles, permissionRoles) {
  let hasPermission = false
  if (permissionRoles instanceof Array) {
    hasPermission = roles.some(role => permissionRoles.includes(role))
  }
  return hasPermission
}
