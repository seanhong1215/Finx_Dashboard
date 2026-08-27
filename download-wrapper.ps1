# FinX - 下載 gradle-wrapper.jar
Write-Host "正在下載 gradle-wrapper.jar..." -ForegroundColor Cyan

$jarPath = "gradle\wrapper\gradle-wrapper.jar"
$url1    = "https://raw.githubusercontent.com/gradle/gradle/v8.6.0/gradle/wrapper/gradle-wrapper.jar"
$url2    = "https://github.com/gradle/gradle/raw/v8.6.0/gradle/wrapper/gradle-wrapper.jar"

if (Test-Path $jarPath) {
    Write-Host "已存在，跳過" -ForegroundColor Green
    exit 0
}

try {
    Invoke-WebRequest -Uri $url1 -OutFile $jarPath -UseBasicParsing
    Write-Host "✅ 下載完成！" -ForegroundColor Green
} catch {
    try {
        Invoke-WebRequest -Uri $url2 -OutFile $jarPath -UseBasicParsing
        Write-Host "✅ 下載完成（備用來源）！" -ForegroundColor Green
    } catch {
        Write-Host "❌ 下載失敗，請手動下載：" -ForegroundColor Red
        Write-Host "   1. 開啟瀏覽器前往：https://gradle.org/releases/" -ForegroundColor Yellow
        Write-Host "   2. 下載 gradle-8.6-bin.zip 並解壓縮" -ForegroundColor Yellow
        Write-Host "   3. 找到 gradle-8.6\lib\gradle-wrapper-8.6.jar" -ForegroundColor Yellow
        Write-Host "   4. 複製到本專案 gradle\wrapper\ 並改名為 gradle-wrapper.jar" -ForegroundColor Yellow
    }
}
