<template>
  <div>
    <el-card class="box-card">
      <el-form label-width="100px">
        <el-row :gutter="24">
          <el-col :span="6">
            <div class="el-tooltips">
              <img src="../../assets/img/touxiang.png" class="one" alt="" />
              <div class="right">
                <h2></h2>
                <p>
                  商户id：{{ ids }}
                  <i class="el-icon-document-copy" style="font-size: 14px"></i>
                </p>
              </div>
            </div>
          </el-col>
        </el-row>

        <div style="display: flex">
          <el-form-item label="绑定邮箱">
            <el-input v-model="emails" disabled></el-input>
          </el-form-item>
          <el-form-item label-width="5px">
            <el-button @click="dialogeditemail = true">修改</el-button>
          </el-form-item>
        </div>

        <div style="display: flex">
          <el-form-item label="谷歌验证">
            <el-button
              @click="
                Googlecodetype = 1;
                dialogVisible1 = true;
              "
              >重置</el-button
            >
          </el-form-item>
          <el-form-item label="登录密码">
            <el-button @click="dialogeditpass = true">重置</el-button>
          </el-form-item>
        </div>

        <!-- <div style="display: flex" v-if="quxian.indexOf('/role') != -1 && quxian.indexOf('/user') != -1">
          <el-form-item label="设置" v-if="quxian.indexOf('/role') != -1">
            <el-button type="primary" @click="getlinks(1)">角色设置</el-button>
          </el-form-item>
          <el-form-item label-width="5px" v-if="quxian.indexOf('/user') != -1">
            <el-button type="primary" @click="getlinks(2)">账号设置</el-button>
          </el-form-item>
        </div> -->
      </el-form>
    </el-card>
    <div style="margin-top: 40px" v-if="quxian.indexOf('/role') != -1 && quxian.indexOf('/user') != -1">
      <el-tabs v-model="activeName" type="border-card">
        <el-tab-pane label="角色设置" name="first" @click="getlinks(1)" v-if="quxian.indexOf('/role') != -1">
          <Role></Role>
        </el-tab-pane>
        <el-tab-pane label="账号设置" name="second" @click="getlinks(2)" v-if="quxian.indexOf('/user') != -1">
          <User></User>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- <div style="margin-top: 20px">
      <User v-if="setuptype == 2"></User>
      <Role v-if="setuptype == 1"></Role>
    </div> -->
    <el-dialog
      title="请用谷歌验证器扫码重新验证验证"
      :visible.sync="dialogVisible"
      width="30%"
      :show-close="false"
    >
      <div style="display: flex; justify-content: center">
        <div
          class="qrcode"
          ref="qrCodeUrl"
          style="width: 200px; height: 200px; margin-bottom: 20px"
        ></div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false"
          >确 定</el-button
        >
      </span>
    </el-dialog>
    <el-dialog
      title="请输入谷歌验证码"
      :visible.sync="dialogVisible1"
      width="30%"
      :show-close="false"
    >
      <div style="display: flex; justify-content: center">
        <el-input
          v-model="Googlecode"
          placeholder="谷歌验证码"
          type="password"
        ></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible1 = false">取 消</el-button>
        <el-button
          type="primary"
          @click="Googlecodetype == 1 ? resetGoogleSign() : submitSetting()"
          >确 定</el-button
        >
      </span>
    </el-dialog>
    <el-dialog title="修改密码" :visible.sync="dialogeditpass" width="30%">
      <div>
        <el-form
          :model="ruleForm"
          :rules="rules"
          ref="ruleForm"
          label-width="100px"
        >
          <el-form-item prop="password" label="原密码">
            <el-input
              type="password"
              v-model="ruleForm.password"
              placeholder="请输入原密码"
            ></el-input>
          </el-form-item>
          <el-form-item prop="password2" label="新密码">
            <el-input
              type="password"
              placeholder="请输入新密码"
              v-model="ruleForm.password2"
            ></el-input>
          </el-form-item>
          <el-form-item prop="truepassword" label="确认密码">
            <el-input
              type="password"
              v-model="ruleForm.truepassword"
              placeholder="确认新密码"
            ></el-input>
          </el-form-item>
          <el-form-item prop="Googlecodes" label="谷歌验证码">
            <el-input
              type="password"
              v-model="ruleForm.Googlecode"
              placeholder="请输入谷歌验证码"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="修改邮箱" :visible.sync="dialogeditemail" width="30%">
      <div>
        <el-form
          :model="ruleForm11"
          :rules="rules11"
          ref="ruleForm11"
          label-width="100px"
        >
          <el-form-item prop="email" label="新邮箱">
            <el-input
              type="text"
              v-model="ruleForm11.email"
              placeholder="请输入新邮箱"
            ></el-input>
          </el-form-item>
          <el-form-item prop="Googlecodes" label="谷歌验证码">
            <el-input
              type="password"
              v-model="ruleForm11.Googlecode"
              placeholder="请输入谷歌验证码"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="geteditemail">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import QRCode from "qrcodejs2";
