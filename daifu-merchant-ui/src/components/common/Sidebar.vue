<template>
  <el-aside width="auto" height="auto">
    <el-menu
      :collapse="isCollapse"
      :default-active="onRoutes"
      background-color="#ffffff"
      text-color="#000000"
      active-text-color="#05aa69"
      unique-opened
      router
    >
      <div class="mydata-tip">
        <el-tooltip
          class="item"
          effect="light"
          placement="right"
          :visible-arrow="false"
        >
          <div>
            <!-- <p class="one"></p> -->
            <img src="../../assets/img/touxiang.png" class="one" alt="" />
            <div class="right">
              <h2></h2>
              <p>
                商户id：{{ ids }}
                <i class="el-icon-document-copy" style="font-size: 14px"></i>
              </p>
            </div>
          </div>
          <div slot="content">
            <div class="usercontent">
              <div>
                <div class="title">
                  <p class="one"></p>
                  <p class="name">
                    <!-- <i class="el-icon-edit-outline" style="font-size: 14px"></i> -->
                  </p>
                  <p class="merid">
                    <span>商户id</span>：{{ ids }}
                    <i
                      class="el-icon-copy-document"
                      style="font-size: 14px"
                    ></i>
                  </p>
                  <!-- <p class="merid"><span>员工号</span>：行政</p> -->
                </div>
                <div class="content-main">
                  <!-- <p class="clearfixs">
                    <span class="name">手机号</span>
                    <span class="num" style="display: none"
                      ><i class="el-icon-edit-outline"></i
                    ></span>
                  </p> -->
                  <p class="clearfixs">
                    <span class="name">邮箱号</span>
                    <span class="num"
                      >{{ emails }}
                      <!-- <i class="el-icon-edit-outline" @click="dialogeditemail = true"></i> -->
                    </span>
                  </p>
                  <p class="clearfixs">
                    <span class="name">密码登录</span>
                    <span class="num">
                      ********<i
                        class="el-icon-edit-outline"
                        @click="dialogeditpass = true"
                      ></i>
                    </span>
                  </p>
                </div>
                <div class="logout">
                  <span @click="getlogout">退出登录</span>
                </div>
              </div>
            </div>
          </div>
        </el-tooltip>
      </div>

      <template v-for="item in items">
        <template v-if="item.subItems">
          <el-submenu>
            <!-- <template slot="title">
              <i :class="item.icon"></i>
              <span slot="title">{{item.title}}</span>
            </template> -->
            <template slot="title">
              <i
                :class="item.isshow ? 'iconfont ' + item.icon : item.icon"
                :style="{
                  'font-size': item.icon == 'icon-caiwuguanli' ? '18px' : '',
                }"
              ></i>
              <span slot="title">{{ item.title }}</span>
            </template>
            <div class="nextc">
              <el-menu-item
                v-for="(subItem, i) in item.subItems"
                :index="subItem.path"
                :key="i"
              >
                {{ subItem.title }}
              </el-menu-item>
            </div>
          </el-submenu>
        </template>
        <template v-else>
          <el-menu-item :index="item.path">
            <i
              :class="item.isshow ? 'iconfont ' + item.icon : item.icon"
              :style="{
                'font-size': item.icon == 'icon-caiwuguanli' ? '22px' : '',
              }"
            ></i>
            <span slot="title"
              >{{ item.title }}
              <span v-if="item.title == '异常订单' && ycOrdernum && ycOrdernum != 0" class="ycordernum">{{
                ycOrdernum ? ycOrdernum : "0"
              }}</span>
              <span v-if="item.title == '大额审查' && deOrdernum && deOrdernum != 0" class="ycordernum">{{
                deOrdernum ? deOrdernum : "0"
              }}</span>
            </span>
          </el-menu-item>
        </template>
      </template>

      <div class="aside-logo">
        <img src="../../assets/img/logos.png" alt="" />
        <span>©2022 PAY CLOUD</span>
      </div>
    </el-menu>
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
        <el-button type="primary" @click="submitForm('ruleForm')"
          >确 定</el-button
        >
      </span>
    </el-dialog>
    <el-dialog
      title="修改邮箱"
      :visible.sync="dialogeditemail"
      width="30%"
      @close="emailclose"
    >
      <div>
        <el-form
          :model="ruleFormemail"
          :rules="rulesemail"
          ref="ruleFormemail"
          label-width="100px"
        >
          <div class="tishi">
            <el-form-item style="margin-left: 0">
              <div data-v-085ef7ca="" class="title-warn">
                <i data-v-085ef7ca="" class="el-icon-warning"></i>
                需先驗證原郵箱號，郵箱號當月只允許修改兩次
              </div>
            </el-form-item>
          </div>
          <div v-if="oldshow">
            <el-form-item prop="email" label="邮箱号">
              <el-input
                v-model="ruleFormemail.email"
                disabled
                placeholder="请输入原邮箱号"
              ></el-input>
            </el-form-item>
            <div class="emailcode">
              <el-form-item prop="code" label="验证码">
                <el-input
                  v-model="ruleFormemail.code"
                  placeholder="请输入验证码"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-button
                  class="login-btn"
                  type="primary"
                  :disabled="!emailcodeshow"
                  @click="getemailcode(1)"
                  >发送验证码
                  <span v-show="!emailcodeshow" class="count"
                    >({{ count }}s)</span
                  ></el-button
                >
              </el-form-item>
            </div>
          </div>
          <div v-else>
            <el-form-item prop="newemail" label="新邮箱号">
              <el-input
                v-model="ruleFormemail.newemail"
                placeholder="请输入新邮箱号"
              ></el-input>
            </el-form-item>
            <div class="emailcode">
              <el-form-item prop="newcode" label="验证码">
                <el-input
                  v-model="ruleFormemail.newcode"
                  placeholder="请输入验证码"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-button
                  class="login-btn"
                  type="primary"
                  :disabled="!emailcodeshow"
                  @click="getemailcode(2)"
                  >发送验证码
                  <span v-show="!emailcodeshow" class="count"
                    >({{ count }}s)</span
                  ></el-button
                >
              </el-form-item>
            </div>
          </div>
        </el-form>
      </div>

      <span slot="footer" class="dialog-footer" v-if="oldshow">
        <el-button type="primary" @click="getemailgo">下一步</el-button>
      </span>
      <span slot="footer" class="dialog-footer" v-else>
        <el-button type="primary" @click="getsubmit">提交</el-button>
      </span>
    </el-dialog>
  </el-aside>
