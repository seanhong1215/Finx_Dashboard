# Finx 前端文件

## 技術

- React 18
- TypeScript
- Vite
- Recharts
- Playwright

## 本機啟動

```bash
cd frontend
npm ci
npm run dev
```

前端位址：`http://localhost:5173`

API 位址可透過 `VITE_API_BASE_URL` 設定，預設為 `http://localhost:8080`。

## 主要畫面

- Dashboard：六個月趨勢、月份切換、分類占比、分類排行、信用卡比較。
- 支出紀錄：新增、編輯、刪除、分類與信用卡篩選。
- 信用卡：管理使用者已持有的信用卡。
- 帳號設定：個人資料與登入密碼。
- Admin：使用者建立、啟用停用與角色管理。

## 前端驗證

```bash
npm run build
npm run test:e2e
```

E2E 測試會錄製每一個案例，測試影片與 HTML 報告屬於可重建產物，不應放入正式部署映像。

正式部署目前仍應使用前端 production build 搭配 Nginx 或雲端靜態網站服務；`docker-compose.yml` 的 Vite server 是本機展示與 E2E 用設定。
