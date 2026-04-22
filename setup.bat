@echo off
echo ============================================
echo  FinX Dashboard - 初始化設定
echo ============================================
echo.

REM ── Step 1: 確認 Java ────────────────────────────────────────────────────
echo [1/3] 確認 Java 環境...
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [錯誤] 找不到 Java！請先安裝 JDK 17：
    echo        https://adoptium.net
    pause
    exit /b 1
)
echo       Java OK

REM ── Step 2: 下載 Gradle Wrapper JAR ─────────────────────────────────────
echo [2/3] 下載 Gradle Wrapper...
if exist "gradle\wrapper\gradle-wrapper.jar" (
    echo       gradle-wrapper.jar 已存在，跳過下載
) else (
    REM 使用 PowerShell 下載
    powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.6.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'" 2>nul
    if not exist "gradle\wrapper\gradle-wrapper.jar" (
        REM 備用下載來源
        powershell -Command "Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/master/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'" 2>nul
    )
    if exist "gradle\wrapper\gradle-wrapper.jar" (
        echo       下載成功！
    ) else (
        echo [錯誤] 下載失敗，請手動執行以下步驟：
        echo.
        echo   1. 用瀏覽器開啟：
        echo      https://services.gradle.org/distributions/gradle-8.6-bin.zip
        echo   2. 解壓縮後找到 gradle-8.6\lib\plugins\gradle-wrapper-*.jar
        echo   3. 複製到本專案的 gradle\wrapper\ 資料夾，改名為 gradle-wrapper.jar
        echo.
        pause
        exit /b 1
    )
)

REM ── Step 3: 啟動 ─────────────────────────────────────────────────────────
echo [3/3] 啟動 FinX Dashboard...
echo.
echo       請確認 MySQL 已啟動且已執行 sql\schema.sql
echo.
gradlew.bat bootRun

pause
