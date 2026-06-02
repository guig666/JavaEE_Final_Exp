<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>
      
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
      
      <el-table :data="tableData" stripe style="width: 100%;" v-loading="loading">
        <el-table-column prop="user_id" label="ID" width="80" />
        <el-table-column prop="user_phone" label="手机号" width="130" />
        <el-table-column prop="user_alias" label="昵称" />
        <el-table-column prop="user_balance" label="余额" width="120">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.user_balance || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="handleRecharge(scope.row)">充值</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end;"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog
      v-model="rechargeDialogVisible"
      title="用户充值"
      width="500px"
      @close="resetRechargeForm"
    >
      <el-form :model="rechargeForm" :rules="rechargeRules" ref="rechargeFormRef" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="rechargeForm.userId" disabled />
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input v-model="rechargeForm.userAlias" disabled />
        </el-form-item>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import userApi from '@/api/user'

const loading = ref(false)
const rechargeLoading = ref(false)
const rechargeDialogVisible = ref(false)
const rechargeFormRef = ref(null)

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  userPhone: '',
  userAlias: ''
})

const rechargeForm = reactive({
  userId: '',
  userAlias: '',
  money: 10
})

const rechargeRules = {
  money: [{ required: true, message: '请输入充值金额', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    }
    const res = await userApi.getUsers(params)
    if (res.code === 1 && res.data) {
      if (res.data.list) {
        tableData.value = res.data.list
        total.value = res.data.total || 0
      } else if (Array.isArray(res.data)) {
        tableData.value = res.data
        total.value = res.data.length
      }
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function handleReset() {
  searchForm.userPhone = ''
  searchForm.userAlias = ''
  handleSearch()
}

function handleRecharge(row) {
  rechargeForm.userId = row.user_id
  rechargeForm.userAlias = row.user_alias
  rechargeForm.money = 10
  rechargeDialogVisible.value = true
}

function resetRechargeForm() {
  rechargeForm.userId = ''
  rechargeForm.userAlias = ''
  rechargeForm.money = 10
  rechargeFormRef.value?.resetFields()
}

async function handleRechargeSubmit() {
  if (!rechargeFormRef.value) return
  
  await rechargeFormRef.value.validate(async (valid) => {
    if (valid) {
      rechargeLoading.value = true
      try {
        await userApi.rechargeUser(rechargeForm.userId, rechargeForm.money)
        ElMessage.success('充值成功')
        rechargeDialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('充值失败:', error)
      } finally {
        rechargeLoading.value = false
      }
    }
  })
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await userApi.deleteUser(row.user_id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadData()
})
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
