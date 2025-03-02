<template>
  <div class="audit-container">
    <el-card class="audit-card">
      <template #header>
        <div class="card-header">
          <h2>审计日志查询</h2>
        </div>
      </template>
      <div class="card-content">
        <p class="description">查询系统操作审计日志，支持多种查询方式。</p>
        
        <el-form :model="queryForm" ref="queryFormRef" label-width="100px">
          <el-form-item label="查询方式">
            <el-radio-group v-model="queryForm.queryType">
              <el-radio label="userId">用户ID</el-radio>
              <el-radio label="idCard">身份证号</el-radio>
              <el-radio label="timeRange">时间范围</el-radio>
              <el-radio label="operation">操作类型</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <!-- 用户ID查询 -->
          <el-form-item label="用户ID" v-if="queryForm.queryType === 'userId'" prop="userId">
            <el-input v-model="queryForm.userId" placeholder="请输入用户ID"></el-input>
          </el-form-item>
          
          <!-- 身份证号查询 -->
          <el-form-item label="身份证号" v-if="queryForm.queryType === 'idCard'" prop="idCardNumber">
            <el-input 
              v-model="queryForm.idCardNumber" 
              placeholder="请输入身份证号码"
              :maxlength="18"
              show-word-limit
            ></el-input>
            <div class="query-type-selector">
              <el-radio-group v-model="queryForm.idCardQueryType" size="small">
                <el-radio label="exact">精确查询</el-radio>
                <el-radio label="fuzzy">模糊查询</el-radio>
              </el-radio-group>
            </div>
          </el-form-item>
          
          <!-- 时间范围查询 -->
          <el-form-item label="时间范围" v-if="queryForm.queryType === 'timeRange'" prop="timeRange">
            <el-date-picker
              v-model="queryForm.timeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              :default-time="['00:00:00', '23:59:59']"
            ></el-date-picker>
          </el-form-item>
          
          <!-- 操作类型查询 -->
          <el-form-item label="操作类型" v-if="queryForm.queryType === 'operation'" prop="operation">
            <el-select v-model="queryForm.operation" placeholder="请选择操作类型">
              <el-option label="身份证查询" value="QUERY_ID_CARD"></el-option>
              <el-option label="用户登录" value="USER_LOGIN"></el-option>
              <el-option label="数据导出" value="DATA_EXPORT"></el-option>
              <el-option label="系统配置" value="SYSTEM_CONFIG"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <div class="form-actions">
              <el-button type="primary" @click="submitQuery" :loading="loading">查询</el-button>
              <el-button @click="resetForm">重置</el-button>
            </div>
          </el-form-item>
        </el-form>
        
        <!-- 查询结果表格 -->
        <div v-if="auditLogs.length > 0" class="audit-results">
          <el-divider>查询结果</el-divider>
          
          <el-table :data="auditLogs" style="width: 100%" border stripe>
            <el-table-column prop="traceId" label="追踪ID" width="280" show-overflow-tooltip></el-table-column>
            <el-table-column prop="userId" label="用户ID" width="120"></el-table-column>
            <el-table-column prop="operation" label="操作类型" width="150">
              <template #default="scope">
                <el-tag :type="getOperationTagType(scope.row.operation)">
                  {{ getOperationName(scope.row.operation) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="requestTime" label="请求时间" width="180"></el-table-column>
            <el-table-column prop="processTime" label="处理时间" width="180"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="ipAddress" label="IP地址" width="150"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="viewDetail(scope.row)"
                  text
                >
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="totalItems"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            ></el-pagination>
          </div>
        </div>
        
        <el-empty v-else-if="hasSearched" description="暂无审计日志记录"></el-empty>
      </div>
    </el-card>
    
    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="审计日志详情"
      width="50%"
    >
      <el-descriptions v-if="selectedLog" :column="1" border>
        <el-descriptions-item label="追踪ID">{{ selectedLog.traceId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ selectedLog.userId }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ getOperationName(selectedLog.operation) }}</el-descriptions-item>
        <el-descriptions-item label="请求时间">{{ selectedLog.requestTime }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ selectedLog.processTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ selectedLog.status }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ selectedLog.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="额外信息">
          <pre>{{ formatExtraInfo(selectedLog.extraInfo) }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import apiService from '../api';

// 表单引用
const queryFormRef = ref(null);

// 加载状态
const loading = ref(false);

// 是否已经搜索过
const hasSearched = ref(false);

// 查询表单数据
const queryForm = reactive({
  queryType: 'userId',
  userId: '',
  idCardNumber: '',
  idCardQueryType: 'exact',
  timeRange: [],
  operation: ''
});

// 审计日志数据
const auditLogs = ref([]);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const totalItems = ref(0);

// 详情对话框
const detailDialogVisible = ref(false);
const selectedLog = ref(null);

// 提交查询
const submitQuery = async () => {
  loading.value = true;
  hasSearched.value = true;
  
  try {
    // 构建查询参数
    const params = buildQueryParams();
    
    // 调用API服务获取审计日志数据
    const data = await apiService.queryAuditLogs(params, currentPage.value, pageSize.value);
    
    auditLogs.value = data.logs || [];
    totalItems.value = data.total || 0;
  } catch (error) {
    console.error('查询审计日志失败:', error);
    ElMessage.error('查询失败: ' + error.message);
    // 如果API调用失败，使用模拟数据（仅用于开发测试）
    if (process.env.NODE_ENV === 'development') {
      console.warn('使用模拟数据进行测试');
      auditLogs.value = generateMockAuditLogs();
      totalItems.value = auditLogs.value.length;
    }
  } finally {
    loading.value = false;
  }
};

// 构建查询参数
const buildQueryParams = () => {
  // 创建一个仅包含当前查询类型所需参数的对象
  // 这样避免后端if-else if逻辑判断问题
  const params = {};
  
  // 清除所有可能的参数，只保留当前查询类型需要的参数
  switch (queryForm.queryType) {
    case 'userId':
      // 只发送用户ID参数
      params.userId = queryForm.userId;
      break;
      
    case 'idCard':
      // 只发送身份证相关参数
      params.idCardNumber = queryForm.idCardNumber;
      params.idCardQueryType = queryForm.idCardQueryType || 'exact';
      // 不要设置userId参数，否则后端会优先处理userId查询
      console.log('发送身份证查询参数:', JSON.stringify(params));
      break;
      
    case 'timeRange':
      // 只发送时间范围参数
      if (queryForm.timeRange && queryForm.timeRange.length === 2) {
        params.startTime = formatDateTime(queryForm.timeRange[0]);
        params.endTime = formatDateTime(queryForm.timeRange[1]);
      }
      break;
      
    case 'operation':
      // 只发送操作类型参数
      params.operation = queryForm.operation;
      break;
  }
  
  console.log('构建的查询参数:', JSON.stringify(params));
  return params;
};

// 格式化日期时间为后端期望的格式
const formatDateTime = (date) => {
  return date ? new Date(date).toISOString().replace('T', ' ').substr(0, 19) : '';
};

// 重置表单
const resetForm = () => {
  if (queryFormRef.value) {
    queryFormRef.value.resetFields();
  }
  auditLogs.value = [];
  hasSearched.value = false;
};

// 查看详情
const viewDetail = (log) => {
  selectedLog.value = log;
  detailDialogVisible.value = true;
};

// 格式化额外信息
const formatExtraInfo = (extraInfo) => {
  if (!extraInfo) return '无';
  try {
    return JSON.stringify(JSON.parse(extraInfo), null, 2);
  } catch (e) {
    return extraInfo;
  }
};

// 获取操作类型名称
const getOperationName = (operation) => {
  switch (operation) {
    case 'QUERY_ID_CARD': return '身份证查询';
    case 'USER_LOGIN': return '用户登录';
    case 'DATA_EXPORT': return '数据导出';
    case 'SYSTEM_CONFIG': return '系统配置';
    default: return operation;
  }
};

// 获取操作类型标签样式
const getOperationTagType = (operation) => {
  switch (operation) {
    case 'QUERY_ID_CARD': return 'primary';
    case 'USER_LOGIN': return 'success';
    case 'DATA_EXPORT': return 'warning';
    case 'SYSTEM_CONFIG': return 'danger';
    default: return 'info';
  }
};

// 获取状态标签样式
const getStatusTagType = (status) => {
  switch (status) {
    case 'SUCCESS': return 'success';
    case 'FAILED': return 'danger';
    case 'PENDING': return 'warning';
    default: return 'info';
  }
};

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val;
  submitQuery();
};

// 处理每页条数变化
const handleSizeChange = (val) => {
  pageSize.value = val;
  submitQuery();
};

// 生成模拟审计日志数据（仅用于开发测试）
const generateMockAuditLogs = () => {
  const operations = ['QUERY_ID_CARD', 'USER_LOGIN', 'DATA_EXPORT', 'SYSTEM_CONFIG'];
  const statuses = ['SUCCESS', 'FAILED', 'PENDING'];
  const users = ['user123', 'admin', 'operator', 'guest'];
  
  return Array.from({ length: 20 }, (_, i) => ({
    id: i + 1,
    traceId: `trace-${Math.random().toString(36).substring(2, 10)}-${Date.now()}`,
    userId: users[Math.floor(Math.random() * users.length)],
    operation: operations[Math.floor(Math.random() * operations.length)],
    requestTime: new Date(Date.now() - Math.floor(Math.random() * 10000000)).toISOString().replace('T', ' ').substring(0, 19),
    processTime: `${Math.floor(Math.random() * 1000)}ms`,
    status: statuses[Math.floor(Math.random() * statuses.length)],
    ipAddress: `192.168.${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}`,
    extraInfo: i % 3 === 0 ? JSON.stringify({detail: '查询详情', queryParams: {idCard: '**********1234'}}) : null
  }));
};
</script>

<style scoped>
.audit-container {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.audit-card {
  width: 100%;
  max-width: 1000px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.description {
  margin-bottom: 20px;
  color: #606266;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 10px;
}

.query-type-selector {
  margin-top: 10px;
}

.audit-results {
  margin-top: 30px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}
</style>