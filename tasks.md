# 全场景 AI 日志分析平台 - 开发任务清单 (Tasks)

## 阶段 1: 核心基础模块剥离与 RuoYi 集成 (Backend)

- [x] Task 1.1: 搭建模块骨架
  - 在现有 RuoYi Spring Boot 4 项目中，新增子模块 `ruoyi-log-collector` (日志接入层)、`ruoyi-log-warehouse` (存储与检索层)、`ruoyi-log-alerter` (告警层)。
- [x] Task 1.2: 抽取通用组件与数据通道
  - 将 HertzBeat `common` 中的队列定义接口（如 `CommonDataQueue`）抽取到新项目中。
  - 实现基于 Redis List 和 Kafka 的日志缓冲队列。
- [x] Task 1.3: 日志接收层集成
  - 提取 HertzBeat 的 `LogIngestionController` 逻辑，在 `ruoyi-log-collector` 模块中实现 `/api/logs/ingest` 接口，验证写入到缓冲队列。
  - 加上 `@PreAuthorize` 或接口白名单机制，供应用端接入。
- [x] Task 1.4: 认证与权限打通
  - 删除所有 HertzBeat 原有的 Sureness 认证过滤器，全面切换为 RuoYi 的 `SysUser` 及 JWT 拦截器，确保通过 RuoYi 的 Token 可正确调用新增的采集接口。

## 阶段 2: 日志存储与检索中心 (Backend)

- [x] Task 2.1: 日志消费与落库
  - 将 HertzBeat `warehouse` 中处理 `log-entry-data` 的逻辑抽取。
  - 实现日志批量消费并持久化写入 ElasticSearch / ClickHouse 的数据分发任务。
- [x] Task 2.2: 基础查询与过滤 API
  - 实现 `GET /api/logs/query`：支持按时间段、级别、服务名及全文关键词检索，必须增加租户/部门的数据权限过滤逻辑（基于 RuoYi 的 `@DataScope`）。
- [x] Task 2.3: 趋势图统计 API
  - 实现按时间聚合（柱状图）的接口 `/api/logs/histogram`，用于前端大盘绘制。

## 阶段 3: Vue3 监控面板与检索中心 (Frontend)

- [x] Task 3.1: 菜单配置与基础路由
  - 在 RuoYi 的 `sys_menu` 中新增“日志分析”父菜单及子菜单：“日志检索”、“监控大盘”、“告警规则”、“告警记录”。
  - 前端 `src/views/monitor/log` 目录下初始化对应组件。
- [x] Task 3.2: 实时/历史日志检索页面
  - 使用 `el-form` 实现多条件筛选，`el-table` 实现日志表格，支持行展开查看 JSON 详情与堆栈。
  - 接入 `ruoyi-log-warehouse` 提供的 `/api/logs/query` 接口。
- [x] Task 3.3: 监控大盘看板
  - 引入 ECharts，对接 `/api/logs/histogram`，绘制各服务的错误日志趋势图与来源占比饼图。

## 阶段 4: AI 智能告警引擎集成 (Backend & Frontend)

- [x] Task 4.1: 规则引擎迁移与数据同步
  - 抽取 HertzBeat `alerter` 模块中对 `AlertExpression` 的 ANTLR4 语法解析与时间窗口聚合能力。
  - 在 RuoYi 后台实现告警规则的增删改查 API（`ruoyi-log-alerter`），关联到具体部门与责任人。
- [x] Task 4.2: 前端告警规则配置页面
  - 在 Vue3 侧实现阈值规则配置表单（如：1分钟内 Error 日志大于 50 条）。
- [x] Task 4.3: 联动平台智能体模型 (核心)
  - 在触发阈值告警时，增加“AI 诊断环节”：引擎自动提取触发告警前后的 50 条日志上下文。
  - 异步调用现有平台的“智能体管理模块 API”，请求大模型分析根因。
- [x] Task 4.4: 告警推送与历史记录
  - 将原始告警信息及 AI 给出的诊断报告合并，发送到钉钉/企微等通道。
  - 实现告警记录页面（Vue3），展示告警详情及 AI 修复建议。

## 任务依赖关系
- [Task 1.3] 依赖 [Task 1.2]
- [Task 2.1] 依赖 [Task 1.3]
- [Task 3.2] 依赖 [Task 2.2]
- [Task 4.3] 依赖 [Task 4.1] 及 [Task 2.2]