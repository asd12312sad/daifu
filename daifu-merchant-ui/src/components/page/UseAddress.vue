<template>
  <div>
    <el-card class="box-card">

      <div class="tablemain">
        <el-table ref="singleTable" :data="tableData" style="width: 100%" row-key="id">
          <el-table-column type="selection" :reserve-selection="true" :selectable="selectable" width="55">
          </el-table-column>
          <el-table-column label="收款地址" align="center" width="180" prop="address">
            <template slot-scope="scope">
              <span @click="hashchaxun(scope.row.address)" style="cursor: pointer; color: #459f75">
                {{ scope.row.address }}
              </span>
            </template>
          </el-table-column>

          <el-table-column label="地址状态" align="center" width="180" prop="address">
            <template slot-scope="scope">
              <span  v-if="scope.row.status === 0" style="color: #00ff8b">
              启用
              </span>
              <span  v-if="scope.row.status === 1" style="color: #ff0000">
              停用
              </span>
            </template>
          </el-table-column>
          <el-table-column label="usdt余额" align="center" prop="usdtBalance" />
          <el-table-column label="在处理订单" align="center" prop="orderSn" />
          <el-table-column label="创建时间" align="center" prop="createTime" />
          <el-table-column label="操作">
            <template slot-scope="scope">

            <el-button size="mini" type="primary" @click="getCollectNotionalPooling(scope.row)">
              手动归集</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination background layout="prev, pager, next, sizes, jumper" :total="queryParams.total"
            @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-size="queryParams.pageSize"
            :current-page.sync="queryParams.pageNumber" :page-sizes="[20, 50, 100]">
          </el-pagination>
        </div>
      </div>
    </el-card>


  </div>
