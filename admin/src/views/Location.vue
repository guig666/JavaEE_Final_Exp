<template>
  <div class="location-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投放点管理</span>
          <el-button type="primary" @click="handleAdd">新增投放点</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="城市">
          <el-input v-model="searchForm.locationCity" placeholder="请输入城市" clearable />
        </el-form-item>
        <el-form-item label="区县">
          <el-input v-model="searchForm.locationDistrict" placeholder="请输入区县" clearable />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="searchForm.locationAddress" placeholder="请输入详细地址" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="location_id" label="ID" width="80" />
        <el-table-column prop="location_city" label="城市" width="100" />
        <el-table-column prop="location_district" label="区县" width="100" />
        <el-table-column prop="location_address" label="详细地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="location_alias" label="名称" width="150" />
        <el-table-column prop="location_amount" label="总容量" width="80" />
        <el-table-column prop="location_available" label="可用数量" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSearch"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑投放点' : '新增投放点'"
      width="500px"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="名称" prop="alias">
          <el-input v-model="form.alias" placeholder="请输入投放点名称" />
        </el-form-item>
        <el-form-item label="总容量" prop="amount">
          <el-input-number v-model="form.amount" :min="1" />
        </el-form-item>
        <el-form-item label="可用数量" prop="available">
          <el-input-number v-model="form.available" :min="0" />
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入经度" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入纬度" />
        </el-form-item>
        <el-form-item>
          <el-link type="primary" href="https://lbs.amap.com/tools/picker" target="_blank">
            去高德地图坐标拾取器获取经纬度
          </el-link>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="400px"
    >
      <p>确定要删除投放点「{{ deleteLocationName }}」吗？</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" :loading="deleteLoading" @click="confirmDelete">
            确认删除
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import locationApi from '@/api/location'

const loading = ref(false)
const submitLoading = ref(false)
const deleteLoading = ref(false)
const dialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const deleteLocationId = ref(null)
const deleteLocationName = ref('')

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  locationCity: '',
  locationDistrict: '',
  locationAddress: ''
})

const form = reactive({
  locationId: null,
  city: '',
  district: '',
  address: '',
  alias: '',
  amount: 1,
  available: 0,
  bylocation: false,
  longitude: '',
  latitude: ''
})

const rules = {
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  alias: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,  // ← 添加这一行，传递每页数量
      locationCity: searchForm.locationCity || undefined,
      locationDistrict: searchForm.locationDistrict || undefined,
      locationAddress: searchForm.locationAddress || undefined
    }

    const res = await locationApi.getLocations(params)

    if (res.code === 1 && res.data) {
      // 后端返回的是 { list: [...], total: xxx }
      if (res.data.list) {
        tableData.value = res.data.list
        total.value = res.data.total || 0
      } else if (Array.isArray(res.data)) {
        tableData.value = res.data
        total.value = res.data.length
      } else {
        tableData.value = []
        total.value = 0
      }
    } else {
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function handleReset() {
  searchForm.locationCity = ''
  searchForm.locationDistrict = ''
  searchForm.locationAddress = ''
  handleSearch()
}

// 页码改变
function handlePageChange(page) {
  pageNum.value = page
  loadData()
}

function handleAdd() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  form.locationId = row.location_id
  form.city = row.location_city
  form.district = row.location_district
  form.address = row.location_address
  form.alias = row.location_alias
  form.amount = row.location_amount
  form.available = row.location_available || 0
  form.longitude = row.location_longitude
  form.latitude = row.location_latitude
  dialogVisible.value = true
}

function handleDelete(row) {
  deleteLocationId.value = row.location_id
  deleteLocationName.value = row.location_alias || '投放点'
  deleteDialogVisible.value = true
}

async function confirmDelete() {
  deleteLoading.value = true
  try {
    await locationApi.deleteLocation(deleteLocationId.value)
    ElMessage.success('删除成功')
    deleteDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  } finally {
    deleteLoading.value = false
  }
}

function resetForm() {
  form.locationId = null
  form.city = ''
  form.district = ''
  form.address = ''
  form.alias = ''
  form.amount = 1
  form.bylocation = false
  form.longitude = ''
  form.latitude = ''
  formRef.value?.resetFields()
}

async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await locationApi.updateLocation(form.locationId, {
            city: form.city,
            district: form.district,
            address: form.address,
            alias: form.alias,
            amount: form.amount,
            available: form.available,
            longitude: form.longitude,
            latitude: form.latitude
          })
          ElMessage.success('修改成功')
        } else {
          // 新增时需要传 bylocation: true 才会保存经纬度
          await locationApi.addLocation({
            ...form,
            bylocation: true
          })
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('操作失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.location-container {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .search-form {
    margin-bottom: 20px;
    background-color: #f5f7fa;
    padding: 20px;
    border-radius: 4px;
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>