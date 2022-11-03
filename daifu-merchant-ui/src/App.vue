<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import music from "@/assets/img/yuyin.mp3";
import music1 from "@/assets/img/yuyin1.mp3";
import music2 from "@/assets/img/yuyin2.mp3";
import music3 from "@/assets/img/yuyin3.mp3";
export default {
  name: "App",
  data: function () {
    return {
      infos: "",
      audio:'',
      audio1:'',
      audio2:'',
      audio3:'',
      isshow:true,
    };
  },
  watch: {
    $route(to, from) {
      var that = this
      if (to.path == "/index") {
        that.getInfo();
      }
    },
  },
  created() {},
  methods: {
    getInfo() {
      this.$apiFun.info().then((res) => {
        this.infos = res.data;
        if (this.isshow == false) {
          return false
        }
        if (this.infos.balance < 50) {
          this.audio = new Audio(music1);
          this.audio.addEventListener("canplaythrough", () => {
            this.audio.play();
          });
        }
        if (this.infos.trxBalance < 50) {
          setTimeout(() => {
            this.audio1 = new Audio(music);
            this.audio1.addEventListener("canplaythrough", () => {
              this.audio1.play();
            });
          }, 1000);
        }
        if (this.infos.usdtPayTrxBalance < 50) {
          setTimeout(() => {
            this.audio2 = new Audio(music2);
            this.audio2.addEventListener("canplaythrough", () => {
              this.audio2.play();
            });
          }, 5000);
        }
        if (this.infos.usdtBalance < 50) {
          setTimeout(() => {
            this.audio3 = new Audio(music3);
            this.audio3.addEventListener("canplaythrough", () => {
              this.audio3.play();
            });
          }, 8000);
        }
        this.isshow = false
      });
    },
  },
};
</script>

<style lang="less">
html,
body,
#app {
  width: 100%;
  height: 100%;
}
.el-tab-pane,
.el-table tr {
  background-color: @boxBgColor;
}
.dialog-footer {
  display: flex;
  justify-content: center;
}
</style>
