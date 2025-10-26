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
.app-footer { /* 底部整体样式 */
  background: $secondary-color;
  color: $text-placeholder;
  margin-top: auto;
}

.footer-content { /* 主内容区域样式 */
  display: grid;
  grid-template-columns: 1fr 2fr; /* 两列布局，左侧1份宽度，右侧2份宽度 */
  gap: $spacing-xl;
  padding: $spacing-xl 0;

  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr; /* 单列布局 */
    gap: $spacing-lg;
  }
}

.footer-section { /* 关于我们区域样式 */
  .footer-title { /* 标题样式 */
    color: $bg-white;
    font-size: 20px;
    font-weight: 700;
    margin-bottom: $spacing-md;
  }

  .footer-desc { /* 描述文本样式 */
    line-height: 1.6;
    color: rgba($bg-white, 0.7);
  }
}

.footer-links { /* 链接区域样式 */
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 四列布局 */
  gap: $spacing-lg;

  @media (max-width: $breakpoint-sm) { /* 当屏幕宽度小于等于小型断点时应用以下样式 */
    grid-template-columns: repeat(2, 1fr); /* 两列布局 */
  }
}

.link-group { /* 每组链接的样式 */
  h4 {
    color: $bg-white;
    font-size: $font-size-base;
    font-weight: 600;
    margin-bottom: $spacing-md;
  }

  a {
    display: block; /* 使链接占据整行 */
    color: rgba($bg-white, 0.7);
    margin-bottom: $spacing-sm;
    transition: $transition-base; /* 添加过渡效果 */

    &:hover {
      color: $primary-color;
    }
  }
}

.footer-bottom {
  display: flex; /* 创建一个弹性布局容器 */
  align-items: center; /* 垂直居中对齐 */
  justify-content: space-between; /* 底部内容左右对齐 */
  padding: $spacing-lg 0;
  border-top: 1px solid rgba($bg-white, 0.1);

  @media (max-width: $breakpoint-sm) { /* 当屏幕宽度小于等于小型断点时应用以下样式 */
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

  a { /* 底部链接样式 */
    color: rgba($bg-white, 0.7);
    transition: $transition-base;

    &:hover {
      color: $primary-color;
    }
  }
}
</style>
