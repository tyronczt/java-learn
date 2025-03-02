<template>
  <div class="query-container">
    <el-card class="query-card">
      <template #header>
        <div class="card-header">
          <h2>安全身份信息查询</h2>
          <el-button @click="goToHome" type="primary" plain>
            <el-icon><ArrowLeft /></el-icon>
            返回首页
          </el-button>
        </div>
      </template>
      <div class="card-content">
        <p class="description">请输入身份证号码进行查询，您的信息将全程加密传输和处理。</p>
        
        <el-form :model="queryForm" :rules="rules" ref="queryFormRef" label-width="100px">
          <el-form-item label="身份证号" prop="idCardNumber">
            <el-input 
              v-model="queryForm.idCardNumber" 
              placeholder="请输入18位身份证号码"
              :maxlength="18"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item>
            <div class="form-actions">
              <el-button type="primary" @click="submitQuery" :loading="loading">安全查询</el-button>
              <el-button @click="resetForm">重置</el-button>
              <el-button @click="goToHome">返回首页</el-button>
            </div>
          </el-form-item>
        </el-form>
        
        <div class="encryption-status" v-if="sessionInfo.hasActiveSession">
          <el-alert
            title="加密会话已建立"
            type="success"
            :closable="false"
            show-icon
          >
            <template #default>
              <p>会话密钥: {{ sessionInfo.sessionKeyPreview }}</p>
              <p>初始向量: {{ sessionInfo.ivPreview }}</p>
            </template>
          </el-alert>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import apiService from '../api';

const router = useRouter();
const queryFormRef = ref(null);
const loading = ref(false);

// 模拟用户ID (实际应用中应从登录会话获取)
const userId = 'user123';

// 表单数据
const queryForm = reactive({
  idCardNumber: ''
});

// 表单验证规则
const rules = {
  idCardNumber: [
    { required: true, message: '请输入身份证号码', trigger: 'blur' },
    { pattern: /(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的18位身份证号码', trigger: 'blur' }
  ]
};

// 获取会话信息
const sessionInfo = computed(() => {
  return apiService.getSessionInfo();
});

// 初始化加密会话
const initSession = async () => {
  try {
    loading.value = true;
    const success = await apiService.initEncryptionSession(userId);
    if (success) {
      ElMessage.success('加密会话建立成功');
    } else {
      ElMessage.error('加密会话建立失败');
    }
  } catch (error) {
    console.error('初始化会话失败:', error);
    ElMessage.error('加密会话建立失败: ' + error.message);
  } finally {
    loading.value = false;
  }
};

// 提交查询
const submitQuery = async () => {
  if (!queryFormRef.value) return;
  
  await queryFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    try {
      loading.value = true;
      
      // 如果没有活跃会话，先初始化会话
      if (!sessionInfo.value.hasActiveSession) {
        await initSession();
      }
      
      // 发送加密查询请求
      const result = await apiService.queryEncrypted(queryForm.idCardNumber);
      
      // 将结果存储到sessionStorage，以便结果页面使用
      sessionStorage.setItem('queryResult', JSON.stringify(result));
      
      // 跳转到结果页面
      router.push('/result');
    } catch (error) {
      console.error('查询失败:', error);
      ElMessage.error('查询失败: ' + error.message);
    } finally {
      loading.value = false;
    }
  });
};

// 重置表单
const resetForm = () => {
  if (queryFormRef.value) {
    queryFormRef.value.resetFields();
  }
};

// 组件挂载时初始化加密会话
onMounted(() => {
  if (!sessionInfo.value.hasActiveSession) {
    initSession();
  }
});

// 返回首页
const goToHome = () => {
  router.push('/');
};
</script>

<style scoped>
.query-container {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.query-card {
  width: 100%;
  max-width: 600px;
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

.encryption-status {
  margin-top: 30px;
}
</style>