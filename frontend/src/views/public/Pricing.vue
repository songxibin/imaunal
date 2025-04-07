<template>
  <div class="pricing-container">
    <div class="pricing-header">
      <h1>选择适合您的方案</h1>
      <p class="subtitle">为您的团队选择最合适的文档管理解决方案</p>
    </div>

    <div class="pricing-cards">
      <!-- 基础版 -->
      <div class="pricing-card">
        <div class="card-header">
          <h2>基础版</h2>
          <div class="price">
            <span class="amount">¥99</span>
            <span class="period">/月</span>
          </div>
          <p>适合小型团队使用</p>
        </div>
        <div class="card-body">
          <ul class="features">
            <li>
              <el-icon><Check /></el-icon>
              文档存储空间 10GB
            </li>
            <li>
              <el-icon><Check /></el-icon>
              最多支持 3 种语言
            </li>
            <li>
              <el-icon><Check /></el-icon>
              单文档最大 100,000 字
            </li>
            <li>
              <el-icon><Check /></el-icon>
              基础文档管理功能
            </li>
            <li>
              <el-icon><Check /></el-icon>
              标准客户支持
            </li>
          </ul>
        </div>
        <div class="card-footer">
          <el-button type="primary" @click="handleSubscribe('basic')">
            开始使用
          </el-button>
        </div>
      </div>

      <!-- 专业版 -->
      <div class="pricing-card featured">
        <div class="card-header">
          <div class="popular-tag">最受欢迎</div>
          <h2>专业版</h2>
          <div class="price">
            <span class="amount">¥299</span>
            <span class="period">/月</span>
          </div>
          <p>适合中型团队使用</p>
        </div>
        <div class="card-body">
          <ul class="features">
            <li>
              <el-icon><Check /></el-icon>
              文档存储空间 50GB
            </li>
            <li>
              <el-icon><Check /></el-icon>
              最多支持 10 种语言
            </li>
            <li>
              <el-icon><Check /></el-icon>
              单文档最大 500,000 字
            </li>
            <li>
              <el-icon><Check /></el-icon>
              高级文档管理功能
            </li>
            <li>
              <el-icon><Check /></el-icon>
              优先客户支持
            </li>
            <li>
              <el-icon><Check /></el-icon>
              高级数据分析
            </li>
          </ul>
        </div>
        <div class="card-footer">
          <el-button type="primary" @click="handleSubscribe('pro')">
            升级到专业版
          </el-button>
        </div>
      </div>

      <!-- 企业版 -->
      <div class="pricing-card">
        <div class="card-header">
          <h2>企业版</h2>
          <div class="price">
            <span class="amount">定制</span>
          </div>
          <p>适合大型企业使用</p>
        </div>
        <div class="card-body">
          <ul class="features">
            <li>
              <el-icon><Check /></el-icon>
              无限文档存储空间
            </li>
            <li>
              <el-icon><Check /></el-icon>
              支持所有语言
            </li>
            <li>
              <el-icon><Check /></el-icon>
              无字数限制
            </li>
            <li>
              <el-icon><Check /></el-icon>
              企业级安全保障
            </li>
            <li>
              <el-icon><Check /></el-icon>
              24/7 专属支持
            </li>
            <li>
              <el-icon><Check /></el-icon>
              自定义集成方案
            </li>
          </ul>
        </div>
        <div class="card-footer">
          <el-button @click="handleContact">联系销售</el-button>
        </div>
      </div>
    </div>

    <div class="pricing-features">
      <h2>所有方案都包含</h2>
      <div class="features-grid">
        <div class="feature-item">
          <el-icon><Document /></el-icon>
          <h3>文档版本控制</h3>
          <p>追踪所有文档的修改历史</p>
        </div>
        <div class="feature-item">
          <el-icon><Search /></el-icon>
          <h3>全文检索</h3>
          <p>快速找到所需文档</p>
        </div>
        <div class="feature-item">
          <el-icon><Lock /></el-icon>
          <h3>安全保障</h3>
          <p>数据加密存储与传输</p>
        </div>
        <div class="feature-item">
          <el-icon><Share /></el-icon>
          <h3>协作功能</h3>
          <p>团队共享与协作</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Check, Document, Search, Lock, Share } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const handleSubscribe = (plan) => {
  ElMessage.info('请先登录以订阅此方案')
}

const handleContact = () => {
  ElMessage.info('请发送邮件至 sales@example.com')
}
</script>

<style scoped>
.pricing-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px;
}

.pricing-header {
  text-align: center;
  margin-bottom: 60px;
}

.pricing-header h1 {
  font-size: 2.5em;
  margin-bottom: 20px;
  color: #303133;
}

.subtitle {
  font-size: 1.2em;
  color: #606266;
}

.pricing-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 60px;
}

.pricing-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  display: flex;
  flex-direction: column;
  position: relative;
  transition: transform 0.3s;
}

.pricing-card:hover {
  transform: translateY(-5px);
}

.pricing-card.featured {
  border: 2px solid #409eff;
}

.popular-tag {
  position: absolute;
  top: -12px;
  right: 20px;
  background: #409eff;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.9em;
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.price {
  margin: 20px 0;
}

.amount {
  font-size: 2.5em;
  font-weight: bold;
  color: #303133;
}

.period {
  color: #909399;
}

.features {
  list-style: none;
  padding: 0;
  margin: 0;
}

.features li {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  color: #606266;
}

.features .el-icon {
  color: #67c23a;
}

.card-footer {
  margin-top: auto;
  padding-top: 30px;
  text-align: center;
}

.pricing-features {
  text-align: center;
  margin-top: 80px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.feature-item {
  padding: 20px;
}

.feature-item .el-icon {
  font-size: 2em;
  color: #409eff;
  margin-bottom: 15px;
}

.feature-item h3 {
  margin: 10px 0;
  color: #303133;
}

.feature-item p {
  color: #606266;
  margin: 0;
}

@media (max-width: 768px) {
  .pricing-cards {
    grid-template-columns: 1fr;
  }
}
</style>