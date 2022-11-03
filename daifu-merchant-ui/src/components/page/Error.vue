<template>
  <div class="wrapper">
    <div class="main">
      <el-button class="btn btn-return J-page-return" @click="goBack">上一页</el-button>
      <router-link to="/">
        <el-button class="btn btn-refresh J-page-refresh">首页</el-button>
      </router-link>
    </div>
  </div>
</template>

<script>
  export default {
    name: "Error",
    methods: {
      goBack() {
        this.$router.go(-1);
      }
    }
  }
</script>

<style scoped lang="less">
  .wrapper {
    position: relative;
    height: 100%;
    background-color: #eee;
  }
  .main {
    position: relative;
    top: 50%;
    height: 300px;
    background: url(../../assets/img/error-page.png) center center no-repeat;
    transform: translateY(-50%);
  }
  .btn {
    position: absolute;
    border: 0;
    box-shadow: none !important;
    background-color: transparent;
    color: white !important;
    font-size: 18px;
  }
  .btn-return {
    left: calc(~"50% - 180px - 99px");
    width: 117px;
    height: 78px;
    padding: 22px 18px 0 0;
    background-image: url(../../assets/img/error-return-normal.png);
    &:hover {
      background-image: url(../../assets/img/error-return-active.png);
    }
  }
  .btn-refresh {
    right: calc(~"50% - 180px - 99px");
    width: 99px;
    height: 64px;
    padding: 8px 5px 0 0;
    background-image: url(../../assets/img/error-refresh-normal.png);
    &:hover {
      background-image: url(../../assets/img/error-refresh-active.png);
    }
  }
</style>
