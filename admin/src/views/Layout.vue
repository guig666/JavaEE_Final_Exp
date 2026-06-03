<template>
  <!-- 主布局：侧边栏导航 + 顶部栏 + 子路由视图，实现整体布局和权限控制 -->
  <el-container class="layout-container">
    <!-- 侧边栏（宽度200px） -->
    <el-aside width="200px">
      <!-- Logo区域 -->
      <div class="logo">
        <el-icon :size="32"><Box /></el-icon>
        <span>共享充电宝</span>
      </div>
      
      <!-- 导航菜单：开启router模式，自动路由跳转 -->
      <el-menu
        :default-active="activeMenu"  <!-- 当前激活的菜单项（根据路由自动切换） -->
        router                        <!-- 启用路由模式（点击菜单项自动跳转） -->
        background-color="#304156"   <!-- 菜单背景色 -->
        text-color="#bfcbd9"         <!-- 正常文字颜色 -->
        active-text-color="#409EFF"  <!-- 激活文字颜色 -->
      >
        <!-- 投放点管理 -->
        <el-menu-item index="/location">
          <el-icon><Location /></el-icon>
          <span>投放点管理</span>
        </el-menu-item>
        <!-- 充电宝管理 -->
        <el-menu-item index="/powerbank">
          <el-icon><Box /></el-icon>
          <span>充电宝管理</span>
        </el-menu-item>
        <!-- 用户管理 -->
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <!-- 订单管理 -->
        <el-menu-item index="/order">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <!-- 主内容容器 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header>
        <div class="header-content">
          <span class="title">共享充电宝管理系统</span>
          <!-- 用户信息下拉菜单 -->
          <div class="user-info">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <el-icon><User /></el-icon>
                {{ adminStore.admin?.admin_account }}  <!-- 当前登录管理员账号 -->
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      
      <!-- 主内容区域（子路由渲染位置） -->
      <el-main>
        <router-view />  <!-- 子页面内容在这里渲染 -->
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
// 计算属性 + 路由API + Pinia状态管理
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Box, Location, User, Document, ArrowDown } from '@element-plus/icons-vue'
import { useAdminStore } from '@/stores/admin'

// 路由实例：route获取当前路由信息，router用于跳转
const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()

// 计算属性：根据当前路由动态激活菜单项
const activeMenu = computed(() => route.path)

/**
 * 退出登录流程：
 * 1. 弹出确认对话框
 * 2. 调用Pinia的logout方法（清除状态和localStorage）
 * 3. 跳转到登录页
 */
async function handleCommand(command) {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定退出登录？', '提示', { type: 'warning' })
      await adminStore.logout()  // 清除登录状态
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (e) { if (e !== 'cancel') console.error(e) }
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    background-color: #263445;
    font-size: 16px;
    font-weight: bold;
    color: #fff;
  }
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
  display: flex;
  align-items: center;
  
  .header-content {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .title {
      font-size: 18px;
      font-weight: bold;
      color: #333;
    }
    
    .user-info {
      .el-dropdown-link {
        display: flex;
        align-items: center;
        gap: 5px;
        cursor: pointer;
        color: #606266;
        
        &:hover {
          color: #409EFF;
        }
      }
    }
  }
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
