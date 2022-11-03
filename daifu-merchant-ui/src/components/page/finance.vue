<template>
  <div>
    <el-card class="box-card">
      <div class="daishoumain" v-if="buzhou == 1">
        <el-row
          :gutter="24"
          style="display: flex; justify-content: space-between"
        >
          <el-col :span="11">
            <div class="daishouleft">
              <p class="names">代收</p>
              <div class="daishouk">
                <div class="everylist">
                  <img src="../../assets/img/tron-trx-logo.png" alt="" />
                  <p class="titles">代收钱包TRX矿工费余额</p>
                  <p class="yue">{{ infos.trxBalance }}TRX</p>
                  <el-button
                    type="primary"
                    size="medium"
                    @click="getdsChongzhi(1)"
                    >充值</el-button
                  >
                </div>
                <div class="everylist">
                  <img
                    style="width: 150px"
                    src="../../assets/img/logos.png"
                    alt=""
                  />
                  <p class="titles">手续费钱包余额</p>
                  <p class="yue">{{ infos.balance }}USDT</p>
                  <el-button
                    type="primary"
                    size="medium"
                    @click="getdsChongzhi(2)"
                    >购买</el-button
                  >
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="2" style="display: flex; justify-content: center">
            <div class="fenge"></div>
          </el-col>
          <el-col :span="11">
            <div class="daishouleft">
              <p class="names">代付</p>
              <div class="daishouks">
                <div class="everylists" style="margin-bottom: 30px">
                  <img src="../../assets/img/tron-trx-logo.png" alt="" />
                  <div>
                    <p class="titles">代付地址TRX总余额</p>
                    <p class="yue">{{ infos.usdtPayTrxBalance }}TRX</p>
                  </div>
                </div>
                <div class="everylists">
                  <img src="../../assets/img/tether-usdt-logo.png" alt="" />
                  <div>
                    <p class="titles">代付地址USDT总余额</p>
                    <p class="yue">{{ infos.usdtBalance }}USDT</p>
                  </div>
                </div>
                <div class="chargeanniu">
                  <el-button type="primary" size="medium" @click="buzhou = 2"
                    >充值</el-button
                  >
                </div>
              </div>
            </div></el-col
          >
        </el-row>
      </div>
      <div v-if="buzhou == 2" style="padding: 40px">
        <div class="titles_search">
          <div class="addaddressreturn">
            <el-button
              type="primary"
              size="medium"
              @click="dialogVisible = true"
              >新建代付地址</el-button
            >
            <div class="fanhui" @click="buzhou = 1">
              <el-button type="primary" size="medium" icon="el-icon-back"
                >返回</el-button
              >
            </div>
          </div>
        </div>
        <div class="tablemain">
          <div class="daifulist" v-for="(item, index) in lists" :key="index">
            <p class="titles">代付地址-{{ index + 1 }}</p>
            <div class="daifuneir">
              <div class="lefts">
                <div class="topimg">
                  <img src="../../assets/img/tron-trx-logo.png" alt="" />
                  <img src="../../assets/img/tether-usdt-logo.png" alt="" />
                </div>
                <div class="zhuandizhi">{{ item.address }}</div>
                <div class="yuesDiv">
                  <p>TRX矿工费余额</p>
                  <p>{{ item.trxBalance }} TRX</p>
                </div>
                <div class="yuesDiv">
                  <p>USDT余额</p>
                  <p>{{ item.usdtBalance }} USDT</p>
                </div>
                <div class="rights">
                  <el-button
                    type="primary"
                    size="medium"
                    @click="dfbuzhou = 1;getdfRecharge(item)"
                    >TRX充值</el-button
                  >
                  <el-button
                    type="primary"
                    size="medium"
                    @click="dfbuzhou = 2;getdfRecharge(item)"
                    >USDT充值</el-button
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    <el-dialog
      title="请输入谷歌验证码"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-input v-model="Googlecode" placeholder="谷歌验证码"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="getnewlybuild">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog :title="dfbuzhou == 1 ? 'TRX矿工费余额充值' : 'USDT余额充值'" :visible.sync="czdialogVisible">
      <div class="dsdialog">
        <div
          class="qrcode"
          ref="qrCodeUrl"
          style="width: 200px; height: 200px; margin-bottom: 20px"
        ></div>
        <div>
          <div class="fuzhimain">
            <el-input v-model="dfitem.address" disabled></el-input>
            <el-button @click="copy(dfitem.address)">复制</el-button>
          </div>

          <p class="guide">请在规定时间内完成充值，超时会自动关闭订单</p>
          <p class="times">{{ countDown }}</p>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <!-- <el-button type="primary" @click="dfbuzhou = 2" v-if="dfbuzhou == 1">我确认已经充值，下一步</el-button> -->
        <el-button type="primary" @click="czdialogVisible = false">完成</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="twotype == 1 ? '代收钱包TRX矿工费充值' : '手续费钱包余额充值'"
      :visible.sync="dsczdialogVisible"
    >
      <div class="dsdialog">
        <div
          class="qrcode"
          ref="qrCodeUrl1"
          style="width: 200px; height: 200px; margin-bottom: 20px"
        ></div>
        <div>
          <div class="fuzhimain">
            <el-input v-model="twotypeaddress" disabled></el-input>
            <el-button @click="copy(twotypeaddress)">复制</el-button>
          </div>

          <p class="guide">请在规定时间内完成充值，超时会自动关闭订单</p>
          <p class="times">{{ countDown }}</p>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dsczdialogVisible = false"
          >确 定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import QRCode from "qrcodejs2";
