<template>
  <div class="app-container">
    <el-config-provider>
      <el-container>
        <el-header height="60px">
          <div class="header-content">
            <h1>安全身份信息查询系统</h1>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
        <el-footer height="40px">
          <div class="footer-content">
            <p>© 2025 全链路安全加密系统 - 保护您的数据安全</p>
          </div>
        </el-footer>
      </el-container>
    </el-config-provider>
  </div>
</template>

<script setup>
// App.vue 不需要特殊逻辑，只作为布局容器
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Roboto', sans-serif;
  background-color: #f5f7fa;
  color: #333;
}

.app-container {
  min-height: 100vh;
}

.el-header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.el-main {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 100px);
}

.el-footer {
  background-color: #f5f7fa;
  border-top: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #909399;
}

.footer-content {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}
</style>