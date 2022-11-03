<template>
  <div>
    <el-card class="box-card">
      <div class="titles_search">
        <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="100px">
          <div class="tongjimain">
            <div class="lefts">
              <p class="allwz">全部</p>
              <div>
                <p>成功请求金额：{{ tjdetail.askSuccessAmount }}</p>
                <p>成功支付金额：{{ tjdetail.paySuccessAmount }}</p>
                <p>手续费：{{ tjdetail.feeAmount }}</p>
              </div>
              <div>
                <p>成功订单：{{ tjdetail.paySuccessCount }}</p>
                <p>总订单：{{ tjdetail.orderCount }}</p>
                <p>
                  转化率：{{
                      tjdetail.paySuccessCount == 0 || !tjdetail.paySuccessCount
                        ? "0%"
                        : (
                          tjdetail.paySuccessCount / tjdetail.orderCount
                        ).toFixed(2) *
                        100 +
                        "%"
                  }}
                </p>
              </div>
            </div>
            <div class="lefts rights">
              <p class="allwz">今天</p>
              <div>
                <p>成功请求金额：{{ tjdetails.askSuccessAmount }}</p>
                <p>成功支付金额：{{ tjdetails.paySuccessAmount }}</p>
                <p>手续费：{{ tjdetails.feeAmount }}</p>
              </div>
              <div>
                <p>成功订单：{{ tjdetails.paySuccessCount }}</p>
                <p>总订单：{{ tjdetails.orderCount }}</p>
                <p>
                  转化率：{{
                      tjdetails.paySuccessCount == 0 || !tjdetails.paySuccessCount
                        ? "0%"
                        : (
                          tjdetails.paySuccessCount / tjdetails.orderCount
                        ).toFixed(2) *
                        100 +
                        "%"
                  }}
                </p>
              </div>
            </div>
            <div class="rights"></div>
          </div>
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="收款地址" prop="address">
                <el-input v-model="queryParams.address" placeholder="请输入收款地址" clearable size="small"
                  @keyup.enter.native="handleQuery" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="回调地址" prop="returnAddress">
                <el-input v-model="queryParams.returnAddress" placeholder="请输入回调地址" clearable size="small"
                  @keyup.enter.native="handleQuery" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="代付地址" prop="ownerAddress">
                <el-input v-model="queryParams.ownerAddress" placeholder="请输入代付地址" clearable size="small" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="商户订单号" prop="merchantOrderNo">
                <el-input v-model="queryParams.merchantOrderNo" placeholder="请输入商户订单号" clearable size="small"
                  @keyup.enter.native="handleQuery" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="到账通知" prop="haveUsdt">
                <el-select v-model="queryParams.haveUsdt" placeholder="请选择结果" clearable size="small">
                  <el-option :key="1" :label="'成功'" :value="1" />
                  <el-option :key="0" :label="'失败'" :value="0" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="回调通知" prop="haveUsdt">
                <el-select v-model="queryParams.message" placeholder="请选择结果" clearable size="small">
                  <el-option :key="1" :label="'成功'" :value="'1'" />
                  <el-option :key="0" :label="'失败'" :value="'0'" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="状态" prop="haveUsdt">
                <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
                  <el-option :key="0" label="待支付" :value="0" />
                  <el-option :key="1" label="支付成功" :value="1" />
                  <el-option :key="2" label="付款金额与订单金额不匹配" :value="2" />
                  <el-option :key="3" label="商户手续费不足" :value="3" />
                  <el-option :key="4" label="代收地址TRX矿工费不足" :value="4" />
                  <el-option :key="5" label="回调失败" :value="5" />
                  <el-option :key="6" label="支付失败" :value="6" />
                  <el-option :key="7" label="逾期未支付" :value="7" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="16">
              <el-form-item label="日期">
                <el-date-picker v-model="value2" value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange"
                  range-separator="至" :default-time="['00:00:00', '23:59:59']" start-placeholder="开始日期"
                  end-placeholder="结束日期" align="right">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="24">
              <div style="margin-left: 32px">
                <el-form-item>
                  <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                  <el-button icon="el-icon-refresh" type="primary" size="mini" @click="getinit">刷新</el-button>
                  <el-button icon="el-icon-refresh" size="mini" type="primary" @click="resetQuery">重置</el-button>
                  <el-button icon="el-icon-download" size="mini" type="primary" @click="getorderexport">导出</el-button>
                  <el-button size="mini" type="primary" @click="dialogVisible1 = true">代收测试</el-button>
                  <el-button type="primary" size="mini" @click="getispiliang">批量回调
                  </el-button>
                </el-form-item>
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>
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
          <el-table-column label="回调地址" align="center" width="180" prop="returnAddress">
            <template slot-scope="scope">
              <span>
                {{
                    scope.row.returnAddress && scope.row.returnAddress != "null"
                      ? scope.row.returnAddress
                      : "--"
                }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="手续费" align="center" width="140" prop="feeAmount" />
          <el-table-column label="商户订单号" align="center" prop="merchantOrderNo" width="180" />
          <el-table-column label="应收金额" align="center" width="140" prop="orderAmount" />
          <el-table-column label="实收金额" align="center" prop="receivedAmount" width="140" />
          <el-table-column label="到账通知" align="center" width="100" prop="haveUsdt">
            <template slot-scope="scope">
              <el-tag type="success" v-if="scope.row.haveUsdt === 1">成功</el-tag>
              <el-tag type="danger" v-else>失败</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="日志记录" align="center" width="180" prop="message">
            <template slot-scope="scope">
              <span>
                {{
                    scope.row.message && scope.row.message != "null"
                      ? scope.row.message
                      : "--"
                }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="订单创建时间" align="center" prop="createTime" width="160" />
          <el-table-column label="状态" align="center" width="180" prop="status">
            <template slot-scope="scope">
              <span>
                {{ scope.row.status | filterstatus }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="回调通知" align="center" width="100" prop="buttonFlag">
            <template slot-scope="scope">
              <el-tag type="success" v-if="
                scope.row.message === 'SUCCESS' ||
                scope.row.message === 'success'
              ">成功</el-tag>
              <el-tag type="danger" v-else>失败</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="错误信息" align="center" width="140" prop="payResult" />
          <el-table-column label="支付订单号" align="center" width="140" prop="orderSn" />
          <el-table-column label="成功时间" align="center" width="160" prop="successTime" />
          <el-table-column label="回调时间" align="center" width="160" prop="returnTime" />
          <el-table-column label="操作" align="center" width="120" fixed="right" prop="buttonFlag">
            <template slot-scope="scope">
              <div class="caozuopaiban">
                <el-button size="mini" type="primary" :disabled="scope.row.status == 5 ? false : true"
                  @click="huidiaoApi(scope.row)">重新回调</el-button>
                <el-button size="mini" type="primary" @click="openDetailFlag(scope.row)">订单详情</el-button>
                <el-button size="mini" type="primary" @click="buDandialog = true; budanItem = scope.row"
                  v-if="scope.row.status == 7">补单
                </el-button>
              </div>
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
    <el-dialog title="代收测试" :visible.sync="dialogVisible1" width="30%" :show-close="false">
      <div style="display: flex; justify-content: center">
        <el-input v-model="Googlecode" placeholder="谷歌验证码"></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="gettestdaishou">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="补单" :visible.sync="buDandialog" width="30%" :show-close="false">
      <div class="dialoginput">
        <p>支付者钱包地址:</p>
        <el-input v-model="userAddress" placeholder="请输入支付者钱包地址"></el-input>
      </div>
      <div class="dialoginput">
        <p>支付金额:</p>
        <el-input v-model="bigDecimal" placeholder="请输入支付金额"></el-input>
      </div>
      <div class="dialoginput">
        <p>谷歌验证码:</p>
        <el-input v-model="googleSignCode" placeholder="请输入谷歌验证码"></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="buDandialog = false">取 消</el-button>
        <el-button type="primary" @click="getbudan">确 定</el-button>
      </span>
    </el-dialog>

    <div class="orderdetail">
      <el-dialog :visible.sync="detaildialog" center width="50%" :show-close="false">
        <div class="detailmain">
          <h1>订单详情</h1>
          <el-tabs v-model="activeName">
            <el-tab-pane label="订单信息" name="first">
              <el-card class="box-card">
                <div slot="header" class="clearfix">
                  <span>订单信息</span>
                </div>
                <div class="row_between_start">
                  <el-col :span="11">
                    <el-row>
                      <span>收款地址:</span>
                      <span>{{ orderdetailitem.address }}</span>
                    </el-row>
                    <el-row>
                      <span>回调地址:</span>
                      <span>{{ orderdetailitem.returnAddress }}</span>
                    </el-row>
                    <el-row>
                      <span>TRX余额:</span>
                      <span>{{ orderdetailitem.trxBalance }}</span>
                    </el-row>
                    <el-row>
                      <span>USDT余额:</span>
                      <span>{{ orderdetailitem.usdtBalance }}</span>
                    </el-row>
                    <el-row>
                      <span>商户订单号:</span>
                      <span>{{ orderdetailitem.merchantOrderNo }}</span>
                    </el-row>
                    <el-row>
                      <span>应收金额:</span>
                      <span>{{ orderdetailitem.orderAmount }}</span>
                    </el-row>
                    <el-row>
                      <span>实收金额:</span>
                      <span>{{ orderdetailitem.receivedAmount }}</span>
                    </el-row>
                    <el-row>
                      <span>到账通知:</span>
                      <span>
                        <el-tag type="success" v-if="orderdetailitem.haveUsdt === 1">成功</el-tag>
                        <el-tag type="danger" v-else>失败</el-tag>
                      </span>
                    </el-row>
                  </el-col>
                  <el-col :span="11">
                    <el-row>
                      <span>日志记录:</span>
                      <span>{{ orderdetailitem.message }}</span>
                    </el-row>
                    <el-row>
                      <span>订单创建时间:</span>
                      <span>{{ orderdetailitem.createTime }}</span>
                    </el-row>
                    <el-row>
                      <span>状态:</span>
                      <span>{{ orderdetailitem.status | filterstatus }}</span>
                    </el-row>
                    <el-row>
                      <span>回调通知:</span>
                      <span>
                        <el-tag type="success" v-if="
                          orderdetailitem.message === 'SUCCESS' ||
                          orderdetailitem.message === 'success'
                        ">成功</el-tag>
                        <el-tag type="danger" v-else>失败</el-tag>
                      </span>
                    </el-row>
                    <el-row>
                      <span>错误信息:</span>
                      <span>{{ orderdetailitem.payResult }}</span>
                    </el-row>
                    <el-row>
                      <span>支付订单号:</span>
                      <span>{{ orderdetailitem.orderSn }}</span>
                    </el-row>
                    <el-row>
                      <span>回调时间:</span>
                      <span>{{ orderdetailitem.returnTime }}</span>
                    </el-row>
                    <el-row>
                      <span>成功时间:</span>
                      <span>{{ orderdetailitem.successTime }}</span>
                    </el-row>
                  </el-col>
                </div>
              </el-card>
            </el-tab-pane>
            <el-tab-pane label="接口信息" name="second">
              <el-tabs type="border-card">
                <el-tab-pane label="请求报文">
                  <el-table :data="baowendetail.requestData" style="width: 100%" border>
                    <el-table-column prop="label" label="字段键值" align="center">
                    </el-table-column>
                    <el-table-column prop="value" label="内容值" align="center">
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
                <el-tab-pane label="响应报文">
                  <el-table :data="baowendetail.responseData" style="width: 100%" border>
                    <el-table-column prop="label" label="字段键值" align="center">
                    </el-table-column>
                    <el-table-column prop="value" label="内容值" align="center">
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
                <el-tab-pane label="通知内容">
                  <el-table :data="baowendetail.returnRequestLog" style="width: 100%" border>
                    <el-table-column prop="label" label="字段键值" align="center">
                    </el-table-column>
                    <el-table-column prop="value" label="内容值" align="center">
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
              </el-tabs>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-dialog>
    </div>
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
      this.queryParams.startDate = this.value2[0];
      this.queryParams.endDate = this.value2[1];
      this.$apiFun.getAddressList(this.queryParams).then((result) => {
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
