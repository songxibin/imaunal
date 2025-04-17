<template>
  <div class="search-container">
    <!-- 搜索头部 -->
    <div class="search-header">
      <h1 class="logo">文档搜索</h1>
      <div class="search-box">
        <input 
          v-model="searchParams.keyword"
          type="text" 
          placeholder="请输入关键词搜索文档..."
          @keyup.enter="handleSearch"
        >
        <button @click="handleSearch">搜索</button>
        <button @click="showAdvanced = !showAdvanced">
          {{ showAdvanced ? '收起' : '高级搜索' }}
        </button>
      </div>
      
      <!-- 高级搜索选项 -->
      <div v-if="showAdvanced" class="advanced-search">
        <div class="search-row">
          <input v-model="searchParams.companyInfo" placeholder="公司信息">
          <input v-model="searchParams.brandInfo" placeholder="品牌信息">
          <input v-model="searchParams.productCategory" placeholder="产品类别">
        </div>
        <div class="search-row">
          <input v-model="searchParams.documentType" placeholder="文档类型">
          <input v-model="searchParams.language" placeholder="语言">
          <input v-model="searchParams.tags" placeholder="标签">
        </div>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-results" v-if="documents.length">
      <div v-for="doc in documents" :key="doc.id" class="result-item">
        <h3>{{ doc.title }}</h3>
        <p class="description">{{ doc.description }}</p>
        <div class="meta-info">
          <span>类型: {{ doc.documentType }}</span>
          <span>语言: {{ doc.language }}</span>
          <span>大小: {{ formatFileSize(doc.fileSize) }}</span>
          <span>更新时间: {{ formatDate(doc.updatedAt) }}</span>
        </div>
        <div class="tags" v-if="doc.tags && doc.tags.length">
          <span v-for="tag in doc.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>
        <a :href="`/public/documents/${doc.id}`" target="_blank">查看详情</a>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <button 
          :disabled="currentPage === 0" 
          @click="handlePageChange(currentPage - 1)"
        >上一页</button>
        <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
        <button 
          :disabled="currentPage >= totalPages - 1" 
          @click="handlePageChange(currentPage + 1)"
        >下一页</button>
      </div>
    </div>

    <!-- 无结果提示 -->
    <div v-else-if="hasSearched" class="no-results">
      未找到相关文档
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'

const showAdvanced = ref(false)
const documents = ref([])
const currentPage = ref(0)
const totalPages = ref(0)
const hasSearched = ref(false)

const searchParams = reactive({
  keyword: '',
  companyInfo: '',
  brandInfo: '',
  productCategory: '',
  documentType: '',
  language: '',
  tags: '',
  page: 0,
  size: 10,
  sortBy: 'updatedAt',
  sortOrder: 'desc'
})

const handleSearch = async () => {
  try {
    const params = { ...searchParams }
    const response = await axios.get('/api/v1/public/documents', { params })
    documents.value = response.data.data.items
    totalPages.value = Math.ceil(response.data.data.total / searchParams.size)
    hasSearched.value = true
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

const handlePageChange = (page) => {
  searchParams.page = page
  currentPage.value = page
  handleSearch()
}

const formatFileSize = (size) => {
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  return (size / (1024 * 1024)).toFixed(2) + ' MB'
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}
</script>

<style scoped>
.search-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  font-size: 2.5em;
  margin-bottom: 20px;
  color: #333;
}

.search-box {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-bottom: 20px;
}

.search-box input {
  width: 500px;
  padding: 10px 15px;
  border: 2px solid #ddd;
  border-radius: 24px;
  font-size: 16px;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 20px;
  background-color: #1a73e8;
  color: white;
  cursor: pointer;
}

button:hover {
  background-color: #1557b0;
}

.advanced-search {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-top: 20px;
}

.search-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.search-row input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.result-item {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.result-item h3 {
  margin: 0 0 10px 0;
  color: #1a0dab;
}

.description {
  color: #545454;
  margin-bottom: 10px;
}

.meta-info {
  display: flex;
  gap: 20px;
  color: #70757a;
  font-size: 0.9em;
  margin-bottom: 10px;
}

.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.tag {
  background: #e8f0fe;
  color: #1967d2;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.9em;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 40px;
}

.no-results {
  text-align: center;
  color: #70757a;
  margin-top: 40px;
}
</style>