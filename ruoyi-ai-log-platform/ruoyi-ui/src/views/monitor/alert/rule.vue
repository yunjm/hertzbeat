<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['monitor:alert:add']">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="ruleList">
      <el-table-column label="规则名称" align="center" prop="name" />
      <el-table-column label="条件表达式" align="center" prop="expr" />
      <el-table-column label="持续时间(秒)" align="center" prop="times" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="text" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['monitor:alert:edit']">修改</el-button>
          <el-button type="text" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['monitor:alert:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改告警规则对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="ruleRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="规则名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="条件表达式" prop="expr">
          <el-input v-model="form.expr" placeholder="如: error_logs > 50" />
        </el-form-item>
        <el-form-item label="持续时间" prop="times">
          <el-input-number v-model="form.times" :min="1" />
        </el-form-item>
        <el-form-item label="AI 诊断" prop="aiAnalyze">
          <el-switch v-model="form.aiAnalyze" active-text="开启" inactive-text="关闭" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';

const loading = ref(false);
const open = ref(false);
const title = ref("");
const ruleList = ref([{ id: 1, name: '错误日志风暴', expr: 'error_logs > 50', times: 60, aiAnalyze: true }]);

const form = reactive({
  id: null,
  name: '',
  expr: '',
  times: 60,
  aiAnalyze: false
});

const rules = reactive({
  name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
  expr: [{ required: true, message: "表达式不能为空", trigger: "blur" }]
});

function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加告警规则";
}

function handleUpdate(row) {
  reset();
  Object.assign(form, row);
  open.value = true;
  title.value = "修改告警规则";
}

function handleDelete(row) {
  // TODO: call delete API
}

function submitForm() {
  // TODO: call save/update API
  open.value = false;
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.id = null;
  form.name = '';
  form.expr = '';
  form.times = 60;
  form.aiAnalyze = false;
}
</script>