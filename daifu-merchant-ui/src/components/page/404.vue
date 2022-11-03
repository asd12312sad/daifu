<template>
<div class="wrapper">
  <div class="main">
    <div class="main-info">
      <span class="main-info--emphasis">4</span>
      <span class="main-info--normal">0</span>
      <span class="main-info--emphasis">4</span>
    </div>
    <div class="main-btn-group">
      <el-row>
        <el-button @click="goBack" type="danger" class="pull-left">上一页</el-button>
        <router-link to="/">
          <el-button type="info" class="pull-right">首页</el-button>
        </router-link>
      </el-row>
    </div>
  </div>
</div>
</template>

<script>
  export default {
    name: "404",
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
    background-color: #323232;
  }
  .main {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 330px;
    height: 260px;
    margin-top: calc(~"-130px - 50px");
    margin-left: -165px;
  }
  .main-info--emphasis, .main-info--normal {
    font-weight: bold;
    font-style: italic;
  }
  .main-info--emphasis {
    color: @mainColor;
    font-size: 170px;
  }
  .main-info--normal {
    color: white;
    font-size: 150px;
  }
  .el-button {
    width: 120px;
  }
</style>
