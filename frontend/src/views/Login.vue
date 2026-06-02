<template>
  <div class="login-container">
    <van-nav-bar title="共享充电宝" />
    
    <div class="content">
      <div class="logo">
        <svg width="120" height="120" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect x="6" y="7" width="12" height="10" rx="2" stroke="white" stroke-width="2"/>
          <rect x="4" y="4" width="2" height="3" rx="1" fill="white"/>
          <rect x="8" y="10" width="8" height="4" rx="1" fill="white"/>
        </svg>
        <h2>共享充电宝</h2>
      </div>

      <van-tabs v-model:active="activeTab" animated>
        <van-tab title="登录">
          <div class="form">
            <van-cell-group inset>
              <van-field
                v-model="loginForm.phone"
                label="手机号"
                placeholder="请输入手机号"
                type="tel"
                maxlength="11"
              />
              <van-field
                v-model="loginForm.password"
                type="password"
                label="密码"
                placeholder="请输入密码"
              />
            </van-cell-group>
            <van-button type="primary" round block class="btn" :loading="loginLoading" @click="handleLogin">
              登录
            </van-button>
          </div>
        </van-tab>
        
        <van-tab title="注册">
          <div class="form">
            <van-cell-group inset>
              <van-field
                v-model="registerForm.phone"
                label="手机号"
                placeholder="请输入手机号"
                type="tel"
                maxlength="11"
              />
              <van-field
                v-model="registerForm.alias"
                label="昵称"
                placeholder="请输入昵称"
              />
              <van-field
                v-model="registerForm.password"
                type="password"
                label="密码"
                placeholder="请输入密码"
              />
              <van-field
                v-model="registerForm.confirmPassword"
                type="password"
                label="确认密码"
                placeholder="请再次输入密码"
              />
            </van-cell-group>
            <van-button type="primary" round block class="btn" :loading="registerLoading" @click="handleRegister">
              注册
            </van-button>
          </div>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref(0)
const loginLoading = ref(false)
const registerLoading = ref(false)

const loginForm = ref({
  phone: '',
  password: ''
})

const registerForm = ref({
  phone: '',
  alias: '',
  password: '',
  confirmPassword: ''
})

async function handleLogin() {
  if (!loginForm.value.phone || !loginForm.value.password) {
    showToast('请填写完整信息')
    return
  }

  if (!/^1[3-9]\d{9}$/.test(loginForm.value.phone)) {
    showToast('请输入正确的手机号')
    return
  }

  loginLoading.value = true
  try {
    await userStore.login(loginForm.value.phone, loginForm.value.password)
    showToast('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loginLoading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.value.phone || !registerForm.value.alias || !registerForm.value.password || !registerForm.value.confirmPassword) {
    showToast('请填写完整信息')
    return
  }

  if (!/^1[3-9]\d{9}$/.test(registerForm.value.phone)) {
    showToast('请输入正确的手机号')
    return
  }

  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    showToast('两次输入的密码不一致')
    return
  }

  if (registerForm.value.password.length < 6) {
    showToast('密码长度不能少于6位')
    return
  }

  registerLoading.value = true
  try {
    await userStore.register(registerForm.value.phone, registerForm.value.password, registerForm.value.alias)
    showToast('注册成功，请登录')
    activeTab.value = 0
    loginForm.value.phone = registerForm.value.phone
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped lang="less">
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.content {
  padding: 40px 20px;
}

.logo {
  text-align: center;
  margin-bottom: 40px;
  
  img {
    width: 120px;
    height: 120px;
  }
  
  h2 {
    color: white;
    margin-top: 10px;
    font-size: 24px;
  }
}

.form {
  padding-top: 20px;
}

.btn {
  margin-top: 30px;
  height: 46px;
  font-size: 16px;
}

:deep(.van-tabs__nav) {
  background: transparent;
}

:deep(.van-tab) {
  color: rgba(255, 255, 255, 0.7);
}

:deep(.van-tab--active) {
  color: white;
  font-weight: bold;
}

:deep(.van-tabs__line) {
  background-color: white;
}
</style>
