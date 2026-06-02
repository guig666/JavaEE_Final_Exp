<template>
  <div class="powerbank-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>充电宝管理</span>
          <el-button type="primary" @click="handleAdd">投放充电宝</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="投放点ID">
          <el-input v-model="searchForm.pobk_location_id" placeholder="请输入投放点ID" type="number" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" stripe style="width: 100%;" v-loading="loading">
        <el-table-column prop="pobk_id" label="ID" width="80" />
        <el-table-column prop="pobk_location_id" label="投放点ID" width="120" />
        <el-table-column prop="pobk_status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.pobk_status === 'available' ? 'success' : 'warning'">
              {{ scope.row.pobk_status === 'available' ? '可用' : '占用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="handleTransfer(scope.row)">转移</el-button>
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
      v-model="addDialogVisible"
      title="投放充电宝"
      width="500px"
      @close="resetAddForm"
    >
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-form-item label="投放点ID" prop="pobk_location_id">
          <el-input v-model="addForm.pobk_location_id" placeholder="请输入投放点ID" type="number" />
        </el-form-item>
        <el-form-item label="投放数量" prop="pobk_amount">
          <el-input-number v-model="addForm.pobk_amount" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="handleAddSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="transferDialogVisible"
      title="转移充电宝"
      width="500px"
      @close="resetTransferForm"
    >
      <el-form :model="transferForm" :rules="transferRules" ref="transferFormRef" label-width="100px">
        <el-form-item label="充电宝ID">
          <el-input v-model="transferForm.pobk_id" disabled />
        </el-form-item>
        <el-form-item label="新投放点ID" prop="pobk_location_id">
          <el-input v-model="transferForm.pobk_location_id" placeholder="请输入新投放点ID" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="transferLoading" @click="handleTransferSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import powerbankApi from '@/api/powerbank'

const loading = ref(false)
const addLoading = ref(false)
const transferLoading = ref(false)
const addDialogVisible = ref(false)
const transferDialogVisible = ref(false)
const addFormRef = ref(null)
const transferFormRef = ref(null)

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  pobk_location_id: ''
})

const addForm = reactive({
  pobk_location_id: '',
  pobk_amount: 1
})

const transferForm = reactive({
  pobk_id: '',
  pobk_location_id: ''
})

const addRules = {
  pobk_location_id: [{ required: true, message: '请输入投放点ID', trigger: 'blur' }],
  pobk_amount: [{ required: true, message: '请输入投放数量', trigger: 'blur' }]
}

const transferRules = {
  pobk_location_id: [{ required: true, message: '请输入新投放点ID', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      pobk_location_id: searchForm.pobk_location_id ? parseInt(searchForm.pobk_location_id) : undefined
    }
    const res = await powerbankApi.getPowerbanks(params)
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
  searchForm.pobk_location_id = ''
  handleSearch()
}

function handleAdd() {
  resetAddForm()
  addDialogVisible.value = true
}

function resetAddForm() {
  addForm.pobk_location_id = ''
  addForm.pobk_amount = 1
  addFormRef.value?.resetFields()
}

async function handleAddSubmit() {
  if (!addFormRef.value) return
  
  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      addLoading.value = true
      try {
        await powerbankApi.addPowerbank(
          parseInt(addForm.pobk_location_id),
          addForm.pobk_amount
        )
        ElMessage.success('投放成功')
        addDialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('投放失败:', error)
      } finally {
        addLoading.value = false
      }
    }
  })
}

function handleTransfer(row) {
  transferForm.pobk_id = row.pobk_id
  transferForm.pobk_location_id = ''
  transferDialogVisible.value = true
}

function resetTransferForm() {
  transferForm.pobk_id = ''
  transferForm.pobk_location_id = ''
  transferFormRef.value?.resetFields()
}

async function handleTransferSubmit() {
  if (!transferFormRef.value) return
  
  await transferFormRef.value.validate(async (valid) => {
    if (valid) {
      transferLoading.value = true
      try {
        await powerbankApi.transferPowerbank(
          transferForm.pobk_id,
          parseInt(transferForm.pobk_location_id)
        )
        ElMessage.success('转移成功')
        transferDialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('转移失败:', error)
      } finally {
        transferLoading.value = false
      }
    }
  })
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该充电宝吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await powerbankApi.deletePowerbank(row.pobk_id)
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
.powerbank-container {
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
