<template>
  <div class="home">
    <!-- <div class="stat">
      <div class="stat-user">
        <div class="stat-user__title">后台管理系统模板</div>
        <div class="stat-user__detail">
          <p>欢迎您，{{ username }}</p>
          <p>当前时间：{{ nowTime }}</p>
        </div>
        <img class="stat-user__portrait" src="static/portrait.jpg" alt="" />
      </div>
      <div class="stat-info">
        <el-row :gutter="20" v-for="(info, index) in stat" :key="index">
          <el-col :span="8" v-for="(item, index) in info" :key="index">
            <div class="stat-info__item">
              <div
                class="stat-info__icon"
                :style="{ 'background-color': item.bgColor }"
              >
                <i :class="item.icon"></i>
              </div>
              <div class="stat-info__detail">
                <p class="stat-info__total">{{ item.total }}</p>
                <p class="stat-info__title">{{ item.title }}</p>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <el-row :gutter="20" class="margin-t-20 list">
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span><i class="el-icon-tickets margin-r-5"></i>待办事项</span>
            <i class="el-icon-plus" @click="addNewTodoItem" title="新增"></i>
          </div>
          <p v-for="(item, i) in todoList" :key="i">
            <el-checkbox v-model="item.isChecked"></el-checkbox>
            <span :class="{ active: item.isChecked }"
              >{{ i + 1 > 9 ? i + 1 : "0" + (i + 1) }}-{{ item.title }}</span
            >
          </p>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span><i class="el-icon-news margin-r-5"></i>最新消息</span>
          </div>
          <p v-for="(item, i) in latestNewList" :key="i">
            <span class="latest-new-list__time"
              ><i class="el-icon-time margin-r-5"></i>{{ item.time }}：</span
            >
            <span>{{ item.title }}</span>
          </p>
        </el-card>
      </el-col>
    </el-row> -->
    <div class="home-main">
      <div class="home-left">
        <el-row>
          <el-col :span="24">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>资源监控</span>
              </div>
              <div class="version-message">
                <div class="version-left" data-position="right" data-step="1">
                  <!-- <div class="title">VIP</div> -->
                  <div class="version-details">
                    <!-- <p>
                      代收地址：<span>{{ infos.trxAddress }}</span>
                    </p> -->
                    <!-- <p>
                      代付地址：：<span>{{ infos.usdtPayAddress }}</span>
                    </p> -->
                    <p>
                      手续费钱包余额：<span>{{ infos.balance }}</span>
                    </p>
                    <p>
                      代收钱包TRX矿工费余额：<span>{{ infos.trxBalance }}</span>
                    </p>
                    <p>
                      代付TRX矿工费余额：<span>{{
                        infos.usdtPayTrxBalance
                      }}</span>
                    </p>
                    <p>
                      代付USDT余额：<span>{{ infos.usdtBalance }}</span>
                    </p>
                    <!-- <p>USDT余额：<span>{{infos.usdtBalance}}</span></p>
                    <p>TRX余额：<span>{{infos.trxBalance}}</span></p> -->
                  </div>
                  <!-- <div class="operate">
                    <div class="btn-upgrade">付费升级</div>
                  </div> -->
                  <div class="btn-angle">
                    <img
                      src="../../assets/img/udun-web36.f31354f4.svg"
                      alt=""
                    />
                  </div>
                </div>
                <div class="version-right">
                  <div class="detail">
                    <div class="weight">
                      {{ xinxis.collectCount }}
                      <!-- <span>/100</span> -->
                    </div>
                    <p>代收总数</p>
                  </div>
                  <div class="detail" data-step="5">
                    <div class="weight">
                      {{ xinxis.payCount }}
                      <!-- <span>/5</span> -->
                    </div>
                    <p>代付总数</p>
                    <!-- <span class="btn">配置</span> -->
                  </div>
                  <div class="detail">
                    <div class="weight">
                      {{ infos.fee * 100 + '%'}}
                      <!-- <span>/1</span> -->
                    </div>
                    <p>费率%</p>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-row class="threeka">
          <el-col :span="8">
            <el-card>
              <div slot="header" class="clearfix">
                <span>成功数量</span>
              </div>
              <div class="detail-item">
                <div>
                  <p>
                    <a class="">{{ xinxis.collectEndCount }}</a>
                  </p>
                  <span>代收成功</span>
                </div>
                <div>
                  <p>
                    <a class="">{{
                      xinxis.payEndCount
                    }}</a>
                  </p>
                  <span>代付成功</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div slot="header" class="clearfix">
                <span>回调成功数</span>
              </div>
              <div class="detail-item">
                <div>
                  <p>
                    <a class="">{{ xinxis.collectReturnCount }}</a>
                  </p>
                  <span>代收回调</span>
                </div>
                <div>
                  <p>
                    <a class="">{{ xinxis.payReturnCount }}</a>
                  </p>
                  <span>代付回调</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card>
              <div slot="header" class="clearfix">
                <span>金额</span>
              </div>
              <div class="detail-item">
                <div>
                  <p>
                    <a class="">{{
                      xinxis.collectSumAmount ? xinxis.collectSumAmount : 0
                    }}</a>
                  </p>
                  <span>代收金额</span>
                  <!-- <el-button type="primary"> 立即邀请 </el-button> -->
                </div>
                <div>
                  <p>
                    <a class="">{{
                      xinxis.paySumAmount ? xinxis.paySumAmount : 0
                    }}</a>
                  </p>
                  <span>代付金额</span>
                  <!-- <el-button type="primary"> 立即邀请 </el-button> -->
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>访问流程</span>
              </div>
              <div data-v-b8582502="" class="process-main">
                <div data-v-b8582502="">
                  <!-- <img
                    data-v-b8582502=""
                    src="../../assets/img/udun-web59.42042406.svg"
                    alt=""
                  /> -->
                  <img
                    data-v-b8582502=""
                    src="../../assets/img/udun-web60.643e855e.svg"
                    alt=""
                  />
                  <p data-v-b8582502="">选择财务管理</p>
                  <span data-v-b8582502=""
                    >充值/代收/代付（如有需要）所有矿工费请充值大于100TRX</span
                  >
                </div>
                <div data-v-b8582502="">
                  <img
                    data-v-b8582502=""
                    src="../../assets/img/udun-web61.f85a0134.svg"
                    alt=""
                  />
                  <p data-v-b8582502="">配置API</p>
                  <span data-v-b8582502=""
                    >选择API文档，接入代收/代付API
                    【设置】代收归集地址/代付/代收API回调地址</span
                  >
                </div>
                <!-- <div data-v-b8582502="">
                  <img
                    data-v-b8582502=""
                    src="../../assets/img/udun-web61.f85a0134.svg"
                    alt=""
                  />
                  <p data-v-b8582502="">下载客户端</p>
                  <span data-v-b8582502="">PAY CLOUD企业领袖实现全终端</span>
                </div> -->
                <div data-v-b8582502="">
                  <img
                    data-v-b8582502=""
                    src="../../assets/img/udun-web62.336eeb76.svg"
                    alt=""
                  />
                  <p data-v-b8582502="">测试</p>
                  <span data-v-b8582502=""
                    >进入【代收订单】/【代付订单】测试</span
                  >
                </div>
                <div data-v-b8582502="">
                  <img
                    data-v-b8582502=""
                    src="../../assets/img/udun-web63.c854c65c.svg"
                    alt=""
                  />
                  <p data-v-b8582502="">上线</p>
                  <span data-v-b8582502="">即可开展您的业务</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <div class="home-right">
        <div>
          <div class="right-main">
            <!-- <img src="../../assets/img/udun-web32.1ff803a1.png" alt="" />
            <div class="yinying">
              <p>PAY CLOUD T1</p>
              <div>全球领先支付技术解决所有商家困扰！</div>
            </div> -->
            <img src="../../assets/img/123123.png" alt="" />
          </div>
        </div>
        <div>
          <el-row>
            <el-col :span="24">
              <el-card class="box-card">
                <div slot="header" class="clearfix">
                  <span>接入文档</span>
                </div>
                <div class="details">
                  <div class="server border-btm" v-if="roleHaveKey">
                    <div style="margin-bottom: 10px">API Key:</div>
                    <div v-show="keyshow" style="margin-bottom: 10px">
                      {{ apiKey }}
                    </div>
                    <el-button type="default" @click="dialogVisible1 = true"
                      >点击获取</el-button
                    >
                  </div>
                  <!-- <div class="link">
                    <a
                      href="https://www.uduncloud.com/geteway-interface"
                      target="_blank"
                      ><i class="el-icon-link"></i><span>網關接口</span></a
                    ><a
                      href="https://www.uduncloud.com/sdk-download"
                      target="_blank"
                      ><i class="icon-udun-web34"></i><span>SDK下載</span></a
                    ><a
                      href="https://www.uduncloud.com/geteway-interface"
                      target="_blank"
                      ><i class="icon-udun-web33"></i><span>參考資料</span></a
                    >
                  </div> -->
                  <div class="server">
                    <div>网关服务器</div>
                    <p>
                      <span>
                        <!-- <p>節點：</p> -->
                        <span>https://gateway.paycloud.pro</span></span
                      ><button
                        type="button"
                        class="
                          el-button
                          copy-btn
                          el-button--text el-button--small
                        "
                        data-clipboard-target="#ip-0"
                      >
                        <span @click="copy('https://gateway.paycloud.pro')"
                          >复制</span
                        >
                      </button>
                    </p>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    <el-dialog
      title="获取API Key"
      :visible.sync="dialogVisible1"
      width="30%"
      :show-close="false"
    >
      <div style="display: flex; justify-content: center">
        <el-input v-model="Googlecode" placeholder="谷歌验证码"></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="getkey">确 定</el-button>
      </span>
    </el-dialog>
    <!-- <audio autoplay="autoplay"
    controls="controls"
    preload="auto"
    src="../../assets/img/yuyin.mp3">
