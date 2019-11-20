<template>
  <el-date-picker
    v-model="myValue"
    type="daterange"
    align="right"
    unlink-panels
    range-separator="-"
    :start-placeholder="`${name}`"
    :picker-options="pickerOptions"
    style="width:290px"
    :class="dateClass"
  />
</template>
<script>
export default {
  name: 'DateBetween',
  props: {
    name: {
      required: true,
      type: String
    },
    value: {
      required: false,
      type: Array,
      default: () => { return [] }
    },
    dateClass: {
      type: String,
      required: false,
      default: ''
    },
    /**
     * 失效的日期
     */
    disabledDate: {
      type: Function,
      default: d => { return false }
    }
  },
  data() {
    return {
      myValue: this.value,
      pickerOptions: {
        shortcuts: [
          {
            text: '本月',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * (start.getDate() - 1))
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '上月',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * start.getDate())
              end.setTime(start.getTime())
              start.setTime(start.getTime() - 3600 * 1000 * 24 * (start.getDate() - 1))
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近7天',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近15天',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 15)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近30天',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              picker.$emit('pick', [start, end])
            }
          }
        ],
        disabledDate: this.disabledDate
      }
    }
  },
  watch: {
    myValue(val) { this.$emit('change', val) },
    value(val) { this.myValue = val },
    disabledDate(val) { this.pickerOptions.disabledDate = val }
  },
  created() { }
}
</script>
