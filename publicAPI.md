
1. 搜索公共文档列表
接口说明
获取已发布的公共文档列表，支持多种搜索条件和分页。
请求信息
请求路径：/public/documents
请求方法：GET
请求参数：
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 否 | 关键词搜索，匹配标题和描述 |
| companyInfo | string | 否 | 公司信息 |
| brandInfo | string | 否 | 品牌信息 |
| productCategory | string | 否 | 产品类别 |
| documentType | string | 否 | 文档类型 |
| language | string | 否 | 语言 |
| tags | string | 否 | 标签 |
| sortBy | string | 否 | 排序字段 |
| sortOrder | string | 否 | 排序方式(asc/desc) |
| page | number | 否 | 页码，从0开始 |
| size | number | 否 | 每页数量 |
响应信息
{
    "code": 200,
    "message": "获取公共文档列表成功",
    "data": {
        "items": [
            {
                "id": "number",
                "documentId": "string",
                "title": "string",
                "description": "string",
                "fileName": "string",
                "fileSize": "number",
                "fileType": "string",
                "tags": ["string"],
                "companyInfo": "string",
                "brandInfo": "string",
                "productCategory": "string",
                "documentType": "string",
                "language": "string",
                "version": "string",
                "createdAt": "string",
                "updatedAt": "string",
                "status": "string",
                "creator": {
                    "userid": "number",
                    "username": "string"
                }
            }
        ],
        "total": "number",
        "page": "number",
        "size": "number"
    }
}
2. 获取公共文档详情
接口说明
获取单个已发布的公共文档的详细信息。
请求信息
请求路径：/public/documents/{id}
请求方法：GET
路径参数：
id: 文档ID
响应信息
{
    "code": 200,
    "message": "获取公共文档详情成功",
    "data": {
        "id": "number",
        "documentId": "string",
        "title": "string",
        "description": "string",
        "fileName": "string",
        "fileSize": "number",
        "fileType": "string",
        "tags": ["string"],
        "companyInfo": "string",
        "brandInfo": "string",
        "productCategory": "string",
        "documentType": "string",
        "language": "string",
        "version": "string",
        "createdAt": "string",
        "updatedAt": "string",
        "status": "string",
        "creator": {
            "userid": "number",
            "username": "string"
        }
    }
}
1. 错误响应
当发生错误时，API将返回以下格式的响应
{
    "code": "number",
    "message": "string",
    "data": null
}

常见错误码：
200: 成功
400: 请求参数错误
404: 资源不存在
500: 服务器内部错误
4. 注意事项
所有接口都是公开的，不需要认证即可访问
只能访问状态为 PUBLISHED 的文档
搜索支持模糊匹配（使用 LIKE 查询）
文档类型和语言使用精确匹配
支持分页查询，默认每页20条数据
支持按多个字段排序
所有时间字段使用 ISO 8601 格式
5. 示例请求
基础搜索
{
    "code": 200,
    "message": "获取公共文档详情成功",
    "data": {
        "id": "number",
        "documentId": "string",
        "title": "string",
        "description": "string",
        "fileName": "string",
        "fileSize": "number",
        "fileType": "string",
        "tags": ["string"],
        "companyInfo": "string",
        "brandInfo": "string",
        "productCategory": "string",
        "documentType": "string",
        "language": "string",
        "version": "string",
        "createdAt": "string",
        "updatedAt": "string",
        "status": "string",
        "creator": {
            "userid": "number",
            "username": "string"
        }
    }
}
高级搜索
