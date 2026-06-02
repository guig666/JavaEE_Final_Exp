<template>
  <div class="return-container">
    <van-nav-bar title="归还充电宝" left-arrow @click-left="$router.back()" />
    
    <div class="content" v-loading="loading">
      <div v-if="currentOrder" class="order-card">
        <div class="order-header">
          <span class="status borrowing">借用中</span>
          <span class="order-id">订单号: {{ currentOrder.order_id }}</span>
        </div>
        
        <div class="order-info">
          <div class="info-item">
            <span class="label">借用时间</span>
            <span class="value">{{ formatTime(currentOrder.order_create_time) }}</span>
          </div>
          <div class="info-item">
            <span class="label">借用时长</span>
            <span class="value">{{ borrowDuration }}</span>
          </div>
          <div class="info-item" v-if="currentOrder.order_cost">
            <span class="label">当前费用</span>
            <span class="value price">¥{{ currentOrder.order_cost }}</span>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <svg width="120" height="120" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect x="6" y="7" width="12" height="10" rx="2" stroke="#969799" stroke-width="2"/>
          <rect x="4" y="4" width="2" height="3" rx="1" stroke="#969799" stroke-width="1"/>
          <rect x="8" y="10" width="8" height="4" rx="1" stroke="#969799" stroke-width="1"/>
          <line x1="12" y1="10" x2="12" y2="14" stroke="#969799" stroke-width="2"/>
        </svg>
        <p>暂无借用中的订单</p>
        <van-button type="primary" @click="$router.push('/')">去租借</van-button>
      </div>

      <div v-if="currentOrder" class="return-form">
        <van-cell-group inset>
          <van-field
            v-model="form.navLocationId"
            label="归还投放点编号"
            placeholder="请输入投放点编号"
            type="number"
          />
        </van-cell-group>
        <van-button type="primary" round block class="return-btn" :loading="returnLoading" @click="handleReturn">
          确认归还
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import orderApi from '@/api/order'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const returnLoading = ref(false)
const currentOrder = ref(null)
const form = ref({
  navLocationId: ''
})

const borrowDuration = computed(() => {
  if (!currentOrder.value?.order_create_time) return '-'
  
  const borrowTime = new Date(currentOrder.value.order_create_time).getTime()
  const now = Date.now()
  const diff = now - borrowTime
  
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  }
  return `${minutes}分钟`
})

function formatTime(time) {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

async function loadCurrentOrder() {
  loading.value = true
  try {
    if (!userStore.isLoggedIn) {
      showToast('请先登录')
      router.push('/login')
      return
    }
    
    const res = await orderApi.getOrders()
    if (res.data) {
      currentOrder.value = res.data.find(order => !order.order_finish_time)
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

async function handleReturn() {
  if (!form.value.navLocationId) {
    showToast('请输入归还投放点编号')
    return
  }

  returnLoading.value = true
  showLoadingToast({ message: '归还中...', forbidClick: true, duration: 0 })
  
  try {
    await orderApi.returnOrder(currentOrder.value.order_id, parseInt(form.value.navLocationId))
    // 归还成功后刷新用户信息（更新余额）
    if (userStore.userId) {
      await userStore.getUserInfo(userStore.userId)
    }
    closeToast()
    showToast('归还成功')
    setTimeout(() => {
      router.push('/orders')
    }, 1500)
  } catch (error) {
    closeToast()
    console.error('归还失败:', error)
  } finally {
    returnLoading.value = false
  }
}

onMounted(() => {
  loadCurrentOrder()
})
</script>

<style scoped lang="less">
.return-container {
  min-height: 100vh;
  background: #f7f8fa;
}

.content {
  padding: 20px;
}

.order-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .status {
      padding: 4px 12px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: bold;
      
      &.borrowing {
        background: #e8f7ff;
        color: #1989fa;
      }
    }
    
    .order-id {
      color: #969799;
      font-size: 13px;
    }
  }
  
  .order-info {
    .info-item {
      display: flex;
      justify-content: space-between;
      padding: 12px 0;
      border-bottom: 1px solid #f2f3f5;
      
      &:last-child {
        border-bottom: none;
      }
      
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
          font-size: 18px;
        }
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  
  img {
    width: 120px;
    height: 120px;
    margin-bottom: 20px;
  }
  
  p {
    color: #969799;
    margin-bottom: 20px;
  }
}

.return-form {
  .return-btn {
    margin-top: 20px;
    height: 46px;
  }
}
</style>
