<template>
  <div class="home-container">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <h2>欢迎使用安全身份信息查询系统</h2>
        </div>
      </template>
      <div class="card-content">
        <p>本系统采用全链路加密技术，确保您的查询信息在传输和存储过程中的安全性。</p>
        <p>系统特点：</p>
        <ul>
          <li>全链路加密：从前端到后端，再到数据仓库，全程加密传输</li>
          <li>用户隔离：确保A用户查询的数据，B用户无法查询</li>
          <li>安全审计：所有查询操作均有记录，支持合规审计</li>
          <li>数据保护：开发人员无法解密用户数据，保障数据隐私</li>
        </ul>
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="goToQuery">开始查询</el-button>
          <el-button type="info" size="large" @click="goToAudit">审计日志</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goToQuery = () => {
  router.push('/query');
};

const goToAudit = () => {
  router.push('/audit');
};
</script>

<style scoped>
.home-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.welcome-card {
  width: 100%;
  max-width: 800px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  line-height: 1.8;
}

.card-content p {
  margin-bottom: 16px;
}

.card-content ul {
  margin-bottom: 20px;
  padding-left: 20px;
}

.card-content li {
  margin-bottom: 8px;
}

.action-buttons {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>