export default {
  name: "finance",
  data() {
    return {
      lists: [],
      dialogVisible: false,
      Googlecode: "",
      infos: "",
      buzhou: 1,
      czdialogVisible: false,
      dsczdialogVisible: false,
      maxTime: "",
      countDown: "",
      interval: null,
      twotypeaddress: "",
      twotype: "",
      dfitem:'',
      dfbuzhou:1,
    };
  },
  created() {
    this.getList();
    this.getInfo();
  },
  watch: {
    dsczdialogVisible(val) {
      if (val == false) {
        // this.maxTime = "";
        // this.interval = null;
        // clearInterval(this.interval);
        location.reload();
      }
    },
    czdialogVisible(val){
       if (val == false) {
        location.reload();
      }
    }
  },
  methods: {
    getdsChongzhi(val) {
      if (this.$refs.qrCodeUrl1) {
        this.$refs.qrCodeUrl1.innerHTML = "";
      }
      this.twotype = val;
      this.twotypeaddress =
        this.twotype == 1 ? this.infos.trxAddress : this.infos.rechargeAddress;
      this.begin();
      this.dsczdialogVisible = true;
      setTimeout(() => {
        var qrcode = new QRCode(this.$refs.qrCodeUrl1, {
          text: this.twotypeaddress, // 需要转换为二维码的内容
          width: 200,
          height: 200,
          colorDark: "#000000",
          colorLight: "#ffffff",
          correctLevel: QRCode.CorrectLevel.H,
        });
      }, 800);
    },
    begin() {
      this.maxTime = 30 * 60;
      this.handleCountDown();
    },
    handleCountDown() {
      this.interval = setInterval(() => {
        this.maxTime--;
        let minutes = Math.floor(this.maxTime / 60);
        let seconds = Math.floor(this.maxTime % 60);
        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;
        this.countDown = minutes + ":" + seconds;
        if (this.maxTime === 0) {
          clearInterval(this.interval);
        }
      }, 1000);
    },
    getdfRecharge(item) {
      this.dfitem = item
      this.maxTime = "";
      this.interval = null;
      clearInterval(this.interval);
      this.begin();
      this.czdialogVisible = true;
      if (this.$refs.qrCodeUrl) {
        this.$refs.qrCodeUrl.innerHTML = "";
      }
      setTimeout(() => {
        var qrcode = new QRCode(this.$refs.qrCodeUrl, {
          text: this.dfitem.address, // 需要转换为二维码的内容
          width: 200,
          height: 200,
          colorDark: "#000000",
          colorLight: "#ffffff",
          correctLevel: QRCode.CorrectLevel.H,
        });
      }, 800);
    },
    getInfo() {
      this.$apiFun.info().then((res) => {
        this.infos = res.data;
      });
    },
    getnewlybuild() {
      this.$apiFun
        .getpayuseaddresscreate({ googleSign: this.Googlecode })
        .then((res) => {
          if (res.code == 200) {
            this.$message({
              message: "创建成功！",
              type: "success",
            });
            this.getList();
          } else {
            this.$message({
              message: res.msg,
              type: "warning",
            });
          }
          this.Googlecode = "";
          this.dialogVisible = false;
        });
    },
    getList() {
      this.$apiFun.getpayuseaddresslist().then((res) => {
        this.lists = res.data;
      });
    },
  },
};
</script>