</template>
<script>
import axios from "axios";
import fileDownload from "js-file-download";
export default {
  name: "Orderlist",
  data() {
    return {
      tableDatas: [
        {
          date: "2016-05-02",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄",
        },
        {
          date: "2016-05-04",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1517 弄",
        },
        {
          date: "2016-05-01",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1519 弄",
        },
        {
          date: "2016-05-03",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1516 弄",
        },
      ],
      queryParams: {
        pageSize: 20,
        pageNumber: 1,
      },
      tableData: [],
      dialogVisible1: false,
      Googlecode: "",
      tjdetail: "",
      tjdetails: "",
      value2: [],
      hdmultipleSelection: [],
      detaildialog: false,
      activeName: "first",
      orderdetailitem: "",
      baowendetail: "",
      buDandialog: false,
      budanItem: "",
      userAddress: '',
      bigDecimal: '',
      googleSignCode: '',
    };
  },
  created() {
    this.getList();
    this.getdaishoutj();
  },
  filters: {
    filterstatus(value) {
      var value = parseInt(value);
      switch (value) {
        case 0:
          return "待支付";
          break;
        case 1:
          return "支付成功";
          break;
        case 2:
          return "付款金额与订单金额不匹配，自动补单";
          break;
        case 3:
          return "商户手续费不足，请充值Pay cloud，系统将在30分钟后自动回调";
          break;
        case 4:
          return "代收地址TRX矿工费余额不足，无法归集";
          break;
        case 5:
          return "返回回调失败";
          break;
        case 6:
          return "支付过程错误，请联系客服";
          break;
        case 7:
          return "逾期未支付";
          break;
        default:
          break;
      }
    },
  },
  methods: {
    getCollectNotionalPooling(row){
      this.$apiFun.collectNotionalPooling({id:row.id}).then(result => {
        this.$message({
          message: "操作成功",
          type: "success",
        });
      })
    },
    getbudan() {
      if (!this.userAddress || !this.bigDecimal || !this.googleSignCode) {
        this.$message.error("请输入完整信息！");
        return false
      }
      var data = {
        userAddress: this.userAddress,
        bigDecimal: this.bigDecimal,
        googleSignCode: this.googleSignCode,
        id: this.budanItem.id,
      }
      this.$apiFun.systemaddressbudan(data).then((result) => {
        if (result.code == 200) {
          this.userAddress = '';
          this.bigDecimal = '';
          this.googleSignCode = '';
          this.$message({
            message: "补单成功",
            type: "success",
          });
          this.getList();
        } else {
          this.$message({
            message: result.msg,
            type: "error",
          });
        }
        this.buDandialog = false;
      });
    },
    openDetailFlag(row) {
      this.orderdetailitem = row;
      let objNew = [];
      let objNews = [];
      let objNews1 = [];
      let objNews2 = [];
      if (row.requestLog) {
        let obj = JSON.parse(row.requestLog);

        for (let i in obj) {
          objNew.push({
            label: i,
            value: obj[i],
          });
        }
      }

      if (row.responseLog) {
        let objs = JSON.parse(row.responseLog);
        for (let t in objs) {
          objNews.push({
            label: t,
            value: objs[t],
          });
        }
      }

      if (row.responseLog && objNews[1].label == 'data') {
        let objs1 = objNews[1].value;
        for (let v in objs1) {
          objNews1.push({
            label: v,
            value: objs1[v],
          });
        }
        var newarray = objNews.concat(objNews1);
        newarray = newarray.filter((a) => {
          return a.label != "data";
        });
      } else {
        var newarray = objNews
      }

      if (row.returnRequestLog) {
        let objs2 = JSON.parse(row.returnRequestLog);
        for (let w in objs2) {
          objNews2.push({
            label: w,
            value: objs2[w],
          });
        }
      }

      this.baowendetail = {
        requestData: objNew && objNew != "" ? objNew : [],
        responseData: newarray && newarray != "" ? newarray : [],
        returnRequestLog: objNews2 && objNews2 != "" ? objNews2 : [],
      };
      this.detaildialog = true;
    },
    selectable(row, index) {
      if (row.status == 5) {
        return true; //通过某个值来进行判断，规定那一行的选择框被禁用
      }
    },
    getispiliang() {
      if (this.hdmultipleSelection.length > 0) {
        this.getplhuitiao();
      } else {
        this.$message.error("请选择要批量回调的订单！");
      }
    },
    getplhuitiao() {
      for (let index = 0; index < this.hdmultipleSelection.length; index++) {
        this.shenheitem = this.hdmultipleSelection[index];
        this.getplhuidiaoApi(this.shenheitem);
        if (index == 0) {
          this.$message({
            message: "正在处理请稍等！",
            duration: "800",
          });
        }
        if (index + 1 == this.hdmultipleSelection.length) {
          setTimeout(() => {
            this.$message.success("批量回调成功！");
          }, 1000);
          this.getList();
        }
      }
    },
    getplhuidiaoApi(row) {
      this.$apiFun.getAddressHuiDiao({ id: row.id }).then((result) => { });
    },
    getinit() {
      this.value2 = [];
      this.queryParams = {
        pageNumber: this.queryParams.pageNumber,
        pageSize: this.queryParams.pageSize,
      };
      this.getList();
      this.getdaishoutj();
      setTimeout(() => {
        this.$message({
          message: "刷新成功",
          type: "success",
        });
      }, 1000);
    },
    getdaishoutj() {
      this.$apiFun.homecollectionheader().then((res) => {
        this.tjdetail = res.data.all;
        this.tjdetails = res.data.today;
      });
    },
    gettestdaishou() {

      this.$apiFun.testMerchant({ googleSign: this.Googlecode }).then((res) => {
        if (res.code == 200) {
          console.log(res.data)
          // window.open(
          //   "https://www.paycloud.pro/py?time=" +
          //     new Date().getTime() +
          //     "&address=" +
          //     res.data.address,
          //   "_blank"
          // );
          // var item = {
          //   time: new Date().getTime(),
          //   address: res.data.address,
          //   backUrl: 'back'
          // }
          // var newitem = encodeURIComponent(JSON.stringify(item))
          // window.location.href = "https://www.paycloud.pro/py?newitem=" + newitem
          window.location.href = res.data.data.cashierDesk
        } else {
          this.$message({
            message: "代收失败",
            type: "error",
          });
        }
        this.dialogVisible1 = false;
      });
    },
    // 导出
    getorderexport() {
      axios({
        method: "get",
        url: `https://api.adminuu.pro/mer/system/address/export`,
        // 注意配置响应方式 responseType: 'blob'
        headers: {
          TokenType: localStorage.getItem("tokenType"), //添加token,需要结合自己的实际情况添加，
          Authorization: localStorage.getItem("token"),
        },
        responseType: "blob",
      })
        .then((res) => {
          fileDownload(res.data, "代收订单.xls");
        })
        .catch((error) => {
          // Message.error("下载失败");
        });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.value2 = [];
      this.queryParams = {
        pageNumber: this.queryParams.pageNumber,
        pageSize: this.queryParams.pageSize,
      };
      this.handleQuery();
    },
    getList() {
      this.$apiFun.getUseAddressList(this.queryParams).then((result) => {
        this.tableData = result.data.records;
        this.queryParams.total = result.data.total;
      });
    },
    huidiaoApi(row) {
      this.$apiFun.getAddressHuiDiao({ id: row.id }).then((result) => {
        if (result.code == 200) {
          this.$message({
            message: "回调成功",
            type: "success",
          });
          this.getList();
        } else {
          this.$message({
            message: "回调失败请检查您网站的API接口是否正常",
            type: "error",
          });
        }
      });
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.queryParams.pageNumber = val;
      this.getList();
    },
  },
};
</script>
<style scoped>
@keyframes dialog-fade-in {
  0% {
    -webkit-transform: translate3d(0, 100%, 0) !important;
    transform: translate3d(0, 100%, 0) !important;
    opacity: 0 !important;
  }

  100% {
    -webkit-transform: translate3d(0, 0, 0) !important;
    transform: translate3d(0, 0, 0) !important;
    opacity: 1 !important;
  }
}

