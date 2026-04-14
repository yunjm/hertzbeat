<template>
  <div class="app-container">
    <el-table v-loading="loading" :data="alertList" type="expand">
      <el-table-column type="expand">
        <template #default="props">
          <div style="padding: 10px;">
            <h4>触发条件: {{ props.row.expr }}</h4>
            <div v-if="props.row.aiAnalysis">
              <h4>🤖 AI 诊断结果:</h4>
              <el-alert :title="props.row.aiAnalysis" type="info" :closable="false" show-icon />
            </div>
            <div v-else>
              <el-tag type="info">未开启 AI 分析</el-tag>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="时间" align="center" prop="timestamp" width="180" />
      <el-table-column label="告警名称" align="center" prop="name" width="200" />
      <el-table-column label="级别" align="center" prop="level" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.level === 'CRITICAL' ? 'danger' : 'warning'">
            {{ scope.row.level }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="告警内容" align="left" prop="message" :show-overflow-tooltip="true" />
      <el-table-column label="处理状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '已处理' : '未处理' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const loading = ref(false);

const alertList = ref([
  {
    id: 1001,
    timestamp: '2026-04-14 10:23:45',
    name: '错误日志风暴',
    level: 'CRITICAL',
    message: 'user-service 触发错误日志风暴，1分钟内 56 条 Error',
    expr: 'error_logs > 50',
    aiAnalysis: 'AI Analysis: The error is caused by a database connection timeout. Suggested fix: Increase hikari pool size or check database network.',
    status: 0
  }
]);
</script>