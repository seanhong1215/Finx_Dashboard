# Finx 部署文件

## 部署前檢查

- Docker daemon 已啟動。
- 已建立正式 `.env`，不可使用預設密碼。
- 已設定 `DB_PASSWORD`、`JWT_SECRET`、`CORS_ALLOWED_ORIGINS`。
- 已確認正式環境啟用 `production` profile，不載入展示用 `data.sql`。
- 已確認反向代理提供 HTTPS。

## 建置驗證

```bash
docker compose build app
cd frontend
npm ci
npm run build
npm run test:e2e
```

## 本機整合啟動

```bash
cd /home/shawn/sample_project/docker_dev/金融科技/Finx
docker compose up -d --build
docker compose ps
```

網站：`http://localhost:5173`

API：`http://localhost:8080`

正式環境請加入：

```text
SPRING_PROFILES_ACTIVE=production
```

這個 profile 只執行 schema，不會建立 `admin`、`james` 或展示支出資料；第一個管理者應透過正式的管理流程建立，或由受控的資料庫初始化程序建立。

## 建立乾淨資料庫

Docker volume 不會隨專案檔案上傳。若要在本機重建乾淨資料庫，請確認沒有需要保留的資料後執行：

```bash
docker compose down -v
docker compose up -d --build
```

這會刪除 Finx 的 MySQL volume，僅適用於展示環境或已完成備份的環境。

## 上傳內容

應上傳：

- `src/`
- `frontend/src/`
- `frontend/public/`
- `frontend/package.json`
- `frontend/package-lock.json`
- `gradle/`、`gradlew`
- `Dockerfile`、`docker-compose.yml`
- `sql/` 與 `src/main/resources/sql/`
- `README.md` 與 `docs/`

不應上傳：

- `.env`
- `frontend/node_modules/`
- `frontend/dist/`
- `frontend/test-results/`
- `docs/e2e-report/`
- `build/`、`.gradle/`
- Docker MySQL volume
