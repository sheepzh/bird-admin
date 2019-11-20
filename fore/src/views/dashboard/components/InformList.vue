<template>
  <div>
    <h3 class="title">系统公告</h3>
    <el-divider />
    <div v-for="i in list" :key="i.id" style="width:100%;padding:10px 10px">
      <el-tag v-if="i.top" size="mini" type="danger" style="margin-right:10px">置顶</el-tag>
      <router-link :to="`inform/${i.id}`">{{ i.title }}</router-link>
      <a style="float:right;cursor:default;">{{ formatDate(i.create_date) }}</a>
    </div>
    <div>
      <el-pagination
        style="float:right;margin:10px 0"
        layout="prev, pager, next"
        :total.sync="total"
        small
        :current-page.sync="page.num"
        :page-size="page.size"
        @current-change="query"
      />
    </div>
  </div>
</template>
<script>
import { parseTime } from '@/utils'
import { queryInform } from '@/api/inform'
export default {
  data() {
    return {
      list: [],
      page: {
        num: 1,
        size: 5
      },
      total: 0,
      queryCond: {
        status: 1,
        topFirst: true
      }
    }
  },
  created() { this.query() },
  methods: {
    query() {
      queryInform(this.queryCond, this.page).then(r => {
        this.list = r.result.list
        this.total = r.result.total
      }).catch(e => {})
    },
    formatDate(d) { return parseTime(d, '{y}/{m}/{d}') }
  }
}
</script>

<style scoped>
.title{
  font: 18px 'Helvetica Neue', Helvetica, Arial, sans-serif;
  line-height: 1.4em;
  color: #4d4d4d;
}
</style>
