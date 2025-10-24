<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3 class="footer-title">星光影城</h3>
          <p class="footer-desc">
            专业的在线电影票务平台，为您提供最新电影资讯、在线选座购票服务。
          </p>
        </div>

        <div class="footer-links">
          <div class="link-group">
            <h4>购票指南</h4>
            <a href="#">在线选座</a>
            <a href="#">优惠活动</a>
            <a href="#">购票说明</a>
          </div>

          <div class="link-group">
            <h4>会员服务</h4>
            <a href="#">会员权益</a>
            <a href="#">积分兑换</a>
            <a href="#">会员卡</a>
          </div>

          <div class="link-group">
            <h4>帮助中心</h4>
            <a href="#">常见问题</a>
            <a href="#">联系我们</a>
            <a href="#">意见反馈</a>
          </div>

          <div class="link-group">
            <h4>关于我们</h4>
            <a href="#">公司简介</a>
            <a href="#">招聘信息</a>
            <a href="#">商务合作</a>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <div class="copyright">
          © 2024 星光影城 版权所有
        </div>
        <div class="footer-links-bottom">
          <a href="#">隐私政策</a>
          <a href="#">用户协议</a>
          <a href="#">法律声明</a>
        </div>
      </div>
    </div>
  </footer>
</template>
<script setup>
// Footer 组件逻辑
</script>
<style scoped lang="scss">
.app-footer {
  background: $secondary-color;
  color: $text-placeholder;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: $spacing-xl;
  padding: $spacing-xl 0;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
    gap: $spacing-lg;
  }
}

.footer-section {
  .footer-title {
    color: $bg-white;
    font-size: 20px;
    font-weight: 700;
    margin-bottom: $spacing-md;
  }

  .footer-desc {
    line-height: 1.6;
    color: rgba($bg-white, 0.7);
  }
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;

  @media (max-width: $breakpoint-sm) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.link-group {
  h4 {
    color: $bg-white;
    font-size: $font-size-base;
    font-weight: 600;
    margin-bottom: $spacing-md;
  }

  a {
    display: block;
    color: rgba($bg-white, 0.7);
    margin-bottom: $spacing-sm;
    transition: $transition-base;

    &:hover {
      color: $primary-color;
    }
  }
}

.footer-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-lg 0;
  border-top: 1px solid rgba($bg-white, 0.1);

  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
    gap: $spacing-md;
    text-align: center;
  }
}

.copyright {
  color: rgba($bg-white, 0.5);
}

.footer-links-bottom {
  display: flex;
  gap: $spacing-lg;

  a {
    color: rgba($bg-white, 0.7);
    transition: $transition-base;

    &:hover {
      color: $primary-color;
    }
  }
}
</style>
