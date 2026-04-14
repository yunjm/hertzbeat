<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <div ref="trendChartRef" style="height: 350px;" />
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <div ref="levelPieChartRef" style="height: 300px;" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div ref="serviceBarChartRef" style="height: 300px;" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import { getLogHistogram } from "@/api/monitor/log";

const trendChartRef = ref(null);
const levelPieChartRef = ref(null);
const serviceBarChartRef = ref(null);

let trendChart = null;
let levelPieChart = null;
let serviceBarChart = null;

function initCharts() {
  if (trendChartRef.value) trendChart = echarts.init(trendChartRef.value);
  if (levelPieChartRef.value) levelPieChart = echarts.init(levelPieChartRef.value);
  if (serviceBarChartRef.value) serviceBarChart = echarts.init(serviceBarChartRef.value);
}

function loadData() {
  getLogHistogram().then(res => {
    // Mock data for now
    const mockTrendOptions = {
      title: { text: '24小时日志量趋势' },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'] },
      yAxis: { type: 'value' },
      series: [{ data: [120, 200, 150, 80, 70, 110], type: 'line', smooth: true }]
    };
    
    const mockLevelOptions = {
      title: { text: '日志级别分布' },
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: '50%',
        data: [
          { value: 1048, name: 'INFO' },
          { value: 735, name: 'WARN' },
          { value: 580, name: 'ERROR' }
        ]
      }]
    };

    if (trendChart) trendChart.setOption(mockTrendOptions);
    if (levelPieChart) levelPieChart.setOption(mockLevelOptions);
    // TODO: set service bar chart option
  });
}

onMounted(() => {
  initCharts();
  loadData();
  window.addEventListener('resize', () => {
    if (trendChart) trendChart.resize();
    if (levelPieChart) levelPieChart.resize();
    if (serviceBarChart) serviceBarChart.resize();
  });
});

onUnmounted(() => {
  if (trendChart) trendChart.dispose();
  if (levelPieChart) levelPieChart.dispose();
  if (serviceBarChart) serviceBarChart.dispose();
});
</script>