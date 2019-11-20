/**
 *
 * 列处理可选属性：
 * name 列名，必选
 * col 列名简写
 * value 数据对应的属性名，必选
 * map value的映射函数，非必选 map(attr,row,index)
 * align 对齐方式，非必选，默认center
 * sum 是否计算合计，默认false
 *
 * @param {数据数组} data
 * @param {列处理} colNames
 * @param {文件名，不含后缀} fileName
 */
export function toTable(data, colNames, startHeight = 0) {
  // var a4Width = 978
  // var a4Height = 650

  var newTable = () => {
    var table = document.createElement('table')
    table.style.border = '1px solid #121212'
    table.cellSpacing = 0
    table.cellPadding = 0
    table.style.fontSize = '9px'
    table.border = '1'
    table.style.width = '100%'
    return table
  }

  var calulateValue = (d, c, i) => {
    var v = d[c.value]
    if (c.map) v = c.map(v, d, i)
    return v || '&emsp;' // 解决整行无数据表格坍缩的问题
  }

  var generateHeader = () => {
    var table_title_tr = document.createElement('tr')
    // 生成表头
    colNames.forEach(col => {
      var th = document.createElement('th')
      th.innerHTML = col.col || col.name
      table_title_tr.appendChild(th)
    })
    return table_title_tr
  }

  // 生成合计栏
  var generateSummarize = () => {
    var table_footer_tr = document.createElement('tr')
    colNames.forEach((col, i) => {
      var td = document.createElement('td')
      td.style.textAlign = 'center'
      if (i === 0) {
        td.innerHTML = '合计'
      } else if (col.sum) {
        td.innerHTML = data.map(d => calulateValue(d, col, i) || 0).reduce((a, b) => a + b)
      }
      table_footer_tr.appendChild(td)
    })
    return table_footer_tr
  }

  var generateAverage = () => {
    var table_footer_tr = document.createElement('tr')
    colNames.forEach((col, i) => {
      var td = document.createElement('td')
      td.style.textAlign = 'center'
      if (i === 0) {
        td.innerHTML = '平均值'
      } else if (col.ave) {
        if (data.length === 0) {
          td.innerHTML = 0
        } else {
          td.innerHTML = data.map(d => calulateValue(d, col, i) || 0).reduce((a, b) => a + b) / data.length
        }
      }
      table_footer_tr.appendChild(td)
    })
    return table_footer_tr
  }

  var generateContentLine = (d, colNames, i) => {
    var content_tr = document.createElement('tr')
    colNames.forEach(c => {
      var td = document.createElement('td')
      td.style.textAlign = c.align || 'center'
      var v = calulateValue(d, c, i)
      td.innerHTML = `<span>${v === undefined ? '' : v}</span>`
      content_tr.appendChild(td)
    })
    return content_tr
  }

  // 生成表格
  var table = newTable()
  table.appendChild(generateHeader())
  data.forEach((d, i) => { table.appendChild(generateContentLine(d, colNames, i)) })
  if (colNames.filter(c => c.sum).length > 0) {
    // 需要添加合计栏
    table.appendChild(generateSummarize())
  }
  if (colNames.filter(c => c.ave).length > 0) {
    // 需要添加平均值
    table.appendChild(generateAverage())
  }
  return table
}
