# Finx Dashboard — 架構說明

## 概覽

金融管理後台系統，採用傳統 **MVC 伺服器端渲染架構**，Java Spring Boot 處理所有請求並透過 Thymeleaf 直接輸出 HTML 頁面。

## 技術棧

| 類別 | 技術 | 版本 |
|------|------|------|
| 語言 | Java | 17 |
| 框架 | Spring Boot | 2.7.18 |
| 模板引擎 | Thymeleaf | — |
| 安全性 | Spring Security | — |
| ORM | Spring Data JPA | — |
| 資料庫 | MySQL | 8.0+ |
| 建置工具 | Gradle | 8.6 |
| 程式碼生成 | Lombok | — |

## 系統架構

```
瀏覽器
  │  HTTP Request
  ▼
Controller（@Controller / @RestController）
  │  呼叫
  ▼
Service（@Service）── 商業邏輯、交易管理（@Transactional）
  │  呼叫
  ▼
Repository（@Repository）── Spring Data JPA 介面
  │  SQL
  ▼
MySQL 8.0
```

### 分層職責

| 層級 | 路徑 | 職責 |
|------|------|------|
| **Controller** | `com/finx/controller/` | 接收 HTTP 請求、參數驗證、呼叫 Service、回傳 View 或 JSON |
| **Service** | `com/finx/service/` | 封裝商業邏輯、管理交易邊界 |
| **Repository** | `com/finx/repository/` | 資料庫 CRUD，繼承 `JpaRepository` |
| **Model** | `com/finx/model/` | JPA 實體，對應資料庫資料表 |
| **Config** | `com/finx/config/` | Spring Security 設定、資料庫連線等 |

## 目錄結構

```
src/main/
├── java/com/finx/
│   ├── config/         ← Spring 設定（Security、Bean 等）
│   ├── controller/     ← HTTP 請求入口
│   ├── model/          ← JPA 實體（@Entity）
│   ├── repository/     ← 資料存取介面（JpaRepository）
│   └── service/        ← 商業邏輯
└── resources/
    ├── templates/      ← Thymeleaf HTML 模板
    ├── static/         ← CSS、JS、圖片
    └── application.properties  ← 資料庫、Port 等設定
sql/
└── schema.sql          ← 資料庫初始化腳本
```

## 認證與授權

由 **Spring Security** 統一管理：

- 表單登入（Form Login）
- Session 管理
- URL 層級存取控制（`antMatchers`）
- 密碼使用 BCrypt 雜湊儲存

## 資料流程

1. 瀏覽器送出 HTTP 請求
2. Spring Security 攔截，驗證 Session / 授權
3. `DispatcherServlet` 路由至對應 `@Controller`
4. Controller 呼叫 `@Service` 取得業務結果
5. Service 透過 `@Repository` 操作 MySQL
6. Thymeleaf 將資料注入 HTML 模板，回傳完整頁面

## 建置與執行

```bash
# 開發啟動
./gradlew bootRun

# 生產打包
./gradlew build
java -jar build/libs/*.jar
```

預設 Port：`http://localhost:8080`
