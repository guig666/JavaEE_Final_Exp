<template>
  <div class="order-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.order_user_id" placeholder="请输入用户ID" type="number" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" stripe style="width: 100%;" v-loading="loading">
        <el-table-column prop="order_id" label="订单ID" width="100" />
        <el-table-column prop="order_user_id" label="用户ID" width="100" />
        <el-table-column prop="order_lent_location_id" label="借用地点ID" width="130" />
        <el-table-column prop="order_revert_location_id" label="归还地点ID" width="130" />
        <el-table-column prop="order_create_time" label="借用时间" width="180" />
        <el-table-column prop="order_finish_time" label="归还时间" width="180" />
        <el-table-column prop="order_cost" label="费用" width="100">
          <template #default="scope">
            <span v-if="scope.row.order_cost" style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.order_cost }}</span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="order_status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.order_finish_time ? 'success' : 'warning'">
              {{ scope.row.order_finish_time ? '已归还' : '借用中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button link type="primary" @click="handleDetail(scope.row)">详情</el-button>
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
      v-model="detailDialogVisible"
      title="订单详情"
      width="600px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单ID">{{ orderDetail.order_id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ orderDetail.order_user_id }}</el-descriptions-item>
        <el-descriptions-item label="借用地点ID">{{ orderDetail.order_lent_location_id }}</el-descriptions-item>
        <el-descriptions-item label="归还地点ID">{{ orderDetail.order_revert_location_id || '-' }}</el-descriptions-item>
        <el-descriptions-item label="借用时间">{{ orderDetail.order_create_time }}</el-descriptions-item>
        <el-descriptions-item label="归还时间">{{ orderDetail.order_finish_time || '-' }}</el-descriptions-item>
        <el-descriptions-item label="费用">
          <span v-if="orderDetail.order_cost" style="color: #f56c6c; font-weight: bold;">¥{{ orderDetail.order_cost }}</span>
          <span v-else style="color: #909399;">-</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="orderDetail.order_finish_time ? 'success' : 'warning'">
            {{ orderDetail.order_finish_time ? '已归还' : '借用中' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import orderApi from '@/api/order'

const loading = ref(false)
const detailDialogVisible = ref(false)

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  order_user_id: ''
})

const orderDetail = reactive({
  order_id: '',
  order_user_id: '',
  order_lent_location_id: '',
  order_revert_location_id: '',
  order_create_time: '',
  order_finish_time: '',
  order_cost: '',
  order_status: ''
})

async function loadData() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      order_user_id: searchForm.order_user_id ? parseInt(searchForm.order_user_id) : undefined
    }
    const res = await orderApi.getOrders(params)
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
  searchForm.order_user_id = ''
  handleSearch()
}

async function handleDetail(row) {
  try {
    const res = await orderApi.getOrderDetail(row.order_id)
    if (res.data && res.data.length > 0) {
      Object.assign(orderDetail, res.data[0])
    } else {
      Object.assign(orderDetail, row)
    }
    detailDialogVisible.value = true
  } catch (error) {
    Object.assign(orderDetail, row)
    detailDialogVisible.value = true
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.order-container {
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
