<template>
  <div class="orders-container">
    <van-nav-bar title="我的订单" />
    
    <div class="content" v-loading="loading">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-empty v-if="orders.length === 0 && !loading" description="暂无订单">
          <van-button type="primary" @click="$router.push('/')">去租借</van-button>
        </van-empty>
        
        <div v-else class="order-list">
          <div v-for="order in orders" :key="order.order_id" class="order-card" @click="showOrderDetail(order)">
            <div class="order-header">
              <span class="status" :class="getStatusClass(order)">
                {{ getStatusText(order) }}
              </span>
              <span class="time">{{ formatTime(order.order_create_time) }}</span>
            </div>
            
            <div class="order-info">
              <div class="info-row">
                <span class="label">订单号</span>
                <span class="value">{{ order.order_id }}</span>
              </div>
              <div class="info-row" v-if="order.order_cost">
                <span class="label">费用</span>
                <span class="value price">¥{{ order.order_cost }}</span>
              </div>
              <div class="info-row" v-if="order.order_finish_time">
                <span class="label">归还时间</span>
                <span class="value">{{ formatTime(order.order_finish_time) }}</span>
              </div>
            </div>
            
            <div class="order-footer">
              <van-button 
                v-if="isBorrowing(order)" 
                type="primary" 
                size="small" 
                @click.stop="goReturn(order)"
              >
                去归还
              </van-button>
            </div>
          </div>
        </div>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import orderApi from '@/api/order'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const refreshing = ref(false)
const orders = ref([])

function formatTime(time) {
  if (!time) return '-'
  // 如果已经是格式化好的字符串，直接返回
  if (typeof time === 'string' && time.includes('-')) return time
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

function getStatusText(order) {
  if (order.order_finish_time) {
    return '已归还'
  }
  return '借用中'
}

function getStatusClass(order) {
  if (order.order_finish_time) {
    return 'completed'
  }
  return 'borrowing'
}

function isBorrowing(order) {
  return !order.order_finish_time
}

async function loadOrders() {
  loading.value = true
  try {
    if (!userStore.isLoggedIn) {
      showToast('请先登录')
      router.push('/login')
      return
    }
    
    const res = await orderApi.getOrders()
    if (res.data) {
      orders.value = Array.isArray(res.data) ? res.data : []
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    if (error.message && error.message.includes('参数有误')) {
      showToast('请重新登录')
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}

async function onRefresh() {
  await loadOrders()
  refreshing.value = false
}

function showOrderDetail(order) {
  console.log('订单详情:', order)
}

function goReturn(order) {
  router.push('/return')
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped lang="less">
.orders-container {
  min-height: 100vh;
  background: #f7f8fa;
}

.content {
  padding: 16px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    
    .status {
      padding: 4px 12px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: bold;
      
      &.borrowing {
        background: #e8f7ff;
        color: #1989fa;
      }
      
      &.completed {
        background: #f0f9eb;
        color: #07c160;
      }
    }
    
    .time {
      color: #969799;
      font-size: 13px;
    }
  }
  
  .order-info {
    .info-row {
      display: flex;
      justify-content: space-between;
      padding: 8px 0;
      
      .label {
        color: #646566;
        font-size: 14px;
      }
      
      .value {
        color: #323233;
        font-size: 14px;
        
        &.price {
          color: #ee0a24;
          font-weight: bold;
        }
      }
    }
  }
  
  .order-footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #f2f3f5;
  }
}
</style>
