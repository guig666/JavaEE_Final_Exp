<template>
  <div class="borrow-container">
    <van-nav-bar title="租借充电宝" left-arrow @click-left="$router.back()" />
    
    <div class="content">
      <div class="scan-area">
        <div class="scan-icon">
          <svg width="120" height="120" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="3" y="3" width="18" height="18" rx="2" stroke="#1989fa" stroke-width="2"/>
            <rect x="7" y="7" width="10" height="10" stroke="#1989fa" stroke-width="2"/>
            <line x1="12" y1="3" x2="12" y2="7" stroke="#1989fa" stroke-width="2"/>
            <line x1="12" y1="17" x2="12" y2="21" stroke="#1989fa" stroke-width="2"/>
            <line x1="3" y1="12" x2="7" y2="12" stroke="#1989fa" stroke-width="2"/>
            <line x1="17" y1="12" x2="21" y2="12" stroke="#1989fa" stroke-width="2"/>
          </svg>
        </div>
        <p class="tip">扫码借充电宝</p>
        <p class="sub-tip">或者手动输入编号</p>
      </div>

      <van-form @submit="handleBorrow">
        <van-cell-group inset>
          <van-field
            v-model="form.navLocationId"
            name="navLocationId"
            label="投放点编号"
            placeholder="请输入投放点编号"
            type="number"
            :rules="[{ required: true, message: '请输入投放点编号' }]"
          />
        </van-cell-group>
        <div class="btn-group">
          <van-button round block type="primary" native-type="submit" :loading="loading">
            确认租借
          </van-button>
        </div>
      </van-form>

      <div class="notice">
        <h4>租借说明：</h4>
        <ul>
          <li>请确保余额充足</li>
          <li>按时归还以免产生额外费用</li>
          <li>如有问题请联系客服</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import orderApi from '@/api/order'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const form = ref({
  navLocationId: ''
})

onMounted(() => {
  // 获取路由参数中的locationId
  const locationId = route.query.locationId
  if (locationId) {
    form.value.navLocationId = locationId
  }
})

async function handleBorrow() {
  if (!form.value.navLocationId) {
    showToast('请输入投放点编号')
    return
  }

  loading.value = true
  showLoadingToast({ message: '租借中...', forbidClick: true, duration: 0 })
  
  try {
    await orderApi.createOrder(parseInt(form.value.navLocationId))
    // 租借成功后刷新用户信息（更新余额）
    if (userStore.userId) {
      await userStore.getUserInfo(userStore.userId)
    }
    closeToast()
    showToast('租借成功')
    setTimeout(() => {
      router.push('/orders')
    }, 1500)
  } catch (error) {
    closeToast()
    console.error('租借失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="less">
.borrow-container {
  min-height: 100vh;
  background: #f7f8fa;
}

.content {
  padding: 20px;
}

.scan-area {
  text-align: center;
  padding: 40px 0;
  
  .scan-icon img {
    width: 120px;
    height: 120px;
  }
  
  .tip {
    font-size: 18px;
    color: #323233;
    margin: 20px 0 8px 0;
  }
  
  .sub-tip {
    font-size: 14px;
    color: #969799;
  }
}

.btn-group {
  margin-top: 30px;
}

.notice {
  margin-top: 40px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  
  h4 {
    margin: 0 0 15px 0;
    font-size: 15px;
    color: #323233;
  }
  
  ul {
    margin: 0;
    padding-left: 20px;
    
    li {
      color: #646566;
      font-size: 14px;
      margin-bottom: 8px;
    }
  }
}
</style>
