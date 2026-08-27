# Finx

Finx 是一個個人支出記帳工具，核心目標是讓使用者登入後清楚管理自己的支出紀錄與既有信用卡。

## 快速開啟

前提：Docker daemon 必須已啟動。若看到 `dial unix /var/run/docker.sock: connect: no such file or directory`，代表 Docker 尚未啟動。

```bash
cd /home/shawn/sample_project/docker_dev/金融科技/Finx
docker compose up --build
```

開啟網站：

```text
http://localhost:5173
```

後端 API：

```text
http://localhost:8080
```

Swagger：

```text
http://localhost:8080/swagger-ui.html
```

初始帳號：

```text
admin / password123
james / password123
```

## Demo 部署

展示時建議用本機 Docker Compose 跑完整前後端，並準備免費靜態 Demo Mode 當公開備援網址。細節請看 [`docs/DEMO_DEPLOYMENT.md`](docs/DEMO_DEPLOYMENT.md)。

## 系統需求

Finx 解決的是個人日常支出管理問題：

- 使用者只想知道本月花了多少錢。
- 使用者需要依分類、日期、信用卡整理支出。
- 使用者希望知道每張信用卡的本月消費金額。
- 管理者需要建立使用者、停用帳號、調整角色，讓不同使用者操作自己的資料。

## 系統架構

```text
Browser / React :5173
  |
  | JSON API + Access JWT
  v
Spring Boot REST API :8080
  |
  | JPA
  v
MySQL 8.0 :3306
```

技術組成：

| 層級 | 技術 |
|---|---|
| Frontend | React、TypeScript、Vite |
| Backend | Spring Boot 2.7、Spring Security、Spring Data JPA |
| Auth | Access JWT + Refresh Token httpOnly Cookie |
| Database | MySQL 8.0 |
| API Docs | SpringDoc OpenAPI |
| Container | Docker Compose |

Docker Compose 服務：

| 服務 | Port | 說明 |
|---|---:|---|
| `finx-frontend` | 5173 | React dev server |
| `finx-app` | 8080 | Spring Boot REST API |
| `finx-mysql` | 3306 | MySQL |

## 使用者流程

### Admin 初始流程

1. 使用 `admin / password123` 登入。
2. 先進入 Dashboard 查看支出圖表。
3. 從側邊選單進入 Admin，查看全站摘要。
4. 建立一般使用者，輸入帳號、姓名、email、臨時密碼與角色。
5. 新使用者第一次登入後必須完成首次登入設定。
6. Admin 可停用/啟用帳號，也可切換 `USER` / `ADMIN` 角色。

### 新使用者第一次登入

1. 使用 Admin 建立的帳號與臨時密碼登入。
2. 系統導向首次登入設定。
3. 使用者必須輸入目前密碼、新密碼、姓名與 email。
4. 完成後才能進入支出記帳功能。
5. 新建立使用者一開始沒有支出與信用卡資料。

### 一般使用者流程

1. 登入後進入本月支出總覽。
2. 查看選定月份支出、支出趨勢、分類統計與信用卡比較。
3. 在支出紀錄新增、編輯、刪除支出。
4. 在信用卡管理新增、編輯、刪除既有信用卡。
5. 在設定頁修改個人資料或密碼。

## 網站功能

### 認證

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/auth/me`
- `POST /api/auth/complete-first-login`

登入成功後，後端回傳 Access Token 給 React，並設定 `finx_refresh` httpOnly Cookie。React 使用 Access Token 呼叫 API；頁面重新整理時透過 Refresh Token 取得新的 Access Token。

### 支出記帳

- 只有支出，不支援收入或轉帳。
- 支出欄位包含分類、商店、金額、日期、備註、信用卡。
- 可依分類與信用卡篩選。

API：

- `GET /api/expenses`
- `POST /api/expenses`
- `PUT /api/expenses/{id}`
- `DELETE /api/expenses/{id}`

### 信用卡管理

信用卡只管理使用者已持有的卡，不做開卡、申請、審核或銀行串接。

欄位包含：

- 銀行
- 卡名
- 組織：VISA、MASTERCARD、JCB、AMEX、OTHER
- 卡號後四碼
- 額度
- 帳單日
- 繳款日

API：

- `GET /api/credit-cards`
- `POST /api/credit-cards`
- `PUT /api/credit-cards/{id}`
- `DELETE /api/credit-cards/{id}`

### Admin

Admin 管理範圍：

- 查看全站摘要。
- 查看使用者列表。
- 建立使用者。
- 啟用/停用帳號。
- 調整角色。

API：

- `GET /api/admin/summary`
- `GET /api/admin/users`
- `POST /api/admin/users`
- `PATCH /api/admin/users/{id}/role`
- `PATCH /api/admin/users/{id}/status`

## 資料庫

Spring Boot 啟動時會執行：

| 檔案 | 用途 |
|---|---|
| `src/main/resources/sql/schema.sql` | 建立資料表並移除舊 demo 表 |
| `src/main/resources/sql/data.sql` | 建立 admin、sample user、範例信用卡與範例支出 |

目前保留資料表：

| 資料表 | 說明 |
|---|---|
| `users` | 使用者、角色、啟用狀態、首次登入狀態 |
| `credit_cards` | 使用者既有信用卡 |
| `expenses` | 使用者支出紀錄 |
| `refresh_tokens` | Refresh Token |

啟動時會移除舊 demo 表：

- `accounts`
- `cards`
- `transactions`
- `stock_holdings`
- `crypto_assets`

## 本機開發

後端：

```bash
cd /home/shawn/sample_project/docker_dev/金融科技/Finx
DB_PASSWORD=finx_password ./gradlew bootRun
```

前端：

```bash
cd /home/shawn/sample_project/docker_dev/金融科技/Finx/frontend
npm ci
npm run dev
```

如果後端不是跑在 `http://localhost:8080`：

```bash
VITE_API_BASE_URL=http://localhost:8081 npm run dev
```

## 延伸文件

- [後端文件](docs/BACKEND.md)：Spring Boot、API、資料庫與安全注意事項。
- [前端文件](docs/FRONTEND.md)：React 畫面、建置與 E2E 測試。
- [部署文件](docs/DEPLOYMENT.md)：部署前檢查、乾淨資料庫與上傳清單。
- [E2E 測試文件](docs/E2E.md)：自動化測試範圍、執行方式與逐案錄影規則。
- [GitHub 與部署文件](docs/GITHUB.md)：Repository、Actions、Secrets、分支保護與部署方式。
- [獨立封面素材](docs/assets/dashboard-cover-personal-expense.png)：不會被網站引用的備用圖片。

## 常見問題

### Docker API 連不上

錯誤：

```text
unable to get image 'mysql:8.0': failed to connect to the docker API
```

代表 Docker daemon 尚未啟動。請先啟動 Docker Desktop 或 Linux Docker service。

### 新使用者登入後不能進 Dashboard

這是預期行為。Admin 新增的使用者會被標記為 `must_change_password=true`，第一次登入必須先完成密碼與基本資料設定。

### 是否需要 H2

不需要。Finx 使用 MySQL SQL 初始化，不使用 H2。
