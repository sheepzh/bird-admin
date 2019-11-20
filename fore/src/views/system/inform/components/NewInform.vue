<template>
  <div>
    <el-form label-position="right" label-width="70px">
      <el-row :gutter="40">
        <el-col :span="14">
          <el-form-item label="标题">
            <el-input v-model="temp.title" placeholder="小于50字" clearable maxlength="50" show-word-limit />
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="置顶">
            <el-select v-model="temp.top" style="width:100%">
              <el-option :value="true" label="是" />
              <el-option :value="false" label="否" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-input v-model="temp.content" placeholder="请输入内容" maxlength="1000" show-word-limit type="textarea" resize="false" rows="20" />
      <file-upload ref="upload" :file-list="temp.attachments" style="margin-top:20px" />
      <div style="float:right;margin-top:15px">
        <el-button icon="el-icon-check" type="info" @click="clearContent()">清空</el-button>
        <el-button style="margin-left:20px;" icon="el-icon-check" type="primary" :loading="isSaving" @click="saveRecord()">发布</el-button>
      </div>
    </el-form>
  </div>
</template>
<script>
import FileUpload from '@/components/FileUpload'
import { addInform } from '@/api/inform'
export default {
  name: 'NewInformTab',
  components: { FileUpload },
  data() {
    return {
      temp: {
        title: '',
        expiration: undefined,
        top: false,
        content: undefined,
        attachments: []
      },
      isSaving: false,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 24 * 3600 * 1000
        },
        shortcuts: [
          {
            text: '明天',
            onClick(picker) {
              const date = new Date()
              date.setTime(date.getTime() + 3600 * 1000 * 24)
              picker.$emit('pick', date)
            }
          }, {
            text: '后天',
            onClick(picker) {
              const date = new Date()
              date.setTime(date.getTime() + 3600 * 1000 * 24 * 2)
              picker.$emit('pick', date)
            }
          }, {
            text: '三天后',
            onClick(picker) {
              const date = new Date()
              date.setTime(date.getTime() + 3600 * 1000 * 24 * 3)
              picker.$emit('pick', date)
            }
          }, {
            text: '五天后',
            onClick(picker) {
              const date = new Date()
              date.setTime(date.getTime() + 3600 * 1000 * 24 * 5)
              picker.$emit('pick', date)
            }
          }
        ]
      }
    }
  },
  methods: {
    saveRecord() {
      if (!this.temp.title) {
        this.$message.error('标题不能为空')
        return
      }
      if (!this.temp.content) {
        this.$message.error('内容不能为空')
        return
      }
      this.isSaving = true
      addInform(this.temp).then(
        r => {
          this.$message.success('公告发布成功！')
          this.$refs['upload'].clearFiles()
          this.temp.title = undefined
          this.temp.expiration = undefined
          this.temp.top = false
          this.temp.content = undefined
          this.$emit('created')
        }
      ).catch(r => {}).finally(() => {
        this.isSaving = false
      })
    },
    clearContent() {
      this.$confirm('确定清空内容？', '操作提示').then(r => {
        this.temp.content = ''
      }).catch(e => {})
    }
  }
}
</script>
