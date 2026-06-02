import request from '@/utils/request'

export default {
  getPowerbanks(params) {
    return request({
      url: '/manage/powerbanks',
      method: 'get',
      params
    })
  },

  addPowerbank(pobk_location_id, pobk_amount) {
    return request({
      url: '/manage/powerbanks',
      method: 'put',
      params: { pobk_location_id, pobk_amount }
    })
  },

  transferPowerbank(pobk_id, pobk_location_id) {
    return request({
      url: `/manage/powerbanks/${pobk_id}`,
      method: 'post',
      params: { pobk_location_id }
    })
  },

  deletePowerbank(pobk_id) {
    return request({
      url: `/manage/powerbanks/${pobk_id}`,
      method: 'delete'
    })
  }
}
