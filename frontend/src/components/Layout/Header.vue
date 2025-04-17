<template>
  <el-header>
    <div class="header-content">
      <div class="header-left">
        <h2>文件管理系统</h2>
      </div>
      <div class="header-right">
        <div class="nav-links">
          <el-button text @click="$router.push('/search')">
            <el-icon><Search /></el-icon>
            搜索文档
          </el-button>
          <el-button text @click="$router.push('/pricing')">
            <el-icon><Tickets /></el-icon>
            定价
          </el-button>
        </div>
        <el-divider direction="vertical" />
        <!-- 登录状态显示 -->
        <template v-if="authStore.isAuthenticated">
          <div class="user-info">
            <el-avatar 
              :size="32" 
              class="user-avatar"
              :src="authStore.user.avatar"
            >
              {{ authStore.user.fullName?.charAt(0) || authStore.user.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <el-dropdown @command="handleCommand" trigger="click">
              <span class="user-dropdown">
                <span class="username">{{ authStore.user.fullName || authStore.user.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout" type="danger">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
        <!-- 未登录状态显示 -->
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">
            <el-icon><User /></el-icon>
            登录
          </el-button>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/authuser'
import { ArrowDown, Search, Tickets, User, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 0 20px;
}

.header-content {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav-links {
  display: flex;
  gap: 16px;
  align-items: center;
}

.user-dropdown {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-button) {
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.el-divider {
  height: 24px;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  background-color: var(--el-color-primary);
  color: white;
  font-weight: bold;
}

.username {
  font-size: 14px;
  color: var(--el-text-color-primary);
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  margin-right: 4px;
}
</style>