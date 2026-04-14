# Tasks
- [x] Task 1: 解析 HertzBeat 核心架构与功能实现原理
  - [x] SubTask 1.1: 分析 Agentless 数据采集机制 (hertzbeat-collector 协议与模板引擎)
  - [x] SubTask 1.2: 分析任务管理与调度机制 (hertzbeat-manager 模块)
  - [x] SubTask 1.3: 分析告警与通知处理流 (hertzbeat-alerter 模块)
  - [x] SubTask 1.4: 分析实时数据与历史数据的存储方案 (hertzbeat-warehouse 模块)
  - [x] SubTask 1.5: 分析 Manager 与 Collector 之间的集群通信机制 (hertzbeat-remoting 模块)
  - [x] SubTask 1.6: 分析主动推送与日志支持等其他核心功能 (hertzbeat-push, hertzbeat-otel, hertzbeat-log 模块)
  - [x] SubTask 1.7: 撰写并输出完整的 Markdown 格式分析报告

# Task Dependencies
- [SubTask 1.7] depends on [SubTask 1.1, SubTask 1.2, SubTask 1.3, SubTask 1.4, SubTask 1.5, SubTask 1.6]