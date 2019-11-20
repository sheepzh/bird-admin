import request from '@/utils/request'
import { search } from '@/utils/common'

export function uploadFile(file, data) {
  var formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/file/upload?' + search(data),
    method: 'post',
    data: formData
  })
}

export function deleteFile(fileId) {
  return request({
    url: '/file/' + fileId,
    method: 'delete'
  })
}

export function downloadFile(id) {
  return request({
    url: '/file/download/' + id,
    method: 'get',
    responseType: 'blob'
  })
}
