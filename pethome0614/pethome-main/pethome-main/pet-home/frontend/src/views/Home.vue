<template>
  <div class="home-page">
    <!-- 轮播图区域 - 纯图片 -->
    <div class="banner-section">
      <el-carousel :interval="4000" height="800px" indicator-position="outside">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <div class="banner-item" :style="{ backgroundImage: `url(${item.image})` }"></div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 品牌介绍卡片（轮播图后面，7张图片前面） -->
    <div class="brand-intro-section">
      <div class="container">
        <div class="brand-card" @click="showBrandDialog = true">
          <div class="brand-icon">🐾</div>
          <div class="brand-text">
            <h3>宠物健康，从日常护理开始</h3>
            <p>点击了解品牌故事 →</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 特色图片展示区（7张图片纵向排列） -->
    <div class="feature-grid-section">
      <div class="container">
        <div class="feature-grid">
          <div class="feature-item" v-for="(item, index) in featureImages" :key="index">
            <img :src="item.image" :alt="'特色图片' + (index + 1)">
          </div>
        </div>
      </div>
    </div>

    <!-- 品牌理念展示 -->
    <div class="philosophy-section">
      <div class="container">
        <div class="philosophy-grid">
          <div class="philosophy-item" v-for="(item, index) in philosophies" :key="index">
            <div class="philosophy-icon">{{ item.icon }}</div>
            <h4>{{ item.title }}</h4>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 品牌介绍弹窗 -->
    <el-dialog
        title="🐾 宠物家品牌故事"
        :visible.sync="showBrandDialog"
        width="700px"
        :before-close="handleDialogClose"
        class="brand-dialog"
    >
      <div class="dialog-content">
        <p>宠物家是深受宠友信赖的品牌，持续引领宠物健康护理行业标准化建设，在行业领先实现了数字化体验，并在超一线城市实现护理型直营门店的快速规模化发展，用户数量及服务体量遥遥领先。</p>
        <br />
        <p>宠物健康，是长久、高质量人宠陪伴的前提，宠物家倡导"健康，从日常护理开始"，专注于宠物健康护理服务、保健服务、疾病预防服务、日用全品类商品零售、宠物药品零售、诊疗等业务，践行让宠物身心健康、延缓衰老，宠友快乐养宠的服务理念。</p>
        <br />
        <p>作为国内领先实现"宠物产业互联网化"的品牌，宠物家着力数字化建设，线上强效率，线下重体验。并依托于宠物个性化数据，深度挖掘用户需求，提供精准健康护理服务，定制个性化养宠解决方案，是率先实现"千宠千面"的数字化企业，满足宠友对于精细化科学养宠的追求，并深入影响用户心智，为品牌伙伴在产品研发及市场投入过程中提供精准赋能。</p>
        <br />
        <p>推出"付费会员制"，为Pet'em Prime宠物家会员提供专属会员服务。目前年提供超200万只次满意服务，直营护理型门店覆盖北京深圳上海核心区域，拥有可持续发展的人才建设体系，目前专业宠物护理师近2000名。创新研发定制化护理项目，目前仅单品种宠物即拥有数十项包含日常保养、抗衰老在内的健康护理SKU，实现了规范化、规模化、创新性发展。我们不断拓展边际，宠大夫药房运营中、宠大夫诊疗等即将启动，将致力于提供全生命周期健康养宠解决方案。</p>
        <br />
        <p style="font-weight: 600; color: #ff9f43;">我们将一直秉承"选择宠物家，尽可放心"的品牌承诺，让宠友轻松养宠，快乐陪伴。</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      showBrandDialog: false,
      banners: [
        { image: '/images/home-bg1.png' },
        { image: '/images/home-bg2.png' },
        { image: '/images/home-bg3.png' },
        { image: '/images/home-bg4.png' },
        { image: '/images/home-bg5.png' },
        { image: '/images/home-bg6.png' }
      ],
      // 7张特色图片
      featureImages: [
        { image: '/images/home-zs1.png' },
        { image: '/images/home-zs2.png' },
        { image: '/images/home-zs3.png' },
        { image: '/images/home-zs4.png' },
        { image: '/images/home-zs5.png' },
        { image: '/images/home-zs6.png' },
        { image: '/images/home-zs7.png' }
      ],
      philosophies: [
        {
          icon: '🏥',
          title: '专业护理',
          desc: '标准化服务流程，近2000名专业护理师'
        },
        {
          icon: '📊',
          title: '数字化体验',
          desc: '线上强效率，线下重体验，千宠千面'
        },
        {
          icon: '🏪',
          title: '直营门店',
          desc: '覆盖北京、深圳、上海核心区域'
        },
        {
          icon: '💎',
          title: '会员专享',
          desc: '付费会员制，享受专属服务权益'
        }
      ]
    }
  },
  methods: {
    handleDialogClose() {
      this.showBrandDialog = false
    }
  }
}
</script>

