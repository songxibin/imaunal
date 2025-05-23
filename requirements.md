# 多语言用户文档平台需求设计

项目概览

这是一个简洁版的文件管理系统，支持文档上传、管理、查看、搜索。
首期阶段为 MVP，最大限度简化，为后期扩展预留空间。
## 1. 用户角色定义

### 1.1 游客
- 无需登录即可访问
- 可以浏览和搜索已发布的文档
- 可以查看价格方案
- 可以注册成为普通用户或商业用户

### 1.2 普通用户
- 需要注册账号
- 具有游客所有权限
- 可以收藏文档
- 可以设置个人偏好语言
- 可以对文档进行评分和评论

### 1.3 商业用户
- 需要注册并订阅付费方案
- 具有文档管理权限：
  - 上传文档
  - 编辑文档
  - 翻译文档
  - 发布/下架文档
  - 管理文档访问权限
- 查看文档访问统计和分析
- 管理团队成员权限

## 2. 核心功能模块

### 2.1 文档管理系统
- 文档属性定义：
  - 公司信息
  - 品牌信息
  - 产品类别
  - 文档类型（用户手册/技术文档/API文档等）
  - 适用语言
  - 版本信息
  - 发布状态
- 文档版本控制
- 多语言内容管理
- 文档审核流程
- 文件格式支持：
  - 上传支持：Word(.docx)、PDF
  - 展示格式：统一转换为PDF
- 文档预览功能：
  - 在线PDF预览
  - 支持目录导航
  - 支持文档内搜索
- 文档转换功能：
  - Word自动转PDF
  - 转换状态追踪
  - 转换失败通知
- 翻译状态管理：
  - 显示各语言版本状态（未开始/进行中/已完成）
  - 翻译进度百分比
  - 最后更新时间
  - 翻译人员信息

### 2.2 翻译管理系统
- 支持多语言翻译
- 翻译进度追踪
- 翻译质量审核
- 翻译记忆库
- 术语管理

### 2.3 搜索系统
- 多语言全文检索
- 高级筛选：
  - 公司
  - 品牌
  - 类别
  - 语言
  - 文档类型
- 相关度排序
- 搜索建议

### 2.4 计费系统
- 订阅方案：
  - 基础版
  - 专业版
  - 企业版
- 计费标准：
  - 按文档字数
  - 按支持语言数量
  - 按存储空间
- 账单管理
- 支付集成

## 3. 非功能需求

### 3.1 性能需求
- 页面首次加载时间 < 5秒
- 页面二次加载时间 < 3秒
- 搜索响应时间 < 3秒
- 支持并发用户数 > 1000
- 文档上传大小限制：50MB
- API请求超时时间：30秒

### 3.2 安全需求
- 用户认证和授权
- 数据加密传输
- 敏感信息保护
- 防止SQL注入和XSS攻击

### 3.3 可用性需求
- 系统可用性 > 99.9%
- 多设备适配
- 友好的用户界面
- 完善的帮助文档

### 3.4 扩展性需求
- 支持水平扩展
- 模块化设计
- API接口标准化
- 支持第三方集成

## 4. 技术架构建议

### 4.1 前端技术
- Vue3.js
- 响应式设计
- 部署在 AWS S3 + CloudFront

### 4.2 后端技术
- java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- RESTful API 
- AWS Lambda 用于文件处理和异步任务

### 4.3 存储服务
- 文档存储：Aliyun OSS， AWS S3
- 数据库：PostgreSQL
- 文件处理：AWS Lambda
- 用户认证：本地PostgreSQL数据库
  - 用户表设计
  - 密码加密存储
  - Session管理
  - 权限控制

## 5. 首期开发范围

### 5.1 第一阶段（MVP）
- 用户注册和认证：
  - 基于Spring Security框架
  - 用户数据存储在PostgreSQL
  - JWT token认证
  - 角色权限管理
### 5.2 第二阶段（MVP）
- 基础文档管理 ( + RDS)
- 简单关键词搜索 (RDS)
- 基础付费功能 (Stripe集成)

### 5.2 第3阶段
- 高级搜索
- 翻译管理
- 数据分析
- API集成

### 5.3 第4阶段
- AI辅助翻译
- 高级统计分析
- 自动化工作流
- 更多第三方集成



文档存储到阿里云oss里面
更新backend使用aliyun oss 存储上传文件

增加预览页面 发布 功能， 发布后云上面会把文件从现在的bucket 转移到 publicName里面，文件状态改成published。
新的需求：已经发布的文件，支持取消发布功能，如果取消，将文件从public bucket里面删除。
进入预览文件页面时候，增加查看其他属性，如果已经发布，将发布的url做成qr图片展示



支持上传文件时：
- 文件属性定义：
  - 公司信息
  - 品牌信息
  - 产品类别
  - 文档类型（用户手册/技术文档/API文档等）
  - 适用语言
  - 版本信息
  - 发布状态

增加一个外部搜索页面，不需要登录就可以访问，查询可以根据需求搜索，或者自定义搜索

设计API接口文件，需求为：增加一个外部搜索页面，不需要登录就可以访问，查询可以根据需求搜索，或者自定义搜索

设计一个外部无需访问页面，类似https://www.google.com.sg/, 搜索页面可以搜索已经发布的文件


设计类似https://openai.com/chatgpt/pricing/的页面，内容实现### 2.4 计费系统
这个定价页面设计包含：
1. 三个定价方案的清晰展示
2. 每个方案的详细特性列表
3. 突出显示最受欢迎的方案
4. 响应式设计，适配各种屏幕尺寸
5. 统一的设计风格
6. 清晰的价格和功能对比
7. 通用功能展示区域
8. 简洁的交互效果
用户可以：

1. 查看各个方案的详细信息
2. 比较不同方案的功能差异
3. 点击订阅按钮（需要登录）
4. 联系销售（企业版）
需要注意的是，这个页面是完全公开的，不需要登录就能访问，但是订阅功能需要用户登录后才能使用。

http://localhost:8080/api/v1/api-docs
根据新的接口，/users/{userId}/stats  更新前端代码，增加用户的状态页面