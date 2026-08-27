# Finx E2E 自動測試

Finx 使用 Playwright 驗證真實瀏覽器中的使用者操作。每一個測試案例都會錄影，成功與失敗的執行結果都會保留，影片會附在 HTML 測試報告中。

## 測試範圍

- 使用者登入與 Dashboard 顯示
- Dashboard 月份切換
- 圖表點擊聯動支出篩選
- 新增與刪除支出
- 信用卡與帳號設定頁
- 一般使用者無法看到 Admin 導覽

## 執行

先啟動 Finx：

```bash
docker compose up -d
```

再執行 E2E：

```bash
cd frontend
npm run test:e2e
```

## 報告與錄影

HTML 報告輸出於：

```text
docs/e2e-report/index.html
```

每個測試的錄影會附在對應報告項目中；失敗時也會另外保留 screenshot 與 trace，方便追查問題。
