<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="服务名称" prop="serviceName">
        <el-input v-model="queryParams.serviceName" placeholder="请输入服务名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="日志级别" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择日志级别" clearable>
          <el-option label="INFO" value="INFO" />
          <el-option label="WARN" value="WARN" />
          <el-option label="ERROR" value="ERROR" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="请输入关键词" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="logList" type="expand">
      <el-table-column type="expand">
        <template #default="props">
          <pre>{{ props.row.fullMessage }}</pre>
        </template>
      </el-table-column>
      <el-table-column label="时间" align="center" prop="timestamp" width="180" />
      <el-table-column label="服务名称" align="center" prop="serviceName" width="150" />
      <el-table-column label="级别" align="center" prop="level" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.level === 'ERROR' ? 'danger' : (scope.row.level === 'WARN' ? 'warning' : 'info')">
            {{ scope.row.level }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="日志摘要" align="left" prop="message" :show-overflow-tooltip="true" />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { listLogs } from "@/api/monitor/log";

const loading = ref(false);
const showSearch = ref(true);
const logList = ref([]);
const total = ref(0);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  serviceName: undefined,
  level: undefined,
  keyword: undefined
});

function getList() {
  loading.value = true;
  listLogs(queryParams).then(response => {
    logList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  queryParams.serviceName = undefined;
  queryParams.level = undefined;
  queryParams.keyword = undefined;
  handleQuery();
}

// Initial load
getList();
</script>