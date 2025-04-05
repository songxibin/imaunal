frontend/
├── public/             # 公共资源（index.html、favicon等）
├── src/
│   ├── assets/         # 静态资源（图片、样式）
│   ├── components/     # 通用组件（上传组件、分页组件等）
│   ├── views/          # 页面级组件（如登录页、文档管理页）
│   ├── router/         # 路由定义（Vue Router）
│   ├── store/          # 状态管理（Pinia/Vuex，保存用户信息、Token等）
│   ├── api/            # API请求封装（Axios实例 + 每个模块的API方法）
│   ├── utils/          # 工具类（如Token处理、下载工具）
│   ├── layouts/        # 布局组件（如顶部导航栏+侧边栏）
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件（挂载Vue实例）
├── vite.config.js      # Vite配置文件
└── package.json        # 项目依赖
