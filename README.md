# T47 AI 订单异常识别助手（后端）

> 《JavaWeb 后端开发技术》期末考查 · 小组项目 · 与《Docker 云容器与管理技术》《MVVM 前端框架开发技术》共用

## 1. 项目简介

电商订单在创建后，由本地规则引擎（模拟 AI）自动识别可疑订单并标注异常；审核员人工核实，确认异常后进入处理（退款/拦截/放行），最后关闭；若判定为误报则驳回关闭。系统统计**异常订单数**与**误报率**，用于评估识别效果。

- 角色：`ADMIN` 管理员 / `AUDITOR` 审核员 / `CUSTOMER` 下单客户
- 主流程：`CREATED → FLAGGED → CONFIRMED → PROCESSED → CLOSED`
- 异常分支：`FLAGGED → DISMISSED`（误报驳回）、`CREATED → CANCELED`（用户取消）
- 统计指标：异常订单数、误报率、规则命中分布

## 2. 技术栈

| 层 | 技术 |
|---|---|
| 语言 | Java 21 |
| 框架 | Spring Boot 3.2.x |
| 构建 | Maven |
| 持久层 | MyBatis-Plus 3.5.7 + MySQL 8 |
| 认证 | JWT (jjwt 0.12.5) + BCrypt (spring-security-crypto) + 自定义拦截器 |
| 文档 | Knife4j (OpenAPI 3) |
| 校验/日志 | spring-boot-starter-validation + logback |

## 3. 目录结构

```
T47-order-anomaly/
├── backend/                后端源码（pom.xml、Dockerfile）
├── frontend/               前端（Vue3 + Vite + Element Plus，最小版 3 页）
├── database/               schema.sql、seed.sql
├── docs/                   需求、数据库设计、接口文档、测试集合、业务流程记录等
├── build.cmd               临时锁定 JDK21 的构建脚本（不改系统环境）
└── README.md
```

## 4. 环境变量（不得硬编码）

| 变量 | 说明 | 默认值（仅本地开发） |
|---|---|---|
| `DB_URL` | 数据库连接串 | `jdbc:mysql://localhost:3306/t47_anomaly?...` |
| `DB_USERNAME` | 数据库账号 | `root` |
| `DB_PASSWORD` | 数据库密码 | `123456` |
| `JWT_SECRET` | JWT 签名密钥（≥32 字节） | 开发用占位串，**线上必须替换** |
| `JWT_EXPIRE_MINUTES` | Token 有效期（分钟） | `120` |
| `SERVER_PORT` | 服务端口 | `8080` |

> 提交前检查：`.env`、`application-local.yml` 一律不进 Git。

## 5. 本地启动

### 5.1 初始化数据库

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p t47_anomaly < database/seed.sql
```

### 5.2 构建与运行

方式 A（推荐，不影响系统 JAVA_HOME）：

```cmd
build.cmd clean package
build.cmd spring-boot:run
```

方式 B（IDEA）：Project SDK 选 JDK 21，直接运行 `T47Application`。

### 5.3 验证

- 健康接口：<http://localhost:8080/api/health>
- 接口文档：<http://localhost:8080/doc.html>

## 6. 演示账号（全部为模拟数据）

| 账号 | 密码 | 角色 |
|---|---|---|
| `admin` | `123456` | 管理员 |
| `auditor01` | `123456` | 审核员 |
| `customer01` | `123456` | 下单客户 |

## 7. 开发进度

- [x] 工程骨架、数据库脚本、公共响应/异常层、健康接口
- [x] 认证权限（注册/登录/JWT/拦截器/BCrypt/角色校验/用户管理）
- [ ] 订单 CRUD、多条件分页、越权校验
- [ ] AI 规则引擎、状态机、事务与防重
- [ ] 核实 / 处理 / 关闭 / 统计
- [ ] 前端 3 页
- [ ] 文档、Apifox 集合、PPT、视频、tag 冻结