</template>

<script>
import Bus from "./bus";
export default {
  name: "Sidebar",
  data() {
    return {
      oldshow: true,
      dialogeditpass: false,
      dialogeditemail: false,
      isCollapse: false,
      items: [],
      itemsYs: [
        {
          title: "控制台",
          path: "/index",
          icon: "el-icon-menu",
        },
        {
          title: "代收订单",
          path: "/orderlist",
          icon: "el-icon-tickets",
        },
        {
          title: "收款地址",
          path: "/UseAddress",
          icon: "el-icon-tickets",
        },
        {
          title: "代付订单",
          path: "/consumption",
          icon: "el-icon-document",
        },
        {
          title: "大额审查",
          path: "/examination",
          icon: "icon-shencha",
          isshow: true,
        },
        {
          title: "异常订单",
          path: "/abnormalorder",
          icon: "icon-mingxiweihuyichang",
          isshow: true,
        },
        {
          title: "财务管理",
          path: "/finance",
          icon: "icon-caiwuguanli",
          isshow: true,
        },
        {
          title: "API文档",
          path: "/apicloud",
          icon: "icon-kuaijierukou_apiwendang",
          isshow: true,
        },
        {
          title: "安全中心",
          path: "/security",
          icon: "icon-anquan",
          isshow: true,
        },
        {
          title: "日志记录",
          path: "/journal",
          icon: "icon-rizhi",
          isshow: true,
        },
        {
          title: "设置",
          path: "/setup",
          icon: "el-icon-setting",
          // subItems: [
          //   {
          //     title: "角色配置",
          //     path: "/role",
          //   },
          //   {
          //     title: "账号配置",
          //     path: "/user",
          //   },
          // ],
        },
        //  {
        //   title: "角色配置",
        //   path: "/role",
        //   icon: "icon-biaoshilei_jiaosepeizhi",
        //   isshow:true,
        // },
        //  {
        //   title: "账号配置",
        //   path: "/user",
        //   icon: "icon-zhanghao",
        //   isshow:true,
        // },
        // {
        //   title: "echarts图表",
        //   path: "echarts",
        //   icon: "el-icon-star-on",
        //   subItems: [
        //     {
        //       title: "简单图表",
        //       path: "/chart-simple",
        //     },
        //     {
        //       title: "复杂图表",
        //       path: "/chart-complex",
        //     },
        //   ],
        // },
        // {
        //   title: "tab选项卡",
        //   path: "/tab",
        //   icon: "el-icon-message",
        // },
        // {
        //   title: "表格",
        //   path: "/table",
        //   icon: "el-icon-tickets",
        // },
        // {
        //   title: "错误页面",
        //   path: "/error",
        //   icon: "el-icon-warning",
        // },
        // {
        //   title: "404页面",
        //   path: "/404",
        //   icon: "el-icon-error",
        // },
      ],
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
      ruleFormemail: {
        email: "123@qq.com",
        code: "",
        newemail: "",
        newcode: "",
      },
      rulesemail: {
        code: [{ required: true, message: "此项为必填项", trigger: "blur" }],
      },
      timer: null, //计时器
      count: "", //倒计时
      emailcodeshow: true, //控制按钮
      emailcodetime: 60,
      ids: "",
      emails: "",
      ycOrdernum: "",
      queryParams: {
        auditStatus: 0,
        pageSize: 10,
        pageNumber: 1,
      },
      deOrdernum: "",
    };
  },
  computed: {
    onRoutes() {
      return this.$route.fullPath;
    },
  },
  created() {
    // 通过 event bus进行组件间的通信 来折叠和展开侧边栏
    Bus.$on("collapse", (isCollapse) => {
      this.isCollapse = isCollapse;
    });
    // 异常订单通信
    Bus.$on("advanced", (data) => {
      // this.ycOrdernum = data;
      this.getycList()
    });
    // 大额审查通信
    Bus.$on("daeOrder", (data) => {
      // this.deOrdernum = data;
      this.getdeList()
    });
  },
  mounted() {
    this.getInfo();
    this.getycList();
    this.getdeList();
  },
  methods: {
    getycList() {
      var that = this;
      this.$apiFun.payOrderabnormitylist(this.queryParams).then((result) => {
        if (result.code == 200) {
          this.ycOrdernum = result.data.total;
          // setTimeout(() => {
          //   that.getycList();
          // }, 5000);
          // alert(1)
        }
      });
    },
    getdeList() {
      var that = this;
      this.$apiFun.payOrderwholesalelist(this.queryParams).then((result) => {
        if (result.code == 200) {
          this.deOrdernum = result.data.total;
          // setTimeout(() => {
          //   that.getdeList();
          // }, 5000);
        }
      });
    },
    getInfo() {
      this.$apiFun.info().then((res) => {
        this.ids = res.data.id;
        this.emails = res.data.account;
        for (let i = 0; i < this.itemsYs.length; i++) {
          for (
            let index = 0;
            index < res.data.merchantMenuList.length;
            index++
          ) {
            var menu = res.data.merchantMenuList[index];

            var items = this.itemsYs[i];
            if (items.path === menu.path) {
              this.items.push(items);
            }
          }
        }
      });
    },
    getlogout() {
      localStorage.removeItem("tokenType");
      localStorage.removeItem("token");
      this.$router.replace("/login");
    },
    emailclose() {
      this.oldshow = true;
      this.emailcodeshow = true;
      clearInterval(this.timer); // 清除定时器
      this.timer = null;
      this.emailcodetime = 60;
    },
    getemailgo() {
      this.oldshow = false;
      this.emailcodeshow = true;
      clearInterval(this.timer); // 清除定时器
      this.timer = null;
      this.emailcodetime = 60;
    },
    getsubmit() {
      this.emailclose();
      this.dialogeditemail = false;
    },
    getemailcode(val) {
      if (val == 1) {
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
      } else {
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
      }
    },
    // 修改密码
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (!valid) {
          return false;
        }
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
      });
    },
    submitemailForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (!valid) {
          return false;
        }
        this.dialogeditemail = false;
      });
    },
  },
};
</script>