</audio> -->
  </div>
</template>

<script>
import Util from "@/assets/js/util";
let todoList = [],
  latestNewList = [];
for (let i = 0; i < 10; i++) {
  todoList.push({
    title: `今天需要做的待办事项咯~~~`,
    isChecked: false,
  });
  latestNewList.push({
    time: new Date(new Date().getTime() + i * 24 * 3600 * 1000).Format(
      "yyyy-MM-dd"
    ),
    title: `今日的最新新闻来咯~~~`,
  });
}
export default {
  name: "Home",
  data() {
    return {
      stat: [
        [
          {
            icon: "el-icon-service",
            title: "公司总员工数",
            total: 198397,
            bgColor: "#ebcc6f",
          },
          {
            icon: "el-icon-location-outline",
            title: "客户分布区域",
            total: 19,
            bgColor: "#3acaa9",
          },
          {
            icon: "el-icon-star-off",
            title: "收货好评",
            total: 190857,
            bgColor: "#67c4ed",
          },
        ],
        [
          {
            icon: "el-icon-edit-outline",
            title: "历史订单数",
            total: 9397,
            bgColor: "#af84cb",
          },
          {
            icon: "el-icon-share",
            title: "产品总转发数量",
            total: 9097,
            bgColor: "#67c4ed",
          },
          {
            icon: "el-icon-goods",
            title: "产品总数",
            total: 397,
            bgColor: "#ebcc6f",
          },
        ],
      ],
      username: localStorage.getItem("username"),
      nowTime: new Date().Format("yyyy-MM-dd hh:mm:ss"),
      todoList,
      latestNewList,
      infos: "",
      roleHaveKey: false,
      keyshow: false,
      xinxis: "",
      jingaoshow: true,
      dialogVisible1: false,
      Googlecode: "",
      apiKey: "",
    };
  },
  created() {
    this.getInfo();
    this.getxinxi();
  },
  methods: {
    getkey() {
      this.$apiFun.ssoapiKey({ googleSign: this.Googlecode }).then((res) => {
        if (res.code == 200) {
          // this.apiKey = res.data.apiKey;
          this.keyshow = false;
          this.dialogVisible1 = false;
          this.$message.success(res.msg);
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    getInfo() {
      var that = this;
      this.$apiFun.info().then((res) => {
        this.infos = res.data;
        for (let index = 0; index < res.data.merchantMenuList.length; index++) {
          var menu = res.data.merchantMenuList[index];
          if ("/APIKEY" === menu.path) {
            this.roleHaveKey = true;
          }
        }
        localStorage.setItem("id", res.data.id);
        localStorage.setItem("email", res.data.account);
        localStorage.setItem("name", res.data.name);
        if (this.jingaoshow == true) {
          if (this.infos.balance < 50) {
            this.$notify({
              title: "警告",
              message: "您的手续费钱包余额不足请充值！",
              type: "warning",
            });
          }
          setTimeout(() => {
            if (this.infos.trxBalance < 50) {
              this.$notify({
                title: "警告",
                message: "您的代收钱包TRX矿工费余额不足请充值！",
                type: "warning",
              });
            }
          }, 500);

          setTimeout(() => {
            if (this.infos.usdtPayTrxBalance < 50) {
              this.$notify({
                title: "警告",
                message: "您的代付TRX矿工费余额不足请充值！",
                type: "warning",
              });
            }
          }, 1000);

          setTimeout(() => {
            if (this.infos.usdtBalance < 50) {
              this.$notify({
                title: "警告",
                message: "您的代付USDT余额不足请充值！",
                type: "warning",
              });
            }
          }, 1500);
        }
        this.jingaoshow = false;
        setTimeout(() => {
          that.getInfo();
        }, 5000);
      });
    },
    getxinxi() {
      var that = this;
      this.$apiFun.homesum().then((res) => {
        this.xinxis = res.data;
        setTimeout(() => {
          that.getxinxi();
        }, 5000);
      });
    },
    setNowTime() {
      setInterval(() => {
        this.nowTime = new Date().Format("yyyy-MM-dd hh:mm:ss");
      }, 1000);
    },
    addNewTodoItem() {
      this.$prompt("请输入待办事项主题", "新增待办事项", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      })
        .then(({ value }) => {
          this.$message({
            type: "success",
            message: "新增待办事项成功",
          });
          this.todoList.unshift({
            title: value,
            isChecked: false,
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "取消新增待办事项",
          });
        });
    },
  },
  mounted() {
    this.setNowTime();
  },
};
</script>

<style scoped lang="less">
/deep/ .el-dialog {
  border-radius: 10px;
}
.home-main {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  .home-left {
    width: 100%;
    margin-right: 16px;
    // height: 300px;
    // background: #fff;
    border-radius: 8px;
    .el-card {
      background-color: #fff;
      .version-message {
        display: flex;
        align-items: flex-start;
        justify-content: flex-start;
        .version-left {
          width: 420px;
          height: 192px;
          padding: 16px 24px;
          margin-right: 16px;
          position: relative;
          border-radius: 8px;
          background: #fcf3ea;
          .title {
            margin-bottom: 16px;
            font-family: PingFangSC-Medium;
            font-size: 17px;
            color: #824b46;
            letter-spacing: -0.3px;
            line-height: 28px;
            font-weight: 700;
          }
          .version-details {
            position: absolute;
            left: 24px;
            top: 20px;
            z-index: 100;
            p {
              margin-bottom: 4px;
              font-family: PingFangSC-Regular;
              font-size: 13px;
              color: rgba(130, 75, 70, 0.6);
              letter-spacing: -0.1px;
              line-height: 20px;
              span {
                font-weight: 700;
              }
            }
          }
          .operate {
            position: absolute;
            top: 14px;
            right: 14px;
            display: flex;
            align-items: center;
            justify-content: flex-start;
            text-align: center;
            line-height: 32px;
            color: #fff;
            font-size: 13px;
            .btn-upgrade {
              line-height: 20px;
              padding: 3px 12px;
              background: #824b46;
              border-radius: 16px;
              cursor: pointer;
              border: 1px solid #824b46;
            }
          }
          .btn-angle {
            z-index: 1;
            position: absolute;
            bottom: 0;
            right: 0;
            width: 176px;
            height: 111px;
            img {
              width: 176px;
              height: 111px;
            }
          }
        }
        .version-right {
          display: flex;
          align-items: center;
          justify-content: flex-start;
          flex: 1;
          .detail {
            cursor: pointer;
            width: 33.3%;
            height: 192px;
            flex-direction: column;
            display: flex;
            align-items: center;
            justify-content: flex-start;
            .weight {
              margin-top: 40px;
              margin-bottom: 28px;
              font-family: DINAlternate-Bold;
              font-size: 24px;
              color: #162630;
              letter-spacing: -0.4px;
              text-align: center;
              font-weight: 700;
              span {
                font-size: 14px;
                color: #162630;
                letter-spacing: -0.4px;
                text-align: center;
                font-weight: 400;
              }
            }
            p {
              font-family: PingFangSC-Regular;
              font-size: 13px;
              color: rgba(22, 38, 48, 0.6);
              letter-spacing: -0.1px;
              text-align: center;
              line-height: 20px;
            }
            .btn {
              margin-top: 8px;
              padding: 0 20px;
              height: 32px;
              line-height: 32px;
              text-align: center;
              border: 1px solid #05aa69;
              border-radius: 28px;
              color: #05aa69;
              font-size: 13px;
              display: block;
            }
          }
        }
      }
      .detail-item {
        height: 240px;
        display: flex;
        align-items: center;
        justify-content: center;
        div {
          height: 100%;
          flex: 1;
          text-align: center;
          display: flex;
          flex-direction: column;
          justify-content: flex-start;
          align-items: center;
          cursor: pointer;

          p {
            margin-top: 46px;
            font-family: DINAlternate-Bold;
            font-size: 30px;
            color: #162630;
            letter-spacing: -0.4px;
            line-height: 72px;
            font-weight: 700;
            margin-bottom: 8px;
            a {
              display: inline-block;
              color: #333;
              text-decoration: none;
              transition: all 0.25s;
            }
          }
          span {
            font-family: PingFangSC-Regular;
            font-size: 13px;
            color: rgba(22, 38, 48, 0.6);
            letter-spacing: -0.1px;
            line-height: 20px;
          }
          &:hover {
            background: rgba(5, 170, 105, 0.04);
            p {
              color: #05aa69 !important;
              a {
                color: #05aa69 !important;
              }
            }
            span {
              color: #05aa69 !important;
            }
          }
          .el-button--primary {
            background: #fff;
            color: #05aa69;
            border-radius: 30px;
            margin-top: 10px;
          }
        }
      }
    }
    .threeka {
      .el-col-8 {
        padding-left: 20px;
        &:nth-child(1) {
          padding-left: 0;
        }
      }
    }
    .process-main {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      div {
        height: 224px;
        flex: 1;
        flex-direction: column;
        display: flex;
        align-items: center;
        img {
          display: block;
          width: 48px;
          height: 48px;
          margin: 32px 0 24px 0;
        }
        p {
          margin-bottom: 8px;
          font-family: PingFangSC-Medium;
          font-size: 15px;
          color: #162630;
          letter-spacing: -0.2px;
          line-height: 24px;
          font-weight: 700;
        }
        span {
          max-width: 128px;
          font-size: 13px;
          font-family: PingFangSC-Regular;
          color: rgba(22, 38, 48, 0.6);
          letter-spacing: -0.1px;
          text-align: center;
          line-height: 20px;
        }
      }
    }
  }
  .home-right {
    width: 376px;
    height: 291px;
    border-radius: 8px;
    .el-card {
      background-color: #ffffff;
    }
    .right-main {
      margin-bottom: 10px;
      background: #242424;
      border-radius: 8px;
      width: 376px;
      height: 291px;
      cursor: pointer;
      text-align: center;
      img {
        // margin-top: 24px;
        vertical-align: middle;
        display: inline-block;
        width: 100%;
        height: 100%;
        // width: 289px;
        // height: 222px;
      }
      .yinying {
        backdrop-filter: blur(8px);
        text-align: left;
        margin: 0 auto;
        margin-top: -56px;
        width: 328px;
        padding: 12px 16px;
        background: hsla(0, 0%, 100%, 0.2);
        border-radius: 8px;
        z-index: 100;
        p {
          font-family: AvenirNext-Bold;
          font-size: 17px;
          color: #fff;
          letter-spacing: -0.3px;
          line-height: 28px;
          font-weight: 700;
        }
        div {
          opacity: 0.6;
          font-family: AvenirNext-Medium;
          font-size: 13px;
          color: #fff;
          letter-spacing: -0.1px;
          line-height: 20px;
          font-weight: 500;
        }
        span {
          display: inline-block;
          position: absolute;
          background: #05aa69;
          top: 20px;
          right: 16px;
          padding: 6px 16px;
          border-radius: 28px;
          color: #fff;
          font-size: 13px;
        }
      }
    }
    .details {
      .border-btm {
        border-bottom: 1px solid #f7f7f7;
      }
      .server {
        margin: 24px 8px 8px 8px;
        padding-bottom: 24px;
        div {
          font-family: PingFangSC-Regular;
          font-size: 13px;
          color: rgba(22, 38, 48, 0.6);
          letter-spacing: -0.1px;
          line-height: 20px;
        }
        .el-button {
          display: inline-block;
          line-height: 1;
          white-space: nowrap;
          cursor: pointer;
          background: #fff;
          border: 1px solid #dcdfe6;
          border-color: #dcdfe6;
          color: #606266;
          text-align: center;
          box-sizing: border-box;
          outline: none;
          margin: 0;
          -webkit-transition: 0.1s;
          transition: 0.1s;
          font-weight: 500;
          padding: 12px 20px;
          font-size: 14px;
          border-radius: 4px;
          font-size: 15px;
          color: #fff;
          background: #162630;
          border: 0 solid #162630;
          border-radius: 28px;
          padding: 10px;
        }
      }
      .link {
        margin: 0 8px 0 8px;
        padding-bottom: 16px;
        border-bottom: 1px solid #f7f7f7;
        a {
          display: flex;
          justify-content: flex-start;
          align-items: center;
          margin: 8px 0;
          cursor: pointer;
          font-family: PingFangSC-Regular;
          font-size: 15px;
          color: #162630;
          letter-spacing: -0.2px;
          line-height: 24px;
          i {
            margin-top: 4px;
            margin-right: 16px;
            font-size: 20px;
            color: #05aa69;
          }
        }
      }
      .server {
        margin: 24px 8px 8px 8px;
        padding-bottom: 24px;
        div {
          font-family: PingFangSC-Regular;
          font-size: 13px;
          color: rgba(22, 38, 48, 0.6);
          letter-spacing: -0.1px;
          line-height: 20px;
        }
        p {
          margin-top: 8px;
          font-family: AvenirNext-Regular;
          font-size: 15px;
          color: #162630;
          letter-spacing: -0.2px;
          line-height: 24px;
          span {
          }
        }
        .copy-btn {
          margin-left: 10px;
          color: #05aa69;
          background: #fff;
          border: 0 solid #162630;
          border-radius: 0;
        }
      }
    }
  }
}
.home {
  height: calc(~"100% - 40px");
}
.stat {
  display: flex;
  height: 230px;
}
.stat-user {
  position: relative;
  width: 300px;
  background-color: @boxBgColor;
  box-shadow: 2px 2px 5px #ccc;
}
.stat-user__title {
  height: 100px;
  background-color: @mainColor;
  color: white;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  line-height: 70px;
}
.stat-user__detail {
  padding-top: 50px;
  color: #666;
  font-size: 13px;
  text-align: center;
}
.stat-user__portrait {
  position: absolute;
  top: 50%;
  left: 50%;
  .circle(80px);
  border: 3px solid white;
  box-shadow: 0 0 5px #ccc;
  margin-top: -55px;
  margin-left: -40px;
}
.stat-info {
  flex: 1;
  margin-left: 20px;
}
.el-row + .el-row {
  margin-top: 10px;
}
.stat-info__item {
  display: flex;
  height: 110px;
  box-shadow: 2px 2px 5px #ccc;
  background-color: @boxBgColor;
}
.stat-info__icon {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 80px;
  color: white;
  [class*="el-icon"] {
    font-size: 50px;
  }
}
.stat-info__detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.stat-info__total {
  color: #0085d0;
  font-size: 27px;
  font-weight: bold;
}
.stat-info__title {
  color: #666;
  font-size: 12px;
}
.list {
  display: flex;
  height: calc(~"100% - 250px");
  .el-col {
    height: 100%;
  }
}
.el-card {
  height: 100%;
  background-color: @boxBgColor;
  .el-icon-plus {
    float: right;
    color: @dangerColor;
    font-weight: bold;
    cursor: pointer;
  }
}
.el-card__header span {
  // color: @subColor;
  color: rgba(22, 38, 48, 0.6);
}
.el-card__body {
  p {
    // border-bottom: 1px solid #e5e5e5;
    color: #555;
    font-size: 15px;
    line-height: 30px;
  }
  .active {
    color: #666;
    text-decoration: line-through;
  }
}
.latest-new-list__time {
  color: #666;
  font-size: 14px;
}
</style>
