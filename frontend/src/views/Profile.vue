<template>
  <div class="profile-container">
    <el-tabs v-model="activeTab">
      <!-- 账户统计标签页 -->
      <el-tab-pane label="账户统计" name="stats">
        <el-card class="stats-card">
          <el-row :gutter="20">
            <!-- 现有的统计卡片内容保持不变 -->
            <el-col :span="8">
              <el-card shadow="hover" class="stat-item">
                <template #header>
                  <div class="stat-header">
                    <el-icon><FolderOpened /></el-icon>  <!-- Changed Disk to FolderOpened -->
                    <span>存储空间</span>
                  </div>
                </template>
                <div class="stat-content">
                  <el-progress
                    type="dashboard"
                    :percentage="Math.round(userStats.storageUsagePercent || 0)"
                    :color="storageProgressColor"
                  />
                  <div class="storage-details">
                    <p>已用：{{ userStats.storageUsedFormatted || '0 B' }}</p>
                    <p>总量：{{ userStats.storageLimitFormatted || '0 B' }}</p>
                  </div>
                </div>
              </el-card>
            </el-col>

            <el-col :span="8">
              <el-card shadow="hover" class="stat-item">
                <template #header>
                  <div class="stat-header">
                    <el-icon><Document /></el-icon>
                    <span>文档统计</span>
                  </div>
                </template>
                <div class="stat-content">
                  <div class="stat-value">{{ userStats.totalWordCount || 0 }}</div>
                  <div class="stat-label">总字数</div>
                </div>
              </el-card>
            </el-col>

            <el-col :span="8">
              <el-card shadow="hover" class="stat-item">
                <template #header>
                  <div class="stat-header">
                    <el-icon><Fold /></el-icon>  <!-- Changed Language to Fold -->
                    <span>语言支持</span>
                  </div>
                </template>
                <div class="stat-content">
                  <div class="stat-value">{{ userStats.languageCount || 0 }}</div>
                  <div class="stat-label">支持的语言数量</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-tab-pane>

      <!-- 个人信息标签页 -->
      <el-tab-pane label="个人信息" name="profile">
        <el-card class="profile-form">
          <!-- 现有的个人信息表单内容保持不变 -->
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            @submit.prevent="handleSubmit"
          >
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" type="email" />
            </el-form-item>
            
            <el-form-item label="姓名" prop="fullName">
              <el-input v-model="form.fullName" />
            </el-form-item>
            
            <el-divider>修改密码</el-divider>
            
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="form.oldPassword"
                type="password"
                show-password
                placeholder="如需修改密码请输入原密码"
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="form.newPassword"
                type="password"
                show-password
                placeholder="请输入新密码"
              />
            </el-form-item>
            
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入新密码"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" native-type="submit" :loading="loading">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/store/authuser'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { Document, FolderOpened, Fold } from '@element-plus/icons-vue'

// 添加用户统计数据
const userStats = ref({
  storageUsed: 0,
  storageLimit: 0,
  storageUsedFormatted: '0 B',
  storageLimitFormatted: '0 B',
  storageUsagePercent: 0,
  totalWordCount: 0,
  languageCount: 0
})

// 存储空间进度条颜色计算
const storageProgressColor = computed(() => {
  const percent = userStats.value.storageUsagePercent
  if (percent < 70) return '#67C23A'
  if (percent < 90) return '#E6A23C'
  return '#F56C6C'
})


// 在现有的 onMounted 中添加获取统计信息的调用
onMounted(() => {
  fetchUserStats()
})

const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  email: '',
  fullName: '',
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (form.value.oldPassword && !value) {
    callback(new Error('请输入新密码'))
  } else if (value && value.length < 6) {
    callback(new Error('密码长度不能小于6位'))
  } else {
    if (form.value.confirmPassword) {
      formRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (form.value.newPassword && !value) {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form.value.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  oldPassword: [
    { validator: (rule, value, callback) => {
      if (form.value.newPassword && !value) {
        callback(new Error('修改密码时需要输入原密码'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  newPassword: [
    { validator: validatePass, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validatePass2, trigger: 'blur' }
  ]
}



// 获取用户统计信息
const fetchUserStats = async () => {
  try {
    const userId = authStore.user.userId
    console.log('User ID:', userId)
    console.log('Auth Store:', authStore)
    const response = await authApi.getUserStats(userId)
    userStats.value = response
  } catch (error) {
    console.error('获取用户统计信息失败:', error)
    ElMessage.error('获取用户统计信息失败')
  }
}
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const updateData = {
      email: form.value.email,
      fullName: form.value.fullName
    }
    
    if (form.value.oldPassword && form.value.newPassword) {
      updateData.oldPassword = form.value.oldPassword
      updateData.newPassword = form.value.newPassword
    }
    
    await authApi.updateUser(updateData)
    ElMessage.success('更新成功')
    
    // 清空密码字段
    form.value.oldPassword = ''
    form.value.newPassword = ''
    form.value.confirmPassword = ''
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新失败')
  } finally {
    loading.value = false
  }
}
// 添加标签页激活状态
const activeTab = ref('stats')
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

/* 移除多余的边距 */
.stats-card {
  margin-bottom: 0;
}

.profile-form {
  margin-top: 0;
}

.stat-item {
  height: 100%;
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.storage-details {
  margin-top: 15px;
  text-align: center;
}

.storage-details p {
  margin: 5px 0;
  color: #606266;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  margin-top: 10px;
  color: #606266;
}

.profile-form {
  margin-top: 20px;
}

.el-divider {
  margin: 30px 0;
}
</style>