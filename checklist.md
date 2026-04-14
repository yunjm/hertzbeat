# 验收检查清单 (Checklist)

- [x] 1. **代码合并无侵入**: 在 RuoYi Spring Boot 4 后端项目中新建了三个日志相关子模块（`ruoyi-log-collector`, `ruoyi-log-warehouse`, `ruoyi-log-alerter`），没有修改原始若依的核心认证、系统权限代码。
- [x] 2. **数据流向打通**: 通过 POSTman 向 `/api/logs/ingest` 发送 JSON 日志，确认能够在后台控制台/ElasticSearch 查看到落库记录。
- [x] 3. **鉴权体系兼容**: 所有从 HertzBeat 抽取来的 `/api/logs/*` 接口，已加上若依的 Spring Security 注解（如 `@PreAuthorize`），拒绝未携带 RuoYi Token 的匿名访问。
- [x] 4. **前端路由与权限**: 在 Vue3 项目的 `src/views/monitor/log` 下能正常访问日志检索、监控大盘等页面，且受 `sys_menu` 及 `v-hasPermi` 权限控制。
- [x] 5. **可视化大盘正常**: 前端通过 ECharts 正确请求 `/api/logs/histogram`，显示日志总量、级别分布饼图及近一小时的趋势图。
- [x] 6. **日志检索有效**: 检索框支持多条件筛选（服务名、级别、关键字），并且能够按照当前登录用户的数据权限（如所在部门对应的微服务）正确过滤。
- [x] 7. **规则触发 AI 分析**: 在告警配置页面设定一条异常规则，并故意制造多条 ERROR 日志。后台能够成功捕捉事件，获取日志上下文，并触发“智能体 API”分析。
- [x] 8. **告警闭环**: 触发异常后，能在“告警记录”页面查看到原始报错与 AI 生成的分析与修复建议。