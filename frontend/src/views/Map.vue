<template>
  <div class="map-container">
    <van-nav-bar title="附近充电站" />
    <div id="map-container" class="map"></div>
    
    <div class="legend">
      <div class="legend-item">
        <span class="legend-icon powerbank"></span>
        <span class="legend-text">充电站</span>
      </div>
      <div class="legend-item">
        <span class="legend-icon user"></span>
        <span class="legend-text">我的位置</span>
      </div>
    </div>
    
    <div class="find-nearest-btn" @click="findNearestStation">
      <span>找充电宝</span>
    </div>
    
    <div class="my-location-btn" @click="goToMyLocation">
      <span>我的位置</span>
    </div>
    
    <div class="manual-location-btn" @click="toggleManualMode">
      <span>{{ isManualMode ? '完成定位' : '手动定位' }}</span>
    </div>
    
    <transition name="fade">
      <div class="manual-tip" v-if="isManualMode">
        <van-icon name="hand" size="14" />
        <span>点击地图选择您的位置</span>
      </div>
    </transition>
    
    <transition name="fade">
      <div class="location-status" v-if="locationStatus">
        <van-icon :name="locationStatus.includes('失败') ? 'warning-o' : 'success'" :color="locationStatus.includes('失败') ? '#faad14' : '#07c160'" />
        <span>{{ locationStatus }}</span>
      </div>
    </transition>
    
    <transition name="fade">
      <div class="distance-tip" v-if="nearestDistance">
        <van-icon name="info" />
        <span>最近充电站距离您约 {{ nearestDistance }} 米</span>
      </div>
    </transition>
    
    <van-popup v-model:show="showLocationPopup" position="bottom" round>
      <div class="location-detail" v-if="currentLocation">
        <div class="popup-header">
          <div class="powerbank-icon-large"></div>
          <div class="header-info">
            <h3>{{ currentLocation.location_alias || '充电站' }}</h3>
            <span class="location-id">ID: {{ currentLocation.location_id }}</span>
          </div>
        </div>
        <p class="address">{{ currentLocation.location_city }}{{ currentLocation.location_district }}{{ currentLocation.location_address }}</p>
        <div class="info">
          <div class="info-item">
            <span class="info-icon available-icon"></span>
            <span class="available">可用: {{ currentLocation.location_available || 0 }} 个</span>
          </div>
          <div class="info-item">
            <span class="info-icon total-icon"></span>
            <span class="total">总数: {{ currentLocation.location_amount || 0 }} 个</span>
          </div>
        </div>
        <div class="distance-info" v-if="currentLocation.distance">
          <van-icon name="location-o" />
          <span>距离您约 {{ Math.round(currentLocation.distance) }} 米</span>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: getProgressPercent() + '%' }"></div>
        </div>
        <div class="status-text">{{ getStatusText() }}</div>
        <van-button type="primary" block @click="goToBorrow">前往租借</van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showLoadingToast, closeToast, showToast, showSuccessToast } from 'vant'
import locationApi from '@/api/location'

const router = useRouter()

let map = null
let markers = []
let userPosition = null
let userMarker = null
const locations = ref([])
const showLocationPopup = ref(false)
const currentLocation = ref(null)
const nearestDistance = ref('')
const locationAccuracy = ref('')
const locationStatus = ref('')
const isManualMode = ref(false)

function createPowerbankIcon(available, total) {
  const ratio = total > 0 ? available / total : 0
  let color = '#07c160'
  if (ratio < 0.2) {
    color = '#ff4d4f'
  } else if (ratio < 0.5) {
    color = '#faad14'
  }
  
  return new AMap.Icon({
    size: new AMap.Size(48, 48),
    image: createIconCanvas(color),
    imageSize: new AMap.Size(48, 48)
  })
}

