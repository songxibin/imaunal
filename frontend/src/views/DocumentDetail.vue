<template>
  <div class="document-detail-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <h2>{{ document.title }}</h2>
          <div class="actions">
            <el-button type="primary" @click="handleDownload">
              <el-icon><Download /></el-icon>
              下载
            </el-button>
            <el-button type="danger" @click="handleDelete">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="document-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件类型">
            {{ document.fileType }}
          </el-descriptions-item>
          <el-descriptions-item label="文件大小">
            {{ formatFileSize(document.fileSize) }}
          </el-descriptions-item>
          <el-descriptions-item label="上传时间">
            {{ formatDate(document.uploadTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatDate(document.updateTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="上传者">
            {{ document.creator?.username || '未知' }}
          </el-descriptions-item>
          <el-descriptions-item label="标签">
            <el-tag
              v-for="tag in document.tags"
              :key="tag"
              class="mx-1"
              size="small"
            >
              {{ tag }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="description-section">
          <h3>描述</h3>
          <p>{{ document.description || '暂无描述' }}</p>
        </div>
        
        <div class="preview-section" v-if="document.previewUrl">
          <h3>预览</h3>
          <div class="preview-container">
            <!-- 根据文件类型显示不同的预览 -->
            <img v-if="isImage" :src="document.previewUrl" alt="预览图" />
            <iframe v-else-if="isPDF" :src="document.previewUrl" width="100%" height="500px"></iframe>
            <div v-else class="no-preview">
              此文件类型暂不支持预览
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Delete } from '@element-plus/icons-vue'
import { documentsApi } from '@/api/documents'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const document = ref({})

// 计算属性
const isImage = computed(() => {
  const imageTypes = ['jpg', 'jpeg', 'png', 'gif']
  return imageTypes.includes(document.value.fileType?.toLowerCase())
})

const isPDF = computed(() => {
  return document.value.fileType?.toLowerCase() === 'pdf'
})

// 获取文档详情
const fetchDocumentDetail = async () => {
  try {
    loading.value = true
    const response = await documentsApi.getDocumentById(route.params.id)
    document.value = response.data
  } catch (error) {
    ElMessage.error('获取文档详情失败')
    router.push('/documents')
  } finally {
    loading.value = false
  }
}

// 处理下载
const handleDownload = () => {
  if (document.value.downloadUrl) {
    window.open(document.value.downloadUrl, '_blank')
  } else {
    ElMessage.warning('下载链接不可用')
  }
}

// 处理删除
const handleDelete = () => {
  ElMessageBox.confirm(
    '确定要删除该文档吗？此操作不可恢复',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await documentsApi.deleteDocument(route.params.id)
      ElMessage.success('删除成功')
      router.push('/documents')
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 工具函数
const formatFileSize = (size) => {
  if (!size) return '0 B'
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else {
    return (size / 1024 / 1024).toFixed(2) + ' MB'
  }
}

const formatDate = (date) => {
  if (!date) return '未知'
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  fetchDocumentDetail()
})
</script>

<style scoped>
.document-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions {
  display: flex;
  gap: 10px;
}

.document-info {
  margin-top: 20px;
}

.description-section, .preview-section {
  margin-top: 20px;
}

.preview-container {
  margin-top: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.preview-container img {
  max-width: 100%;
  display: block;
}

.no-preview {
  padding: 50px;
  text-align: center;
  color: #909399;
}

.el-tag {
  margin-right: 5px;
}
</style> 