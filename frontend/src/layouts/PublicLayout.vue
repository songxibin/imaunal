<template>
  <el-container class="layout-container">
    <el-header>
      <div class="header-content">
        <div class="logo">
          <router-link to="/search">
            <h2>文档管理系统</h2>
          </router-link>
        </div>
        <div class="nav-links">
          <el-button text @click="$router.push('/search')">
            <el-icon><Search /></el-icon>
            搜索文档
          </el-button>
          <el-button text @click="$router.push('/pricing')">
            <el-icon><Tickets /></el-icon>
            定价
          </el-button>
          <el-button type="primary" @click="$router.push('/login')">
            <el-icon><User /></el-icon>
            登录
          </el-button>
        </div>
      </div>
    </el-header>
    
    <el-main>
      <router-view />
    </el-main>
    
    <el-footer>
      <div class="footer-content">
        <p>&copy; 2023 文档管理系统. 保留所有权利.</p>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup>
import { Search, Tickets, User } from '@element-plus/icons-vue'
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 0 20px;
}

.header-content {
  height: 60px;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo a {
  text-decoration: none;
  color: #303133;
}

.logo h2 {
  margin: 0;
}

.nav-links {
  display: flex;
  gap: 16px;
  align-items: center;
}

.el-main {
  flex: 1;
  background-color: #f0f2f5;
  padding: 20px;
}

.el-footer {
  background-color: #304156;
  color: #909399;
  padding: 20px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
}

:deep(.el-button) {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>