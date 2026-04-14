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

## 阶段 2: 日志存储与检索中心 (Backend & Database)

- [x] Task 2.1: 日志消费与落库 (ElasticSearch/ClickHouse)
  - 将 HertzBeat `warehouse` 中处理 `log-entry-data` 的逻辑抽取。
  - 编写 Elasticsearch 的 Index 模板 (映射 `timestamp`, `serviceName` 为 keyword, `message` 为 text/analyzer)。
  - 实现 Spring Data Elasticsearch 或 RestHighLevelClient 的批量异步插入 (BulkRequest)。
- [x] Task 2.2: 基础查询与过滤 API (`/api/logs/query`)
  - 实现分页查询 API：支持按时间段、级别、服务名及全文关键词检索 (`match` / `term` / `range` queries)。
  - **关键点**: 必须增加租户/部门的数据权限过滤逻辑（基于 RuoYi 的 `@DataScope`，将当前用户的 `dept_id` 追加为 ES 查询的 `filter` 子句）。
- [x] Task 2.3: 趋势图统计 API (`/api/logs/histogram`)
  - 实现按时间聚合（柱状图）的接口，使用 ES 的 `date_histogram` 聚合（按分钟/小时分桶），以及基于 `level` 的 `terms` 子聚合。
  - 返回适配前端 ECharts `series` 结构的数据格式。

## 阶段 3: Vue3 监控面板与检索中心 (Frontend)

- [x] Task 3.1: 菜单配置与基础路由
  - 在 RuoYi 的 `sys_menu` 中新增“日志分析”父菜单及子菜单：“日志检索”、“监控大盘”、“告警规则”、“告警记录”。
  - 前端 `src/views/monitor/log` 目录下初始化对应组件。
- [x] Task 3.2: 实时/历史日志检索页面
  - 使用 `el-form` 实现多条件筛选，`el-table` 实现日志表格，支持行展开查看 JSON 详情与堆栈。
  - 接入 `ruoyi-log-warehouse` 提供的 `/api/logs/query` 接口。
- [x] Task 3.3: 监控大盘看板
  - 引入 ECharts，对接 `/api/logs/histogram`，绘制各服务的错误日志趋势图与来源占比饼图。

## 阶段 4: AI 智能告警引擎集成 (Backend, Frontend & MySQL)

- [x] Task 4.1: 数据库与 MyBatis/MyBatis-Plus 配置
  - 在 MySQL 执行 DDL 脚本，创建 `sys_log_alert_rule` 和 `sys_log_alert_record` 两张核心表。
  - 使用若依代码生成器或手动编写 Controller/Service/Mapper/XML，生成告警规则的 CRUD 接口。
- [x] Task 4.2: 告警规则引擎与流计算
  - 抽取 HertzBeat `alerter` 中的 ANTLR4 语法解析 (`AlertExpression.g4`)，移植到 `ruoyi-log-alerter`。
  - 实现一个定时或滑动窗口任务 (类似 `WindowAggregator`)，周期性拉取 ElasticSearch 匹配 `expr` 的聚合值。
- [x] Task 4.3: Vue3 告警管理控制台
  - 基于 RuoYi 前端脚手架，在 `src/views/monitor/alert` 中生成或编写 `rule.vue`。
  - 增加表单字段：表达式、统计周期、`ai_analyze` (布尔开关) 等。
- [x] Task 4.4: AI Agent 诊断链路 (核心)
  - 触发阈值告警后，引擎根据当前触发时间前后的 10 分钟范围，发起对 ES 的上下文日志检索 (Fetch raw context)。
  - 调用 `AiDiagnosticService`，将上下文日志发送给平台已有的“智能体大模型 API”。
  - 接收模型生成的 `ai_analysis_result`，插入到 `sys_log_alert_record` 表中。
- [x] Task 4.5: 告警记录与通知 (Frontend & Backend)
  - 实现 `/api/alerts/records` 的查询接口。
  - 在前端 `record.vue` 中，当表格展开（expand）时，若 `ai_analyze=true`，通过 Element Plus 的 `el-alert` 组件高亮显示大模型的诊断与修复建议。
  - 实现钉钉/企微的 Webhook 推送。

## 任务依赖关系
- [Task 1.3] 依赖 [Task 1.2]
- [Task 2.1] 依赖 [Task 1.3]
- [Task 3.2] 依赖 [Task 2.2]
- [Task 4.3] 依赖 [Task 4.1] 及 [Task 2.2]