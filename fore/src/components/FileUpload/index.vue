<template>
  <el-upload
    ref="myUpload"
    :action="filePostUrl"
    :http-request="uploadFile"
    multiple
    :limit="maxNum"
    name="file"
    :on-exceed="fileExceed"
    :on-success="afterLoad"
    accept="jpg"
    :on-remove="whileRemoveFile"
    :before-remove="beforeRemoveFile"
    :before-upload="beforeUploadFile"
  >
    <el-button size="small" type="primary">点击上传附件，文件大小&lt;{{ maxSize }}M，支持任意格式</el-button>
  </el-upload>
</template>

<script>
import { uploadFile, deleteFile } from '@/api/file'
export default {
  name: 'FileUpload',
  props: {
    maxNum: {
      type: Number,
      required: false,
      default: 10
    },
    maxSize: {
      type: Number,
      required: false,
      default: 5
    },
    fileList: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      filePostUrl: process.env.VUE_APP_BASE_API,
      addFiles: []
    }
  },
  methods: {
    uploadFile(content) {
      uploadFile(content.file).then(r => { content.onSuccess(r, content.file) })
        .catch(e => {
          this.addFiles.splice(this.addFiles.indexOf(content.file.name), 1)
          content.onError(e.message)
        })
    },
    fileExceed() { this.$message.error(`超出文件个数限制：${this.maxNum}`) },
    beforeRemoveFile(file, fileList) {
      if (file.raw.quietRemove) return true
      return this.$confirm(`确定移除 ${file.name}？`, '操作提示')
    },
    whileRemoveFile(file, fileList) {
      var fileId = file.id
      var index = this.fileList.indexOf(fileId)
      var fileIndex = this.addFiles.indexOf(file.raw.name)
      if (fileIndex >= 0) this.addFiles.splice(fileIndex, 1)
      if (index >= 0) this.fileList.splice(index, 1)
      if (fileId) deleteFile(file.id)
    },
    beforeUploadFile(file) {
      var exceed = file.size / 1024 / 1024 > this.maxSize
      var exists = this.addFiles.includes(file.name)
      if (exceed) this.$message.error(`文件${file.name}大小超出限制：${this.maxSize}M`)
      if (exists) this.$message.error(`重复添加，如需覆盖请先删除原文件`)
      var valid = !exceed && !exists
      if (valid) this.addFiles.push(file.name)
      file.quietRemove = !valid
      return valid
    },
    afterLoad(r, file, fileList) {
      file.id = r.result
      if (file.id) this.fileList.push(file.id)
    },
    clearFiles() { this.$refs['myUpload'].clearFiles() }
  }
}
</script>
