<template>
  <div>
    <el-card class="box-card">
      <el-form :model="form" label-width="240px">
        <el-row :gutter="24">
          <el-col :span="10">
            <el-form-item label="Api密钥">
              <el-input v-model="form.sign" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-button @click="
              Googlecodetype = 1;
            dialogVisible1 = true;
            ">重置</el-button>
          </el-col>
          <!-- <el-col :span="6">
            <el-form-item label="谷歌验证">
              <el-button
                @click="
                  Googlecodetype = 1;
                  dialogVisible1 = true;
                "
                >重置</el-button
              >
            </el-form-item>
          </el-col> -->
        </el-row>
        <!-- <el-row :gutter="24">
          <el-col :span="10">
            <el-form-item label="TRX手续费代收地址">
              <el-input v-model="form.trxAddress" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-button @click="copy(form.trxAddress)">复制</el-button>
          </el-col>
        </el-row> -->
        <!-- <el-row :gutter="24">
          <el-col :span="10">
            <el-form-item label="代付充值地址">
              <el-input v-model="form.usdtPayAddress" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-button @click="copy(form.usdtPayAddress)">复制</el-button>
          </el-col>
        </el-row> -->
        <!-- <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="TRX手续费余额">
              <el-input v-model="form.trxBalance" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="代付地址TRX余额">
              <el-input v-model="form.usdtPayTrxBalance" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="代付地址USDT余额">
              <el-input v-model="form.usdtBalance" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row> -->
        <el-row :gutter="24">
          <el-col :span="10">
            <el-form-item label="代收归集地址">
              <el-input v-model="form.usdtAddress" v-if="!usdtAddressSea"></el-input>
              <el-input v-model="form.usdtAddress" disabled v-else></el-input>

            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="10">
            <el-form-item label="代收API回调地址">
              <el-input v-model="form.returnAddress"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="10">
            <el-form-item label="代付API回调地址">
              <el-input v-model="form.returnPayAddress"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="10">
            <el-form-item label="代付API IP白名单">
              <el-input v-model="form.payApiWhile"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <span style="color: #999; font-size: 14px">多个IP用英文,分隔</span>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="10">
            <el-form-item label="归集金额（达到此金额进行归集）">
              <el-input v-model="form.notionalPoolingAmount"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="10">
            <el-form-item label="代收API IP白名单">
              <el-input v-model="form.collectionApiWhile"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <span style="color: #999; font-size: 14px">多个IP用英文,分隔</span>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="10">
            <el-form-item label="Telegram token">
              <el-input v-model="form.telegramBotToken"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="btnmain">
        <el-button type="primary" @click="
          Googlecodetype = 2;
        dialogVisible1 = true;
        ">提交</el-button>
      </div>
    </el-card>
    <el-dialog title="请扫码验证" :visible.sync="dialogVisible" width="30%" :before-close="handleClose" :show-close="false">
      <div style="display: flex; justify-content: center">
        <div class="qrcode" ref="qrCodeUrl" style="width: 200px; height: 200px; margin-bottom: 20px"></div>
      </div>
      <span slot="footer" class="dialog-footer">
        <!-- <el-button @click="dialogVisible = false">取 消</el-button> -->
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="请输入谷歌验证码" :visible.sync="dialogVisible1" width="30%" :before-close="handleClose"
      :show-close="false">
      <div style="display: flex; justify-content: center">
        <el-input v-model="Googlecode" placeholder="谷歌验证码" type="password"></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="Googlecodetype == 1 ? resetSign() : submitSetting()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import QRCode from "qrcodejs2";
export default {
  name: "Orderlist",
  data() {
    return {
      form: {
        name: "",
      },
      dialogVisible: false,
      loginewm: "",
      usdtAddressSea:"",
      dialogVisible1: false,
      Googlecode: "",
      Googlecodetype: "",
    };
  },
  created() {
    this.getInfo();
  },
  watch: {
    dialogVisible1() {
      this.Googlecode = "";
    },
  },
  methods: {
    resetSign() {
      this.$apiFun.resetSign({ googleSign: this.Googlecode }).then((result) => {
        if (result.code == 200) {
          // this.form.sign = result.data.sign;
          this.$message.success(result.msg);
        } else {
          this.$message.error("重置失败");
        }
        this.dialogVisible1 = false;
      });
    },
    testfile(value) {
      if (value == "" || !value) {
        return '';
      }
      const start = value.slice(0, 5);
      const end = value.slice(-5);
      return `${start}********${end}`;
    },
    resetGoogleSign() {
      this.$apiFun
        .resetGoogleSign({ googleSign: this.Googlecode })
        .then((res) => {
          this.dialogVisible1 = false;
          if (res.code == 200) {
            this.$message.success("重置成功,请扫码！");
            this.dialogVisible = true;
            // this.loginewm = res.data.googleSignUrl;
            // setTimeout(() => {
            //   this.dialogVisible = true;
            // }, 1000);
            // window.open(result.data.googleSignUrl);
            if (this.$refs.qrCodeUrl) {
              this.$refs.qrCodeUrl.innerHTML = "";
            }
            setTimeout(() => {
              var qrcode = new QRCode(this.$refs.qrCodeUrl, {
                text: res.data.sign, // 需要转换为二维码的内容
                width: 200,
                height: 200,
                colorDark: "#000000",
                colorLight: "#ffffff",
                correctLevel: QRCode.CorrectLevel.H,
              });
            }, 800);
          } else {
            this.$message.error("重置失败");
          }
        });
    },
    handleClose(done) {
      this.$confirm("确认关闭？")
        .then((_) => {
          done();
        })
        .catch((_) => { });
    },
    submitSetting() {
      this.$apiFun
        .updateSetting({
          notionalPoolingAmount:this.form.notionalPoolingAmount,
          usdtAddress: this.form.usdtAddress,
          returnAddress: this.form.returnAddress,
          returnPayAddress: this.form.returnPayAddress,
          payApiWhile: this.form.payApiWhile,
          collectionApiWhile: this.form.collectionApiWhile,
          telegramBotToken: this.form.telegramBotToken,
          googleSign: this.Googlecode,
        })
        .then((result) => {
          this.dialogVisible1 = false;
          if (result.code == 200) {
            this.$message.success("提交成功");
            this.getInfo();
          } else {
            this.$message.error(result.msg);
          }
        });
    },
    getInfo() {
      this.$apiFun.info().then((res) => {
        this.form = res.data;
        this.form.sign = this.form.sign;
        this.form.usdtAddress = this.form.usdtAddress;
        this.usdtAddressSea =  this.form.usdtAddress;
        this.form.returnAddress = this.form.returnAddress;
        this.form.returnPayAddress = this.form.returnPayAddress;
        this.form.payApiWhile = this.form.payApiWhile;
        this.form.collectionApiWhile = this.form.collectionApiWhile;
        this.form.telegramBotToken = this.form.telegramBotToken;
        this.form.notionalPoolingAmount = this.form.notionalPoolingAmount
      });
    },
  },
};
</script>

<style scoped lang="less">
.el-row {
  display: flex;
  align-items: center;
}

/deep/ .el-dialog {
  border-radius: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
}

.el-form-item {
  margin-bottom: 0px;
}

.el-row {
  margin-bottom: 30px;

  &:first-child {
    margin-top: 30px;
  }
}

.btnmain {
  margin-top: 50px;
  display: flex;
  justify-content: center;

  .el-button--medium {
    padding: 10px 60px;
  }
}
</style>
