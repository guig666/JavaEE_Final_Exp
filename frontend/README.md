# 共享充电宝前端项目

## 技术栈

- Vue 3
- Vite
- Vant 4
- Pinia
- Axios

## 项目配置

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 配置高德地图

打开 `index.html`，替换以下内容：

```javascript
window._AMapSecurityConfig = {
  securityJsCode: 'YOUR_SECURITY_CODE' // 替换为你的安全密钥
}
```

```html
<script src="https://webapi.amap.com/maps?v=2.0&key=YOUR_AMAP_KEY"></script>
```

将 `YOUR_AMAP_KEY` 替换为你的高德地图 API Key

### 3. 配置后端代理

打开 `vite.config.js`，如果需要修改后端地址：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080/ChargerSharingSystem', // 修改为你的后端地址
    changeOrigin: true
  }
}
```

## 运行项目

```bash
npm run dev
```

## 功能页面

- 登录/注册页面
- 地图找桩页面
- 租借页面
- 归还页面
- 订单列表页面
- 个人中心页面

## 注意事项

1. 确保后端服务已启动
2. 配置正确的高德地图 Key
3. axios 已配置 `withCredentials: true`，支持 Session 认证
