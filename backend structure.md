server/
├── src/main/java/com/filemanager/
│   ├── config/         # 配置类（安全配置、跨域CORS、Swagger等）
│   ├── controller/     # API接口（RestController）
│   ├── service/        # 业务逻辑层（Service接口和实现）
│   ├── repository/     # JPA数据访问（Repository接口）
│   ├── model/          # 实体类（Entity / DTO / VO）
│   ├── security/       # JWT认证、用户权限相关
│   ├── util/           # 工具类（如文件处理、加密工具）
│   └── FileManagerApplication.java # Spring Boot 启动类
├── src/main/resources/
│   ├── application.yml # SpringBoot配置文件
│   ├── static/         # 静态资源（如上传临时文件）
│   └── templates/      # 可选，邮件模板、页面模板
└── pom.xml             # Maven构建文件