<style scoped>
.home-page {
  background: #faf8f5;
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* ===== 轮播图 - 纯图片 ===== */
.banner-section {
  margin-bottom: 0;
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

/* ===== 品牌介绍卡片（轮播图后面） ===== */
.brand-intro-section {
  padding: 20px 0;
  background: #fff;
  border-bottom: 1px solid #f0ebe5;
}

.brand-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 30px;
  background: linear-gradient(135deg, #fff8f0, #fff0e0);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #ffd4a0;
}

.brand-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(255,159,67,0.2);
}

.brand-icon {
  font-size: 48px;
  flex-shrink: 0;
}

.brand-text h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 4px;
}

.brand-text p {
  color: #ff9f43;
  font-size: 14px;
  margin: 0;
}

/* ===== 特色图片展示区（7张图片纵向排列） ===== */
.feature-grid-section {
  padding: 20px 0;
  background: #fff;
  border-bottom: 1px solid #f0ebe5;
}

.feature-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  width: 100%;
  max-width: 1500px;
  margin: 0 auto;
  border-radius: 14px;
  overflow: hidden;
  background: #f5f0eb;
  transition: transform 0.3s, box-shadow 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.feature-item:hover {
  transform: scale(1.008);
  box-shadow: 0 8px 24px rgba(255,159,67,0.15);
}

.feature-item img {
  width: 100%;
  height: auto;
  display: block;
}

/* ===== 品牌理念 ===== */
.philosophy-section {
  padding: 48px 0;
  background: #faf8f5;
}

.philosophy-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.philosophy-item {
  background: #fff;
  border-radius: 16px;
  padding: 30px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  transition: all 0.3s;
}

.philosophy-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255,159,67,0.12);
}

.philosophy-icon {
  font-size: 40px;
  margin-bottom: 12px;
}

.philosophy-item h4 {
  font-size: 18px;
  color: #333;
  margin-bottom: 8px;
}

.philosophy-item p {
  font-size: 14px;
  color: #888;
  margin: 0;
  line-height: 1.6;
}

/* ===== 弹窗样式 ===== */
.brand-dialog >>> .el-dialog {
  border-radius: 20px;
}

.brand-dialog >>> .el-dialog__header {
  border-bottom: 1px solid #f0ebe5;
  padding: 20px 24px;
}

.brand-dialog >>> .el-dialog__title {
  font-size: 20px;
  color: #333;
}

.dialog-content {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
  font-size: 14px;
  line-height: 1.8;
  color: #555;
}

.dialog-content::-webkit-scrollbar {
  width: 4px;
}

.dialog-content::-webkit-scrollbar-thumb {
  background: #ffd4a0;
  border-radius: 2px;
}

/* ===== 响应式 ===== */
@media (max-width: 992px) {
  .philosophy-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .feature-item {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .banner-section {
    height: 300px;
  }

  .banner-item {
    height: 300px;
  }

  .brand-card {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }

  .philosophy-grid {
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }

  .feature-grid {
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .banner-section {
    height: 220px;
  }

  .banner-item {
    height: 220px;
  }

  .philosophy-grid {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }

  .philosophy-item {
    padding: 20px 16px;
  }

  .philosophy-icon {
    font-size: 30px;
  }

  .feature-grid {
    gap: 10px;
  }
}
</style>