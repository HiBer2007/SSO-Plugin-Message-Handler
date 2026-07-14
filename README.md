# HiBerNET SSO Plugin Message Handler Mod

一个 **NeoForge 1.21.1** 客户端模组，处理 Velocity 代理上 `HiBerNET.SSO` 通道的插件消息，用于 SSO 登录认证流程。

## 概述

该模组运行在 NeoForge 客户端上，通过 Minecraft 插件消息系统与 Velocity 代理通信。典型使用场景是在 Velocity 代理后为 Minecraft 服务器实现统一的 SSO（单点登录）认证。

## 插件消息协议

所有消息在 Minecraft 通道 `hbnssopmhm:<type>` 上传输，详细的协议文档见 [SSO_PLUGIN_MESSAGES.md](./SSO_PLUGIN_MESSAGES.md)。

### 消息类型

| 方向 | 通道 | 用途 | 载荷 |
|------|------|------|------|
| S→C | `hbnssopmhm:query_sso` | 服务端查询客户端 SSO 登录信息 | 空 |
| C→S | `hbnssopmhm:sso_response` | 客户端返回 SSO 凭证 | `username(String)`, `uuid(String)`, `accessToken(String)`, `idToken(String)`, `expiresAt(VarLong)` |
| S→C | `hbnssopmhm:open_auth_gui` | 服务端要求客户端打开认证等待界面 | 空 |
| S→C | `hbnssopmhm:auth_success` | 认证成功通知（显示 3 秒倒计时后自动关闭） | 空 |
| S→C | `hbnssopmhm:open_browser` | 打开浏览器（MCEF/系统浏览器） | `url(String)`, `fullscreen(boolean)`, `allowExit(boolean)` |
| S→C | `hbnssopmhm:close_browser` | 关闭浏览器 | 空 |
| C→S | `hbnssopmhm:gui_closed` | 客户端 GUI 已关闭通知 | `reason(String)` |

## SSO 数据读取

模组从 JVM 系统属性或环境变量读取 SSO 凭证：

```bash
# 方式一：JVM 属性（默认）
-DHiBerNET.SSO.username=hiber2007
-DHiBerNET.SSO.uuid=b3492950-2453-40c8-9efb-c63094d271de
-DHiBerNET.SSO.accesstoken=J-BK4ObPF9JwcNZLT2_a2QhBQldGd3M-GbdriuxeZZz
-DHiBerNET.SSO.idtoken=eyJhbGciOiJSUzI1NiIsImtpZCI6...
-DHiBerNET.SSO.expiresat=1783908334563

# 方式二：环境变量（需设置 -DHiBerNET.SSO.useenv=true）
set HiBerNET.SSO.username=hiber2007
set HiBerNET.SSO.uuid=b3492950-2453-40c8-9efb-c63094d271de
# ...等
```

## 浏览器策略

| 优先级 | 方案 | 条件 |
|--------|------|------|
| 1 | **MCEF 内嵌 Chromium 浏览器** | 模组文件夹中同时安装了 [MCEF](https://www.curseforge.com/minecraft/mc-mods/mcef)（`mcef-neoforge`），且 CEF 运行时已下载 |
| 2 | **系统默认浏览器 + 确认对话框** | MCEF 未安装或未初始化 |

### MCEF 集成

MCEF（Minecraft Chromium Embedded Framework）提供基于 Chromium 的内嵌浏览器。如果安装了 `mcef-neoforge` 模组且 CEF 运行时下载完成，收到 `open_browser` 消息时会弹出全屏 Chromium 浏览器窗口，支持鼠标点击、键盘输入和事件转发。

如果 MCEF 不可用，模组会显示确认对话框，用户点击「打开浏览器」后使用系统默认浏览器打开 URL，同时显示游戏内覆盖层（含右上角的退出按钮）。

## GUI 屏幕

| 屏幕 | 用途 | 行为 |
|------|------|------|
| `AuthWaitScreen` | 认证等待中 | 显示在服务器认证期间，不可关闭 |
| `AuthSuccessScreen` | 认证成功 | 绿色主题，3 秒倒计时后自动关闭返回游戏 |
| `MCEFScreen` | MCEF 全屏浏览器 | 嵌入 Chromium 浏览器，右上角退出/离开按钮 |
| `BrowserConfirmScreen` | 系统浏览器确认 | 确认打开外部 URL |
| `BrowserScreen` | 浏览器覆盖层 | 系统浏览器打开后显示的覆盖层，右上角控制按钮 |

## GUI 文字与翻译

模组支持中英文双语界面：

- 英文：`assets/hbnssopmhm/lang/en_us.json`
- 中文：`assets/hbnssopmhm/lang/zh_cn.json`

## 测试插件

项目附带了 Velocity 测试插件，位于 `HBNET-sso-velocity-plugin/` 目录。

### 控制台命令

仅在 Velocity 控制台执行，指定目标玩家：

```
/hbnsstest <player> start       — 初始化测试状态
/hbnsstest <player> list        — 列出测试步骤
/hbnsstest <player> step <n>    — 执行指定步骤(1-6)
/hbnsstest <player> status      — 查看测试状态
/hbnsstest <player> summary     — 输出测试报告
```

### 测试步骤

| 步骤 | 动作 | 预期结果 |
|------|------|----------|
| 1 | 发送 `query_sso` | 客户端读取 SSO 属性并回复 `sso_response` |
| 2 | 发送 `open_auth_gui` | 客户端显示认证等待界面 |
| 3 | 发送 `auth_success` | 客户端显示绿色成功界面 + 3 秒倒计时自动关闭 |
| 4 | 发送 `open_browser` (bilibili.com, 全屏, 不可退出) | 打开浏览器，右上角「退出服务器」按钮 |
| 5 | 发送 `close_browser` | 关闭浏览器 |
| 6 | 发送 `open_browser` (example.com, 可自行退出) | 打开浏览器，玩家可自行按 Esc 关闭 |

## 构建

### 前置要求

- JDK 21
- 网络连接（首次构建需下载 NeoForge MDG 依赖）

### 构建模组

```bash
cd hibernet_sso_mod-source
./gradlew build
```

输出文件在 `build/libs/HBNET-sso-pm-handler-0.1.0-alpha.jar`

### 构建 Velocity 插件

```bash
cd HBNET-sso-velocity-plugin
./gradlew build
```

输出文件在 `build/libs/`，构建后自动部署到 `servertest/velocity/plugins/`

### 仓库镜像

如果遇到 NeoForged Maven 仓库 SSL 连接问题，`build.gradle` 中的 `repositories.each` 循环会自动将 `maven.neoforged.net` 的请求重定向到 `neoforged.forgecdn.net` 镜像。

## 依赖

### 运行时依赖

- NeoForge 21.1.200+
- MCEF（可选，用于内嵌 Chromium 浏览器）

### 编译时依赖

- `com.cinemamod:mcef-neoforge:2.2.0-1.21.1`（本地 flatDir 引用，见 `MCEF-dependence/`）

## 许可证

All Rights Reserved
