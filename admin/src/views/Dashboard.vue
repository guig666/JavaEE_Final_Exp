<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user">
              <el-icon :size="40"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon location">
              <el-icon :size="40"><Location /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.locationCount }}</div>
              <div class="stat-label">投放点数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon powerbank">
              <el-icon :size="40"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.powerbankCount }}</div>
              <div class="stat-label">充电宝数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.orderCount }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>订单趋势</span>
          </template>
          <div ref="orderChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>借用状态分布</span>
          </template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>各投放点充电宝数量</span>
          </template>
          <div ref="locationChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { User, Location, Box, Document } from '@element-plus/icons-vue'

const stats = ref({
  userCount: 0,
  locationCount: 0,
  powerbankCount: 0,
  orderCount: 0
})

const orderChartRef = ref(null)
const statusChartRef = ref(null)
const locationChartRef = ref(null)

let orderChart = null
let statusChart = null
let locationChart = null

function initOrderChart() {
  if (!orderChartRef.value) return
  
  orderChart = echarts.init(orderChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '订单数',
        type: 'line',
        data: [120, 200, 150, 80, 70, 110, 130],
        smooth: true,
        areaStyle: {
          opacity: 0.3
        }
      }
    ]
  }
  
  orderChart.setOption(option)
}

function initStatusChart() {
  if (!statusChartRef.value) return
  
  statusChart = echarts.init(statusChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: '5%',
      left: 'center'
    },
    series: [
      {
        name: '借用状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 35, name: '借用中' },
          { value: 65, name: '已归还' }
        ]
      }
    ]
  }
  
  statusChart.setOption(option)
}

function initLocationChart() {
  if (!locationChartRef.value) return
  
  locationChart = echarts.init(locationChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: ['地点A', '地点B', '地点C', '地点D', '地点E', '地点F']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '充电宝数量',
        type: 'bar',
        data: [50, 32, 45, 28, 60, 35],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        }
      }
    ]
  }
  
  locationChart.setOption(option)
}

function handleResize() {
  orderChart?.resize()
  statusChart?.resize()
  locationChart?.resize()
}

onMounted(() => {
  initOrderChart()
  initStatusChart()
  initLocationChart()
  
  setInterval(() => {
    stats.value = {
      userCount: Math.floor(Math.random() * 100) + 200,
      locationCount: Math.floor(Math.random() * 20) + 30,
      powerbankCount: Math.floor(Math.random() * 200) + 500,
      orderCount: Math.floor(Math.random() * 500) + 1000
    }
  }, 2000)
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  orderChart?.dispose()
  statusChart?.dispose()
  locationChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.dashboard-container {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .stat-icon {
        width: 80px;
        height: 80px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        
        &.user {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        &.location {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        
        &.powerbank {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
        
        &.order {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }
      
      .stat-info {
        flex: 1;
        
        .stat-value {
          font-size: 32px;
          font-weight: bold;
          color: #333;
        }
        
        .stat-label {
          font-size: 14px;
          color: #999;
          margin-top: 5px;
        }
      }
    }
  }
  
  .chart-container {
    height: 300px;
    width: 100%;
  }
}
</style>
