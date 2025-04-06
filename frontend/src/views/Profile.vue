<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <h2>个人信息</h2>
      </template>
      
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

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

const fetchUserInfo = async () => {
  try {
    const response = await authApi.getCurrentUser()
    console.log('Profile received user data:', response)
    
    // Update the form with user data
    const { username, email, fullName } = response
    form.value = {
      ...form.value,
      username,
      email,
      fullName
    }
    
    // Update the auth store with user data
    authStore.updateUserInfo({
      username,
      email,
      fullName
    })
  } catch (error) {
    console.error('Error fetching user info:', error)
    ElMessage.error('获取用户信息失败')
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

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.el-divider {
  margin: 30px 0;
}
</style> 