function createIconCanvas(color) {
  const canvas = document.createElement('canvas')
  canvas.width = 48
  canvas.height = 48
  const ctx = canvas.getContext('2d')
  
  ctx.beginPath()
  ctx.arc(24, 24, 22, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(255, 255, 255, 0.95)'
  ctx.fill()
  ctx.strokeStyle = color
  ctx.lineWidth = 3
  ctx.stroke()
  
  ctx.fillStyle = color
  ctx.fillRect(14, 16, 20, 18)
  
  ctx.fillStyle = '#333'
  ctx.fillRect(16, 12, 4, 6)
  ctx.fillRect(28, 12, 4, 6)
  
  ctx.fillStyle = '#fff'
  ctx.fillRect(16, 22, 4, 2)
  ctx.fillRect(22, 22, 4, 2)
  ctx.fillRect(28, 22, 4, 2)
  
  return canvas.toDataURL()
}

function generateMockLocations(userLng, userLat) {
  const stationNames = [
    '万达广场充电站', '中心医院充电站', '火车站充电站', 
    '大学城充电站', '科技园充电站', '商业街充电站',
    '体育馆充电站', '图书馆充电站', '公园充电站',
    '地铁站充电站', '购物中心充电站', '写字楼充电站',
    '小区充电站', '学校充电站', '医院充电站'
  ]
  
  const addresses = [
    '一层大厅', '北门入口', '南门出口', '地下停车场',
    '东门服务台', '西门便利店旁', '主楼大厅', '附楼一层'
  ]
  
  const mockData = []
  const numStations = 8 + Math.floor(Math.random() * 5)
  
  for (let i = 0; i < numStations; i++) {
    const angle = Math.random() * Math.PI * 2
    const distance = 0.005 + Math.random() * 0.02
    
    const lng = userLng + Math.cos(angle) * distance
    const lat = userLat + Math.sin(angle) * distance
    
    const total = 6 + Math.floor(Math.random() * 15)
    const available = Math.floor(Math.random() * (total + 1))
    
    mockData.push({
      location_id: i + 1,
      location_alias: stationNames[i % stationNames.length],
      location_city: '',
      location_district: '',
      location_address: addresses[i % addresses.length],
      location_longitude: lng,
      location_latitude: lat,
      location_available: available,
      location_amount: total
    })
  }
  
  return mockData
}

function calculateDistance(lat1, lng1, lat2, lng2) {
  const R = 6371000
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return R * c
}

function findNearestStation() {
  if (!userPosition || locations.value.length === 0) {
    showToast('请先获取您的位置')
    return
  }
  
  let nearest = null
  let minDistance = Infinity
  
  locations.value.forEach(location => {
    const distance = calculateDistance(
      userPosition.lat, userPosition.lng,
      location.location_latitude, location.location_longitude
    )
    location.distance = distance
    
    if (distance < minDistance) {
      minDistance = distance
      nearest = location
    }
  })
  
  if (nearest) {
    nearestDistance.value = Math.round(minDistance)
    
    map.setCenter([nearest.location_longitude, nearest.location_latitude])
    map.setZoom(17)
    
    currentLocation.value = nearest
    showLocationPopup.value = true
    
    showSuccessToast(`已定位到最近的充电站：${nearest.location_alias}`)
    
    markers.forEach((marker, index) => {
      if (locations.value[index] && locations.value[index].location_id === nearest.location_id) {
        try {
          marker.setAnimation('AMAP_ANIMATION_BOUNCE')
          setTimeout(() => {
            try {
              marker.setAnimation('AMAP_ANIMATION_NONE')
            } catch (e) {}
          }, 2000)
        } catch (e) {}
      }
    })
  }
}

function goToMyLocation() {
  if (!userPosition) {
    showToast('请先获取您的位置')
    return
  }
  
  map.setCenter([userPosition.lng, userPosition.lat])
  map.setZoom(16)
  
  if (userMarker) {
    try {
      userMarker.setAnimation('AMAP_ANIMATION_BOUNCE')
      setTimeout(() => {
        try {
          userMarker.setAnimation('AMAP_ANIMATION_NONE')
        } catch (e) {}
      }, 2000)
    } catch (e) {}
  }
  
  showSuccessToast('已定位到您的位置')
}

function toggleManualMode() {
  isManualMode.value = !isManualMode.value
  if (isManualMode.value) {
    showToast('请点击地图选择您的位置')
    map.on('click', handleMapClick)
  } else {
    map.off('click', handleMapClick)
  }
}

function handleMapClick(e) {
  if (!isManualMode.value) return
  
  const lng = e.lnglat.getLng()
  const lat = e.lnglat.getLat()
  
  userPosition = { lng, lat }
  locationStatus.value = '手动定位成功'
  locationAccuracy.value = '手动选择位置'
  
  if (userMarker) {
    map.remove(userMarker)
  }
  
  userMarker = new AMap.Marker({
    position: [lng, lat],
    icon: new AMap.Icon({
      size: new AMap.Size(32, 32),
      image: createUserIcon(),
      imageSize: new AMap.Size(32, 32)
    }),
    offset: new AMap.Pixel(-16, -16),
    zIndex: 999
  })
  map.add(userMarker)
  
  isManualMode.value = false
  map.off('click', handleMapClick)
  
  showSuccessToast('位置已更新')
}

async function loadLocations() {
  showLoadingToast({ message: '加载中...', forbidClick: true, duration: 0 })
  console.log('开始加载投放点数据...')
  try {
    const res = await locationApi.getLocations(1)
    console.log('接口返回原始数据:', res)
    if (res.data && res.data.list && res.data.list.length > 0) {
      locations.value = res.data.list
      addMarkers()
    } else {
      console.warn('接口返回数据为空，生成模拟数据')
      if (userPosition) {
        try {
          const mockRes = await locationApi.generateMockLocations(userPosition.lng, userPosition.lat)
          console.log('生成模拟数据返回:', mockRes)
          if (mockRes.data && mockRes.data.list && mockRes.data.list.length > 0) {
            locations.value = mockRes.data.list
            showToast('已生成 ' + mockRes.data.list.length + ' 个模拟充电桩')
          } else {
            locations.value = generateMockLocations(userPosition.lng, userPosition.lat)
          }
          addMarkers()
        } catch (mockError) {
          console.error('生成模拟数据失败:', mockError)
          locations.value = generateMockLocations(userPosition.lng, userPosition.lat)
          addMarkers()
        }
      }
    }
  } catch (error) {
    console.error('加载位置失败:', error)
    showToast('使用模拟数据')
    if (userPosition) {
      locations.value = generateMockLocations(userPosition.lng, userPosition.lat)
      addMarkers()
    }
  } finally {
    closeToast()
  }
}

async function initMap() {
  console.log('开始初始化地图...')
  if (typeof AMap === 'undefined') {
    console.error('AMap 对象未定义')
    showToast('地图加载失败，请检查配置')
    userPosition = { lng: 116.397428, lat: 39.90923 }
    locations.value = generateMockLocations(userPosition.lng, userPosition.lat)
    addMarkers()
    return
  }

  try {
    map = new AMap.Map('map-container', {
      resizeEnable: true,
      zoom: 14,
      center: [116.397428, 39.90923],
      logoPosition: 'RB',
      viewMode: '2D'
    })
    console.log('地图实例创建成功')

    userPosition = { lng: 116.397428, lat: 39.90923 }
    
    await attemptLocation()
  } catch (e) {
    console.error('地图初始化过程中出错:', e)
    userPosition = { lng: 116.397428, lat: 39.90923 }
    locations.value = generateMockLocations(userPosition.lng, userPosition.lat)
    addMarkers()
  }
}

async function attemptLocation() {
  const permission = await checkLocationPermission()
  console.log('定位权限状态:', permission)
  
  if (permission === 'denied') {
    showToast('定位权限已被拒绝，使用默认位置')
    locationStatus.value = '定位权限被拒绝'
    useDefaultLocation()
    return
  }

  const locationResult = await tryAmapGeolocation()
  if (locationResult.success) {
    handleLocationSuccess(locationResult.lng, locationResult.lat, locationResult.accuracy)
  } else {
    console.warn('高德定位失败，尝试浏览器定位...')
    const browserResult = await tryBrowserGeolocation()
    if (browserResult.success) {
      handleLocationSuccess(browserResult.lng, browserResult.lat, browserResult.accuracy)
    } else {
      handleLocationFail()
    }
  }
}

function tryAmapGeolocation() {
  return new Promise((resolve) => {
    if (!map) {
      resolve({ success: false })
      return
    }

    map.plugin('AMap.Geolocation', function() {
      const geolocation = new AMap.Geolocation({
        enableHighAccuracy: true,
        timeout: 10000,
        buttonPosition: 'RB',
        buttonOffset: new AMap.Pixel(10, 20),
        showButton: false,
        showMarker: false,
        showCircle: true,
        panToLocation: false,
        zoomToAccuracy: false,
        maximumAge: 0
      })

      const timeout = setTimeout(() => {
        console.warn('高德地图定位超时')
        resolve({ success: false })
      }, 10000)

      geolocation.getCurrentPosition((status, result) => {
        clearTimeout(timeout)
        if (status === 'complete') {
          resolve({
            success: true,
            lng: result.position.lng,
            lat: result.position.lat,
            accuracy: result.accuracy
          })
        } else {
          console.warn('高德地图定位失败:', result.message)
          resolve({ success: false })
        }
      })
    })
  })
}

function tryBrowserGeolocation() {
  return new Promise((resolve) => {
    if (!navigator.geolocation) {
      console.warn('浏览器不支持地理定位')
      resolve({ success: false })
      return
    }

    const timeout = setTimeout(() => {
      console.warn('浏览器定位超时')
      resolve({ success: false })
    }, 10000)

    navigator.geolocation.getCurrentPosition(
      (position) => {
        clearTimeout(timeout)
        resolve({
          success: true,
          lng: position.coords.longitude,
          lat: position.coords.latitude,
          accuracy: position.coords.accuracy
        })
      },
      (error) => {
        clearTimeout(timeout)
        console.warn('浏览器定位失败:', error.message)
        resolve({ success: false })
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0
      }
    )
  })
}

function useDefaultLocation() {
  userPosition = { lng: 116.397428, lat: 39.90923 }
  locations.value = generateMockLocations(userPosition.lng, userPosition.lat)
  addMarkers()
}

function handleLocationSuccess(lng, lat, accuracy) {
  userPosition = { lng, lat }
  console.log('获取用户位置成功:', userPosition)

  if (accuracy) {
    const acc = Math.round(accuracy)
    if (acc < 10) {
      locationAccuracy.value = `定位精度: 高 (${acc}米)`
    } else if (acc < 50) {
      locationAccuracy.value = `定位精度: 中 (${acc}米)`
    } else if (acc < 100) {
      locationAccuracy.value = `定位精度: 低 (${acc}米)`
    } else {
      locationAccuracy.value = `定位精度: 较差 (${acc}米)`
    }
  }
  locationStatus.value = '定位成功'

  addUserMarker(lng, lat)
  loadLocations()
}

function handleLocationFail() {
  console.warn('所有定位尝试都失败，使用默认位置')
  locationStatus.value = '定位失败，使用默认位置'
  useDefaultLocation()
  showToast('无法获取当前位置，已使用默认位置')
}

function createUserIcon() {
  const canvas = document.createElement('canvas')
  canvas.width = 32
  canvas.height = 32
  const ctx = canvas.getContext('2d')
  
  ctx.beginPath()
  ctx.arc(16, 16, 14, 0, Math.PI * 2)
  ctx.fillStyle = 'rgba(64, 158, 255, 0.2)'
  ctx.fill()
  
  ctx.beginPath()
  ctx.arc(16, 16, 10, 0, Math.PI * 2)
  ctx.fillStyle = '#409EFF'
  ctx.fill()
  
  ctx.beginPath()
  ctx.arc(16, 16, 5, 0, Math.PI * 2)
  ctx.fillStyle = '#fff'
  ctx.fill()
  
  return canvas.toDataURL()
}

async function checkLocationPermission() {
  if (!navigator.geolocation) {
    console.warn('浏览器不支持地理定位')
    return 'not_supported'
  }
  
  try {
    const result = await navigator.permissions.query({ name: 'geolocation' })
    return result.state
  } catch (e) {
    console.warn('权限查询失败:', e)
    return 'prompt'
  }
}

function addUserMarker(lng, lat) {
  if (userMarker) {
    map.remove(userMarker)
  }
  
  userMarker = new AMap.Marker({
    position: [lng, lat],
    icon: new AMap.Icon({
      size: new AMap.Size(32, 32),
      image: createUserIcon(),
      imageSize: new AMap.Size(32, 32)
    }),
    offset: new AMap.Pixel(-16, -16),
    zIndex: 999
  })
  map.add(userMarker)
  
  map.setCenter([lng, lat])
  map.setZoom(16)
}

function addMarkers() {
  if (!map) {
    console.warn('地图尚未初始化')
    return
  }

  console.log(`准备添加标记，当前共有 ${locations.value.length} 个投放点`)
  markers.forEach(marker => map.remove(marker))
  markers = []

  locations.value.forEach((location, index) => {
    const lng = parseFloat(location.location_longitude)
    const lat = parseFloat(location.location_latitude)
    const available = location.location_available || 0
    const total = location.location_amount || 0
    
    const icon = createPowerbankIcon(available, total)
    
    const marker = new AMap.Marker({
      position: [lng, lat],
      title: location.location_alias || '充电站',
      icon: icon,
      offset: new AMap.Pixel(-24, -24)
    })

    marker.on('click', () => {
      if (userPosition) {
        location.distance = calculateDistance(
          userPosition.lat, userPosition.lng, lat, lng
        )
      }
      currentLocation.value = location
      showLocationPopup.value = true
    })

    const infoWindow = new AMap.InfoWindow({
      content: `
        <div style="padding: 10px; min-width: 140px;">
          <div style="font-weight: bold; color: #333; margin-bottom: 3px;">${location.location_alias}</div>
          <div style="font-size: 11px; color: #999; margin-bottom: 5px;">ID: ${location.location_id}</div>
          <div style="font-size: 12px; color: #666;">可用: <span style="color: ${available > total * 0.5 ? '#07c160' : available > total * 0.2 ? '#faad14' : '#ff4d4f'}; font-weight: bold;">${available}</span> / ${total}</div>
        </div>
      `,
      offset: new AMap.Pixel(0, -40)
    })

    marker.on('mouseover', () => {
      infoWindow.open(map, marker.getPosition())
    })

    marker.on('mouseout', () => {
      infoWindow.close()
    })

    markers.push(marker)
    map.add(marker)
  })

  if (markers.length > 0) {
    map.setFitView(markers, true, [100, 100, 100, 100])
  }
}

function getProgressPercent() {
  if (!currentLocation.value) return 0
  const available = currentLocation.value.location_available || 0
  const total = currentLocation.value.location_amount || 1
  return Math.round((available / total) * 100)
}

function getStatusText() {
  if (!currentLocation.value) return ''
  const available = currentLocation.value.location_available || 0
  const total = currentLocation.value.location_amount || 1
  const ratio = available / total
  if (ratio >= 0.8) return '库存充足'
  if (ratio >= 0.5) return '库存正常'
  if (ratio >= 0.2) return '库存紧张'
  return '即将售罄'
}

function goToBorrow() {
  if (currentLocation.value) {
    showLocationPopup.value = false
    router.push({ 
      name: 'borrow',
      query: { locationId: currentLocation.value.location_id }
    })
  }
}

onMounted(() => {
  setTimeout(() => {
    initMap()
  }, 100)
})

onUnmounted(() => {
  if (map) {
    map.destroy()
  }
})
</script>

<style scoped>
.map-container {
  position: relative;
  height: 100vh;
  width: 100%;
}

#map-container {
  position: absolute;
  top: 46px;
  left: 0;
  right: 0;
  bottom: 60px;
}

