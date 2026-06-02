<template>
  <div class="profile-container">
    <van-nav-bar title="个人中心" />
    
    <div class="content">
      <div class="user-card" v-if="userStore.user">
        <div class="avatar">
          <svg width="70" height="70" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="8" r="4" stroke="#667eea" stroke-width="2"/>
            <path d="M4 20C4 16.6863 7.58172 14 12 14C16.4183 14 20 16.6863 20 20" stroke="#667eea" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="user-info">
          <h3>{{ userStore.user.user_alias || '用户' }}</h3>
          <p class="phone">{{ userStore.user.user_phone }}</p>
          <p class="balance">余额: <span class="amount">¥{{ userStore.user.user_balance || 0 }}</span></p>
        </div>
      </div>

      <van-cell-group inset class="menu-group">
        <van-cell title="充值" is-link @click="showRechargeDialog = true">
          <template #icon>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="6" width="18" height="14" rx="2" stroke="#1989fa" stroke-width="2"/>
              <path d="M3 10H21" stroke="#1989fa" stroke-width="2"/>
              <circle cx="17" cy="14" r="2" fill="#1989fa"/>
            </svg>
          </template>
        </van-cell>
        <van-cell title="修改昵称" is-link @click="showAliasDialog = true">
          <template #icon>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="8" r="4" stroke="#1989fa" stroke-width="2"/>
              <path d="M4 20C4 16.6863 7.58172 14 12 14C16.4183 14 20 16.6863 20 20" stroke="#1989fa" stroke-width="2" stroke-linecap="round"/>
              <path d="M16 6L20 2M20 2L16 6M20 2H18M20 2V4" stroke="#1989fa" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </template>
        </van-cell>
        <van-cell title="修改手机号" is-link @click="showPhoneDialog = true">
          <template #icon>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="5" y="2" width="14" height="20" rx="2" stroke="#1989fa" stroke-width="2"/>
              <line x1="12" y1="18" x2="12" y2="18.01" stroke="#1989fa" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </template>
        </van-cell>
        <van-cell title="修改密码" is-link @click="showPasswordDialog = true">
          <template #icon>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="5" y="11" width="14" height="10" rx="2" stroke="#1989fa" stroke-width="2"/>
              <path d="M8 11V7C8 4.79086 9.79086 3 12 3C14.2091 3 16 4.79086 16 7V11" stroke="#1989fa" stroke-width="2" stroke-linecap="round"/>
              <circle cx="12" cy="16" r="1" fill="#1989fa"/>
            </svg>
          </template>
        </van-cell>
      </van-cell-group>

      <div class="logout-btn">
        <van-button round block type="danger" @click="handleLogout">
          退出登录
        </van-button>
      </div>
    </div>

    <van-dialog v-model:show="showRechargeDialog" title="充值" show-cancel-button @confirm="handleRecharge">
      <van-field v-model="rechargeAmount" type="number" placeholder="请输入充值金额" style="padding: 16px;" />
    </van-dialog>

    <van-dialog v-model:show="showAliasDialog" title="修改昵称" show-cancel-button @confirm="handleUpdateAlias">
      <van-field v-model="newAlias" placeholder="请输入新昵称" style="padding: 16px;" />
    </van-dialog>

    <van-dialog v-model:show="showPhoneDialog" title="修改手机号" show-cancel-button @confirm="handleUpdatePhone">
      <van-field v-model="newPhone" type="tel" maxlength="11" placeholder="请输入新手机号" style="padding: 16px;" />
    </van-dialog>

    <van-dialog v-model:show="showPasswordDialog" title="修改密码" show-cancel-button @confirm="handleUpdatePassword">
      <van-form style="padding: 16px;">
        <van-field v-model="oldPassword" type="password" label="原密码" placeholder="请输入原密码" />
        <van-field v-model="newPassword" type="password" label="新密码" placeholder="请输入新密码" />
        <van-field v-model="confirmPassword" type="password" label="确认密码" placeholder="请再次输入新密码" />
      </van-form>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showToast, showLoadingToast, closeToast } from 'vant'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const showRechargeDialog = ref(false)
