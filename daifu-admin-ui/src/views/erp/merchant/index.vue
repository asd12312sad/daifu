<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable size="small"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['erp:merchant:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['erp:merchant:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['erp:merchant:remove']">删除</el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="merchantList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="所属代理" align="center" prop="agentName">

        <template slot-scope="scope">
          <span>{{ scope.row.agentName ? scope.row.agentName : "平台直属" }}</span>
        </template>
      </el-table-column>

      <el-table-column label="归集地址" align="center" prop="usdtAddress" />
      <el-table-column label="trx手续费余额(请保证最低有50trx)" align="center" prop="trxBalance" />
      <el-table-column label="usdt手续费余额" align="center" prop="balance" />
      <el-table-column label="trx手续费(充值)地址" align="center" prop="trxAddress" />
      <el-table-column label="usdt付款(充值)地址" align="center" prop="usdtPayAddress" />
      <el-table-column label="付款地址USDT余额" align="center" prop="usdtBalance" />
      <el-table-column label="付款地址TRX余额(请保证最低有10trx)" align="center" prop="usdtPayTrxBalance" />
      <el-table-column label="试用额度" align="center" prop="trialQuota" />
      <el-table-column label="手续费率" align="center" prop="fee" />
      <el-table-column label="密钥" align="center" prop="sign" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">

          <el-button size="mini" type="text" icon="el-icon-edit" @click="openEmailUpdate(scope.row)"
            v-hasPermi="['erp:merchant:edit']">修改安全邮箱</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['erp:merchant:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="recharge(scope.row)"
            v-hasPermi="['erp:merchant:edit']">充值手续费</el-button>

          <el-button size="mini" type="text" icon="el-icon-edit" @click="addTrialQuota(scope.row)"
            v-hasPermi="['erp:merchant:edit']">增加试用额度</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="updateGoogleSignCode(scope.row)"
            v-hasPermi="['erp:merchant:edit']">重置谷歌验证器</el-button>

          <el-button size="mini" type="text" icon="el-icon-edit" @click="updatePassWord(scope.row)"
            v-hasPermi="['erp:merchant:edit']">重置密码</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['erp:merchant:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改商户管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="70%" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="200px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="usdt收款地址" prop="usdtAddress">
          <el-input v-model="form.usdtAddress" placeholder="请输入usdt收款地址" />
        </el-form-item>
        <el-form-item label="代收手续费率（0.01为百分之一）" prop="fee">
          <el-input v-model="form.fee" placeholder="请输入手续费" />
        </el-form-item>

        <el-form-item label="代付手续费（单笔收取）" prop="fee">
          <el-input v-model="form.payFee" placeholder="请输入手续费" />
        </el-form-item>

        <el-form-item label="telegram机器人密钥" prop="fee">
          <el-input v-model="form.telegramBotToken" placeholder="请输入手续费" />
        </el-form-item>
        <el-form-item label="回调地址" prop="fee">
          <el-input v-model="form.returnAddress" placeholder="请输入回调地址" />
        </el-form-item>
        <el-form-item label="账号" prop="fee">
          <el-input v-model="form.account" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="fee" v-if="form.id === null">
          <el-input v-model="form.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="谷歌验证码" prop="adminGoogleSign">
          <el-input v-model="adminGoogleSign" placeholder="请输入谷歌验证码" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>


    <el-dialog title="修改安全邮箱" :visible.sync="openEmailUpdateFlag" width="900px" append-to-body>
      <el-form ref="form" label-width="180px">
        <el-form-item label="请输入新的邮箱" prop="name">
          <el-input v-model="email" placeholder="请输入新的邮箱" />
        </el-form-item>
        <el-form-item label="请输入验证码" prop="payAmount" >
          <div style="display: flex">
            <el-input v-model="emailCode" placeholder="请输入验证码" /> <el-button @click="getEmailCode">获取验证码</el-button>

          </div>
        </el-form-item>
        <el-form-item label="谷歌验证码" prop="payAmount">
          <el-input v-model="adminGoogleSign1" placeholder="请输入谷歌验证码" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPay">确 定</el-button>
        <el-button @click="cancelPay">取 消</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
import { addTrialQuota, updateGoogleSignCodeApi, listMerchant, testPay, getMerchant, recharge, delMerchant, addMerchant, updateMerchant, exportMerchant, testMerchant,getEmailCode,updateEmailCode } from "@/api/erp/merchant";
export default {
  name: "Merchant",
  data() {
    return {
      openEmailUpdateFlag: false,
      // 遮罩层
      loading: true,
      emailCode:'',
      // 导出遮罩层
      exportLoading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 商户管理表格数据
      merchantList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,

      },
      // 表单参数
      form: {},
      payId: "",
      payAddress: "",
      email:"",
      payAmount: "",
      // 表单校验
      rules: {
      },
      adminGoogleSign: '',
      adminGoogleSign1: ''
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询商户管理列表 */
    getList() {
      this.loading = true;
      listMerchant(this.queryParams).then(response => {
        this.merchantList = response.rows;
        this.total = parseInt(response.total);
        this.loading = false;
      });
    },
    cancelPay() {
      this.payId = ""
      this.payAddress = ""
      this.payAmount = ""
      this.openEmailUpdateFlag = false
    },

    updateGoogleSignCode(row) {
      this.$prompt('请输入谷歌验证码', '重置谷歌验证器', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        var query = {
          adminGoogleSign: value,
          id: row.id
        }
        updateGoogleSignCodeApi(query).then(response => {
          if (response.code == 200) {
            this.$message.success("重置谷歌验证成功")
          } else {
            this.$message.error(response.msg)
          }

        })
      }).catch(() => {
      });
    },
    getEmailCode(){
      if (!this.email){
        this.$message.error("请先输入邮箱")
        return
      }
      getEmailCode({
        id:this.payId,
        email:this.email
      }).then(res=>{
        this.$message.success()
      })

    },
    submitPay() {
      var query = {
        id:this.payId,
        email: this.email,
        code: this.emailCode,
        adminGoogleSign: this.adminGoogleSign1,
      }
      updateEmailCode(query).then(response => {
        this.$message.success("修改成功")
      }).catch((response) => {
        this.$message({
          type: 'info',
          message:  response.MSG
        });
        this.openEmailUpdateFlag = false;
      });

    },
    updatePassWord(row) {
      this.$prompt('请输入密码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        // updateMerchant({ id: row.id, password: value }).then(response => {
        //   this.$message.success("修改成功")
        // })
        var password = value
        this.$prompt('请输入谷歌验证码', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          var query = {
            adminGoogleSign: value
          }
          updateMerchant({ id: row.id, password: password }, query).then(response => {
            if (response.code == 200) {
              this.$message.success("修改成功")
            } else {
              this.$message.error(response.msg)
            }
          })
        }).catch(() => {
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        });
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    openEmailUpdate(row) {
      this.payId = row.id;
      this.openEmailUpdateFlag = true;
    },
    openTest(row) {
      this.$prompt('请输入谷歌验证码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        testMerchant(row.id, { adminGoogleSign: value }).then(response => {
          console.log(response.data)
          window.open("https://www.paycloud.pro/py?time=" + new Date().getTime() + "&address=" + response.data.address, "_blank");
        });
      }).catch(() => {

      });
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,

        name: null,

        balance: null,

        createBy: null,

        createTime: null,

        updateBy: null,

        updateTime: null,

        fee: null,

      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加商户管理";
    },
    addTrialQuota(row) {
      this.$prompt('请输入商户' + row.id + '增加的试用额度.负数为扣款', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        // addTrialQuota(row.id, value).then(response => {
        //   this.$message({
        //     type: 'success',
        //     message: '增加的试用额度为: ' + value
        //   });
        //   this.getList()
        // })
        var trialQuota = value
        this.$prompt('请输入谷歌验证码', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          var query = {
            trialQuota: trialQuota,
            adminGoogleSign: value
          }
          addTrialQuota(row.id, query).then(response => {
            this.$message({
              type: 'success',
              message: '增加的试用额度为: ' + trialQuota
            });
            this.getList()
          })
        }).catch(() => {
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        });
      });
    },
    recharge(row) {
      this.$prompt('请输入商户' + row.id + '充值金额.负数为扣款', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        var amount = value
        this.$prompt('请输入谷歌验证码', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          var query = {
            amount: amount,
            adminGoogleSign: value
          }
          recharge(row.id, query).then(response => {
            this.$message({
              type: 'success',
              message: '充值的金额为: ' + amount
            });
            this.getList()
          })
        }).catch(() => {
        });
      }).catch(() => {
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getMerchant(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改商户管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateMerchant(this.form, { adminGoogleSign: this.adminGoogleSign }).then(response => {
              this.$message.success("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMerchant(this.form, { adminGoogleSign: this.adminGoogleSign }).then(response => {
              this.$message.success("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      // this.$confirm('是否确认删除商户管理编号为"' + ids + '"的数据项?', "警告", {
      //   confirmButtonText: "确定",
      //   cancelButtonText: "取消",
      //   type: "warning"
      // }).then(function () {
      //   return delMerchant(ids);
      // }).then(() => {
      //   this.getList();
      //   this.$message.success("删除成功");
      // }).catch(() => { });
      this.$prompt('请输入谷歌验证码', '是否确认删除商户管理编号为"' + ids + '"的数据项?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        var query = {
          adminGoogleSign: value
        }
        delMerchant(ids, query).then(response => {
          if (response.code == 200) {
            this.$message.success("删除成功")
          } else {
            this.$message.error(response.msg)
          }
        })

      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有商户管理数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.exportLoading = true;
        return exportMerchant(queryParams);
      }).then(response => {
        this.download(response.msg);
        this.exportLoading = false;
      }).catch(() => { });
    }
  }
};
</script>