.legend {
  position: absolute;
  top: 60px;
  left: 15px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  padding: 10px 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.legend-item {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.legend-item:last-child {
  margin-bottom: 0;
}

.legend-icon {
  width: 20px;
  height: 20px;
  margin-right: 8px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.legend-icon.powerbank {
  background: rgba(255, 255, 255, 0.95);
  border: 2px solid #07c160;
  position: relative;
}

.legend-icon.powerbank::after {
  content: '';
  width: 8px;
  height: 10px;
  background: #07c160;
  position: absolute;
  top: 5px;
}

.legend-icon.user {
  background: #409EFF;
  position: relative;
}

.legend-icon.user::after {
  content: '';
  width: 6px;
  height: 6px;
  background: #fff;
  border-radius: 50%;
  position: absolute;
}

.legend-text {
  font-size: 12px;
  color: #666;
}

.find-nearest-btn,
.my-location-btn,
.manual-location-btn {
  position: absolute;
  border-radius: 25px;
  padding: 10px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  font-size: 14px;
  cursor: pointer;
  z-index: 100;
}

.find-nearest-btn {
  bottom: 150px;
  left: 50%;
  transform: translateX(-50%);
  background: #FFC107;
  padding: 14px 30px;
  font-size: 18px;
  color: #fff;
  font-weight: bold;
}

.my-location-btn {
  bottom: 90px;
  right: 15px;
  background: #409EFF;
  color: #fff;
}

.manual-location-btn {
  bottom: 150px;
  right: 15px;
  background: #409EFF;
  color: #fff;
}

.manual-tip {
  position: absolute;
  top: 60px;
  right: 15px;
  background: rgba(64, 158, 255, 0.95);
  color: #fff;
  padding: 8px 15px;
  border-radius: 8px;
  font-size: 12px;
  display: flex;
  align-items: center;
  z-index: 100;
}

.location-status {
  position: absolute;
  top: 60px;
  right: 15px;
  background: rgba(255, 255, 255, 0.95);
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.location-status span {
  margin-left: 5px;
}

.distance-tip {
  position: absolute;
  top: 100px;
  right: 15px;
  background: rgba(255, 255, 255, 0.95);
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.distance-tip span {
  margin-left: 5px;
}

.location-detail {
  padding: 20px;
}

.popup-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.powerbank-icon-large {
  width: 50px;
  height: 50px;
  background: #07c160;
  border-radius: 50%;
  margin-right: 15px;
}

.header-info h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.location-id {
  font-size: 12px;
  color: #999;
}

.address {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
}

.info {
  display: flex;
  justify-content: space-around;
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  margin-right: 8px;
}

.info-icon.available-icon {
  background: #07c160;
}

.info-icon.total-icon {
  background: #999;
}

.available {
  font-size: 14px;
  color: #07c160;
}

.total {
  font-size: 14px;
  color: #999;
}

.distance-info {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #666;
  margin-bottom: 15px;
}

.progress-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #07c160, #91d5a8);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.status-text {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin-bottom: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>