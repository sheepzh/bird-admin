<template>
  <div style="width:100%;text-align:center;height:350px;">
    <iframe ref="printIframe" style="display:none;padding:10px;" />
    <div style="height:303px">
      <el-transfer
        v-model="selected"
        style="text-align: left; display: inline-block"
        filterable
        target-order="push"
        :titles="['未选列', '已选列']"
        :button-texts="['到左边', '到右边']"
        :format="{
          noChecked: '${total}',
          hasChecked: '${checked}/${total}'
        }"
        :data="colNames"
      />
    </div>
    <div style="margin:20px;" class="filter-container">
      <el-form inline>
        <!-- <el-form-item label="格式">
          <el-select v-model="fileType" style="width:90px">
            <el-option value="pdf">.pdf</el-option>
            <el-option value="doc">.xls</el-option>
            <el-option value="csv">.csv</el-option>
          </el-select>
        </el-form-item> -->
        <el-form-item label="表头标题/可选">
          <el-input v-model="myTableName" style="width:180px" />
        </el-form-item>
        <el-button icon="el-icon-check" :loading="isDownloading" type="primary" @click="generate">{{ '导出'+(fileType==='pdf'?'/打印':'') }}</el-button>
      </el-form>
    </div>
  </div>
</template>
<script>
import { toTable } from '@/utils/export'
export default {
  name: 'FileExport',
  props: {
    data: {
      type: Array,
      required: true
    },
    cols: {
      type: Array,
      required: true
    },
    fileName: {
      type: String,
      default: '下载文件'
    },
    tableName: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      myTableName: '',
      selected: [],
      isDownloading: false,
      fileType: 'pdf'
    }
  },
  computed: {
    colNames: {
      get() {
        const result = []
        this.cols.forEach((v, i) => {
          if (v.name) {
            result.push({ key: i, label: v.name })
          }
        })
        return result
      }
    }
  },
  created() {
    this.myTableName = this.tableName
  },
  mounted() {
  },
  methods: {
    generate() {
      if (!this.selected.length) {
        this.$message.error('未选择打印列')
        return
      }
      this.isDownloading = true
      var iframe = this.$refs['printIframe']
      var iframeBody = iframe.contentWindow.document.body
      // 删除之前的元素
      iframeBody.innerHTML = ''

      var h3 = document.createElement('h3')
      h3.style.width = '100%'
      h3.style.textAlign = 'center'
      h3.innerText = this.myTableName
      iframeBody.appendChild(h3)
      iframeBody.appendChild(toTable(this.data, this.selected.map(i => this.cols[i]), h3.offsetTop + h3.offsetHeight))
      iframe.contentWindow.focus()
      iframe.contentWindow.print()
      this.isDownloading = false
    }
  }
}
</script>
