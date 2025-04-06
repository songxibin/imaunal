<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>文档总数</span>
              <el-icon><Document /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.totalDocuments }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>本月上传</span>
              <el-icon><Upload /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.monthlyUploads }}</div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>存储空间</span>
              <el-icon><Folder /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ formatFileSize(stats.totalStorage) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近上传的文档</span>
              <el-button type="primary" link @click="$router.push('/documents')">
                查看全部
              </el-button>
            </div>
          </template>
          
          <el-table :data="recentDocuments" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="fileType" label="类型" width="100" />
            <el-table-column prop="uploadTime" label="上传时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.uploadTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  link
                  @click="$router.push(`/documents/${row.documentId}`)"
                >
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>文档类型分布</span>
            </div>
          </template>
          <div class="chart-container">
            <!-- 这里可以添加图表组件 -->
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, Upload, Folder } from '@element-plus/icons-vue'
import { documentsApi } from '@/api/documents'
import dayjs from 'dayjs'

const stats = ref({
  totalDocuments: 0,
  monthlyUploads: 0,
  totalStorage: 0
})

const recentDocuments = ref([])

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

const fetchDashboardData = async () => {
  try {
    // Fetch dashboard statistics
    const statsResponse = await documentsApi.getDashboardStats()
    stats.value = statsResponse.data

    // Fetch recent documents
    const documentsResponse = await documentsApi.getDocuments({
      page: 1,
      size: 5,
      sortBy: 'createdAt',
      sortOrder: 'desc'
    })
    recentDocuments.value = documentsResponse.data.items
  } catch (error) {
    console.error('Failed to fetch dashboard data:', error)
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.stat-card {
  height: 180px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  text-align: center;
  margin-top: 20px;
  color: #409EFF;
}

.chart-container {
  height: 300px;
}
</style> 