<style scoped lang="less">
/deep/ .el-dialog {
  border-radius: 10px;
}
.tablemain {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  &::after {
    width: 30%;
    content: "";
  }
  .pagination {
    margin-top: 20px;
  }
  .daifulist {
    padding: 20px 40px;
    border: 1px solid #05aa69;
    border-radius: 20px;
    width: 31%;
    margin-top: 30px;
    // margin-right: 3.33%;
    .titles {
      font-size: 24px;
      font-weight: 600;
      text-align: center;
    }
    .daifuneir {
      // display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 30px;
      .lefts {
        display: flex;
        flex-direction: column;
        align-items: center;
        .topimg {
          display: flex;
          justify-content: center;
          flex-wrap: wrap;

          img {
            width: 40px;
            margin: 0 20px;
            margin-top: 10px;
          }
        }
        .zhuandizhi {
          margin-top: 14px;
          font-weight: 600;
          font-size: 14px;
          width: 100%;
          word-break: break-word;
        }
        .yuesDiv {
          margin-top: 14px;
          width: 100%;
          p {
            text-align: left;
            &:first-child {
              font-size: 16px;
              font-weight: 600;
            }
            &:last-child {
              color: #05aa69;
              font-size: 16px;
            }
          }
        }
        .rights {
          margin-top: 14px;
        }
      }
    }
  }
}

.titles_search {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  .addaddressreturn {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    .fanhui {
      cursor: pointer;
    }
  }
  .el-input {
    width: 264px;
    /deep/ .el-input__inner {
      height: 38px !important;
      line-height: 38px !important;
      background: rgba(22, 38, 48, 0.04) !important;
      border-color: rgba(22, 38, 48, 0.04) !important;
      border-radius: 38px;
    }
  }
  .typeselect {
    margin-left: 20px;
    .el-select {
      width: 144px;
      /deep/ .el-input__inner {
        height: 38px !important;
        line-height: 38px !important;
        border-radius: 38px;
      }
    }
  }
}
.daishoumain {
  // margin: 160px 0;
  .fenge {
    width: 6px;
    height: 100%;
    background: #f0f3f8;
    margin: 30px 0;
  }
  .daishouleft {
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
    margin: 80px 0;
    .names {
      font-size: 24px;
      font-weight: 600;
      letter-spacing: 2px;
      margin-bottom: 40px;
    }
    .daishouk {
      display: flex;
      flex-wrap: wrap;
      justify-content: center;

      .everylist {
        margin: 0 20px;
        padding: 40px 30px;
        border: 1px solid #d6d6d6;
        display: flex;
        justify-content: center;
        flex-direction: column;
        align-items: center;
        border-radius: 20px;
        width: 260px;
        margin-top: 20px;
        img {
          width: 60px;
          height: 60px;
        }
        .titles {
          margin-top: 40px;
          font-weight: 600;
          font-size: 16px;
        }
        .yue {
          margin: 16px 0;
          color: #05aa69;
          font-size: 16px;
        }
      }
    }

    .daishouks {
      border: 1px solid #d6d6d6;
      padding: 30px 60px;
      border-radius: 20px;
      .everylists {
        display: flex;
        align-items: center;

        img {
          width: 60px;
          margin-right: 30px;
        }
        .titles {
          font-weight: 600;
        }
        .yue {
          color: #05aa69;
        }
      }
      .chargeanniu {
        display: flex;
        justify-content: center;
        margin-top: 30px;
      }
    }
  }
}
.dsdialog {
  display: flex;
  align-items: center;
  justify-content: center;
  .qrcode {
    margin-right: 20px;
  }
  .fuzhimain {
    display: flex;
    align-items: center;
  }
  .guide {
    color: #05aa69;
    margin-top: 20px;
    font-size: 14px;
  }
  .times {
    text-align: center;
    font-size: 50px;
    font-weight: 600;
    letter-spacing: 20px;
    color: #000;
    margin-top: 20px;
  }
}
</style>
