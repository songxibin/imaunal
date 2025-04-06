<template>
  <div class="documents-container">
    <div class="documents-header">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索文档"
        class="search-input"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-upload
        class="upload-demo"
        :action="null"
        :http-request="handleUpload"
        :show-file-list="false"
        :before-upload="beforeUpload"
      >
        <el-button type="primary">
          <el-icon><Upload /></el-icon>
          上传文档
        </el-button>
      </el-upload>
    </div>

    <el-table
      v-loading="loading"
      :data="documents"
      style="width: 100%"
    >
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="fileType" label="类型" width="100" />
      <el-table-column prop="fileSize" label="大小" width="100">
        <template #default="{ row }">
          {{ formatFileSize(row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="上传时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button-group>
            <el-button
              size="small"
              @click="handlePreview(row)"
            >
              预览
            </el-button>
            <el-button
              size="small"
              type="primary"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑文档"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="editForm"
        label-width="80px"
      >
        <el-form-item label="标题">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="editForm.description"
            type="textarea"
            rows="3"
          />
        </el-form-item>
        <el-form-item label="标签">
          <el-select
            v-model="editForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择标签"
          >
            <el-option
              v-for="tag in tagOptions"
              :key="tag"
              :label="tag"
              :value="tag"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Upload } from '@element-plus/icons-vue'
import { documentsApi } from '@/api/documents'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const documents = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const dialogVisible = ref(false)
const editForm = ref({
  id: '',
  title: '',
  description: '',
  tags: []
})
const tagOptions = ref([])

// 获取文档列表
const fetchDocuments = async () => {
  try {
    loading.value = true
    const response = await documentsApi.getDocuments({
      page: currentPage.value - 1, // Spring Data JPA的页码从0开始
      size: pageSize.value,
      keyword: searchKeyword.value,
      sortBy: 'createdAt',
      sortOrder: 'desc'
    })
    console.log('API Response:', response)
    
    // 检查响应结构
    if (response && response.code === 200 && response.data) {
      documents.value = response.data.items || []
      total.value = response.data.total || 0
      console.log('Documents loaded:', documents.value)
    } else {
      console.error('Invalid response format:', response)
      ElMessage.error(response?.message || '获取文档列表失败')
    }
  } catch (error) {
    console.error('Error fetching documents:', error)
    ElMessage.error('获取文档列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchDocuments()
}

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchDocuments()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchDocuments()
}

// 处理上传
const beforeUpload = (file) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  return true
}

const handleUpload = async (options) => {
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    formData.append('title', options.file.name)
    
    await documentsApi.uploadDocument(formData)
    ElMessage.success('上传成功')
    fetchDocuments()
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

// 处理编辑
const handleEdit = (row) => {
  editForm.value = {
    id: row.documentId,
    title: row.title,
    description: row.description,
    tags: row.tags || []
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    await documentsApi.updateDocument(editForm.value.id, {
      title: editForm.value.title,
      description: editForm.value.description,
      tags: editForm.value.tags
    })
    ElMessage.success('更新成功')
    dialogVisible.value = false
    fetchDocuments()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该文档吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await documentsApi.deleteDocument(row.documentId)
      ElMessage.success('删除成功')
      fetchDocuments()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 处理预览
const handlePreview = (row) => {
  router.push(`/documents/${row.documentId}`)
}

// 工具函数
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else {
    return (size / 1024 / 1024).toFixed(2) + ' MB'
  }
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  fetchDocuments()
})
</script>

<style scoped>
.documents-container {
  padding: 20px;
}

.documents-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 