<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>登录</h2>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading">
            登录
          </el-button>
          <el-button @click="$router.push('/register')">
            注册账号
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/store/authuser'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    console.log('Login form data:', form.value)
    const result = await authStore.login(form.value)
    console.log('Login result:', result)
    ElMessage.success('登录成功')
  } catch (error) {
    console.error('Login error:', error)
    ElMessage.error(error.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const handleLogin = async () => {
  try {
    const response = await authApi.login(loginForm.value)
    if (response.code === 200) {
      const { token, user } = response.data
      authStore.setToken(token)
      authStore.setUserInfo({
        userId: user.id,
        username: user.username,
        email: user.email,
        fullName: user.fullName
      })
      router.push('/')
    }
  } catch (error) {
    ElMessage.error('登录失败')
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}

.login-card {
  width: 400px;
}

.el-button {
  width: 100%;
  margin-bottom: 10px;
}
</style>