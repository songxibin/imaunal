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
        <template #tip>
          <div class="el-upload__tip">
            支持上传 Word(.docx) 和 PDF 文件，单个文件不超过 50MB
          </div>
        </template>
      </el-upload>
    </div>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传文档"
      width="600px"
    >
      <el-form
        ref="uploadFormRef"
        :model="uploadForm"
        label-width="120px"
      >
        <el-form-item label="文件">
          <div class="selected-file">
            <el-icon><Document /></el-icon>
            <span>{{ selectedFileName }}</span>
          </div>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="uploadForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            rows="3"
          />
        </el-form-item>
        <el-form-item label="公司信息">
          <el-input v-model="uploadForm.companyInfo" />
        </el-form-item>
        <el-form-item label="品牌信息">
          <el-input v-model="uploadForm.brandInfo" />
        </el-form-item>
        <el-form-item label="产品类别">
          <el-input v-model="uploadForm.productCategory" />
        </el-form-item>
        <el-form-item label="文档类型">
          <el-select v-model="uploadForm.documentType" placeholder="请选择文档类型">
            <el-option label="用户手册" value="用户手册" />
            <el-option label="技术文档" value="技术文档" />
            <el-option label="API文档" value="API文档" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="适用语言">
          <el-select v-model="uploadForm.language" placeholder="请选择语言">
            <el-option label="中文" value="中文" />
            <el-option label="英文" value="英文" />
            <el-option label="日文" value="日文" />
            <el-option label="韩文" value="韩文" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="版本信息">
          <el-input v-model="uploadForm.version" placeholder="例如: 1.0.0" />
        </el-form-item>
        <el-form-item label="标签">
          <el-select
            v-model="uploadForm.tags"
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
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload">
            上传
          </el-button>
        </span>
      </template>
    </el-dialog>

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
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">
            {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="上传时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300">
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
              v-if="row.status !== 'PUBLISHED'"
              size="small"
              type="success"
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button
              v-if="row.status === 'PUBLISHED'"
              size="small"
              type="warning"
              @click="handleUnpublish(row)"
            >
              取消发布
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
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        @update:current-page="currentPage = $event"
        @update:page-size="pageSize = $event"
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
        label-width="120px"
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
        <el-form-item label="公司信息">
          <el-input v-model="editForm.companyInfo" />
        </el-form-item>
        <el-form-item label="品牌信息">
          <el-input v-model="editForm.brandInfo" />
        </el-form-item>
        <el-form-item label="产品类别">
          <el-input v-model="editForm.productCategory" />
        </el-form-item>
        <el-form-item label="文档类型">
          <el-select v-model="editForm.documentType" placeholder="请选择文档类型">
            <el-option label="用户手册" value="用户手册" />
            <el-option label="技术文档" value="技术文档" />
            <el-option label="API文档" value="API文档" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="适用语言">
          <el-select v-model="editForm.language" placeholder="请选择语言">
            <el-option label="中文" value="中文" />
            <el-option label="英文" value="英文" />
            <el-option label="日文" value="日文" />
            <el-option label="韩文" value="韩文" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="版本信息">
          <el-input v-model="editForm.version" placeholder="例如: 1.0.0" />
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
import { Search, Upload, Document } from '@element-plus/icons-vue'
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
const uploadDialogVisible = ref(false)
const selectedFile = ref(null)
const selectedFileName = ref('')
const uploadFormRef = ref(null)
const uploadForm = ref({
  title: '',
  description: '',
  companyInfo: '',
  brandInfo: '',
  productCategory: '',
  documentType: '',
  language: '',
  version: '',
  tags: []
})
const editForm = ref({
  id: '',
  title: '',
  description: '',
  companyInfo: '',
  brandInfo: '',
  productCategory: '',
  documentType: '',
  language: '',
  version: '',
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
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过 50MB!')
    return false
  }
  
  const isDocxOrPdfOrTxt = file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' || 
                     file.type === 'application/pdf' || 
                     file.type === 'text/plain' ||
                     file.name.toLowerCase().endsWith('.txt')

  if (!isDocxOrPdfOrTxt) {
    ElMessage.error('只支持 Word(.docx)、PDF 和 TXT 文件!')
    return false
  }
  
  selectedFile.value = file
  selectedFileName.value = file.name
  uploadForm.value.title = file.name.replace(/\.[^/.]+$/, '') // Remove file extension
  uploadDialogVisible.value = true
  
  return false // Prevent automatic upload
}

const handleUpload = async (options) => {
  // This is now just a placeholder, actual upload happens in submitUpload
}

const submitUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.error('请选择文件')
    return
  }
  
  if (!uploadForm.value.title) {
    ElMessage.error('请输入标题')
    return
  }
  
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('title', uploadForm.value.title)
    formData.append('description', uploadForm.value.description || '')
    formData.append('companyInfo', uploadForm.value.companyInfo || '')
    formData.append('brandInfo', uploadForm.value.brandInfo || '')
    formData.append('productCategory', uploadForm.value.productCategory || '')
    formData.append('documentType', uploadForm.value.documentType || '')
    formData.append('language', uploadForm.value.language || '')
    formData.append('version', uploadForm.value.version || '')
    formData.append('tags', JSON.stringify(uploadForm.value.tags || []))
    
    const response = await documentsApi.uploadDocument(formData)
    if (response.code === 200) {
      ElMessage.success('上传成功')
      uploadDialogVisible.value = false
      resetUploadForm()
      fetchDocuments()
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '上传失败')
  }
}

const resetUploadForm = () => {
  selectedFile.value = null
  selectedFileName.value = ''
  uploadForm.value = {
    title: '',
    description: '',
    companyInfo: '',
    brandInfo: '',
    productCategory: '',
    documentType: '',
    language: '',
    version: '',
    tags: []
  }
}

// 处理编辑
const handleEdit = (row) => {
  editForm.value = {
    id: row.documentId,
    title: row.title,
    description: row.description,
    companyInfo: row.companyInfo || '',
    brandInfo: row.brandInfo || '',
    productCategory: row.productCategory || '',
    documentType: row.documentType || '',
    language: row.language || '',
    version: row.version || '',
    tags: row.tags || []
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    await documentsApi.updateDocument(editForm.value.id, {
      title: editForm.value.title,
      description: editForm.value.description,
      companyInfo: editForm.value.companyInfo,
      brandInfo: editForm.value.brandInfo,
      productCategory: editForm.value.productCategory,
      documentType: editForm.value.documentType,
      language: editForm.value.language,
      version: editForm.value.version,
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

// 处理发布
const handlePublish = (row) => {
  ElMessageBox.confirm(
    '确定要发布该文档吗？发布后文档将变为公开状态。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      await documentsApi.publishDocument(row.documentId)
      ElMessage.success('发布成功')
      fetchDocuments()
    } catch (error) {
      ElMessage.error('发布失败')
    }
  })
}

// 处理取消发布
const handleUnpublish = (row) => {
  ElMessageBox.confirm(
    '确定要取消发布该文档吗？取消发布后文档将从公共存储桶中删除。',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await documentsApi.unpublishDocument(row.documentId)
      ElMessage.success('取消发布成功')
      fetchDocuments()
    } catch (error) {
      ElMessage.error('取消发布失败')
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

.selected-file {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.selected-file .el-icon {
  font-size: 18px;
  color: #409eff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 