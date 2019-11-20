<template>
  <div class="app-container">
    <div v-if="inform" style="padding:0px 200px">
      <div style="text-align:center;">
        <h1>{{ inform.title }}</h1>
      </div>
      <div style="text-align:right;color:#555;margin-bottom:40px;">
        <a>发布/{{ inform.creator_name }}</a>
        <a style="margin-left:30px">{{ parseDate(inform.create_date) }}</a>
        <el-tag size="mini" style="margin-left:30px" :type="statusType[inform.status]">{{ statusLabel[inform.status] }}</el-tag>
      </div>
      <div v-if="inform&&inform.status!==0">
        <p v-for="(s,i) in inform.segments" :key="i" style="text-indent:2em;font-size:17px;">{{ s }}</p>
        <el-table :data="inform.attachments_to_show" border highlight-current-row fit style="width: 100%;margin-top:100px">
          <el-table-column label="附件列表" align="center">
            <el-table-column type="index" label="序号" align="center" width="70px" />
            <el-table-column label="名称" align="center" :formatter="r=>r.file_name" min-width="100px" />
            <el-table-column label="大小" align="center" :formatter="fileSizeCalc" />
            <el-table-column label="操作" align="center" width="140px">
              <template slot-scope="{row}">
                <el-button size="mini" type="primary" icon="el-icon-download" @click="download(row)">下载</el-button>
              </template>
            </el-table-column>
          </el-table-column>
        </el-table>
      </div>
      <div v-if="inform&&inform.status===0" style="text-align:center">
        <h4>该公告已被管理员撤销，无法查看内容！</h4>
      </div>
    </div>
  </div>
</template>
<script>
import { informInfo } from '@/api/inform'
import { downloadFile } from '@/api/file'
import { parseTime, numberFormat } from '@/utils'

export default {
  name: 'InformInfo',
  data() {
    return {
      inform: undefined,
      statusLabel: ['已撤销', '有效', '已过期'],
      statusType: ['info', 'success', 'warning']
    }
  },
  computed: {
    id() { return this.$route.params.id }
  },
  created() {
    this.inform = undefined
    informInfo(this.id).then(r => {
      this.inform = r.result
      this.inform.segments = this.inform.content
        ? this.inform.content.split('\n').filter(s => s.length > 0)
        : []
      // 动态更新标签栏标题
      this.$emit('custom-tag', `公告:${this.inform.title}`)
    }).catch(e => {})
  }, methods: {
    parseDate(d) {
      return parseTime(d, '{y}年{m}月{d}日{h}:{i}:{s}')
    },
    download(row) {
      downloadFile(row.id).then(r => {
        if (!r) return
        const url = window.URL.createObjectURL(new Blob([r]))
        const link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('download', row.file_name)
        document.body.appendChild(link)
        link.click()
      }).catch(e => { this.$message.error('下载失败') })
    },
    fileSizeCalc(row) { return row.file_size ? numberFormat(row.file_size / 1000000, 2) + ' MB' : '——' }
  }
}
</script>