<style scoped lang="less">
.ycordernum {
  background: red;
  padding: 0px 14px;
  border-radius: 20px;
  margin-left: 10px;
  font-size: 14px;
  color: #fff;
}
/deep/ .el-icon-setting {
  margin-right: 20px;
}
/deep/ .nextc .el-menu-item {
  padding-left: 100px !important;
}
/deep/ .el-menu {
  height: auto;
  min-height: 100%;
}
.tishi {
  /deep/ .el-form-item__content {
    margin-left: 0 !important;
  }
}
.title-warn {
  line-height: 16px;
  background-color: #fdf6ec;
  border-color: #faecd8;
  color: #e6a23c;
  padding: 6px;
}
.emailcode {
  display: flex;
  align-items: center;
  justify-content: space-between;
  .el-input {
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
  /deep/ .el-form-item__content {
    margin-left: 0 !important;
    display: flex;
    align-items: center;
  }
}
/deep/ .el-dialog {
  border-radius: 16px !important;
}
.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: center;
}
.aside-logo {
  // position: absolute;
  // bottom: 24px;
  // left: 55px;
  margin-left: 55px;
  margin-top: 50px;
  margin-bottom: 20px;
  img {
    display: block;
    width: 112px;
    height: 32px;
  }
  span {
    margin-top: 8px;
    font-size: 10px;
    color: rgba(22, 38, 48, 0.4);
    line-height: 16px;
  }
}
.el-tooltip__popper.is-light {
  background: #fff !important;
  padding: 0 !important;
  border-radius: 4px !important;
  border-color: #e6e6e6 !important;
  box-shadow: 0px 0px 7px 0px rgba(42, 42, 42, 0.2) !important;
}
.usercontent {
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  align-content: center;
  justify-items: center;
  div {
    display: flex;
    flex-direction: column;
    // align-content: center;
    .title {
      width: 100%;

      .one {
        display: inline-block;
        margin-bottom: 16px;
        width: 48px;
        height: 48px;
        font-size: 21px;
        color: #fff;
        background: #162630;
        line-height: 48px;
        text-align: center;
        border-radius: 50%;
        margin: 0 auto;
      }
      .name {
        cursor: pointer;
        font-family: PingFangSC-Medium;
        font-size: 17px;
        color: #162630;
        letter-spacing: -0.3px;
        line-height: 28px;
        font-weight: 700;
        margin-bottom: 10px;
        margin: 0 auto;
      }
      .merid {
        width: 240px;
        margin: 0 auto;
        margin-bottom: 16px;
        text-align: left;
        font-family: AvenirNext-Regular;
        font-size: 13px;
        color: rgba(22, 38, 48, 0.6);
        line-height: 5px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-top: 10px;
      }
    }
    .content-main {
      width: 100%;
      padding: 16px 0;
      display: flex;
      justify-content: flex-start;
      .clearfixs {
        display: flex;
        align-items: center;
        justify-content: space-between;
        cursor: pointer;
        width: 100%;
        padding: 16px 0;
        .name {
          font-family: PingFangSC-Regular;
          font-size: 15px;
          color: rgba(22, 38, 48, 0.6);
        }
        .num {
          display: flex;
          justify-content: flex-end;
          align-items: center;
          font-family: AvenirNext-Regular;
          font-size: 15px;
          color: #000;
        }
      }
    }
    .logout {
      margin: 32px 0 8px 0;
      text-align: center;
      cursor: pointer;
      span {
        display: inline-block;
        width: 104px;
        height: 40px;
        border-radius: 28px;
        font-family: AvenirNext-Medium;
        font-size: 15px;
        color: #dd5144;
        text-align: center;
        background: #faeeec;
        letter-spacing: -0.2px;
        line-height: 40px;
        font-weight: 700;
        margin: 0 auto;
      }
    }
  }
}
.el-tooltip {
  margin-bottom: 24px;
  padding: 40px 0 40px 55px;
  margin-right: 16px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  cursor: pointer;
  .one {
    font-size: 21px;
    width: 48px;
    height: 48px;
    line-height: 48px;
    color: #fff;
    background: #162630;
    text-align: center;
    border-radius: 50%;
  }
  .right {
    margin-left: 8px;
    h2 {
      max-width: 180px;
      font-size: 17px;
      letter-spacing: -0.3px;
      line-height: 28px;
      color: #162630;
    }
    p {
      font-size: 13px;
      color: rgba(22, 38, 48, 0.6);
      letter-spacing: -0.1px;
      line-height: 20px;
    }
  }
}
.el-menu {
  height: 100%;
  border: none;
  &:not(.el-menu--collapse) {
    width: 296px;
  }
}
.el-menu-item.is-active {
  // border-left: 3px solid @mainColor;
  background-color: rgba(5, 170, 105, 0.04) !important;
}

.el-aside > ul > li.el-menu-item {
  margin: 0 16px !important;
  padding-left: 35px !important;
}
.el-aside > ul > li.el-menu-item i {
  margin-right: 23.88px;
}
/deep/ .el-submenu__title {
  margin: 0 16px !important;
  padding-left: 35px !important;
}
.el-aside > ul > li.el-menu-item:hover {
  background-color: rgba(5, 170, 105, 0.04) !important;
  color: #05aa69 !important;
}
.el-aside > ul > li.el-menu-item:hover i {
  color: #05aa69 !important;
}
.el-aside .el-submenu .el-submenu__title:hover {
  background: rgba(5, 170, 105, 0.04) !important;
  color: #05aa69 !important;
}
</style>
