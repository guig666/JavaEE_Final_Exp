<template>
  <!-- 用户管理：核心功能是用户列表查询、余额充值、删除用户 -->
  <div class="user-container">
    <el-card>
      <!-- 卡片头部 -->
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>
      
      <!-- 搜索表单：按手机号、昵称搜索 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="手机号">
          <el-input v-model="searchForm.userPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="searchForm.userAlias" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 数据表格 -->
      <el-table :data="tableData" stripe style="width: 100%;" v-loading="loading">
        <el-table-column prop="user_id" label="ID" width="80" />
        <el-table-column prop="user_phone" label="手机号" width="130" />
        <el-table-column prop="user_alias" label="昵称" />
        <!-- 余额列（红色显示） -->
        <el-table-column prop="user_balance" label="余额" width="120">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.user_balance || 0 }}</span>
          </template>
        </el-table-column>
        <!-- 操作列：充值和删除 -->
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="handleRecharge(scope.row)">充值</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页组件 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end;"
        @current-change="loadData"
      />
    </el-card>

    <!-- 充值对话框 -->
    <el-dialog v-model="rechargeDialogVisible" title="用户充值" width="500px" @close="resetRechargeForm">
      <el-form :model="rechargeForm" :rules="rechargeRules" ref="rechargeFormRef" label-width="100px">
        <el-form-item label="用户ID"><el-input v-model="rechargeForm.userId" disabled /></el-form-item>
        <el-form-item label="用户昵称"><el-input v-model="rechargeForm.userAlias" disabled /></el-form-item>
        <el-form-item label="充值金额" prop="money">
          <el-input-number v-model="rechargeForm.money" :min="0.01" :precision="2" :step="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="rechargeLoading" @click="handleRechargeSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 响应式API + Element组件 + 用户API
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import userApi from '@/api/user'

// ===== 状态定义 =====
const loading = ref(false)
const rechargeLoading = ref(false)
const rechargeDialogVisible = ref(false)
const rechargeFormRef = ref(null)

// 表格数据和分页
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索条件（手机号、昵称）
const searchForm = reactive({ userPhone: '', userAlias: '' })

// 充值表单（用户ID和昵称只读）
const rechargeForm = reactive({ userId: '', userAlias: '', money: 10 })

// 表单验证规则
const rechargeRules = { money: [{ required: true, message: '请输入充值金额', trigger: 'blur' }] }

// ===== 核心方法 =====

/**
 * 加载用户列表：分页查询
 */
async function loadData() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm }
    const res = await userApi.getUsers(params)
    if (res.code === 1 && res.data) {
      tableData.value = res.data.list || res.data
      total.value = res.data.total || res.data.length
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

// 搜索
function handleSearch() { pageNum.value = 1; loadData() }

// 重置搜索条件
function handleReset() { Object.assign(searchForm, { userPhone: '', userAlias: '' }); handleSearch() }

// 充值（显示弹窗，填充用户信息）
function handleRecharge(row) { rechargeForm.userId = row.user_id; rechargeForm.userAlias = row.user_alias; rechargeForm.money = 10; rechargeDialogVisible.value = true }

// 重置充值表单
function resetRechargeForm() { rechargeForm.userId = ''; rechargeForm.userAlias = ''; rechargeForm.money = 10; rechargeFormRef.value?.resetFields() }

/**
 * 提交充值：
 * 1. 表单验证
 * 2. 调用充值API
 * 3. 刷新用户列表（余额更新）
 */
async function handleRechargeSubmit() {
  await rechargeFormRef.value.validate(async (valid) => {
    if (valid) {
      rechargeLoading.value = true
      try {
        await userApi.rechargeUser(rechargeForm.userId, rechargeForm.money)
        ElMessage.success('充值成功')
        rechargeDialogVisible.value = false
        loadData()
      } catch (e) { console.error(e) }
      finally { rechargeLoading.value = false }
    }
  })
}

// 删除用户（带确认对话框）
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该用户？', '提示', { type: 'warning' })
    await userApi.deleteUser(row.user_id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

// 页面挂载时加载数据
onMounted(() => loadData())
</script>

<style scoped lang="scss">
.user-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .search-form {
    margin-bottom: 20px;
  }
}
</style>
