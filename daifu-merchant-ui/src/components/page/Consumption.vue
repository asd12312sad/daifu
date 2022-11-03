<template>
  <div>
    <el-card class="box-card">
      <div class="titles_search">
        <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="120px">
          <div class="tongjimain">
            <div class="lefts">
              <div>
                <p>在途金额：{{ passage.paySuccessAmount }}</p>
                <p>在途交易：{{ passage.orderCount }}</p>
                <!-- <p>手续费：{{ passage.feeAmount ? passage.feeAmount : 0 }}</p> -->
              </div>
            </div>
            <div class="lefts ins">
              <div>
                <p>成功金额：{{ successSummer.paySuccessAmount }}</p>
                <p>成功交易：{{ successSummer.orderCount }}</p>
                <p>手续费：{{ successSummer.feeAmount }}</p>
              </div>
            </div>
            <div class="lefts rights">
              <div>
                <p>拒绝金额：{{ failSummer.paySuccessAmount }}</p>
                <p>拒绝交易：{{ failSummer.orderCount }}</p>
                <!-- <p>
                  手续费：{{ failSummer.feeAmount ? passage.feeAmount : 0 }}
                </p> -->
              </div>
            </div>
          </div>
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="收款地址" prop="address">
                <el-input v-model="queryParams.address" placeholder="请输入收款地址" clearable size="small" />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item label="回调地址" prop="returnAddress">
                <el-input v-model="queryParams.returnAddress" placeholder="请输入回调地址" clearable size="small" />
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
                <el-input v-model="queryParams.merchantOrderNo" placeholder="请输入商户订单号" clearable size="small" />
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
                  <el-option :key="0" :label="'支付失败'" :value="0" />
                  <el-option :key="1" :label="'支付中'" :value="1" />
                  <el-option :key="2" :label="'支付到账'" :value="2" />
                  <el-option :key="3" :label="'队列中'" :value="3" />
                  <el-option :key="4" :label="'待处理'" :value="4" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="16">
              <el-form-item label="日期" prop="haveUsdt">
                <el-date-picker v-model="value2" value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange"
                  range-separator="至" :default-time="['00:00:00', '23:59:59']" start-placeholder="开始日期"
                  end-placeholder="结束日期" align="right">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="24">
              <div style="margin-left: 34px">
                <el-form-item>
                  <div style="display: flex; align-items: center">
                    <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                    <el-button icon="el-icon-refresh" type="primary" size="mini" @click="getinit()">刷新</el-button>
                    <el-button icon="el-icon-refresh" type="primary" size="mini" @click="resetQuery">重置</el-button>
                    <el-button icon="el-icon-download" type="primary" size="mini" @click="getorderexport">导出</el-button>
