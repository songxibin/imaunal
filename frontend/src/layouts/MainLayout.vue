<template>
  <el-container class="layout-container">
    <el-container class="layout-content">
      <Header />
      <el-container>
        <Sidebar />
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>

<script setup>
import Sidebar from '@/components/layout/Sidebar.vue'
import Header from '@/components/layout/Header.vue'
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-content {
  flex-direction: column;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>