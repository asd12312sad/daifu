<template>
  <div class="login-wrapper">
    <div class="login background-img-conf">
      <div class="login-form login-child">
        <div class="box-card">
          <div class="logo">
            <a href="https://www.paycloud.pro/" target="_blank"><img src="../../assets/img/logos.png" alt="" /></a>
          </div>
          <p class="links">
            <img src="../../assets/img/udun-web55.0e5a37c0.svg" alt="" />
            URL verification:<span>https://</span>www.paycloud.pro/admin/#/login
          </p>
          <div class="login-title">
            <div class="name">
              <span>创建新账户</span>
            </div>
          </div>
          <el-form :model="ruleForm" :rules="rules" ref="ruleForm">
            <el-form-item prop="email">
              <el-input v-model="ruleForm.email" placeholder="邮箱"></el-input>
            </el-form-item>
            <div class="emailcode">
              <el-form-item prop="code">
                <el-input v-model="ruleForm.code" placeholder="请输入验证码"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button class="login-btn" type="primary" :disabled="!emailcodeshow" @click="getemailcode()">发送验证码
                  <span v-show="!emailcodeshow" class="count">({{ count }}s)</span>
                </el-button>
              </el-form-item>
            </div>
            <div>
              <el-form-item>
                <el-input type="text" placeholder="请输入邀请码" v-model="ruleForm.invitationCode"></el-input>
              </el-form-item>
            </div>
             <div>
              <el-form-item prop="nickName">
                <el-input type="text" placeholder="请输入昵称" v-model="ruleForm.nickName"></el-input>
              </el-form-item>
            </div>

            <el-form-item prop="password">
              <el-input type="password" placeholder="请输入密码" v-model="ruleForm.password"></el-input>
            </el-form-item>
            <el-form-item prop="password2">
              <el-input type="password" placeholder="请确认密码" v-model="ruleForm.password2"
                @keyup.enter.native="submitForm('ruleForm')"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button class="login-btn" type="primary" @click="submitForm('ruleForm')">提交</el-button>
            </el-form-item>
          </el-form>
          <div class="login-btm">
            <div>
              <router-link to="/login" class="attach register">
                已有账号，去登录
              </router-link>
            </div>
            <p>©2022 PAY CLOUD</p>
          </div>
        </div>
      </div>
      <div class="login-img">
        <div class="title">
          <p>PAY CLOUD T1</p>
          <div>透过加密货币支付技术</div>
          <div>拓展您全球客户业务</div>
          <div>全球领先支付技术解决所有商家困扰!</div>
        </div>
        <img src="../../assets/img/login.026815f3.png" alt="" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "login",
  data: function () {
    return {
      addressoptions: [
        { area: "中国 +86", prefix: "0086" },
        { area: "中国香港 +852", prefix: "00852" },
        { area: "中国台湾 +886", prefix: "00886" },
        { area: "韩国 +82", prefix: "0082" },
        { area: "日本 +81", prefix: "0081" },
        { area: "越南 +84", prefix: "0084" },
        { area: "英国 +44", prefix: "0044" },
        { area: "德国 +49", prefix: "0049" },
        { area: "美国/加拿大 +1", prefix: "001" },
        { area: "菲律宾 +63", prefix: "0063" },
        { area: "新加坡 +65", prefix: "0065" },
        { area: "泰国 +66", prefix: "0066" },
        { area: "缅甸 +95", prefix: "0095" },
        { area: "马来西亚 +60", prefix: "0060" },
        { area: "柬埔寨 +855", prefix: "00855" },
        { area: "阿联酋 +971", prefix: "00971" },
        { area: "老挝 +856", prefix: "00856" },
        { area: "印度尼西亚 +62", prefix: "0062" },
        { area: "西班牙 +34", prefix: "0034" },
      ],
      btnidx: 0,
      ruleForm: {
        email: "",
        code: "",
        invitationCode:"",
        nickName:"",
        password: "",
        password2: "",
      },
      rules: {
        email: [{ required: true, message: "此项为必填项", trigger: "blur" }],
        code: [
          { required: true, message: "此项为必填项", trigger: "blur" },
        ],
        nickName:[
          { required: true, message: "此项为必填项", trigger: "blur" },
        ],
        password: [
          { required: true, message: "此项为必填项", trigger: "blur" },
        ],
        password2: [
          { required: true, message: "此项为必填项", trigger: "blur" },
        ],
      },
      timer: null, //计时器
      count: "", //倒计时
      emailcodeshow: true, //控制按钮
      emailcodetime: 60,
    };
  },
  methods: {
    //发送验证码
    getemailcode() {
      if (this.ruleForm.email == "") {
        this.$message.error("请填写邮箱");
        return false;
      }
      if (!this.timer) {
        this.count = this.emailcodetime;
        this.emailcodeshow = false;
        this.timer = setInterval(() => {
          if (this.count > 0 && this.count <= this.emailcodetime) {
            this.count--;
          } else {
            this.emailcodeshow = true;
            clearInterval(this.timer); // 清除定时器
            this.timer = null;
          }
        }, 1000);
      }
      this.$apiFun.registerCode({ email: this.ruleForm.email }).then((res) => {
        if (res.code == 200) {
          this.$message.success("验证码已发送");
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (!valid) {
          return false;
        }
        if (this.ruleForm.password != this.ruleForm.password2) {
          this.$message.error("两次密码不一致");
          return false;
        }
        this.$apiFun.register(this.ruleForm).then((res) => {
          // localStorage.setItem("googleSignUrl", this.res.data.googleSignUrl);
          if (res.code == 200) {
            this.$message.success("注册成功");
            this.$router.push("/login");
          } else {
            this.$message.error(res.msg);
          }
        });
      });
    },
  },

  created() {
    // // 实现页面底部波纹特效
    // let canvas = document.getElementById("canvas");
    // let ctx = canvas.getContext("2d");
    // canvas.width = canvas.parentNode.offsetWidth;
    // canvas.height = canvas.parentNode.offsetHeight;
    // console.log(canvas.width, canvas.height);
    // // 如果浏览器支持requestAnimFrame则使用requestAnimFrame否则使用setTimeout
    // window.requestAnimFrame = (function () {
    //   return (
    //     window.requestAnimationFrame ||
    //     window.webkitRequestAnimationFrame ||
    //     window.mozRequestAnimationFrame ||
    //     function (callback) {
    //       window.setTimeout(callback, 1000 / 60);
    //     }
    //   );
    // })();
    // // 波浪大小
    // let boHeight = 40; // canvas.height / 10;
    // let posHeight = canvas.height - 150; // canvas.height / 1.2;
    // // 初始角度为0
    // let step = 0;
    // // 定义三条不同波浪的颜色
    // let lines = [
    //   "rgba(69, 159, 117, 0.1)",
    //   "rgba(95, 170, 135, 0.6)",
    //   "rgba(69, 159, 117, 0.4)",
    // ];
    // function loop() {
    //   ctx.clearRect(0, 0, canvas.width, canvas.height);
    //   step++;
    //   // 画3个不同颜色的矩形
    //   for (let j = lines.length - 1; j >= 0; j--) {
    //     ctx.fillStyle = lines[j];
    //     //每个矩形的角度都不同，每个之间相差45度
    //     let angle = ((step + j * 70) * Math.PI) / 180; // 50
    //     let deltaHeight = Math.sin(angle) * boHeight;
    //     let deltaHeightRight = Math.cos(angle) * boHeight;
    //     ctx.beginPath();
    //     ctx.moveTo(0, posHeight + deltaHeight);
    //     ctx.moveTo(0, posHeight + deltaHeight);
    //     ctx.bezierCurveTo(
    //       canvas.width / 2,
    //       posHeight + deltaHeight - boHeight,
    //       canvas.width / 2,
    //       posHeight + deltaHeightRight - boHeight,
    //       canvas.width,
    //       posHeight + deltaHeightRight
    //     );
    //     ctx.lineTo(canvas.width, canvas.height);
    //     ctx.lineTo(0, canvas.height);
    //     ctx.lineTo(0, posHeight + deltaHeight);
    //     ctx.closePath();
    //     ctx.fill();
    //   }
    //   requestAnimFrame(loop);
    // }
    // loop();
  },
};
</script>

