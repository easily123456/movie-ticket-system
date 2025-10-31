<template>
  <div class="not-found page-center">
    <div class="card">
      <div class="status">404</div>
      <h2 class="title">页面未找到</h2>
      <p class="desc">抱歉，你访问的页面不存在或已被移除。</p>
      <div class="actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button plain @click="$router.back()">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const goHome = () => router.push('/')
</script>

<style scoped lang="scss">
.page-center {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 160px);
  padding: 40px 16px;
}
.card {
  text-align: center;
  background: $bg-white;
  padding: 48px 36px;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(18, 22, 26, 0.06);
  max-width: 520px;
  width: 100%;
}
.status {
  font-size: 64px;
  font-weight: 700;
  color: $danger-color;
}
.title {
  margin: 12px 0 8px;
  font-size: 22px;
  color: $text-primary;
}
.desc {
  color: $text-secondary;
  margin-bottom: 20px;
}
.actions > * {
  margin: 0 8px;
}

@media (max-width: 480px) {
  .status { font-size: 48px; }
  .card { padding: 32px 20px; }
}
</style>
