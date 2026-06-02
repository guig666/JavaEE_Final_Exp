# 共享充电宝管理系统 - 后台

## 技术栈

- Vue 3
- Vite
- Element Plus
- Pinia
- ECharts

## 安装依赖

```bash
cd admin
npm install
```

## 配置

打开 `vite.config.js`，如需要可修改后端地址：

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

项目将在 `http://localhost:3001` 启动。

## 功能模块

- 登录/退出
- 数据统计（Dashboard）
  - 用户总数、投放点数、充电宝数、订单总数
  - 订单趋势图
  - 借用状态分布图
  - 各投放点充电宝数量图
- 投放点管理
  - 查看投放点列表
  - 新增投放点
  - 编辑投放点
- 充电宝管理
  - 查看充电宝列表
  - 投放充电宝
  - 转移充电宝
  - 删除充电宝
- 用户管理
  - 查看用户列表
  - 给用户充值
  - 删除用户
- 订单管理
  - 查看订单列表
  - 查看订单详情
