<template>
  <div class="result-container">
    <el-card class="result-card">
      <template #header>
        <div class="card-header">
          <h2>查询结果</h2>
          <el-button @click="goBack" type="primary" plain>返回查询</el-button>
        </div>
      </template>
      <div class="card-content">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>
        <div v-else-if="error" class="error-container">
          <el-empty description="查询失败，请返回重试">
            <template #description>
              <p>{{ error }}</p>
            </template>
          </el-empty>
        </div>
        <div v-else-if="!result" class="empty-container">
          <el-empty description="暂无查询结果" />
        </div>
        <div v-else class="result-data">
          <el-descriptions title="个人基本信息" :column="1" border>
            <el-descriptions-item label="姓名">
              <el-tag size="small">{{ maskSensitiveInfo(result.name) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="身份证号">
              {{ maskIdCard(result.idCard) }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              {{ result.gender }}
            </el-descriptions-item>
            <el-descriptions-item label="出生日期">
              {{ result.birthDate }}
            </el-descriptions-item>
            <el-descriptions-item label="年龄">
              {{ result.age }}岁
            </el-descriptions-item>
            <el-descriptions-item label="地址">
              {{ maskAddress(result.address) }}
            </el-descriptions-item>
          </el-descriptions>

          <el-divider />

          <el-descriptions title="其他信息" :column="1" border>
            <el-descriptions-item label="联系电话">
              {{ maskPhone(result.phone) }}
            </el-descriptions-item>
            <el-descriptions-item label="婚姻状况">
              {{ result.maritalStatus }}
            </el-descriptions-item>
            <el-descriptions-item label="学历">
              {{ result.education }}
            </el-descriptions-item>
            <el-descriptions-item label="职业">
              {{ result.occupation }}
            </el-descriptions-item>
          </el-descriptions>

          <div class="security-notice">
            <el-alert
              title="安全提示"
              type="info"
              description="查询结果已在前端进行脱敏处理，保护个人隐私。本次查询已记录在审计日志中。"
              :closable="false"
              show-icon
            />
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const result = ref(null);
const loading = ref(true);
const error = ref(null);

// 从sessionStorage获取查询结果
onMounted(() => {
  loading.value = true;
  try {
    const storedResult = sessionStorage.getItem('queryResult');
    if (storedResult) {
      result.value = JSON.parse(storedResult);
    }
  } catch (err) {
    error.value = '结果解析失败: ' + err.message;
  } finally {
    loading.value = false;
  }
});

// 返回查询页面
const goBack = () => {
  router.push('/query');
};

// 身份证号脱敏
const maskIdCard = (idCard) => {
  if (!idCard) return '';
  return idCard.replace(/^(.{6})(.*)(.{4})$/, '$1********$3');
};

// 姓名脱敏
const maskSensitiveInfo = (name) => {
  if (!name) return '';
  if (name.length <= 1) return name;
  if (name.length === 2) return name.substring(0, 1) + '*';
  return name.substring(0, 1) + '*'.repeat(name.length - 2) + name.substring(name.length - 1);
};

// 电话号码脱敏
const maskPhone = (phone) => {
  if (!phone) return '';
  return phone.replace(/^(.{3})(.*)(.{4})$/, '$1****$3');
};

// 地址脱敏
const maskAddress = (address) => {
  if (!address) return '';
  if (address.length <= 6) return address;
  // 保留前6个字符和后4个字符
  return address.substring(0, 6) + '****' + address.substring(address.length - 4);
};
</script>

<style scoped>
.result-container {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.result-card {
  width: 100%;
  max-width: 800px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container,
.error-container,
.empty-container {
  padding: 40px 0;
  display: flex;
  justify-content: center;
}

.result-data {
  margin-top: 10px;
}

.security-notice {
  margin-top: 30px;
}
</style>