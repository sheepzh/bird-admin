<template>
  <div>
    <ul v-infinite-scroll="loadLog" :infinite-scroll-immediate="false" class="log-scroll" style="overflow:auto;height:570px;">
      <el-card v-for="(l,i) in logList" :key="i" style="margin:15px 0px">
        <p>{{ l.des }}</p>
        <p>{{ ' @ ' + dateFormat(l.request_time) + ' from '+l.request_source_ip }}</p>
      </el-card>
    </ul>
    <br>
    <p v-if="logLoading" style="text-align:center;">加载中...</p>
    <p v-if="!logLoading&&!noMoreLog" style="text-align:center;">下滑查看更多记录</p>
    <p v-if="noMoreLog" style="text-align:center;">没有更多了</p>
  </div>
</template>
<script>
import { queryLog } from '@/api/log'
import { parseTime } from '@/utils'
export default {
  name: 'OperationLog',
  data() {
    return {
      logList: [],
      logTotal: 0,
      logPage: {
        num: 0,
        limit: 4
      },
      logLoading: false,
      noMoreLog: false
    }
  },
  created() {
    this.loadLog()
  },
  methods: {
    loadLog() {
      if (this.noMoreLog || this.logLoading) {
        return
      }
      this.logLoading = true
      this.logPage.num++
      queryLog({ userAccount: this.$store.getters.account }, this.logPage)
        .then(r => {
          this.logTotal = r.result.total
          this.logList = this.logList.concat(r.result.list)
          if (this.logTotal === this.logList.length) this.noMoreLog = true
        }).catch(e => {}).finally(() => { this.logLoading = false })
    },
    dateFormat(d) { return parseTime(d) }
  }
}
</script>
