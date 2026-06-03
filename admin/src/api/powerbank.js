import request from '@/utils/request'
/**
 * 封装充电宝设备的管理 API（列表、添加、转移、删除）
 */
export default {
  // 获取充电宝设备列表（支持分页、搜索）
  getPowerbanks(params) {
    return request({
      url: '/manage/powerbanks',
      method: 'get',
      params
    })
  },

  // 添加充电宝设备（批量添加到指定投放点）
  addPowerbank(pobk_location_id, pobk_amount) {
    return request({
      url: '/manage/powerbanks',
      method: 'put',
      params: { pobk_location_id, pobk_amount }
    })
  },

  // 移动充电宝设备（指定充电宝ID和目标投放点ID）
  transferPowerbank(pobk_id, pobk_location_id) {
    return request({
      url: `/manage/powerbanks/${pobk_id}`,
      method: 'post',
      params: { pobk_location_id }
    })
  },

  // 删除充电宝设备（指定充电宝ID）
  deletePowerbank(pobk_id) {
    return request({
      url: `/manage/powerbanks/${pobk_id}`,
      method: 'delete'
    })
  }
}