<!--                    <el-button size="mini" type="primary" @click="dialogVisible1 = true">代付测试</el-button>-->
                    <el-button type="primary" size="mini" @click="getispiliang">批量回调
                    </el-button>
                  </div>
                </el-form-item>
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="tablemain">
        <el-table ref="singleTable" :data="tableData" highlight-current-row style="width: 100%"
          @selection-change="handleSelectionChange" row-key="id">
          <el-table-column type="selection" :reserve-selection="true" :selectable="selectable" width="55">
          </el-table-column>
          <el-table-column label="收款地址" align="center" prop="address" width="180">
            <template slot-scope="scope">
              <span @click="hashchaxun(scope.row.address)" style="cursor: pointer; color: #459f75">
                {{ scope.row.address }}
              </span>
            </template>
          </el-table-column>
          <!-- <el-table-column label="商户ID" align="center" prop="merchantId" /> -->
          <el-table-column label="回调地址" align="center" prop="returnAddress" width="180">
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
          <el-table-column label="USDT金额" align="center" prop="usdtBalance" width="140" />
          <el-table-column label="商户订单号" align="center" prop="merchantOrderNo" width="180" />
          <el-table-column label="手续费" align="center" width="140" prop="feeAmount" />
          <el-table-column label="到账通知" align="center" prop="haveUsdt">
            <template slot-scope="scope">
              <el-tag type="success" v-if="scope.row.haveUsdt === 1">成功</el-tag>
              <el-tag type="danger" v-else>失败</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="日志记录" align="center" prop="message" width="180">
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
          <el-table-column label="付款地址" align="center" prop="ownerAddress" width="180" />
          <el-table-column label="订单创建时间" align="center" prop="createTime" width="160" />
          <el-table-column label="状态" align="center" prop="status" width="140">
            <template slot-scope="scope">
              <span>
                {{ scope.row.status | filterstatus }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="错误信息" align="center" prop="payResult" width="140" />
          <el-table-column label="支付订单号" align="center" prop="orderSn" width="140" />
          <el-table-column label="成功时间" align="center" prop="successTime" width="160" />
          <el-table-column label="回调通知" align="center" prop="buttonFlag">
            <template slot-scope="scope">
              <el-tag type="success" v-if="
                scope.row.message === 'SUCCESS' ||
                scope.row.message === 'success'
              ">成功</el-tag>
              <el-tag type="danger" v-else>失败</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="回调时间" align="center" prop="returnTime" width="160" />
          <el-table-column label="操作" align="center" prop="buttonFlag" width="140" fixed="right">
            <template slot-scope="scope">
              <div class="caozuopaiban">
                <el-button size="mini" type="primary" @click="huidiaoApi(scope.row)"
                  :disabled="scope.row.status == 2 ? false : true">重新回调</el-button>

<!--                <el-button size="mini" type="warning" @click="getchongxin(scope.row)"-->
<!--                  :disabled="(scope.row.status == 0 || scope.row.status == 2 || scope.row.status == 3) ? true : false">-->
<!--                  重新支付</el-button>-->

                <el-button size="mini" type="primary" @click="openDetailFlag(scope.row)">订单详情</el-button>

                <el-button size="mini" type="primary" @click="getdaozhang(scope.row, 1)" v-if="scope.row.status == 1">
                  手动标记到账</el-button>

                <el-button size="mini" type="primary" @click="getdaozhang(scope.row, 2)" v-if="scope.row.status == 1">
                  手动标记放弃</el-button>
                <!-- <el-button
                size="mini"
                type="text"
                @click="
                  cxpayitem = scope.row;
                  editdialogVisible = true;
                "
                >修改金额</el-button
              > -->
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page.sync="queryParams.pageNumber" :page-sizes="[20, 50, 100]" :page-size="queryParams.pageSize"
            layout="prev, pager, next, sizes, jumper" :total="queryParams.total">
          </el-pagination>
        </div>
      </div>
    </el-card>
    <el-dialog title="代付测试" :visible.sync="dialogVisible1" width="30%" :show-close="false">
      <div>
        <el-form label-width="100px">
          <el-form-item label="收款地址">
            <el-input v-model="shouaddress" placeholder="请输入收款地址"></el-input>
          </el-form-item>
          <el-form-item label="支付金额">
            <el-input v-model="paymoney" placeholder="请输入支付金额"></el-input>
          </el-form-item>
          <el-form-item label="谷歌验证码">
            <el-input v-model="Googlecode" placeholder="请输入谷歌验证码"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="gettestdaifu">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="请输入谷歌验证码" :visible.sync="dialogVisible" width="30%">
      <div v-if="buzhou == 1">
        <div style="
            text-align: center;
            color: #459f75;
            font-size: 24px;
            font-weight: 600;
            margin-bottom: 40px;
          ">
          请先在区块浏览器中核查到账信息
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="buzhou = 2">我已核查确定无误，确定</el-button>
        </span>
      </div>

      <div v-if="buzhou == 2">
        <div style="display: flex; justify-content: center; margin-bottom: 40px">
          <el-input v-model="Googlecode1" placeholder="谷歌验证码"></el-input>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="getcxpay">确 定</el-button>
        </span>
      </div>
    </el-dialog>
    <el-dialog title="修改金额" :visible.sync="editdialogVisible" width="30%" :show-close="false">
      <div style="display: flex; justify-content: center">
        <el-input v-model="amount" placeholder="请输入修改金额"></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editdialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="geteditmoney">确 定</el-button>
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
                      <span>付款地址:</span>
                      <span>{{ orderdetailitem.ownerAddress }}</span>
                    </el-row>
                    <el-row>
                      <span>回调地址:</span>
                      <span>{{ orderdetailitem.returnAddress }}</span>
                    </el-row>
                    <el-row>
                      <span>USDT金额:</span>
                      <span>{{ orderdetailitem.usdtBalance }}</span>
                    </el-row>
                    <el-row>
                      <span>商户订单号:</span>
                      <span>{{ orderdetailitem.merchantOrderNo }}</span>
                    </el-row>
                    <el-row>
                      <span>到账通知:</span>
                      <span>
                        <el-tag type="success" v-if="orderdetailitem.haveUsdt === 1">成功</el-tag>
                        <el-tag type="danger" v-else>失败</el-tag>
                      </span>
                    </el-row>
                    <el-row>
                      <span>日志记录:</span>
                      <span>{{ orderdetailitem.message }}</span>
                    </el-row>
                  </el-col>
                  <el-col :span="11">
                    <el-row>
                      <span>订单创建时间:</span>
                      <span>{{ orderdetailitem.createTime }}</span>
                    </el-row>
                    <el-row>
                      <span>状态:</span>
                      <span>{{ orderdetailitem.status | filterstatus }}</span>
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
      queryParams: {
        pageSize: 20,
        pageNumber: 1,
      },
      tableData: [],
      dialogVisible1: false,
      shouaddress: "",
      paymoney: "",
      Googlecode: "",
      dialogVisible: false,
      cxpayitem: "",
      editdialogVisible: false,
      amount: "",
      cxpayshow: true,
      Googlecode1: "",
      buzhou: 1,
      value2: [],
      failSummer: "",
      passage: "",
      successSummer: "",
      hdmultipleSelection: [],
      detaildialog: false,
      activeName: "first",
      orderdetailitem: "",
      baowendetail: "",
    };
  },
  created() {
    this.getList();
    this.getdaifutj();
  },
  filters: {
    filterstatus(value) {
      var value = parseInt(value);
      switch (value) {
        case 0:
          return "支付失败（不可操作）";
          break;
        case 1:
          return "冻结（创建订单30分钟后可操作，其余时间不可操作）";
          break;
        case 2:
          return "支付到账（只可以操作回调）";
          break;
        case 3:
          return "队列中（不可操作）";
          break;
        case 4:
          return "待处理";
          break;
        default:
          break;
      }
    },
  },
  methods: {
    getdaozhang(item, val) {
      var times = Date.parse(new Date(item.createTime)) + 30 * 60 * 1000;
      var newtime = new Date().getTime()
      if (newtime > times) {
        if (val == 1) {
          this.$confirm('您确定要标记到账吗？', '标记到账', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
          }).then(({ value }) => {
            this.$apiFun.payOrdermarkPayment({ id: item.id }).then((result) => {
              if (result.code == 200) {
                this.$message({
                  message: "标记到账成功",
                  type: "success",
                });
                this.getList();
              } else {
                this.$message({
                  message: res.msg,
                  type: "error",
                });
              }
            });
          })
        } else {
          this.$confirm('您确定要标记放弃吗？', '标记失败', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
          }).then(({ value }) => {
            this.$apiFun.payOrderabandon({ id: item.id }).then((result) => {
              if (result.code == 200) {
                this.$message({
                  message: "标记放弃成功",
                  type: "success",
                });
                this.getList();
              } else {
                this.$message({
                  message: res.msg,
                  type: "error",
                });
              }
            });
          })
        }
      } else {
        this.$message.error("请30分钟后再点击吧！");
      }
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
      if (row.haveUsdt != 1) {
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
      this.$apiFun.getPayOrderHuiDiao({ id: row.id }).then((result) => { });
    },
    handleSelectionChange(val) {
      this.hdmultipleSelection = val;
    },
    getinit() {
      // location.reload()
      this.value2 = [];
      this.queryParams = {
        pageNumber: this.queryParams.pageNumber,
        pageSize: this.queryParams.pageSize,
      };
      this.getList();
      this.getdaifutj();
      setTimeout(() => {
        this.$message({
          message: "刷新成功",
          type: "success",
        });
      }, 1000);
    },
    getdaifutj() {
      this.$apiFun.homepayheader().then((res) => {
        this.failSummer = res.data.failSummer;
        this.passage = res.data.passage;
        this.successSummer = res.data.successSummer;
      });
    },
    getchongxin(item) {
      if (item.status == 1) {
        var times = Date.parse(new Date(item.createTime)) + 30 * 60 * 1000;
        var newtime = new Date().getTime()
        if (newtime > times) {
          if (this.cxpayshow == true) {
            this.cxpayshow = false;
            this.cxpayitem = item;
            this.dialogVisible = true;
            setTimeout(() => {
              this.cxpayshow = true;
            }, 60000);
          } else {
            this.$message.error("请1分钟后再发起支付吧！");
          }
        } else {
          this.$message.error("请30分钟后再发起支付吧！");
        }
      } else {
        if (this.cxpayshow == true) {
          this.cxpayshow = false;
          this.cxpayitem = item;
          this.dialogVisible = true;
          setTimeout(() => {
            this.cxpayshow = true;
          }, 60000);
        } else {
          this.$message.error("请1分钟后再发起支付吧！");
        }
      }
    },
    geteditmoney() {
      this.$apiFun
        .payOrderupdateamount({ id: this.cxpayitem.id, amount: this.amount })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success("金额修改成功");
          } else {
            this.$message.error("修改失败");
          }
          this.editdialogVisible = false;
          this.cxpayitem = "";
        });
    },
    getcxpay() {
      if (this.Googlecode1 == "") {
        this.$message.error("请填写谷歌验证码");
        return false;
      }
      this.$apiFun
        .payOrderrealpay({
          id: this.cxpayitem.id,
          googleSign: this.Googlecode1,
        })
        .then((res) => {
          if (res.code == 200) {
            this.$message.success("重新支付成功");
          } else {
            this.$message.error(res.msg);
          }
          this.dialogVisible = false;
          this.cxpayitem = "";
        });
    },
    gettestdaifu() {
      if (
        this.shouaddress == "" ||
        this.paymoney == "" ||
        this.Googlecode == ""
      ) {
        this.$message({
          message: "请输入完整信息",
          type: "error",
        });
        return false;
      }
      var data = {
        address: this.shouaddress,
        amount: this.paymoney,
        googleSign: this.Googlecode,
      };
      this.$apiFun.testPay(data).then((res) => {
        if (res.code == 200) {
          this.$message.success("付款成功订单ID" + res.data.orderId);
          this.getList();
        } else {
          this.$message({
            type: "error",
            message: "支付失败" + res.msg,
          });
        }
        this.dialogVisible1 = false;
      });
    },
    // 导出
    getorderexport() {
      axios({
        method: "get",
        url: `https://api.adminuu.pro/mer/payOrder/export`,
        // 注意配置响应方式 responseType: 'blob'
        headers: {
          TokenType: localStorage.getItem("tokenType"), //添加token,需要结合自己的实际情况添加，
          Authorization: localStorage.getItem("token"),
        },
        responseType: "blob",
      })
        .then((res) => {
          fileDownload(res.data, "代付订单.xls");
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

      this.$apiFun.getPayOrderList(this.queryParams).then((result) => {
        if (result.code == 200) {
          this.tableData = result.data.records;
          this.queryParams.total = result.data.total;
        }
      });
    },
    huidiaoApi(row) {

      this.$apiFun.getPayOrderHuiDiao({ id: row.id }).then((result) => {
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

<style scoped lang="less">
/deep/ .el-message-box {
  text-align: center !important;
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

.caozuopaiban .el-button {
  margin: 5px;
}

/deep/ .el-dialog {
  border-radius: 10px !important;
}

// /deep/ .el-form-item--medium .el-form-item__content{
//   align-items: center;
//   display: flex;
// }
// /deep/ .el-range-editor--medium.el-input__inner{
//   height: 28px !important;
// }
.tablemain {
  .pagination {
    margin-top: 20px;
  }
}

.titles_search {
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
      width: 31%;
      display: flex;
      align-items: center;
      background: #006ef4;
      color: #fff;
      padding: 15px;
      border-radius: 10px;
      height: 135px;

      p {
        line-height: 35px;
      }

      .allwz {
        width: 20%;
        text-align: center;
      }

      div {
        width: 40%;
        text-align: left;
      }
    }

    .ins {
      background: #1fb700;
    }

    .rights {
      background: #e24e5b;
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
