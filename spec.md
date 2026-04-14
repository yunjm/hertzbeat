# 全场景 AI 日志分析平台 Spec（基于 HertzBeat 重构）

## 1. 背景与目标 (Why)
当前平台是基于 RuoYi Vue3 (前端) + Spring Boot 4 (后端) 的前后端分离架构，已初步具备“模型管理”与“智能体管理”能力。
为了实现“全场景 AI 日志分析”，需要快速补齐日志采集、统一存储、规则告警、监控大盘等基础能力。
本 Spec 旨在从开源项目 `HertzBeat` 中提取其优秀的采集调度（Collector）、数据流转（Queue）、时序/日志存储（Warehouse）、告警引擎（Alerter）等核心模块，以若依微服务/单体模块的规范重构并融入当前项目，打造一个端到端的 AI 日志监控与分析平台。

## 2. 改造范围 (What Changes)
- **日志采集与解析引擎**：抽取 HertzBeat `collector` 模块，重构为支持 HTTP Push、Kafka 消费、Agent 拉取的多协议日志采集服务。
- **数据通道与存储**：抽取 HertzBeat `common` 与 `warehouse` 模块，将日志数据与指标数据通过 Kafka/Redis 异步解耦，并落地到 ElasticSearch / ClickHouse 或其他 TSDB。
- **规则引擎与告警**：抽取 HertzBeat `alerter` 模块，与平台现有的 AI 智能体结合。支持静态阈值告警，并新增由 AI Agent 驱动的日志异常检测告警。
- **统一监控大盘**：参考 HertzBeat `manager` 与 `grafana` 模块，在 RuoYi Vue3 前端重构日志检索、监控指标可视化面板。
- **权限与多租户隔离**：废弃 HertzBeat 的 Sureness 认证体系，**完全复用 RuoYi 的 Spring Security + JWT 体系**及数据权限控制机制。

## 3. 架构影响 (Impact)
- **Affected specs**: 
  - 系统管理 (SysUser, SysMenu, SysRole) - 需新增监控与日志相关菜单及权限点。
  - AI 引擎 (Agent, Model) - 需要对外提供日志上下文分析 API，供告警模块调用。
- **Affected code**: 
  - 新增 `ruoyi-log-collector` 模块（采集服务）
  - 新增 `ruoyi-log-warehouse` 模块（存储与查询服务）
  - 新增 `ruoyi-log-alerter` 模块（告警引擎）
  - 前端 `src/views/monitor/` 下新增日志分析与大盘相关视图。

## 4. 新增需求 (ADDED Requirements)

### Requirement 1: 多源日志统一采集
系统必须提供标准化的接入端，支持接收多种格式的日志流。

#### Scenario: 接收应用日志推送
- **WHEN** 应用端通过 HTTP API (参考 HertzBeat `/api/logs/ingest`) 推送 JSON 格式日志。
- **THEN** `ruoyi-log-collector` 模块接收并校验数据，追加时间戳与来源标签，将其推入 Kafka 对应 Topic，返回 200 OK。

### Requirement 2: 异步日志落库与检索
系统必须异步消费队列中的日志数据并持久化，同时提供高效检索接口。

#### Scenario: 日志落库与前端检索
- **WHEN** `ruoyi-log-warehouse` 消费到 Kafka 的日志消息。
- **THEN** 批量写入 ElasticSearch/ClickHouse。
- **WHEN** 用户在 RuoYi Vue3 前端日志控制台输入关键词并查询。
- **THEN** 后端根据用户的数据权限（所属部门/项目租户）过滤查询条件，返回高亮命中的日志列表及趋势图数据。

### Requirement 3: AI 驱动的智能告警
系统必须支持在传统阈值告警的基础上，引入已有的 AI 智能体进行异常研判。

#### Scenario: 触发 AI 日志诊断告警
- **WHEN** `ruoyi-log-alerter` 引擎匹配到 ERROR 级别日志风暴（如 1 分钟内 > 100 条）。
- **THEN** 引擎组装该时间段的日志上下文，异步调用平台现有的“智能体管理”模块 API 进行根因分析。
- **THEN** 智能体返回分析结论后，引擎将原始告警信息与 AI 诊断结论合并，推送至企微/钉钉/邮件，并在前端告警中心展示。

## 5. 详细技术规范 (Technical Details)

### 5.1 数据库设计 (Database)
为了与 RuoYi 兼容，需新增以下表结构（MySQL）：
- **`sys_log_alert_rule`**: 告警规则表
  - `rule_id` (PK), `rule_name`, `expr` (阈值表达式), `duration` (持续时间), `ai_analyze` (是否开启 AI), `tenant_id` (多租户标识), `create_by`, `create_time`
- **`sys_log_alert_record`**: 告警记录表
  - `record_id` (PK), `rule_id`, `alert_level`, `alert_message`, `ai_analysis_result` (AI诊断结果文本), `status` (0:未处理, 1:已处理), `create_time`

*注意：实际海量日志数据不存 MySQL，而是存入 ElasticSearch。MySQL 仅存配置和结果。*

### 5.2 API 接口规范 (Backend)
- **Log Ingestion API**:
  - `POST /api/logs/ingest`
  - Body: `{ "serviceName": "app1", "level": "ERROR", "message": "...", "timestamp": 1234567890 }`
- **Log Query API**:
  - `GET /api/logs/query?keyword={kw}&level={lvl}&serviceName={svc}&startTime={st}&endTime={et}&pageNum=1&pageSize=10`
  - 必须经过 `@PreAuthorize` 鉴权，并根据用户所属 `dept_id` 自动追加 ES 过滤条件。
- **Histogram API**:
  - `GET /api/logs/histogram?startTime={st}&endTime={et}`
  - 返回 ECharts 适配的数据结构（如 Date Histogram 聚合）。

### 5.3 前端组件设计 (Frontend Vue3)
在 RuoYi-Vue3 的 `src/views/monitor` 下增加日志域：
- **`log/index.vue`**: 日志检索主页。顶层为 `el-form` 高级搜索，主体为 `el-table`，利用 `<template #expand>` 实现日志 JSON 的高亮折叠展示。
- **`log/dashboard.vue`**: 监控大盘。利用 `echarts` 绘制多折线图（各级别日志时间序列）与饼图（微服务日志量占比）。
- **`alert/rule.vue`**: `el-crud` 标准页面，管理 `sys_log_alert_rule`。
- **`alert/record.vue`**: 告警记录查看，针对开启了 AI 诊断的记录，通过 `el-alert` 组件显著高亮展示“AI 修复建议”。

## 6. 移除/替换的原始需求 (REMOVED Requirements)

### Requirement: HertzBeat 原始 Sureness 认证体系
**Reason**: 与现有的 RuoYi Spring Security 体系冲突，导致双重认证且无法复用 RuoYi 的用户、角色、部门数据权限。
**Migration**: 彻底剥离 HertzBeat 的鉴权拦截器。重构的监控模块直接依赖 RuoYi 的 `@PreAuthorize("@ss.hasPermi('...')")` 注解进行接口权限控制。

### Requirement: HertzBeat 的 Angular 前端控制台
**Reason**: 技术栈不统一，现有平台是 Vue3 + Element Plus。
**Migration**: 废弃 Angular 代码。将 HertzBeat 核心接口的参数模型提炼后，在 RuoYi Vue3 中使用 `el-table`、`el-form` 及 `ECharts` 从零编写监控面板与日志检索页面。