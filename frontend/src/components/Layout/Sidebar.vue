<template>
  <el-aside width="200px">
    <el-menu
      :router="true"
      :default-active="route.path"
      class="el-menu-vertical"
      background-color="#304156"
      text-color="#fff"
      active-text-color="#409EFF"
    >
      <el-menu-item index="/">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item index="/documents">
        <el-icon><Document /></el-icon>
        <span>文档管理</span>
      </el-menu-item>
      <el-menu-item index="/profile">
        <el-icon><User /></el-icon>
        <span>个人中心</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { HomeFilled, Document, User } from '@element-plus/icons-vue'

const route = useRoute()
</script>

<style scoped>
.el-aside {
  background-color: #304156;
}

.el-menu {
  border-right: none;
}

:deep(.el-menu-item) {
  &.is-active {
    background-color: #263445;
  }
  
  &:hover {
    background-color: #263445;
  }
}
</style>