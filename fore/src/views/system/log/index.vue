<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="queryCond.userAccount" placeholder="用户账号" class="filter-item" @keypress.native.enter="query" />
      <el-input v-model="queryCond.des" placeholder="功能描述" class="filter-item" @keypress.native.enter="query" />
      <date-between :value="queryCond.requestDate" name="请求日期" class="filter-item" @keypress.native.enter="query" @change="val=>{queryCond.requestDate = val;query()}" />
      <el-select v-model="queryCond.isSuccess" placeholder="是否成功" class="filter-item" clearable @change="query">
        <el-option label="是" :value="1" />
        <el-option label="否" :value="0" />
      </el-select>
      <el-input v-model="queryCond.handlingTimeMin" placeholder="处理时长≥ms" class="filter-item" step="100" min="0" type="number" style="width:130px" @keypress.native.enter="query" />
      <el-input v-model="queryCond.handlingTimeMax" placeholder="处理时长<ms" class="filter-item" step="100" min="0" type="number" style="width:130px" @keypress.native.enter="query" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" :loading="listLoading" @click="query">
        查询
      </el-button>
      <el-button class="filter-item" type="success" icon="el-icon-download" :disabled="listLoading" @click="showDownloadDialog=true">
        导出当页数据
      </el-button>
    </div>
    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column type="index" label="序号" align="center" width="70" />
      <el-table-column label="描述" align="center" :formatter="r=>r.des" min-width="140" />
      <el-table-column label="账号" align="center" :formatter="r=>r.staff_account" min-width="140" />
      <el-table-column label="请求" align="center">
        <el-table-column label="时间" align="center" :formatter="r=>dateFormat(r.request_time)" width="155" />
        <el-table-column label="源IP" align="center" :formatter="r=>r.request_source_ip" width="135" />
        <el-table-column label="URI" align="center" :formatter="r=>r.request_uri" min-width="200" />
        <el-table-column label="方式" align="center" :formatter="r=>r.request_method" width="90" />
      </el-table-column>
      <el-table-column label="处理结果" align="center">
        <el-table-column label="时长/ms" align="center" :formatter="r=>r.handling_time" width="80" />
        <el-table-column label="是否成功" align="center" width="80">
          <template slot-scope="{row}">
            <el-tag :type="row.success?'success':'danger'" size="mini">{{ row.success?'成功':'失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="异常反馈" align="center" :formatter="r=>r.error_message" min-width="150" />
      </el-table-column>
    </el-table>
    <nav style="text-align: center; padding-top: 30px">
      <el-pagination
        :page-sizes="[10,20,30,50,100,300,500,1000]"
        :total="total"
        :current-page.sync="page.num"
        :page-size.sync="page.limit"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="query"
        @current-change="query"
      />
    </nav>

    <el-dialog :visible.sync="showDownloadDialog" width="695px" title="请选择打印内容">
      <file-export :data="list" :cols="downloadColomns" table-name="用户操作日志数据导出" file-name="用户操作记录" />
    </el-dialog>
  </div>
</template>
<script>
import { queryLog } from '@/api/log'
import { parseTime } from '@/utils'
import DateBetween from '@/components/DateBetween'
import FileExport from '@/components/FileExport'
export default {
  name: 'UserActionLog',
  components: { DateBetween, FileExport },
  data() {
    return {
      queryCond: {
        userAccount: undefined,
        des: undefined,
        requestDate: [],
        isSuccess: undefined,
        handlingTimeMin: undefined,
        handlingTimeMax: undefined
      },
      page: {
        num: 1,
        limit: 10
      },
      list: [],
      total: 0,
      listLoading: false,
      showDownloadDialog: false,
      downloadColomns: [
        { name: '序号', map: (v, c, i) => i + 1 },
        { name: '操作描述', value: 'des' },
        { name: '操作人账户', value: 'staff_account' },
        { name: '时间', value: 'request_time', map: this.dateFormat },
        { name: 'IP', value: 'request_source_ip' },
        { name: 'URI', value: 'request_uri' },
        { name: '请求方式', value: 'request_method' },
        { name: '处理时长/ms', value: 'handling_time', ave: true },
        { name: '是否成功', value: 'success', map: s => s ? '✔' : '✖' },
        { name: '异常信息', value: 'error_message', map: (m, r) => r.success ? '-' : m }

      ]
    }
  },
  created() { this.query() },
  methods: {
    query() {
      this.listLoading = true
      queryLog(this.queryCond, this.page)
        .then(r => {
          this.list = r.result.list
          this.total = r.result.total
        }).catch(e => {}).finally(() => { this.listLoading = false })
    },
    dateFormat(d) { return parseTime(d, '{y}-{m}-{d} {h}:{i}:{s}') }
  }
}
</script>
