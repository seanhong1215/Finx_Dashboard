# Finx Demo 部署方案

這份方案把 demo 分成兩個入口，避免免費雲端後端休眠影響展示。

## 方案 A：完整前後端本機展示

適合課堂、面試、簡報現場操作。這是最能證明專案架構的方式，會跑完整的 React、Spring Boot、MySQL。

```bash
docker compose up -d --build
docker compose ps
```

展示網址：

```text
Frontend: http://localhost:5173
Backend API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
```

展示帳號：

```text
admin / password123
james / password123
```

建議展示順序：

1. 用 `james / password123` 登入，展示 Dashboard 圖表、月份切換、分類聯動支出篩選。
2. 展示新增、編輯、刪除支出。
3. 展示信用卡管理。
4. 登出後用 `admin / password123` 登入，展示 Admin 使用者管理。
5. 開 Swagger 展示後端 API 文件。

## 方案 B：GitHub Pages 免費靜態 Demo Mode

適合先給對方一個不會冷啟動的網址。這個模式只部署 React static site，不連後端、不連資料庫，資料存在瀏覽器 `localStorage`。

它可以展示登入、Dashboard、支出 CRUD、信用卡 CRUD、Admin 使用者管理，但不是正式多人後端。

本機驗證：

```bash
cd frontend
npm ci
npm run build -- --mode demo
npm run preview
```

GitHub Pages 部署：

1. Push 到 GitHub 的 `main` 或 `master` branch。
2. 到 GitHub repo 的 `Settings > Pages`。
3. `Build and deployment` 的 `Source` 選 `GitHub Actions`。
4. 等待 `Deploy Demo to GitHub Pages` workflow 完成。

部署網址：

```text
https://seanhong1215.github.io/Finx_Dashboard/
```

展示帳號同樣是：

```text
admin / password123
james / password123
```

Demo Mode 的資料會存在同一台瀏覽器。如果需要重置資料，清除瀏覽器 localStorage，或在 DevTools Console 執行：

```js
localStorage.removeItem('finx:demo-state:v1');
localStorage.removeItem('finx:demo-session:v1');
location.reload();
```

## 備選：Render Static Site

如果還是要放 Render Static Site，也可以用同一個 Demo Mode：

```text
Service Type: Static Site
Root Directory: frontend
Build Command: npm ci && npm run build -- --mode demo
Publish Directory: dist
```

Render Environment Variables：

```text
VITE_DEMO_MODE=true
```

## 不建議的免費部署

不要把 Spring Boot 後端放 Render Free 當主要 demo 入口。Render Free Web Service 會在閒置後休眠，下一次請求會冷啟動，展示時容易卡在載入。

不要刪資料庫。這個後端是 Spring Boot + MySQL 架構，沒有資料庫會啟動失敗。

## 建議對外說法

```text
正式架構是 React + Spring Boot REST API + MySQL，完整環境可用 Docker Compose 一鍵啟動。
公開 demo 網址使用前端 Demo Mode，避免免費雲端後端休眠；它保留主要操作流程，完整 API 與資料庫可在本機展示。
```
