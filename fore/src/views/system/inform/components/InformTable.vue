<template>
  <div>
    <div class="filter-container">
      <el-input v-model="queryCond.title" class="filter-item" placeholder="标题" />
      <el-select v-model="queryCond.operator" class="filter-item" placeholder="发布人" clearable @change="query">
        <el-option v-for="u in allUsers" :key="u.id" :value="u.id" :label="u.name" />
      </el-select>
      <el-select v-model="queryCond.status" class="filter-item" placeholder="状态" clearable @change="query">
        <el-option v-for="(s,i) in statusLabel" :key="i" :value="i" :label="s" />
      </el-select>
      <date-between class="filter-item" name="发布日期" :value="queryCond.createDate" @keypress.native.enter="query" @change="val=>{queryCond.createDate = val;query()}" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" :loading="listLoading" @click="query">
        搜索
      </el-button>
    </div>
    <el-table v-loading="listLoading" border fit highlight-current-row style="width: 100%" :data="list">
      <el-table-column type="index" label="序号" align="center" width="60" />
      <el-table-column label="标题" align="center" min-width="360" :formatter="r=>r.title" />
      <el-table-column label="发布时间" :formatter="r=>parseDate(r.create_date)" align="center" min-width="120" />
      <el-table-column label="发布人" :formatter="r=>getUserName(r.creator)" align="center" min-width="120" />
      <el-table-column label="状态" align="center">
        <template slot-scope="{row}">
          <el-tag size="mini" :type="statusType[row.status]">
            {{ statusLabel[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="置顶" align="center" width="100">
        <template slot-scope="{row}">
          <el-tag v-if="row.top" size="mini" type="danger">TOP</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" width="360" label="操作">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="showInfo(row.id)">查看详情</el-button>
          <el-button v-if="row.status===1&&!row.top&&permissions.top_and_cancel" size="mini" type="danger" icon="el-icon-top" @click="top(row.id)">置顶</el-button>
          <el-button v-if="row.status===1&&row.top&&permissions.top_and_cancel" size="mini" type="" icon="el-icon-bottom" @click="untop(row.id)">取消置顶</el-button>
          <el-button v-if="row.status===1&&permissions.cancel" size="mini" type="info" @click="cancel(row.id,row.title)">撤销</el-button>
          <el-button v-if="row.status===1&&permissions.outdate" size="mini" type="warning" @click="outdate(row.id,row.title)">过期</el-button>
        </template>
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
  </div>
</template>
<script>
import DateBetween from '@/components/DateBetween'
import { queryInform, topInform, untopInform, cancelInform, outdateInform } from '@/api/inform'
import { parseTime } from '@/utils'
import { allUserSimple } from '@/api/user'
export default {
  name: 'InformTable',
  components: { DateBetween },
  data() {
    return {
      permissions: {},
      queryCond: {
        title: '',
        createDate: [],
        status: undefined,
        creater: undefined
      },
      page: {
        num: 1,
        size: 10
      },
      total: 0,
      list: [],
      listLoading: false,
      allUsers: [],
      statusLabel: ['已撤销', '有效中', '已过期'],
      statusType: ['info', 'success', 'warning']
    }
  },
  created() {
    this.query()
    this.$permission()
    allUserSimple().then(r => { this.allUsers = r.result })
  },
  methods: {
    query() {
      this.listLoading = true
      queryInform(this.queryCond, this.page).then(r => {
        this.list = r.result.list
        this.total = r.result.total
      }).catch(e => {}).finally(() => { this.listLoading = false })
    },
    parseDate(t) { return parseTime(t) },
    top(id) {
      topInform(id).then(r => {
        this.$message.success('操作成功')
        this.query()
      }).catch(e => {})
    },
    untop(id) {
      untopInform(id).then(r => {
        this.$message.success('操作成功')
        this.query()
      }).catch(e => {})
    },
    cancel(id, title) {
      this.$confirm(`公告撤销后不可恢复，确定撤销《${title}》？`, '操作提示').then(r => {
        cancelInform(id).then(r => {
          this.$message.success('操作成功')
          this.query()
        }).catch(e => {})
      }).catch(e => {})
    },
    outdate(id, title) {
      this.$confirm(`公告过期后不可恢复，确定要让《${title}》过期？`, '操作提示').then(r => {
        outdateInform(id).then(r => {
          this.$message.success('操作成功')
          this.query()
        }).catch(e => {})
      }).catch(e => {})
    },
    getUserName(id) {
      const user = this.$getById(this.allUsers, id) || {}
      return user.name || '未知'
    },
    showInfo(id) {
      this.$router.push({ path: `/inform/${id}` })
    }
  }
}
</script>
