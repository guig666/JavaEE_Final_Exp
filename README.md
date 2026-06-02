# 共享充电宝系统

## 1. 项目简介

**项目名称**：共享充电宝系统

**项目描述**：一个基于前后端分离架构的共享充电宝管理平台，提供用户租借/归还充电宝功能，以及管理员后台管理功能。

### 功能特性

- **用户端功能**：
  - 用户注册/登录
  - 地图查看投放点
  - 租借充电宝
  - 归还充电宝
  - 订单查询
  - 个人中心（充值、修改信息）

- **管理端功能**：
  - 管理员登录
  - 数据统计（Dashboard）
  - 投放点管理（增删改查）
  - 充电宝管理（投放、转移、删除）
  - 用户管理（充值、删除）
  - 订单管理（查看、详情）

---

## 2. 技术栈

### 后端技术栈

- Spring MVC 4.x
- Spring 4.x
- MyBatis 3.x
- MySQL 8.0
- Tomcat 8.5/9.0
- Maven 3.x

### 前端技术栈

- Vue 3 + Vite
- Vant 4（移动端用户端）
- Element Plus（PC端管理端）
- Pinia（状态管理）
- Axios（HTTP请求）
- ECharts（数据可视化）
- 高德地图 JS API 2.0

---

## 3. 环境要求

- JDK 8+
- MySQL 8.0+
- Node.js 16+
- Maven 3.x
- IDE（IntelliJ IDEA / Eclipse）
- Tomcat 8.5+

---

## 4. 项目配置（启动前必读）

### 4.1 数据库配置

#### 4.1.1 创建数据库

```sql
CREATE DATABASE powerbank CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 4.1.2 导入数据表

找到项目中的 `files/powerbank.sql` 文件，执行：

```bash
mysql -u root -p powerbank < files/powerbank.sql
```

或者在 MySQL 客户端中直接执行该 SQL 文件。

#### 4.1.3 修改数据库连接配置

找到后端项目中的 `src/main/resources/jdbc.properties`，修改为你的实际配置：

```properties
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/powerbank?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
jdbc.username=你的数据库用户名
jdbc.password=你的数据库密码
jdbc.maxTotal=45
jdbc.maxIdle=10
jdbc.initialSize=5
```

⚠️ **注意**：MySQL 8.0 必须使用驱动类 `com.mysql.cj.jdbc.Driver`，且 URL 必须加 `serverTimezone=Asia/Shanghai`

---

### 4.2 高德地图配置（重要）

1. 前往 [高德开放平台](https://lbs.amap.com/) 注册账号
2. 申请 Web端（JS API）的 Key 和安全密钥
3. 在用户端项目的 `index.html` 中替换：

```html
<script type="text/javascript">
  window._AMapSecurityConfig = {
    securityJsCode: '你的安全密钥'
  };
</script>
<script src="https://webapi.amap.com/maps?v=2.0&key=你的高德Key"></script>
```

---

### 4.3 前端代理配置

#### 用户端

打开 `frontend/vite.config.js`，修改代理目标为你的后端实际地址：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080/powerbank',
    changeOrigin: true
  }
}
```

#### 管理端

打开 `admin/vite.config.js`，修改代理目标为你的后端实际地址：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080/powerbank',
    changeOrigin: true
  }
}
```

---

## 5. 后端运行步骤

### 5.1 修改 Maven 配置（可选）

如果使用代理，修改 `settings.xml` 或 `pom.xml` 中的仓库地址。

### 5.2 编译打包

```bash
cd d:\Develop\JavaEE\ChargerSharingSystem-master
mvn clean package
```

### 5.3 部署到 Tomcat

**方式一**：将生成的 `target/powerbank.war` 文件放到 Tomcat 的 `webapps` 目录，启动 Tomcat。

**方式二**：在 IDE 中配置 Tomcat，直接运行。

### 5.4 确认后端启动成功

访问：`http://localhost:8080/powerbank/api/manage/administrators/account/admin/actions/login?password=123456`

返回 JSON 数据即成功。

---

## 6. 前端运行步骤

### 6.1 用户端（移动端）

```bash
cd frontend
npm install
npm run dev
```

访问：`http://localhost:5173`

### 6.2 管理端（PC端）

```bash
cd admin
npm install
npm run dev
```

访问：`http://localhost:3001`

---

## 7. 测试 账号

### 用户端

- 手机号：可自行注册
- 密码：可自行设置

### 管理端

- 账号：admin
- 密码：123456

（具体账号密码请查看数据库中的 admin 表）

---

## 8. 常见问题

| 问题 | 解决方法 |
|------|---------|
| 登录失败/404 | 检查后端是否启动，代理配置是否正确 |
| 地图不显示 | 检查高德 Key 是否配置，是否开启 Web 服务 |
| 数据库连接失败 | 检查 MySQL 服务是否启动，账号密码是否正确 |
| Session 失效 | 检查 axios 是否配置 `withCredentials: true` |
| 跨域报错 | 后端已添加 CORS 配置，或使用代理解决 |

---

## 9. 项目结构说明

### 后端

```
src/main/java/com/konsonx/
├── controller/          # 控制层
│   ├── client/api/      # 用户端API
│   └── manage/api/      # 管理端API
├── service/             # 业务层
├── dao/                 # 数据层
├── po/                  # 实体类
├── interceptor/         # 拦截器
└── utils/               # 工具类
```

### 用户端

```
frontend/src/
├── views/               # 页面组件
├── api/                 # API请求
├── stores/              # Pinia状态管理
├── utils/               # 工具函数
└── router/              # 路由配置
```

### 管理端

```
admin/src/
├── views/               # 页面组件
├── api/                 # API请求
├── stores/              # Pinia状态管理
├── utils/               # 工具函数
└── router/              # 路由配置
```

---

## 10. 项目截图

- 前端首页（参考 files/01.JPG）
- 管理后台（参考 files/02.JPG）
- 数据库表结构（参考 files/table.png）

---

## 11. 开发说明

- 后端使用 Session 进行认证
- 前端请求需携带 Cookie（axios 已配置 withCredentials）
- 数据库表结构已在 files/powerbank.sql 中
- 如需修改业务逻辑，请参考对应的 Service 层代码

---

## 12. 许可证

仅供学习交流使用。
