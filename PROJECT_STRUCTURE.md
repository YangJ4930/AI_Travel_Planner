# 项目目录结构

```
AI_Travel_Planner/
├── README.md                          # 项目说明文档
├── DESIGN_DOCUMENT.md                 # 设计文档
├── PROJECT_STRUCTURE.md               # 项目结构说明
├── .gitignore                         # Git忽略文件
├── .env.example                       # 环境变量示例文件
├── docker-compose.yml                 # Docker Compose配置
├── docker-compose.dev.yml             # 开发环境Docker配置
├── Makefile                           # 构建脚本
│
├── frontend/                          # Vue.js前端项目
│   ├── public/                        # 静态资源
│   │   ├── index.html
│   │   ├── favicon.ico
│   │   └── icons/
│   ├── src/                           # 源代码
│   │   ├── main.ts                    # 应用入口
│   │   ├── App.vue                    # 根组件
│   │   ├── assets/                    # 静态资源
│   │   │   ├── images/
│   │   │   ├── styles/
│   │   │   └── fonts/
│   │   ├── components/                # 通用组件
│   │   │   ├── common/                # 基础组件
│   │   │   │   ├── Header.vue
│   │   │   │   ├── Footer.vue
│   │   │   │   ├── Loading.vue
│   │   │   │   └── Modal.vue
│   │   │   ├── map/                   # 地图相关组件
│   │   │   │   ├── MapContainer.vue
│   │   │   │   ├── RouteDisplay.vue
│   │   │   │   └── LocationPicker.vue
│   │   │   ├── voice/                 # 语音相关组件
│   │   │   │   ├── VoiceRecorder.vue
│   │   │   │   └── VoicePlayer.vue
│   │   │   └── travel/                # 旅行相关组件
│   │   │       ├── PlanCard.vue
│   │   │       ├── ItineraryItem.vue
│   │   │       └── ExpenseTracker.vue
│   │   ├── views/                     # 页面组件
│   │   │   ├── Home.vue               # 首页
│   │   │   ├── Login.vue              # 登录页
│   │   │   ├── Register.vue           # 注册页
│   │   │   ├── Dashboard.vue          # 用户仪表板
│   │   │   ├── PlanCreate.vue         # 创建计划
│   │   │   ├── PlanDetail.vue         # 计划详情
│   │   │   ├── PlanEdit.vue           # 编辑计划
│   │   │   ├── ExpenseManage.vue      # 费用管理
│   │   │   └── Profile.vue            # 用户资料
│   │   ├── router/                    # 路由配置
│   │   │   └── index.ts
│   │   ├── store/                     # Vuex/Pinia状态管理
│   │   │   ├── index.ts
│   │   │   ├── modules/
│   │   │   │   ├── auth.ts            # 认证状态
│   │   │   │   ├── travel.ts          # 旅行计划状态
│   │   │   │   ├── map.ts             # 地图状态
│   │   │   │   └── voice.ts           # 语音状态
│   │   ├── services/                  # API服务
│   │   │   ├── api.ts                 # API基础配置
│   │   │   ├── auth.ts                # 认证API
│   │   │   ├── travel.ts              # 旅行API
│   │   │   ├── map.ts                 # 地图API
│   │   │   ├── voice.ts               # 语音API
│   │   │   └── expense.ts             # 费用API
│   │   ├── utils/                     # 工具函数
│   │   │   ├── request.ts             # HTTP请求封装
│   │   │   ├── storage.ts             # 本地存储
│   │   │   ├── date.ts                # 日期处理
│   │   │   ├── format.ts              # 格式化工具
│   │   │   └── validation.ts          # 表单验证
│   │   ├── types/                     # TypeScript类型定义
│   │   │   ├── api.ts
│   │   │   ├── travel.ts
│   │   │   ├── user.ts
│   │   │   └── common.ts
│   │   └── composables/               # Vue 3 组合式函数
│   │       ├── useAuth.ts
│   │       ├── useMap.ts
│   │       ├── useVoice.ts
│   │       └── useTravel.ts
│   ├── tests/                         # 测试文件
│   │   ├── unit/
│   │   └── e2e/
│   ├── package.json                   # 依赖配置
│   ├── vite.config.ts                 # Vite配置
│   ├── tsconfig.json                  # TypeScript配置
│   ├── tailwind.config.js             # Tailwind CSS配置
│   ├── .eslintrc.js                   # ESLint配置
│   ├── .prettierrc                    # Prettier配置
│   └── Dockerfile                     # Docker构建文件
│
├── backend/                           # Spring Boot后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/travelplanner/
│   │   │   │       ├── TravelPlannerApplication.java  # 应用入口
│   │   │   │       ├── config/                        # 配置类
│   │   │   │       │   ├── SecurityConfig.java
│   │   │   │       │   ├── WebConfig.java
│   │   │   │       │   ├── RedisConfig.java
│   │   │   │       │   └── SwaggerConfig.java
│   │   │   │       ├── controller/                    # 控制器
│   │   │   │       │   ├── AuthController.java
│   │   │   │       │   ├── TravelPlanController.java
│   │   │   │       │   ├── VoiceController.java
│   │   │   │       │   ├── MapController.java
│   │   │   │       │   └── ExpenseController.java
│   │   │   │       ├── service/                       # 服务层
│   │   │   │       │   ├── AuthService.java
│   │   │   │       │   ├── TravelPlanService.java
│   │   │   │       │   ├── VoiceService.java
│   │   │   │       │   ├── MapService.java
│   │   │   │       │   ├── ExpenseService.java
│   │   │   │       │   └── AIService.java
│   │   │   │       ├── repository/                    # 数据访问层
│   │   │   │       │   ├── UserRepository.java
│   │   │   │       │   ├── TravelPlanRepository.java
│   │   │   │       │   ├── ItineraryItemRepository.java
│   │   │   │       │   └── ExpenseRecordRepository.java
│   │   │   │       ├── entity/                        # 实体类
│   │   │   │       │   ├── User.java
│   │   │   │       │   ├── UserPreference.java
│   │   │   │       │   ├── TravelPlan.java
│   │   │   │       │   ├── ItineraryItem.java
│   │   │   │       │   └── ExpenseRecord.java
│   │   │   │       ├── dto/                           # 数据传输对象
│   │   │   │       │   ├── request/
│   │   │   │       │   │   ├── LoginRequest.java
│   │   │   │       │   │   ├── RegisterRequest.java
│   │   │   │       │   │   ├── CreatePlanRequest.java
│   │   │   │       │   │   └── VoiceRequest.java
│   │   │   │       │   └── response/
│   │   │   │       │       ├── ApiResponse.java
│   │   │   │       │       ├── UserResponse.java
│   │   │   │       │       ├── PlanResponse.java
│   │   │   │       │       └── VoiceResponse.java
│   │   │   │       ├── exception/                     # 异常处理
│   │   │   │       │   ├── GlobalExceptionHandler.java
│   │   │   │       │   ├── BusinessException.java
│   │   │   │       │   └── ResourceNotFoundException.java
│   │   │   │       ├── security/                      # 安全相关
│   │   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │   │       │   ├── JwtTokenProvider.java
│   │   │   │       │   └── UserDetailsServiceImpl.java
│   │   │   │       ├── util/                          # 工具类
│   │   │   │       │   ├── DateUtil.java
│   │   │   │       │   ├── JsonUtil.java
│   │   │   │       │   └── ValidationUtil.java
│   │   │   │       └── integration/                   # 第三方集成
│   │   │   │           ├── amap/                      # 高德地图
│   │   │   │           │   ├── AmapService.java
│   │   │   │           │   └── AmapClient.java
│   │   │   │           ├── xunfei/                    # 科大讯飞
│   │   │   │           │   ├── XunfeiService.java
│   │   │   │           │   └── XunfeiClient.java
│   │   │   │           └── alibaba/                   # 阿里云
│   │   │   │               ├── BailianService.java
│   │   │   │               └── OssService.java
│   │   │   └── resources/
│   │   │       ├── application.yml                    # 应用配置
│   │   │       ├── application-dev.yml                # 开发环境配置
│   │   │       ├── application-prod.yml               # 生产环境配置
│   │   │       ├── application-docker.yml             # Docker环境配置
│   │   │       ├── logback-spring.xml                 # 日志配置
│   │   │       └── db/
│   │   │           └── migration/                     # 数据库迁移脚本
│   │   │               ├── V1__Create_users_table.sql
│   │   │               ├── V2__Create_travel_plans_table.sql
│   │   │               └── V3__Create_expenses_table.sql
│   │   └── test/                                      # 测试代码
│   │       └── java/
│   │           └── com/travelplanner/
│   │               ├── controller/
│   │               ├── service/
│   │               └── repository/
│   ├── sql/                                           # SQL脚本
│   │   ├── init.sql                                   # 初始化脚本
│   │   └── data.sql                                   # 测试数据
│   ├── logs/                                          # 日志目录
│   ├── pom.xml                                        # Maven配置
│   └── Dockerfile                                     # Docker构建文件
│
├── docs/                              # 文档目录
│   ├── api/                           # API文档
│   │   ├── auth.md
│   │   ├── travel.md
│   │   ├── voice.md
│   │   └── map.md
│   ├── deployment/                    # 部署文档
│   │   ├── docker.md
│   │   ├── kubernetes.md
│   │   └── ci-cd.md
│   └── development/                   # 开发文档
│       ├── setup.md
│       ├── coding-standards.md
│       └── testing.md
│
├── scripts/                           # 脚本目录
│   ├── build.sh                       # 构建脚本
│   ├── deploy.sh                      # 部署脚本
│   ├── test.sh                        # 测试脚本
│   └── backup.sh                      # 备份脚本
│
├── nginx/                             # Nginx配置
│   ├── nginx.conf                     # Nginx主配置
│   └── ssl/                           # SSL证书目录
│
├── k8s/                               # Kubernetes配置
│   ├── namespace.yml
│   ├── configmap.yml
│   ├── secret.yml
│   ├── deployment.yml
│   ├── service.yml
│   └── ingress.yml
│
└── .github/                           # GitHub Actions
    └── workflows/
        ├── ci.yml                     # 持续集成
        ├── cd.yml                     # 持续部署
        └── docker-build.yml           # Docker构建
```

