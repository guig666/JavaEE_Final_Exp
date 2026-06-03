<template>
  <!-- 投放点管理：核心功能是CRUD操作，包含分页查询、新增/编辑/删除对话框 -->
  <div class="location-container">
    <el-card>
      <!-- 卡片头部：标题 + 新增按钮 -->
      <template #header>
        <div class="card-header">
          <span>投放点管理</span>
          <el-button type="primary" @click="handleAdd">新增投放点</el-button>
        </div>
      </template>

      <!-- 搜索表单：支持按城市、区县、地址搜索 -->
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

      <!-- 数据表格：展示投放点列表，带分页 -->
      <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="location_id" label="ID" width="80" />
        <el-table-column prop="location_city" label="城市" width="100" />
        <el-table-column prop="location_district" label="区县" width="100" />
        <el-table-column prop="location_address" label="详细地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="location_alias" label="名称" width="150" />
        <el-table-column prop="location_amount" label="总容量" width="80" />
        <el-table-column prop="location_available" label="可用数量" width="100" />
        <!-- 操作列：编辑和删除 -->
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"    <!-- 当前页码 -->
          v-model:page-size="pageSize"      <!-- 每页数量 -->
          :total="total"                    <!-- 总条数 -->
          :page-sizes="[10, 20, 50, 100]"  <!-- 可选每页数量 -->
          layout="total, sizes, prev, pager, next, jumper"  <!-- 分页布局 -->
          @current-change="handlePageChange"  <!-- 页码改变事件 -->
          @size-change="handleSearch"         <!-- 每页数量改变事件 -->
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑投放点' : '新增投放点'"
      width="500px"
      destroy-on-close  <!-- 关闭时销毁内容，避免缓存 -->
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="城市" prop="city"><el-input v-model="form.city" placeholder="请输入城市" /></el-form-item>
        <el-form-item label="区县" prop="district"><el-input v-model="form.district" placeholder="请输入区县" /></el-form-item>
        <el-form-item label="详细地址" prop="address"><el-input v-model="form.address" placeholder="请输入详细地址" /></el-form-item>
        <el-form-item label="名称" prop="alias"><el-input v-model="form.alias" placeholder="请输入投放点名称" /></el-form-item>
        <el-form-item label="总容量" prop="amount"><el-input-number v-model="form.amount" :min="1" /></el-form-item>
        <el-form-item label="可用数量" prop="available"><el-input-number v-model="form.available" :min="0" /></el-form-item>
        <el-form-item label="经度" prop="longitude"><el-input v-model="form.longitude" placeholder="请输入经度" /></el-form-item>
        <el-form-item label="纬度" prop="latitude"><el-input v-model="form.latitude" placeholder="请输入纬度" /></el-form-item>
        <el-form-item>
          <el-link type="primary" href="https://lbs.amap.com/tools/picker" target="_blank">去高德地图坐标拾取器获取经纬度</el-link>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="400px">
      <p>确定要删除投放点「{{ deleteLocationName }}」吗？</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" :loading="deleteLoading" @click="confirmDelete">确认删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 响应式API + 生命周期钩子
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import locationApi from '@/api/location'

// ===== 状态定义 =====
const loading = ref(false)
const submitLoading = ref(false)
const deleteLoading = ref(false)
const dialogVisible = ref(false)     // 新增/编辑弹窗
const deleteDialogVisible = ref(false) // 删除确认弹窗
const isEdit = ref(false)            // 编辑标识
const formRef = ref(null)
const deleteLocationId = ref(null)
const deleteLocationName = ref('')

// 表格数据和分页（核心：分页查询）
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索条件
const searchForm = reactive({ locationCity: '', locationDistrict: '', locationAddress: '' })

// 表单数据
const form = reactive({
  locationId: null, city: '', district: '', address: '', alias: '',
  amount: 1, available: 0, bylocation: false, longitude: '', latitude: ''
})

// 表单验证规则
const rules = {
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  alias: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

// ===== 核心方法 =====

/**
 * 数据加载流程：
 * 1. 组装分页和搜索参数
 * 2. 调用API获取数据
 * 3. 更新表格数据和总数
 */
async function loadData() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm }
    const res = await locationApi.getLocations(params)
    if (res.code === 1 && res.data) {
      tableData.value = res.data.list || res.data
      total.value = res.data.total || res.data.length
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

// 搜索（重置页码）
function handleSearch() { pageNum.value = 1; loadData() }

// 重置搜索条件
function handleReset() { Object.assign(searchForm, { locationCity: '', locationDistrict: '', locationAddress: '' }); handleSearch() }

// 分页切换
function handlePageChange(page) { pageNum.value = page; loadData() }

// 新增（重置表单）
function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }

// 编辑（填充数据到表单）
function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    locationId: row.location_id, city: row.location_city, district: row.location_district,
    address: row.location_address, alias: row.location_alias, amount: row.location_amount,
    available: row.location_available || 0, longitude: row.location_longitude, latitude: row.location_latitude
  })
  dialogVisible.value = true
}

// 删除（显示确认弹窗）
function handleDelete(row) { deleteLocationId.value = row.location_id; deleteLocationName.value = row.location_alias || '投放点'; deleteDialogVisible.value = true }

// 确认删除
async function confirmDelete() {
  deleteLoading.value = true
  try {
    await locationApi.deleteLocation(deleteLocationId.value)
    ElMessage.success('删除成功')
    deleteDialogVisible.value = false
    loadData()
  } catch (e) { ElMessage.error('删除失败') }
  finally { deleteLoading.value = false }
}

// 重置表单
function resetForm() { Object.assign(form, { locationId: null, city: '', district: '', address: '', alias: '', amount: 1, bylocation: false, longitude: '', latitude: '' }); formRef.value?.resetFields() }

/**
 * 提交流程：
 * 1. 表单验证
 * 2. 根据isEdit判断新增或编辑
 * 3. 调用对应API
 * 4. 刷新列表
 */
async function handleSubmit() {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await locationApi.updateLocation(form.locationId, { city: form.city, district: form.district, address: form.address, alias: form.alias, amount: form.amount, available: form.available, longitude: form.longitude, latitude: form.latitude })
          ElMessage.success('修改成功')
        } else {
          await locationApi.addLocation({ ...form, bylocation: true })
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (e) { console.error(e) }
      finally { submitLoading.value = false }
    }
  })
}

// 页面挂载时加载数据
onMounted(() => loadData())
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