<style scoped lang="less">
.el-input,
.el-select {
  width: 360px !important;
}

/deep/ .el-input__inner {
  height: 42px !important;
  line-height: 56px !important;
}

.login-wrapper {
  height: 100%;
}

.login {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
}

.background-img-conf {
  background-repeat: no-repeat;
  background-size: cover;
  background-position: 50% 50%;
}

.login .login-child {
  width: 50%;
}

.box-card {
  position: relative;
  height: 100vh;

  .logo {
    display: inline-block;
    margin: 2.2vh 0 0 40px;

    a {
      display: inline-block;
      color: #333;
      text-decoration: none;
      transition: all 0.25s;
    }

    img {
      width: 140px;
      height: 40px;
    }
  }

  .links {
    text-align: center;
    width: 100%;
    height: 40px;
    margin-bottom: 3.7vh;
    background: rgba(250, 205, 95, 0.2);
    font-family: AvenirNext-Medium;
    font-size: 12px;
    color: #162630;
    letter-spacing: -0.2px;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      width: 10px;
      height: 10px;
      margin-right: 0.6%;
    }

    span {
      color: #05aa69;
    }
  }

  .login-title {
    margin: 7.4vh 0 0 120px;
    position: relative;

    .name {
      position: relative;
      margin-bottom: 8px;
      font-family: PingFangSC-Semibold;
      font-size: 32px;
      color: #162630;
      letter-spacing: -0.4px;
      line-height: 48px;
      font-weight: 600;

      span {
        display: inline-block;
      }
    }
  }

  .login__check__box {
    display: flex;
    align-items: center;
    margin: 0 0 16px;
    padding-left: 120px;

    .login__check {
      width: 78px;
      height: 42px;
      background: #fff;
      border: none;
    }

    .active {
      background: hsla(0, 3.9%, 80%, 0.91);
      border: 0 solid hsla(0, 3.9%, 80%, 0.91);
      border-radius: 8px;
    }
  }

  .el-form {
    padding-left: 120px;

    .el-button {
      width: 270px !important;
      height: 42px !important;
      border-radius: 8px;
      background-color: #05aa69;
      border-color: #05aa69;
      margin-top: 10px;
    }

    .emailcode {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 360px;

      .el-input {
        width: calc(360px - 120px) !important;
      }

      /deep/ .el-button {
        width: 120px !important;
        margin-top: 0 !important;
        background: #f5f7fa;
        color: #909399;
        border: none;
        font-size: 14px;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }

  .login-btm {
    position: absolute;
    bottom: 16px;
    left: 120px;

    div {
      margin-bottom: 24px;

      .attach {
        font-family: PingFangSC-Medium;
        font-size: 15px;
        color: #000;
        letter-spacing: -0.2px;
        line-height: 24px;
      }

      span {
        display: inline-block;
        margin: 0 12px;
        font-family: PingFangSC-Medium;
        font-size: 15px;
        color: rgba(22, 38, 48, 0.2);
        letter-spacing: -0.2px;
        line-height: 24px;
      }
    }

    p {
      font-family: AvenirNext-Medium;
      font-size: 13px;
      color: rgba(22, 38, 48, 0.6);
      letter-spacing: -0.1px;
      line-height: 20px;
      font-weight: 700;
    }
  }
}

.login-img {
  width: 50%;
  height: 100vh;
  background: #111;
  border: 1px solid rgba(22, 38, 48, 0);
  overflow: hidden;
  position: relative;

  .title {
    width: 500px;
    display: flex;
    justify-content: center;
    flex-direction: column;
    margin-top: 10%;
    position: absolute;
    left: 25%;

    p {
      margin-bottom: 8px;
      font-family: GTWalsheim-Bold;
      color: #fff;
      letter-spacing: -0.5px;
      font-size: 40px;
      line-height: 104px;
      font-weight: 700;
    }

    div {
      font-family: PingFangTC-Semibold;
      font-size: 25px;
      color: #fff;
      letter-spacing: -0.6px;
      line-height: 34px;
      font-weight: 500;
      text-align: left;
    }
  }

  img {
    display: block;
    width: 50%;
    position: absolute;
    bottom: -10%;
    left: 50%;
    transform: translateX(-50%);
  }
}

#canvas {
  width: 100%;
}

.login-main {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 320px;
  height: 370px;
  padding: 20px 35px;
  border: 1px solid #eee;
  margin: -185px 0 0 -160px;

  &::before,
  &::after {
    position: absolute;
    height: 12px;
    content: "";
  }

  &::before {
    left: 4px;
    right: 4px;
    top: -12px;
    z-index: 2;
    background-color: #f5f5f5;
  }

  &::after {
    left: 10px;
    right: 10px;
    top: -22px;
    z-index: 1;
    background-color: #f0f0f0;
  }
}

// .login-title {
//   padding-bottom: 15px;
//   border-bottom: 2px solid @mainColor;
//   margin: 15px 0 45px 0;
//   color: #555;
//   text-align: center;
//   font-size: 30px;
// }
.login-btn {
  width: 100%;
  height: 36px;
  margin-top: 30px;
  font-size: 16px;
}

.login-tip {
  color: #999;
  font-size: 12px;
  line-height: 30px;
}

.login-anim {
  position: absolute;
  bottom: 0;
  left: 0;
  z-index: -1;
}
</style>
