/**
 * 对象转search参数
 * @param {参数列表} params
 */
export function search(params) {
  var paramArr = []
  var i = 0
  for (var x in params) {
    var v = params[x]
    if (v !== undefined && v !== null && v !== '') {
      paramArr[i] = x + '=' + v
      i = i + 1
    }
  }
  return paramArr.length ? paramArr.reduce((a, b) => a + '&' + b) : ''
}

/**
 * 通过id获取数组内元素
 * @param {数组} arr
 * @param {id} id
 */
export function getById(arr, id) {
  var ret
  arr.forEach(element => {
    if (element.id === id) ret = element
  })
  return ret
}