## 目录说明

### 前端 (frontend/)
- **public/**: 静态资源文件，包括HTML模板、图标等
- **src/components/**: 可复用的Vue组件，按功能模块分类
- **src/views/**: 页面级组件，对应路由
- **src/services/**: API调用服务，封装HTTP请求
- **src/store/**: 状态管理，使用Pinia
- **src/utils/**: 工具函数和辅助方法
- **src/types/**: TypeScript类型定义
- **src/composables/**: Vue 3组合式API函数

### 后端 (backend/)
- **controller/**: REST API控制器，处理HTTP请求
- **service/**: 业务逻辑层，实现核心功能
- **repository/**: 数据访问层，与数据库交互
- **entity/**: JPA实体类，映射数据库表
- **dto/**: 数据传输对象，用于API请求和响应
- **config/**: 配置类，包括安全、数据库等配置
- **integration/**: 第三方服务集成，如地图、语音、AI服务

### 配置文件
- **.env.example**: 环境变量模板，包含所有需要的API密钥
- **docker-compose.yml**: Docker容器编排配置
- **.gitignore**: Git忽略规则，确保敏感信息不被提交

### 文档 (docs/)
- **api/**: API接口文档
- **deployment/**: 部署相关文档
- **development/**: 开发指南和规范

### 部署相关
- **nginx/**: 反向代理配置
- **k8s/**: Kubernetes部署配置
- **scripts/**: 自动化脚本
- **.github/workflows/**: CI/CD流水线配置

## 开发流程

1. **环境准备**: 复制`.env.example`为`.env`，填入相应的API密钥
2. **本地开发**: 使用`docker-compose up -d`启动开发环境
3. **代码提交**: 遵循Git Flow规范，提交前确保通过测试
4. **自动部署**: 通过GitHub Actions自动构建和部署

## 注意事项

1. **API密钥安全**: 绝不将真实的API密钥提交到代码仓库
2. **环境隔离**: 开发、测试、生产环境使用不同的配置
3. **代码规范**: 遵循ESLint和Prettier配置的代码风格
4. **测试覆盖**: 确保核心功能有充分的单元测试和集成测试