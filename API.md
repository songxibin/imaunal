# 文件管理系统 API 文档

## 基础信息

- 基础URL: `http://localhost:8080/api/v1`
- 所有请求都需要在Header中携带JWT Token: `Authorization: Bearer <token>`
- 响应格式统一为JSON
- 时间格式: ISO 8601 (YYYY-MM-DDTHH:mm:ss.sssZ)

## 通用响应格式

```json
{
    "code": 200,          // 状态码
    "message": "success", // 响应消息
    "data": {}           // 响应数据
}
```

## 错误码说明

- 200: 成功
- 400: 请求参数错误
- 401: 未认证
- 403: 无权限
- 404: 资源不存在
- 500: 服务器内部错误

## 用户认证相关接口

### 用户注册

- **POST** `/auth/register`
- **描述**: 注册新用户
- **请求体**:
```json
{
    "username": "string",     // 用户名
    "password": "string",     // 密码
    "email": "string",        // 邮箱
    "fullName": "string"      // 全名
}
```
- **响应**:
```json
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "userId": "string",
        "username": "string",
        "email": "string",
        "fullName": "string"
    }
}
```

### 用户登录

- **POST** `/auth/login`
- **描述**: 用户登录获取token
- **请求体**:
```json
{
    "username": "string",  // 用户名
    "password": "string"   // 密码
}
```
- **响应**:
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "string",
        "userId": "string",
        "username": "string"
    }
}
```

## 文档管理接口

### 上传文档

- **POST** `/documents/upload`
- **描述**: 上传新文档
- **Content-Type**: `multipart/form-data`
- **请求参数**:
  - `file`: 文件
  - `title`: 文档标题
  - `description`: 文档描述
  - `tags`: 标签（可选，JSON数组）
- **响应**:
```json
{
    "code": 200,
    "message": "上传成功",
    "data": {
        "documentId": "string",
        "title": "string",
        "description": "string",
        "fileSize": "number",
        "fileType": "string",
        "uploadTime": "string",
        "tags": ["string"]
    }
}
```

### 获取文档列表

- **GET** `/documents`
- **描述**: 获取文档列表，支持分页和搜索
- **查询参数**:
  - `page`: 页码（默认1）
  - `size`: 每页数量（默认10）
  - `keyword`: 搜索关键词（可选）
  - `sortBy`: 排序字段（可选，默认uploadTime）
  - `sortOrder`: 排序方向（可选，默认desc）
- **响应**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": "number",
        "pages": "number",
        "current": "number",
        "items": [{
            "documentId": "string",
            "title": "string",
            "description": "string",
            "fileSize": "number",
            "fileType": "string",
            "uploadTime": "string",
            "tags": ["string"]
        }]
    }
}
```

### 获取文档详情

- **GET** `/documents/{documentId}`
- **描述**: 获取单个文档的详细信息
- **响应**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "documentId": "string",
        "title": "string",
        "description": "string",
        "fileSize": "number",
        "fileType": "string",
        "uploadTime": "string",
        "updateTime": "string",
        "tags": ["string"],
        "creator": {
            "userId": "string",
            "username": "string"
        },
        "downloadUrl": "string",
        "previewUrl": "string"
    }
}
```

### 更新文档信息

- **PUT** `/documents/{documentId}`
- **描述**: 更新文档信息
- **请求体**:
```json
{
    "title": "string",        // 可选
    "description": "string",  // 可选
    "tags": ["string"]        // 可选
}
```
- **响应**:
```json
{
    "code": 200,
    "message": "更新成功",
    "data": {
        "documentId": "string",
        "title": "string",
        "description": "string",
        "tags": ["string"]
    }
}
```

### 删除文档

- **DELETE** `/documents/{documentId}`
- **描述**: 删除指定文档
- **响应**:
```json
{
    "code": 200,
    "message": "删除成功"
}
```

## 用户管理接口

### 获取当前用户信息

- **GET** `/users/current`
- **描述**: 获取当前登录用户信息
- **响应**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": "string",
        "username": "string",
        "email": "string",
        "fullName": "string",
        "roles": ["string"],
        "createTime": "string"
    }
}
```

### 修改用户信息

- **PUT** `/users/current`
- **描述**: 修改当前用户信息
- **请求体**:
```json
{
    "email": "string",      // 可选
    "fullName": "string",   // 可选
    "oldPassword": "string", // 修改密码时必填
    "newPassword": "string"  // 修改密码时必填
}
```
- **响应**:
```json
{
    "code": 200,
    "message": "更新成功",
    "data": {
        "userId": "string",
        "username": "string",
        "email": "string",
        "fullName": "string"
    }
}
```

## 注意事项

1. 文件上传大小限制：10MB
2. 支持的文件类型：
   - 文档：.doc, .docx, .pdf, .txt
   - 图片：.jpg, .jpeg, .png, .gif
3. JWT Token 有效期为24小时
4. 所有时间相关的字段都使用UTC时间
5. 文件下载链接有效期为1小时 