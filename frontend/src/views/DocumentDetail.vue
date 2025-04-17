<template>
  <div class="document-detail-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <div class="title-section">
            <el-button 
              type="info" 
              plain 
              @click="router.push('/documents')"
              class="back-button"
            >
              <el-icon><ArrowLeft /></el-icon>
              返回列表
            </el-button>
            <h2>{{ document.title }}</h2>
          </div>
          <div class="actions">
            <el-button type="primary" @click="handleDownload">
              <el-icon><Download /></el-icon>
              下载
            </el-button>
            <el-button 
              v-if="document.status === 'DRAFT'" 
              type="success" 
              @click="handlePublish"
            >
              <el-icon><Upload /></el-icon>
              发布
            </el-button>
            <el-button 
              v-if="document.status === 'PUBLISHED'" 
              type="warning" 
              @click="handleUnpublish"
            >
              <el-icon><Download /></el-icon>
              取消发布
            </el-button>
            <el-button type="danger" @click="handleDelete">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
            <el-button 
              v-if="isWordDocument"
              type="primary"
              :loading="translating"
              @click="showTranslateDialog"
            >
              <el-icon><Document /></el-icon>
              翻译文档
            </el-button>
          </div>
          <!-- 翻译对话框 -->
          <el-dialog
            v-model="translateDialogVisible"
            title="文档翻译"
            width="500px"
          >
            <el-form :model="translateForm" label-width="120px">
              <el-form-item label="源语言">
                <el-select v-model="translateForm.sourceLang">
                  <el-option label="中文" value="ZH" />
                  <el-option label="英文" value="EN" />
                  <el-option label="日文" value="JA" />
                  <!-- 可以添加更多语言选项 -->
                </el-select>
              </el-form-item>
              <el-form-item label="目标语言">
                <el-select v-model="translateForm.targetLang">
                  <el-option label="中文" value="ZH" />
                  <el-option label="英文" value="EN" />
                  <el-option label="日文" value="JA" />
                  <!-- 可以添加更多语言选项 -->
                </el-select>
              </el-form-item>
            </el-form>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="translateDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleTranslate" :loading="translating">
                  开始翻译
                </el-button>
              </span>
            </template>
          </el-dialog>
        </div>
      </template>
      
      <div class="document-info">
        <el-row :gutter="20">
          <!-- 左侧信息栏 -->
          <el-col :span="18">
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
              <el-descriptions-item label="状态">
                <el-tag :type="document.status === 'PUBLISHED' ? 'success' : 'info'">
                  {{ document.status === 'PUBLISHED' ? '已发布' : '草稿' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="公司信息">
                {{ document.companyInfo || '未设置' }}
              </el-descriptions-item>
              <el-descriptions-item label="品牌信息">
                {{ document.brandInfo || '未设置' }}
              </el-descriptions-item>
              <el-descriptions-item label="产品类别">
                {{ document.productCategory || '未设置' }}
              </el-descriptions-item>
              <el-descriptions-item label="文档类型">
                {{ document.documentType || '未设置' }}
              </el-descriptions-item>
              <el-descriptions-item label="适用语言">
                {{ document.language || '未设置' }}
              </el-descriptions-item>
              <el-descriptions-item label="版本信息">
                {{ document.version || '未设置' }}
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
              <el-descriptions-item label="文档ID">
                {{ document.documentId }}
              </el-descriptions-item>
            </el-descriptions>
            
            <div class="description-section">
              <h3>描述</h3>
              <p>{{ document.description || '暂无描述' }}</p>
            </div>
            
            <!-- 添加语言版本信息 -->
            <div class="language-versions-section">
              <h3>其他语言版本</h3>
              <el-table :data="document.languageVersions" stripe style="width: 100%">
                <el-table-column prop="fileName" label="文件名" />
                <el-table-column prop="language" label="语言">
                  <template #default="{ row }">
                    <el-tag size="small">{{ getLanguageLabel(row.language) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column align="right">
                  <template #default="{ row }">
                    <el-button 
                      type="primary" 
                      link 
                      size="small" 
                      @click="handleVersionDownload(row.downloadUrl)"
                    >
                      下载
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            
            <div class="preview-section" v-if="document.previewUrl">
              <h3>预览</h3>
              <div class="preview-container">
                <!-- 根据文件类型显示不同的预览 -->
                <img v-if="isImage" :src="document.previewUrl" alt="预览图" />
                <iframe v-else-if="isPDF" :src="document.previewUrl" width="100%" height="500px"></iframe>
                <div v-else-if="isTXT" class="txt-preview">
                  <pre>{{ txtContent }}</pre>
                </div>
                <div v-else class="no-preview">
                  此文件类型暂不支持预览
                </div>
              </div>
            </div>
          </el-col>

          <!-- 右侧二维码栏 -->
          <el-col :span="6">
            <div class="qr-section" v-if="document.status === 'PUBLISHED'">
              <div class="qr-sticky">
                <h3>文档访问二维码</h3>
                <div class="qr-container">
                  <QRCodeVue 
                    :value="publicUrl" 
                    :size="200" 
                    level="H"
                    :foreground="'#000000'"
                    :background="'#ffffff'"
                  />
                  <p class="qr-tip">扫描二维码访问文档</p>
                  <p class="qr-url">{{ publicUrl }}</p>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { Download, Delete, ArrowLeft, Upload } from '@element-plus/icons-vue'
import { documentsApi } from '@/api/documents'
import QRCodeVue from 'qrcode.vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const document = ref({})
const publicUrl = ref('')
const txtContent = ref('')

// 计算属性
const isImage = computed(() => {
  const imageTypes = ['jpg', 'jpeg', 'png', 'gif']
  return imageTypes.includes(document.value.fileType?.toLowerCase())
})

const isPDF = computed(() => {
  return document.value.fileType?.toLowerCase() === 'pdf'
})

const isTXT = computed(() => {
  return document.value.fileType?.toLowerCase() === 'text/plain' || 
         document.value.fileType?.toLowerCase() === 'txt'
})
const translating = ref(false)
const translateDialogVisible = ref(false)
const translateForm = ref({
  sourceLang: 'ZH',
  targetLang: 'EN'
})

const isWordDocument = computed(() => {
  const wordTypes = ['doc', 'docx']
  return wordTypes.includes(document.value.fileType?.toLowerCase())
})

const showTranslateDialog = () => {
  translateDialogVisible.value = true
}

const handleTranslate = async () => {
  try {
    translating.value = true
    await documentsApi.translateDocument(document.value.documentId, translateForm.value)
    ElMessage.success('文档翻译已完成')
    translateDialogVisible.value = false
    // 刷新文档列表
    fetchDocumentDetail()
  } catch (error) {
    ElMessage.error('翻译失败：' + error.message)
  } finally {
    translating.value = false
  }
}
// 获取文档详情
const fetchDocumentDetail = async () => {
  try {
    loading.value = true
    const response = await documentsApi.getDocumentById(route.params.id)
    document.value = response.data
    
    // 如果是已发布的文档，生成公共访问URL
    if (document.value.status === 'PUBLISHED') {
      // 这里使用当前域名和路径构建公共URL
      const baseUrl = window.location.origin
      publicUrl.value = `${baseUrl}/public/documents/${document.value.documentId}`
    }
    
    // 如果是TXT文件，获取内容
    if (isTXT.value && document.value.previewUrl) {
      try {
        const response = await fetch(document.value.previewUrl)
        txtContent.value = await response.text()
      } catch (error) {
        console.error('Failed to fetch TXT content:', error)
        txtContent.value = '无法加载文本内容'
      }
    }
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

// 处理发布
const handlePublish = () => {
  ElMessageBox.confirm(
    '确定要发布该文档吗？发布后文档将公开可见',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      loading.value = true
      await documentsApi.publishDocument(route.params.id)
      ElMessage.success('发布成功')
      fetchDocumentDetail()
    } catch (error) {
      ElMessage.error('发布失败')
    } finally {
      loading.value = false
    }
  })
}

// 处理取消发布
const handleUnpublish = () => {
  ElMessageBox.confirm(
    '确定要取消发布该文档吗？取消发布后文档将不再公开可见',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      loading.value = true
      await documentsApi.unpublishDocument(route.params.id)
      ElMessage.success('取消发布成功')
      fetchDocumentDetail()
    } catch (error) {
      ElMessage.error('取消发布失败')
    } finally {
      loading.value = false
    }
  })
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

// 语言标签映射
const languageMap = {
  'ZH': '中文',
  'EN': '英文',
  'JA': '日文',
  // 可以添加更多语言映射
}

// 获取语言显示标签
const getLanguageLabel = (langCode) => {
  return languageMap[langCode] || langCode
}

// 处理版本下载
const handleVersionDownload = (url) => {
  if (url) {
    window.open(url, '_blank')
  } else {
    ElMessage.warning('下载链接不可用')
  }
}
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

.title-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 5px;
}

.actions {
  display: flex;
  gap: 10px;
}

.document-info {
  margin-top: 20px;
}

.description-section, .preview-section, .qr-section {
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

.qr-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 10px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.qr-sticky {
  position: sticky;
  top: 20px;
}

.qr-section {
  background: #fff;
  border-radius: 4px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.qr-container {
  margin-top: 15px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #f9f9f9;
  text-align: center;
}

.qr-tip {
  margin-top: 10px;
  color: #606266;
  font-size: 14px;
}

.qr-url {
  margin-top: 10px;
  color: #606266;
  font-size: 12px;
  word-break: break-all;
  text-align: center;
  max-width: 200px;
}

.txt-preview {
  padding: 20px;
  background-color: #f9f9f9;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  max-height: 500px;
  overflow: auto;
  white-space: pre-wrap;
  font-family: monospace;
  line-height: 1.5;
}

.txt-preview pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.language-versions-section {
  margin: 20px 0;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.language-versions-section h3 {
  margin-bottom: 15px;
  color: #303133;
}
</style>