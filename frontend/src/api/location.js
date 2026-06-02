import request from '@/utils/request'

export default {
  getLocations(pageNum = 1) {
    return request({
      url: '/client/locations',
      method: 'get',
      params: { pageNum, pageSize: 100 }
    })
  },
  generateMockLocations(longitude, latitude) {
    return request({
      url: '/client/locations/generateMock',
      method: 'get',
      params: { longitude, latitude }
    })
  }
}
