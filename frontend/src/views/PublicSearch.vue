<template>
  <div class="public-search-container">
    <div class="search-header">
      <h1>文档搜索</h1>
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="输入关键词搜索文档"
          class="search-input"
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          搜索
        </el-button>
      </div>
    </div>

    <div class="search-filters">
      <el-collapse v-model="activeFilters">
        <el-collapse-item title="高级筛选" name="1">
          <el-form :model="filters" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="公司信息">
                  <el-input v-model="filters.companyInfo" placeholder="输入公司信息" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="品牌信息">
                  <el-input v-model="filters.brandInfo" placeholder="输入品牌信息" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="产品类别">
                  <el-input v-model="filters.productCategory" placeholder="输入产品类别" clearable />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="文档类型">
                  <el-select v-model="filters.documentType" placeholder="选择文档类型" clearable>
                    <el-option label="用户手册" value="用户手册" />
                    <el-option label="技术文档" value="技术文档" />
                    <el-option label="API文档" value="API文档" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="适用语言">
                  <el-select v-model="filters.language" placeholder="选择语言" clearable>
                    <el-option label="中文" value="中文" />
                    <el-option label="英文" value="英文" />
                    <el-option label="日文" value="日文" />
                    <el-option label="韩文" value="韩文" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="标签">
                  <el-select
                    v-model="filters.tags"
                    multiple
                    filterable
                    allow-create
                    default-first-option
                    placeholder="选择标签"
                    clearable
                  >
                    <el-option
                      v-for="tag in tagOptions"
                      :key="tag"
                      :label="tag"
                      :value="tag"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-button type="primary" @click="applyFilters">应用筛选</el-button>
              <el-button @click="resetFilters">重置</el-button>
            </el-form-item>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </div>

    <div class="search-results" v-loading="loading">
      <div v-if="documents.length === 0 && !loading" class="no-results">
        <el-empty description="未找到匹配的文档" />
      </div>
      
      <el-card v-for="doc in documents" :key="doc.documentId" class="document-card">
        <div class="document-header">
          <h3>{{ doc.title }}</h3>
          <el-tag :type="doc.status === 'PUBLISHED' ? 'success' : 'info'" size="small">
            {{ doc.status === 'PUBLISHED' ? '已发布' : '草稿' }}
          </el-tag>
        </div>
        
        <div class="document-meta">
          <span><el-icon><Document /></el-icon> {{ doc.fileType }}</span>
          <span><el-icon><Timer /></el-icon> {{ formatDate(doc.createdAt) }}</span>
          <span v-if="doc.language"><el-icon><ChatLineRound /></el-icon> {{ doc.language }}</span>
          <span v-if="doc.documentType"><el-icon><Files /></el-icon> {{ doc.documentType }}</span>
        </div>
        
        <div class="document-tags" v-if="doc.tags && doc.tags.length > 0">
          <el-tag
            v-for="tag in doc.tags"
            :key="tag"
            size="small"
            class="mx-1"
          >
            {{ tag }}
          </el-tag>
        </div>
        
        <div class="document-description" v-if="doc.description">
          {{ doc.description }}
        </div>
        
        <div class="document-actions">
          <el-button type="primary" size="small" @click="viewDocument(doc)">
            查看详情
          </el-button>
          <el-button type="success" size="small" @click="downloadDocument(doc)">
            下载
          </el-button>
        </div>
      </el-card>
    </div>

    <div class="pagination-container" v-if="documents.length > 0">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Document, Timer, ChatLineRound, Files } from '@element-plus/icons-vue'
import { documentsApi } from '@/api/publicdocuments'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const documents = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const activeFilters = ref(['1'])
const tagOptions = ref([])

const filters = reactive({
  companyInfo: '',
  brandInfo: '',
  productCategory: '',
  documentType: '',
  language: '',
  tags: []
})

// 获取文档列表
const fetchDocuments = async () => {
  try {
    loading.value = true
    const response = await documentsApi.getPublicDocuments({
      page: currentPage.value - 1,
      size: pageSize.value,
      keyword: searchKeyword.value,
      companyInfo: filters.companyInfo,
      brandInfo: filters.brandInfo,
      productCategory: filters.productCategory,
      documentType: filters.documentType,
      language: filters.language,
      tags: filters.tags,
      sortBy: 'createdAt',
      sortOrder: 'desc'
    })
    
    if (response && response.code === 200 && response.data) {
      documents.value = response.data.items || []
      total.value = response.data.total || 0
    } else {
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

// 应用筛选
const applyFilters = () => {
  currentPage.value = 1
  fetchDocuments()
}

// 重置筛选
const resetFilters = () => {
  Object.keys(filters).forEach(key => {
    filters[key] = Array.isArray(filters[key]) ? [] : ''
  })
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

// 查看文档详情
const viewDocument = (doc) => {
  router.push(`/public/documents/${doc.documentId}`)
}

// 下载文档
const downloadDocument = (doc) => {
  if (doc.downloadUrl) {
    window.open(doc.downloadUrl, '_blank')
  } else {
    ElMessage.warning('下载链接不可用')
  }
}

// 工具函数
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  fetchDocuments()
})
</script>

<style scoped>
.public-search-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-header {
  text-align: center;
  margin-bottom: 30px;
}

.search-box {
  display: flex;
  max-width: 600px;
  margin: 0 auto;
  gap: 10px;
}

.search-input {
  flex: 1;
}

.search-filters {
  margin-bottom: 20px;
}

.document-card {
  margin-bottom: 20px;
}

.document-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.document-header h3 {
  margin: 0;
  font-size: 18px;
}

.document-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.document-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.document-tags {
  margin-bottom: 10px;
}

.document-description {
  margin-bottom: 15px;
  color: #606266;
  font-size: 14px;
}

.document-actions {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.no-results {
  padding: 50px 0;
}
</style> 