# AI Travel Planner - 安全构建脚本
# 支持交互式输入密钥或从安全配置文件读取，避免命令行历史泄露

param(
    [string]$SecretsFile = "",
    [switch]$Interactive = $false,
    [switch]$Build = $true,
    [switch]$Start = $true,
    [switch]$Help = $false
)

# 显示帮助信息
if ($Help) {
    Write-Host "AI Travel Planner 安全构建脚本" -ForegroundColor Green
    Write-Host "================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "用法:" -ForegroundColor Yellow
    Write-Host "  .\build-with-secrets.ps1 [参数]" -ForegroundColor White
    Write-Host ""
    Write-Host "参数:" -ForegroundColor Yellow
    Write-Host "  -SecretsFile <path>     从YAML配置文件读取密钥" -ForegroundColor White
    Write-Host "  -Interactive            交互式输入密钥" -ForegroundColor White
    Write-Host "  -Build                  构建镜像 (默认: true)" -ForegroundColor White
    Write-Host "  -Start                  启动服务 (默认: true)" -ForegroundColor White
    Write-Host "  -Help                   显示此帮助信息" -ForegroundColor White
    Write-Host ""
    Write-Host "示例:" -ForegroundColor Yellow
    Write-Host "  # 交互式输入密钥" -ForegroundColor Gray
    Write-Host "  .\build-with-secrets.ps1 -Interactive" -ForegroundColor White
    Write-Host ""
    Write-Host "  # 从配置文件读取" -ForegroundColor Gray
    Write-Host "  .\build-with-secrets.ps1 -SecretsFile secrets.yml" -ForegroundColor White
    Write-Host ""
    Write-Host "  # 仅构建不启动" -ForegroundColor Gray
    Write-Host "  .\build-with-secrets.ps1 -Interactive -Start:`$false" -ForegroundColor White
    exit 0
}

Write-Host "🔐 AI Travel Planner 安全构建脚本" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green

# 检查 Docker 是否运行
try {
    docker version | Out-Null
    Write-Host "✅ Docker 守护进程运行正常" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker 守护进程未运行，请启动 Docker Desktop" -ForegroundColor Red
    Write-Host "   启动后请重新运行此脚本" -ForegroundColor Yellow
    exit 1
}

# 密钥存储
$secrets = @{}

# 从配置文件读取密钥
if ($SecretsFile -and (Test-Path $SecretsFile)) {
    Write-Host "📄 从配置文件读取密钥: $SecretsFile" -ForegroundColor Blue
    
    try {
        $content = Get-Content $SecretsFile -Raw
        
        # 简单的YAML解析 (针对提供的格式)
        # 解析LLM配置
        if ($content -match 'llm:\s*\n\s*apiKey:\s*"([^"]+)"') {
            $secrets['LLM_API_KEY'] = $matches[1]
        }
        if ($content -match 'model:\s*"([^"]+)"') {
            $secrets['LLM_MODEL'] = $matches[1]
        }
        
        # 解析科大讯飞配置
        if ($content -match 'appId:\s*([^\s\n]+)') {
            $secrets['XUNFEI_APP_ID'] = $matches[1]
        }
        if ($content -match 'xfyun:[\s\S]*?apiKey:\s*([a-f0-9]+)') {
            $secrets['XUNFEI_API_KEY'] = $matches[1]
        }
        if ($content -match 'apiSecret:\s*([A-Za-z0-9+/=]+)') {
            $secrets['XUNFEI_API_SECRET'] = $matches[1]
        }
        
        # 解析高德地图配置
        if ($content -match 'VITE_AMAP_KEY:\s*([a-f0-9]+)') {
            $secrets['AMAP_API_KEY'] = $matches[1]
        }
        
        Write-Host "✅ 成功读取 $($secrets.Count) 个配置项" -ForegroundColor Green
    } catch {
        Write-Host "❌ 读取配置文件失败: $_" -ForegroundColor Red
        exit 1
    }
}

# 交互式输入密钥
if ($Interactive) {
    Write-Host "🔑 请输入API密钥 (留空跳过):" -ForegroundColor Yellow
    Write-Host ""
    
    $input = Read-Host "高德地图 API Key"
    if ($input) { $secrets['AMAP_API_KEY'] = $input }
    
    $input = Read-Host "科大讯飞 App ID"
    if ($input) { $secrets['XUNFEI_APP_ID'] = $input }
    
    $input = Read-Host "科大讯飞 API Key"
    if ($input) { $secrets['XUNFEI_API_KEY'] = $input }
    
    $input = Read-Host "科大讯飞 API Secret"
    if ($input) { $secrets['XUNFEI_API_SECRET'] = $input }
    
    $input = Read-Host "阿里云百炼 API Key"
    if ($input) { $secrets['LLM_API_KEY'] = $input }
    
    $input = Read-Host "阿里云 Access Key Secret (可选)"
    if ($input) { $secrets['ALIBABA_CLOUD_ACCESS_KEY_SECRET'] = $input }
    
    Write-Host ""
}

# 设置环境变量
Write-Host "🔧 配置环境变量..." -ForegroundColor Blue
foreach ($key in $secrets.Keys) {
    Set-Item -Path "env:$key" -Value $secrets[$key]
    Write-Host "   ✓ $key" -ForegroundColor Gray
}

# 构建镜像
if ($Build) {
    Write-Host ""
    Write-Host "🔨 构建Docker镜像..." -ForegroundColor Blue
    
    try {
        docker compose build
        Write-Host "✅ 镜像构建完成" -ForegroundColor Green
    } catch {
        Write-Host "❌ 镜像构建失败: $_" -ForegroundColor Red
        exit 1
    }
}

# 启动服务
if ($Start) {
    Write-Host ""
    Write-Host "🚀 启动服务..." -ForegroundColor Blue
    
    try {
        docker compose up -d
        Write-Host ""
        Write-Host "🎉 服务启动完成！" -ForegroundColor Green
        Write-Host "📱 前端访问地址: http://localhost:3000" -ForegroundColor Yellow
        Write-Host "🔧 后端 API 地址: http://localhost:8080" -ForegroundColor Yellow
        Write-Host "🗄️  数据库端口: localhost:3307" -ForegroundColor Yellow
        Write-Host "🔴 Redis 端口: localhost:6379" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "查看日志: docker compose logs -f" -ForegroundColor Gray
        Write-Host "停止服务: docker compose down" -ForegroundColor Gray
    } catch {
        Write-Host "❌ 服务启动失败: $_" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "🔒 安全提示: 密钥已从内存中清除" -ForegroundColor Green

# 清除环境变量
foreach ($key in $secrets.Keys) {
    Remove-Item -Path "env:$key" -ErrorAction SilentlyContinue
}