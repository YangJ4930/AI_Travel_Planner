# AI Travel Planner - Docker Compose 启动脚本
# 支持命令行传入 API 密钥，避免在 .env 文件中硬编码敏感信息

param(
    [string]$AmapKey = "",
    [string]$XunfeiAppId = "",
    [string]$XunfeiApiSecret = "",
    [string]$XunfeiApiKey = "",
    [string]$AlibabaAccessKeyId = "",
    [string]$AlibabaAccessKeySecret = "",
    [string]$Action = "up",
    [switch]$Build = $false,
    [switch]$Detach = $true,
    [switch]$Help = $false
)

# 显示帮助信息
if ($Help) {
    Write-Host "AI Travel Planner Docker Compose 启动脚本" -ForegroundColor Green
    Write-Host ""
    Write-Host "用法:" -ForegroundColor Yellow
    Write-Host "  .\start-with-keys.ps1 [参数]" -ForegroundColor White
    Write-Host ""
    Write-Host "参数:" -ForegroundColor Yellow
    Write-Host "  -AmapKey <string>                高德地图 API Key" -ForegroundColor White
    Write-Host "  -XunfeiAppId <string>            科大讯飞 App ID" -ForegroundColor White
    Write-Host "  -XunfeiApiSecret <string>        科大讯飞 API Secret" -ForegroundColor White
    Write-Host "  -XunfeiApiKey <string>           科大讯飞 API Key" -ForegroundColor White
    Write-Host "  -AlibabaAccessKeyId <string>     阿里云 Access Key ID" -ForegroundColor White
    Write-Host "  -AlibabaAccessKeySecret <string> 阿里云 Access Key Secret" -ForegroundColor White
    Write-Host "  -Action <string>                 Docker Compose 操作 (默认: up)" -ForegroundColor White
    Write-Host "  -Build                           强制重新构建镜像" -ForegroundColor White
    Write-Host "  -Detach                          后台运行 (默认: true)" -ForegroundColor White
    Write-Host "  -Help                            显示此帮助信息" -ForegroundColor White
    Write-Host ""
    Write-Host "示例:" -ForegroundColor Yellow
    Write-Host "  # 使用临时密钥启动服务" -ForegroundColor Gray
    Write-Host "  .\start-with-keys.ps1 -AmapKey 'your_amap_key' -XunfeiAppId 'your_app_id'" -ForegroundColor White
    Write-Host ""
    Write-Host "  # 重新构建并启动" -ForegroundColor Gray
    Write-Host "  .\start-with-keys.ps1 -Build" -ForegroundColor White
    Write-Host ""
    Write-Host "  # 停止服务" -ForegroundColor Gray
    Write-Host "  .\start-with-keys.ps1 -Action down" -ForegroundColor White
    exit 0
}

Write-Host "🚀 AI Travel Planner Docker Compose 启动脚本" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green

# 检查 Docker 是否运行
try {
    docker version | Out-Null
    Write-Host "✅ Docker 守护进程运行正常" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker 守护进程未运行，请启动 Docker Desktop" -ForegroundColor Red
    exit 1
}

# 设置环境变量
if ($AmapKey) {
    $env:AMAP_API_KEY = $AmapKey
    Write-Host "🔑 设置高德地图 API Key" -ForegroundColor Yellow
}

if ($XunfeiAppId) {
    $env:XUNFEI_APP_ID = $XunfeiAppId
    Write-Host "🔑 设置科大讯飞 App ID" -ForegroundColor Yellow
}

if ($XunfeiApiSecret) {
    $env:XUNFEI_API_SECRET = $XunfeiApiSecret
    Write-Host "🔑 设置科大讯飞 API Secret" -ForegroundColor Yellow
}

if ($XunfeiApiKey) {
    $env:XUNFEI_API_KEY = $XunfeiApiKey
    Write-Host "🔑 设置科大讯飞 API Key" -ForegroundColor Yellow
}

if ($AlibabaAccessKeyId) {
    $env:ALIBABA_CLOUD_ACCESS_KEY_ID = $AlibabaAccessKeyId
    Write-Host "🔑 设置阿里云 Access Key ID" -ForegroundColor Yellow
}

if ($AlibabaAccessKeySecret) {
    $env:ALIBABA_CLOUD_ACCESS_KEY_SECRET = $AlibabaAccessKeySecret
    Write-Host "🔑 设置阿里云 Access Key Secret" -ForegroundColor Yellow
}

# 构建 Docker Compose 命令
$composeCmd = "docker compose"

if ($Action -eq "up") {
    if ($Build) {
        $composeCmd += " up --build"
        Write-Host "🔨 强制重新构建镜像" -ForegroundColor Blue
    } else {
        $composeCmd += " up"
    }
    
    if ($Detach) {
        $composeCmd += " -d"
        Write-Host "🔄 后台运行模式" -ForegroundColor Blue
    }
} else {
    $composeCmd += " $Action"
}

Write-Host ""
Write-Host "执行命令: $composeCmd" -ForegroundColor Cyan
Write-Host ""

# 执行命令
try {
    Invoke-Expression $composeCmd
    
    if ($Action -eq "up") {
        Write-Host ""
        Write-Host "🎉 服务启动完成！" -ForegroundColor Green
        Write-Host "📱 前端访问地址: http://localhost:3000" -ForegroundColor Yellow
        Write-Host "🔧 后端 API 地址: http://localhost:8080" -ForegroundColor Yellow
        Write-Host "🗄️  数据库端口: localhost:3306" -ForegroundColor Yellow
        Write-Host "🔴 Redis 端口: localhost:6379" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "查看日志: docker compose logs -f" -ForegroundColor Gray
        Write-Host "停止服务: .\start-with-keys.ps1 -Action down" -ForegroundColor Gray
    }
} catch {
    Write-Host "❌ 命令执行失败: $_" -ForegroundColor Red
    exit 1
}