import User from "./user.vue";
import Role from "./role.vue";
export default {
  components: { User, Role },
  name: "setup",
  data() {
    return {
      dialogVisible: false,
      dialogVisible1: false,
      Googlecode: "",
      Googlecodetype: "",
      form: "",
      dialogeditpass: false,
      ruleForm: {
        password: "",
        password2: "",
        truepassword: "",
        Googlecode: "",
      },
      rules: {
        password: [
          { required: true, message: "此项为必填项", trigger: "blur" },
        ],
        password2: [
          { required: true, message: "此项为必填项", trigger: "blur" },
        ],
        truepassword: [
          { required: true, message: "此项为必填项", trigger: "blur" },
        ],
        Googlecodes: [
          { required: true, message: "此项为必填项", trigger: "blur" },
        ],
      },
      ruleForm11: {
        email: "",
        Googlecode: "",
      },
      rules11: {
        email: [{ required: true, message: "此项为必填项", trigger: "blur" }],
        Googlecodes: [
          { required: true, message: "此项为必填项", trigger: "blur" },
        ],
      },
      ids: "",
      emails: "",
      dialogeditemail: false,
      setuptype: "1",
      quxian: "",
      activeName: "first",
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
    getlinks(val) {
      this.setuptype = val;
    },
    geteditemail() {
      if (this.ruleForm11.email == "" || this.ruleForm11.googleSign) {
        this.$message({
          message: "请输入完整信息",
          type: "warning",
        });
        return false;
      }
      var data = {
        email: this.ruleForm11.email,
        googleSign: this.ruleForm11.Googlecode,
      };
      this.$apiFun.ssoresetemail(data).then((res) => {
        if (res.code == 200) {
          this.$message({
            message: "修改成功",
            type: "success",
          });
          this.getInfo();
        } else {
          this.$message({
            message: res.msg,
            type: "error",
          });
        }
        this.dialogeditemail = false;
      });
    },
    // 修改密码
    submitForm() {
      if (
        this.ruleForm.password == "" ||
        this.ruleForm.password2 == "" ||
        this.ruleForm.truepassword == "" ||
        this.ruleForm.Googlecode == ""
      ) {
        this.$message({
          message: "请输入完整信息",
          type: "warning",
        });
        return false;
      }
      if (this.ruleForm.password2 != this.ruleForm.truepassword) {
        this.$message({
          message: "两次密码不一致",
          type: "error",
        });
        return false;
      }
      var data = {
        oldPassWord: this.ruleForm.password,
        passWord: this.ruleForm.password2,
        googleSign: this.ruleForm.Googlecode,
      };
      this.$apiFun.resetpassword(data).then((res) => {
        if (res.code == 200) {
          this.dialogeditpass = false;
          this.$message({
            message: "修改成功",
            type: "success",
          });
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    resetGoogleSign() {
      this.$apiFun
        .resetGoogleSign({ googleSign: this.Googlecode })
        .then((res) => {
          this.dialogVisible1 = false;
          if (res.code == 200) {
            this.$message.success("重置成功,请扫码！");
            this.dialogVisible = true;
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
        .catch((_) => {});
    },
    submitSetting() {
      this.$apiFun
        .updateSetting({
          usdtAddress: this.form.usdtAddress,
          returnAddress: this.form.returnAddress,
          returnPayAddress: this.form.returnPayAddress,
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
        this.ids = res.data.id;
        this.emails = res.data.account;
        this.quxian = JSON.stringify(res.data.merchantMenuList);
        
        if (this.quxian.indexOf('/user') != -1 && this.quxian.indexOf('/role') == -1) {
          this.activeName = 'second'
        }
      });
    },
  },
};
</script>

<style scoped lang="less">
/deep/ .el-tabs__content{
  padding: 0 !important;
}
.el-tooltips {
  margin-left: 32px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  cursor: pointer;
}
.el-tooltips .one {
  font-size: 21px;
  width: 48px;
  height: 48px;
  line-height: 48px;
  color: #fff;
  background: #162630;
  text-align: center;
  border-radius: 50%;
  margin-right: 16px;
}
/deep/ .el-dialog {
  border-radius: 10px;
}
.dialog-footer {
  display: flex;
  justify-content: center;
}
// .el-form-item {
//   margin-bottom: 0px;
// }
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