@keyframes dialog-fade-out {
  0% {
    -webkit-transform: translate3d(0, 0, 0) !important;
    transform: translate3d(0, 0, 0) !important;
    opacity: 1 !important;
  }

  100% {
    -webkit-transform: translate3d(0, -100%, 0) !important;
    transform: translate3d(0, -100%, 0) !important;
    opacity: 0 !important;
  }
}

@-webkit-keyframes dialog-fade-in {
  0% {
    -webkit-transform: translate3d(0, 100%, 0) !important;
    transform: translate3d(0, 100%, 0) !important;
    opacity: 0 !important;
  }

  100% {
    -webkit-transform: translate3d(0, 0, 0) !important;
    transform: translate3d(0, 0, 0) !important;
    opacity: 1 !important;
  }
}

@-webkit-keyframes dialog-fade-out {
  0% {
    -webkit-transform: translate3d(0, 0, 0) !important;
    transform: translate3d(0, 0, 0) !important;
    opacity: 1 !important;
  }

  100% {
    -webkit-transform: translate3d(0, -100%, 0) !important;
    transform: translate3d(0, -100%, 0) !important;
    opacity: 0 !important;
  }
}
</style>
<style scoped lang="less">
.dialoginput {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;

  p {
    margin-bottom: 6px;
  }
}

.caozuopaiban .el-button {
  margin: 5px;
}

.orderdetail {
  .el-dialog__wrapper {
    display: flex;
    justify-content: flex-end;

    /deep/ .el-dialog {
      border-radius: 0 !important;
      margin: 0 !important;
      animation: rtl-drawer-in 0.3s 1ms;
    }

    /deep/ .el-dialog__header {
      display: none !important;
    }
  }

  .detailmain {
    h1 {
      color: #000 !important;
      text-align: center;
      margin-bottom: 20px;
    }

    /deep/.el-card__header {
      color: #459f75;
      font-size: 20px;
      font-weight: 500;
    }

    /deep/ .el-card {
      background-color: #f5f5f5;
    }

    .row_between_start {
      display: flex;
      justify-content: space-between;

      /deep/ .el-row {
        padding: 10px 0;

        span:last-child {
          word-wrap: break-word;
          word-break: break-all;
        }
      }
    }
  }
}

/deep/ .el-dialog {
  border-radius: 10px !important;
}

.tablemain {
  .pagination {
    margin-top: 20px;
  }
}

.titles_search {
  // display: flex;
  align-items: center;
  margin-bottom: 20px;

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

  .el-select {
    width: 264px;

    /deep/ .el-input__inner {
      height: 38px !important;
      line-height: 38px !important;
      background: rgba(22, 38, 48, 0.04) !important;
      border-color: rgba(22, 38, 48, 0.04) !important;
      border-radius: 38px;
    }
  }

  .tongjimain {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 20px 0;

    .lefts {
      width: 48%;
      display: flex;
      align-items: center;
      background: #e8a20c;
      color: #fff;
      padding: 15px 0;
      border-radius: 10px;

      .allwz {
        width: 20%;
        text-align: center;
      }

      div {
        width: 40%;
        text-align: left;
      }
    }

    .rights {
      background: #1fb700;
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
</style>
