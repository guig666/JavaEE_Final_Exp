<template>
  <!-- 登录页：核心功能是管理员身份验证，通过表单验证 + Pinia状态管理实现登录 -->
  <div class="login-container">
    <div class="login-box">
      <!-- Logo区域 -->
      <div class="logo">
        <el-icon :size="60" color="#409EFF"><Box /></el-icon>
      </div>
      <h2>共享充电宝管理系统</h2>
      
      <!-- 登录表单：使用Element Plus表单组件，带验证规则 -->
      <el-form 
        :model="loginForm"    <!-- 表单数据绑定到loginForm对象 -->
        :rules="rules"        <!-- 表单验证规则 -->
        ref="loginFormRef"    <!-- 表单引用，用于手动触发验证 -->
        label-width="0" 
        class="login-form"
      >
        <!-- 账号输入框 -->
        <el-form-item prop="account">
          <el-input v-model="loginForm.account" placeholder="请输入账号" prefix-icon="User" size="large" />
        </el-form-item>
        <!-- 密码输入框（带显示/隐藏按钮） -->
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <!-- 登录按钮 -->
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width: 100%;">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
// 组合式API + 路由 + Pinia状态管理
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Box, User, Lock } from '@element-plus/icons-vue'
import { useAdminStore } from '@/stores/admin'

// 核心实例：路由用于页面跳转，adminStore管理登录状态
const router = useRouter()
const adminStore = useAdminStore()

// 表单引用和加载状态
const loginFormRef = ref(null)
const loading = ref(false)

// 响应式表单数据（双向绑定到表单控件）
const loginForm = reactive({
  account: '',
  password: ''
})

// 表单验证规则（非空校验）
const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

/**
 * 核心登录流程：
 * 1. 触发表单验证
 * 2. 调用Pinia的login方法（封装了HTTP请求和状态持久化）
 * 3. 登录成功后跳转到首页
 */
async function handleLogin() {
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await adminStore.login(loginForm.account, loginForm.password)
        ElMessage.success('登录成功')
        router.push('/')  // 路由跳转
      } catch (e) { console.error(e) }
      finally { loading.value = false }
    }
  })
}
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.logo {
  text-align: center;
  margin-bottom: 20px;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 22px;
}

.login-form {
  margin-top: 30px;
}
</style>
