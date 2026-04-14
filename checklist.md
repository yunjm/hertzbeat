# 验收检查清单 (Checklist)

- [x] 1. **代码与库表合并**: 在 RuoYi Spring Boot 4 后端新建了日志相关子模块（`ruoyi-log-*`）。在 MySQL 中成功执行了 `sys_log_alert_rule` 等新建表脚本，并通过 MyBatis Plus / 代码生成器跑通了增删改查。
- [x] 2. **异步落库验证**: 通过 POSTman 向 `/api/logs/ingest` 发送 JSON 日志。能够在 Kafka 对应 Topic 看到消息积压。启动 `ruoyi-log-warehouse` 消费者后，能够在 ElasticSearch 中通过 Kibana/Kibana 查询到写入的日志条目。
- [x] 3. **鉴权体系兼容**: 所有从 HertzBeat 抽取来的 `/api/logs/*` 接口，已加上若依的 Spring Security 注解（如 `@PreAuthorize`），并且请求未携带合法 JWT Token 时返回 401。
- [x] 4. **租户数据权限隔离**: 验证使用“部门管理员A”和“部门管理员B”登录系统，调用 `/api/logs/query` 查询时，后端动态拼接的 ES `filter` 能确保 A 只能看到 A 部门的微服务日志。
- [x] 5. **前端路由与权限**: 在 Vue3 项目的 `src/views/monitor/log` 下能正常访问日志检索、监控大盘等页面，且受 `sys_menu` 及 `v-hasPermi` 权限控制（即没有权限的用户看不到对应的按钮或菜单）。
- [x] 6. **可视化大盘正常**: 前端通过 ECharts 成功调用 `/api/logs/histogram`（返回的是 ES 的 `date_histogram` 结果），显示了带有图例和坐标轴的各级别日志 24 小时折线趋势图。
- [x] 7. **告警配置与流计算**: 在 `rule.vue` 中添加告警（如：1分钟 ERROR > 50 且 `ai_analyze=true`）。`ruoyi-log-alerter` 中的滑动窗口机制能准确捕捉到测试期间制造的日志风暴事件，并生成一条告警记录。
- [x] 8. **AI 诊断链路打通**: 触发上述风暴后，后端能够成功从 ES 获取前后 10 分钟日志上下文，并成功调用现有的“智能体大模型 API”。大模型返回的 `ai_analysis_result` 被正确写入 MySQL 的 `sys_log_alert_record` 表。
- [x] 9. **前端展示闭环**: 在前端告警记录页 (`record.vue`) 展开该条风暴记录时，能通过带有明显 UI 样式的 `el-alert` 看到大模型输出的“根本原因分析”与“修复建议”。并且企微/钉钉群中收到了格式化后的通知。