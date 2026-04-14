# HertzBeat 功能实现原理解析规范 (Spec)

## Why
用户要求梳理 `https://gitee.com/hertzbeat/hertzbeat.git` 项目的所有功能及其实现原理。该文档旨在定义分析任务的目标和产出，为用户提供清晰的架构和核心机制解析。

## What Changes
- 分析 HertzBeat 项目的模块结构和核心功能
- 梳理其无侵入（Agentless）监控数据采集机制、任务调度机制、告警分发机制、数据存储方案以及集群通信原理
- 输出一份完整的分析报告

## Impact
- 受影响的功能: 无 (纯分析任务)
- 受影响的代码: 无 (纯分析任务)

## ADDED Requirements
### Requirement: 架构与功能原理解析报告
系统需要提供对 HertzBeat 以下核心模块的深度原理解析：
1. **Agentless 数据采集机制**: 解析 `hertzbeat-collector` 如何利用 YAML 模板引擎，通过多种协议（HTTP、JMX、JDBC、SSH、SNMP、Prometheus 等）无侵入地收集指标数据。
2. **任务管理与调度**: 解析 `hertzbeat-manager` 模块如何分发采集任务、管理监控配置。
3. **告警与通知流**: 解析 `hertzbeat-alerter` 如何根据阈值规则计算告警，并使用多种通知渠道（邮件、钉钉、Slack 等）分发。
4. **数据存储**: 解析 `hertzbeat-warehouse` 对实时数据（内存/Redis）和历史数据（如 InfluxDB、VictoriaMetrics、DuckDB 等）的双层存储策略。
5. **集群通信**: 解析 `hertzbeat-remoting` 中基于 Netty 的 Manager 与 Collector 间的通信机制。
6. **推送与 OTel 集成**: 解析 `hertzbeat-push` 与 `hertzbeat-otel` 对主动上报数据和 OpenTelemetry 链路的支持。

#### Scenario: 成功场景
- **WHEN** 用户阅读分析报告
- **THEN** 用户能够透彻理解 HertzBeat 的底层架构及各项监控特性的实现方式。