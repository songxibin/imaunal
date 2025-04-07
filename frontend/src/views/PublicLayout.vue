<template>
  <div class="public-layout">
    <header class="public-header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/public/search">
            <h1>文档管理系统</h1>
          </router-link>
        </div>
        <div class="nav-links">
          <router-link to="/public/search">搜索文档</router-link>
          <router-link to="/login">登录</router-link>
        </div>
      </div>
    </header>
    
    <main class="public-main">
      <router-view />
    </main>
    
    <footer class="public-footer">
      <div class="footer-content">
        <p>&copy; 2023 文档管理系统. 保留所有权利.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
// No additional setup needed
</script>

<style scoped>
.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.public-header {
  background-color: #409eff;
  color: white;
  padding: 15px 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo a {
  text-decoration: none;
  color: white;
}

.logo h1 {
  margin: 0;
  font-size: 24px;
}

.nav-links {
  display: flex;
  gap: 20px;
}

.nav-links a {
  color: white;
  text-decoration: none;
  font-weight: 500;
  transition: opacity 0.3s;
}

.nav-links a:hover {
  opacity: 0.8;
}

.public-main {
  flex: 1;
  background-color: #f5f7fa;
}

.public-footer {
  background-color: #303133;
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
</style> 