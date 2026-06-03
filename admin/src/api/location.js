import request from '@/utils/request'
/**
 * 封装投放点的增删改查 API
 */
export default {
  // 获取投放点列表（支持分页、搜索）
  getLocations(params) {
    return request.get('/manage/locations', { params })
  },

  // 新增投放点
  addLocation(data) {
    return request.put('/manage/locations', null, { params: data })
  },

  // 更新投放点
  updateLocation(id, data) {
    return request.post(`/manage/locations/${id}`, null, { params: data })
  },

  // 删除投放点
  deleteLocation(id) {
    return request.get(`/manage/locations/delete/${id}`)
  }
}