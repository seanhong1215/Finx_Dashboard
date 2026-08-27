# GitHub 專案設定與部署

## 建立 Repository

1. 在 GitHub 建立新的 Private 或 Public repository。
2. Repository 不要自動建立 README、`.gitignore` 或 License，避免和本專案檔案衝突。
3. 在本機專案根目錄設定遠端：

```bash
git remote add origin https://github.com/<帳號>/<repository>.git
git branch -M main
git add .
git commit -m "prepare Finx deployment"
git push -u origin main
```

不要執行 `git add .env`，正式密鑰不可上傳。

## GitHub Actions

`.github/workflows/ci.yml` 會在 `main`、`master` 的 push 與 Pull Request 執行：

- Java 17 Spring Boot build
- React build
- Docker Compose 啟動
- Playwright E2E 測試
- 每個 E2E 案例錄影
- 失敗時上傳 screenshot、trace 與測試報告 artifact

在 GitHub 的 `Actions` 頁面即可查看執行結果。測試產物可在該次 workflow 的 `Artifacts` 下載。

## Repository 設定建議

在 `Settings > Branches > Branch protection rules`：

- 保護 `main`
- 要求 Pull Request 才能合併
- 要求 `Backend build`、`Frontend build`、`E2E tests` 通過
- 禁止 force push
- 禁止直接刪除 main

## Secrets

若使用 GitHub Actions 部署到主機，在 `Settings > Secrets and variables > Actions` 設定：

- `DEPLOY_HOST`
- `DEPLOY_USER`
- `DEPLOY_SSH_KEY`
- `DEPLOY_PATH`
- `DB_PASSWORD`
- `JWT_SECRET`

不要把這些值寫入 workflow 或 `.env.example`。

## 目前可用的部署方式

本專案目前以 Docker Compose 作為部署單位：

```bash
docker compose up -d --build
```

適合部署到有 Docker 的 VPS 或雲端主機。正式環境請使用 `SPRING_PROFILES_ACTIVE=production`，並設定正式資料庫密碼、JWT secret、CORS 網域與 HTTPS 反向代理。

目前 `docker-compose.yml` 的 frontend 是 Vite development server，適合展示與測試。正式上線前應改成 React production build，再由 Nginx 或雲端靜態服務提供檔案。

## 乾淨部署流程

```bash
git clone https://github.com/<帳號>/<repository>.git
cd <repository>
cp .env.example .env
# 編輯 .env，填入正式設定
SPRING_PROFILES_ACTIVE=production docker compose up -d --build
```

部署後檢查：

```bash
docker compose ps
docker compose logs --tail=100 app
```

## 注意

- Docker MySQL volume 不會被 Git 上傳。
- `data.sql` 是展示資料；production profile 使用空的 production data script。
- GitHub Actions 的 E2E 會使用測試資料庫，workflow 結束時會刪除 volume。