const showAliasDialog = ref(false)
const showPhoneDialog = ref(false)
const showPasswordDialog = ref(false)

const rechargeAmount = ref('')
const newAlias = ref('')
const newPhone = ref('')
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

async function refreshUserInfo() {
  if (userStore.userId) {
    await userStore.getUserInfo(userStore.userId)
  }
}

onMounted(() => {
  refreshUserInfo()
})

async function handleRecharge() {
  const amount = parseFloat(rechargeAmount.value)
  if (!amount || amount <= 0) {
    showToast('请输入有效金额')
    return
  }

  showLoadingToast({ message: '充值中...', forbidClick: true, duration: 0 })
  try {
    await userStore.recharge(userStore.userId, amount)
    closeToast()
    showToast('充值成功')
    showRechargeDialog.value = false
    rechargeAmount.value = ''
  } catch (error) {
    closeToast()
    console.error('充值失败:', error)
  }
}

async function handleUpdateAlias() {
  if (!newAlias.value) {
    showToast('请输入昵称')
    return
  }

  showLoadingToast({ message: '修改中...', forbidClick: true, duration: 0 })
  try {
    await userStore.updateAlias(userStore.userId, newAlias.value)
    closeToast()
    showToast('修改成功')
    showAliasDialog.value = false
    newAlias.value = ''
  } catch (error) {
    closeToast()
    console.error('修改失败:', error)
  }
}

async function handleUpdatePhone() {
  if (!newPhone.value) {
    showToast('请输入手机号')
    return
  }

  if (!/^1[3-9]\d{9}$/.test(newPhone.value)) {
    showToast('请输入正确的手机号')
    return
  }

  showLoadingToast({ message: '修改中...', forbidClick: true, duration: 0 })
  try {
    await userStore.updatePhone(userStore.userId, newPhone.value)
    closeToast()
    showToast('修改成功')
    showPhoneDialog.value = false
    newPhone.value = ''
  } catch (error) {
    closeToast()
    console.error('修改失败:', error)
  }
}

async function handleUpdatePassword() {
  if (!oldPassword.value || !newPassword.value || !confirmPassword.value) {
    showToast('请填写完整信息')
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    showToast('两次输入的密码不一致')
    return
  }

  if (newPassword.value.length < 6) {
    showToast('密码长度不能少于6位')
    return
  }

  showLoadingToast({ message: '修改中...', forbidClick: true, duration: 0 })
  try {
    await userStore.updatePassword(userStore.userId, oldPassword.value, newPassword.value)
    closeToast()
    showToast('修改成功')
    showPasswordDialog.value = false
    oldPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  } catch (error) {
    closeToast()
    console.error('修改失败:', error)
  }
}

async function handleLogout() {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要退出登录吗？'
    })
    
    await userStore.logout()
    showToast('退出成功')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出过程中出现错误:', error)
      router.push('/login')
    }
  }
}
</script>

<style scoped lang="less">
.profile-container {
  min-height: 100vh;
  background: #f7f8fa;
}

.content {
  padding: 16px;
}

.user-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px 20px;
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  
  .avatar svg {
    width: 70px;
    height: 70px;
    border-radius: 50%;
    background: white;
  }
  
  .user-info {
    margin-left: 16px;
    flex: 1;
    
    h3 {
      color: white;
      margin: 0 0 8px 0;
      font-size: 20px;
    }
    
    .phone {
      color: rgba(255, 255, 255, 0.8);
      font-size: 14px;
      margin: 0 0 8px 0;
    }
    
    .balance {
      color: white;
      font-size: 14px;
      margin: 0;
      
      .amount {
        font-size: 24px;
        font-weight: bold;
      }
    }
  }
}

.menu-group {
  margin-bottom: 20px;
}

.logout-btn {
  margin-top: 40px;
}
</style>
