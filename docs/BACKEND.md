# Finx 後端文件

## 技術

- Java 17
- Spring Boot 2.7
- Spring Security
- Spring Data JPA
- MySQL 8.0
- JWT Access Token
- Refresh Token httpOnly Cookie

## 啟動

使用 Docker Compose 啟動完整環境：

```bash
docker compose up -d --build
```

後端位址：`http://localhost:8080`

API 文件：`http://localhost:8080/swagger-ui.html`

## API 分層

| 領域 | Controller | 說明 |
|---|---|---|
| 認證 | `AuthController` | 登入、刷新、登出、首次登入 |
| Dashboard | `DashboardApiController` | 月份、分類、信用卡支出統計 |
| 支出 | `ExpenseApiController` | 支出 CRUD 與篩選 |
| 信用卡 | `CreditCardApiController` | 既有信用卡 CRUD |
| 使用者 | `UserApiController` | 個人資料與密碼 |
| Admin | `AdminApiController` | 使用者與角色管理 |

## 資料庫

正式使用前請設定 `.env` 的 `DB_PASSWORD` 與 `JWT_SECRET`。SQL 原始檔位於：

- `src/main/resources/sql/schema.sql`
- `src/main/resources/sql/data.sql`
- `src/main/resources/sql/production-data.sql`

`data.sql` 包含展示用的 `admin` 與 `james` 帳號。正式部署請啟用 `production` profile，使用空的 `production-data.sql`，避免載入展示帳號與支出資料。

## 安全注意事項

- 正式環境必須使用長度足夠且不可預測的 `JWT_SECRET`。
- Refresh Token Cookie 應在 HTTPS 環境啟用 Secure 設定。
- 不要把 `.env`、實際密碼或資料庫備份上傳到版本庫。
- 正式環境應加入 Flyway/Liquibase migration、稽核紀錄、備份與監控。
