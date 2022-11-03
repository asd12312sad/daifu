<template>
  <div>
    <el-card class="box-card">
      <div class="titles_search">
        <el-form
          :model="queryParams"
          ref="queryForm"
          :inline="true"
          label-width="100px"
        >
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="收款地址" prop="address">
                <el-input
                  v-model="queryParams.address"
                  placeholder="请输入收款地址"
                  clearable
                  size="small"
                  @keyup.enter.native="handleQuery"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="回调地址" prop="returnAddress">
                <el-input
                  v-model="queryParams.returnAddress"
                  placeholder="请输入回调地址"
                  clearable
                  size="small"
                  @keyup.enter.native="handleQuery"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="代付地址" prop="ownerAddress">
                <el-input
                  v-model="queryParams.ownerAddress"
                  placeholder="请输入代付地址"
                  clearable
                  size="small"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="商户订单号" prop="merchantOrderNo">
                <el-input
                  v-model="queryParams.merchantOrderNo"
                  placeholder="请输入商户订单号"
                  clearable
                  size="small"
                  @keyup.enter.native="handleQuery"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="到账通知" prop="haveUsdt">
                <el-select
                  v-model="queryParams.haveUsdt"
                  placeholder="请选择结果"
                  clearable
                  size="small"
                >
                  <el-option :key="1" :label="'成功'" :value="1" />
                  <el-option :key="0" :label="'失败'" :value="0" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="回调通知" prop="haveUsdt">
                <el-select
                  v-model="queryParams.message"
                  placeholder="请选择结果"
                  clearable
                  size="small"
                >
                  <el-option :key="1" :label="'成功'" :value="'1'" />
                  <el-option :key="0" :label="'失败'" :value="'0'" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="状态" prop="haveUsdt">
                <el-select
                  v-model="queryParams.status"
                  placeholder="请选择状态"
                  clearable
                  size="small"
                >
                  <el-option :key="0" label="待支付" :value="0" />
                  <el-option :key="1" label="支付成功" :value="1" />
                  <el-option
                    :key="2"
                    label="付款金额与订单金额不匹配"
                    :value="2"
                  />
                  <el-option :key="3" label="商户手续费不足" :value="3" />
                  <el-option
                    :key="4"
                    label="代收地址TRX矿工费不足"
                    :value="4"
                  />
                  <el-option :key="5" label="回调失败" :value="5" />
                  <el-option :key="6" label="支付失败" :value="6" />
                  <el-option :key="7" label="逾期未支付" :value="7" />
                </el-select>
              </el-form-item>
            </el-col>
             <el-col :span="8">
              <el-form-item label="审查状态" prop="auditStatus">
                <el-select
                  v-model="queryParams.auditStatus"
                  placeholder="请选择审查状态"
                  clearable
                  size="small"
                >
                  <el-option :key="0" :label="'未审核'" :value="0" />
                  <el-option :key="1" :label="'审核通过'" :value="1" />
                  <el-option :key="2" :label="'审核拒绝'" :value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="16">
              <el-form-item label="日期">
                <el-date-picker
                  v-model="value2"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  type="datetimerange"
                  range-separator="至"
                  :default-time="['00:00:00', '23:59:59']"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  align="right"
                >
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="24">
              <div style="margin-left: 32px">
                <el-form-item>
                  <el-button
                    type="primary"
                    icon="el-icon-search"
                    size="mini"
                    @click="handleQuery"
                    >搜索</el-button
                  >
                  <el-button
                    icon="el-icon-refresh"
                    type="primary"
                    size="mini"
                    @click="getinit"
                    >刷新</el-button
                  >

                  <el-button
                    icon="el-icon-refresh"
                    size="mini"
                    type="primary"
                    @click="resetQuery"
                    >重置</el-button
                  >
                  <el-button type="primary" size="mini" @click="getispiliang"
                    >批量审核
                  </el-button>
                </el-form-item>
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="tabswitch">
        <div class="left" @click="getlinks">代付订单</div>
        <div class="left righttabactive">代收订单</div>
      </div>
      <div class="tablemain">
        <el-table
          ref="singleTable"
          :data="tableData"
          highlight-current-row
          style="width: 100%"
          tooltip-effect="dark"
          @selection-change="handleSelectionChange"
          row-key="id"
        >
          <el-table-column
            type="selection"
            :reserve-selection="true"
            width="55"
            :selectable="selectable"
          >
          </el-table-column>
          <el-table-column label="收款地址" align="center" prop="address" width="200">
            <template slot-scope="scope">
              <span
                @click="hashchaxun(scope.row.address)"
                style="cursor: pointer; color: #459f75"
              >
                {{ scope.row.address }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="回调地址" align="center" prop="returnAddress" width="200">
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
          <el-table-column label="拒绝理由" align="center" prop="refuseMsg" width="180"/>
          <el-table-column
            label="商户订单号"
            align="center"
            prop="merchantOrderNo"
            width="180"
          />
          <el-table-column label="应收金额" align="center" prop="orderAmount" width="140"/>
          <el-table-column
            label="实收金额"
            align="center"
            prop="receivedAmount"
            width="140"
          />
        <el-table-column label="手续费" align="center" width="140" prop="feeAmount" />

          <el-table-column label="到账通知" align="center" prop="haveUsdt">
            <template slot-scope="scope">
              <el-tag type="success" v-if="scope.row.haveUsdt === 1"
                >成功</el-tag
              >
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
          <el-table-column
            label="订单创建时间"
            align="center"
            prop="createTime"
            width="180"
          />
          <el-table-column label="订单状态" align="center" prop="status" width="180">
            <template slot-scope="scope">
              <span>
                {{ scope.row.status | filterstatus }}
              </span>
            </template>
          </el-table-column>
              <el-table-column label="审查状态" align="center" prop="status" width="140">
            <template slot-scope="scope">
              <span>
                {{ scope.row.auditStatus | filterauditStatus }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="回调通知" align="center" prop="buttonFlag">
            <template slot-scope="scope">
              <el-tag
                type="success"
                v-if="
                  scope.row.message === 'SUCCESS' ||
                  scope.row.message === 'success'
                "
                >成功</el-tag
              >
              <el-tag type="danger" v-else>失败</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="错误信息" align="center" prop="payResult" width="160"/>
          <el-table-column label="支付订单号" align="center" prop="orderSn" width="160"/>
          <el-table-column label="成功时间" align="center" prop="successTime" width="180"/>
          <el-table-column label="回调时间" align="center" prop="returnTime" width="180"/>

          <el-table-column label="操作" align="center" prop="buttonFlag" width="120" fixed="right">
            <template slot-scope="scope">
              <div class="caozuopaiban">
                <el-button
                  :disabled="
                    scope.row.auditStatus == 0
                      ? false
                      : true
                  "
                  size="mini"
                  type="primary"
                   @click="
                    shenheitem = scope.row;
                    dialogVisible = true;
                    status = 1;
                  "
                  >{{scope.row.auditStatus == 0 ? '审核' : '已审核'}}
                </el-button>
                <el-button
                  :disabled=" scope.row.auditStatus != 2
                      ? false
                      : true
                  "
                  size="mini"
                  type="warning"
                  @click="
                    shenheitem = scope.row;
                    refuseMsg = null;
                    status = 2;
                    dialogVisible = true;
                  "
                  >放弃订单
                </el-button>
                 <el-button
                  size="mini"
                  type="primary"
                  @click="openDetailFlag(scope.row)"
                  >订单详情</el-button
                >
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            background
            layout="prev, pager, next, sizes, jumper"
            :total="queryParams.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :page-size="queryParams.pageSize"
            :page-sizes="[20, 50, 100]"
            :current-page.sync="queryParams.pageNumber"
          >
          </el-pagination>
        </div>
      </div>
    </el-card>
    <el-dialog title="审核" :visible.sync="dialogVisible" width="30%">
      <el-form>
        <el-form-item v-if="status == 2" label="拒绝理由">
          <el-input v-model="refuseMsg" placeholder="拒绝理由"></el-input>
        </el-form-item>

        <el-form-item v-if="status == 2 || status == 1" label="谷歌验证码">
          <el-input v-model="Googlecode" placeholder="谷歌验证码"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="getshenhe">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog title="批量审核" :visible.sync="pldialogVisible" width="30%">
      <div>
        <el-form>
          <el-form-item label="谷歌验证码">
            <el-input
              v-model="plGooglecode"
              placeholder="谷歌验证码"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <!-- <el-button @click="getplshenheJv">拒 绝</el-button> -->
        <el-button type="primary" @click="getplshenhe">确 定</el-button>
      </span>
    </el-dialog>
        <div class="orderdetail">
      <el-dialog
        :visible.sync="detaildialog"
        center
        width="50%"
        :show-close="false"
      >
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
                      <span>创建时间:</span>
                      <span>{{ orderdetailitem.createTime }}</span>
                    </el-row>
                    <el-row>
                      <span>订单状态:</span>
                      <span>
                        {{ orderdetailitem.status | filterstatus }}
                      </span>
                    </el-row>
                  </el-col>
                  <el-col :span="11">
                  <el-row>
                      <span>审查状态:</span>
                      <span>{{
                        orderdetailitem.auditStatus | filterauditStatus
                      }}</span>
                    </el-row>
                    <el-row>
                      <span>到账通知:</span>
                      <span>
                        <el-tag
                          type="success"
                          v-if="orderdetailitem.haveUsdt === 1"
                          >成功</el-tag
                        >
                        <el-tag type="danger" v-else>失败</el-tag></span
                      >
                    </el-row>
                    <el-row>
                      <span>日志记录:</span>
                      <span>{{ orderdetailitem.message }}</span>
                    </el-row>

                    <el-row>
                      <span>错误信息:</span>
                      <span>{{ orderdetailitem.payResult }}</span>
                    </el-row>
                    <el-row>
                      <span>拒绝理由:</span>
                      <span>{{ orderdetailitem.refuseMsg }}</span>
                    </el-row>
                    <el-row>
                      <span>支付订单号:</span>
                      <span>{{ orderdetailitem.orderSn }}</span>
                    </el-row>
                    <el-row>
                      <span>回调通知:</span>
                      <span
                        ><el-tag
                          type="success"
                          v-if="
                            orderdetailitem.message === 'SUCCESS' ||
                            orderdetailitem.message === 'success'
                          "
                          >成功</el-tag
                        >
                        <el-tag type="danger" v-else>失败</el-tag></span
                      >
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
                  <el-table
                    :data="baowendetail.requestData"
                    style="width: 100%"
                    border
                  >
                    <el-table-column
                      prop="label"
                      label="字段键值"
                      align="center"
                    >
                    </el-table-column>
                    <el-table-column prop="value" label="内容值" align="center">
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
                <el-tab-pane label="响应报文">
                  <el-table
                    :data="baowendetail.responseData"
                    style="width: 100%"
                    border
                  >
                    <el-table-column
                      prop="label"
                      label="字段键值"
                      align="center"
                    >
                    </el-table-column>
                    <el-table-column prop="value" label="内容值" align="center">
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
                <el-tab-pane label="通知内容">
                  <el-table
                    :data="baowendetail.returnRequestLog"
                    style="width: 100%"
                    border
                  >
                    <el-table-column
                      prop="label"
                      label="字段键值"
                      align="center"
                    >
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
import Bus from "../common/bus";
export default {
  name: "Orderlist",
  data() {
    return {
      tablist: [
        {
          name: "代付订单",
        },
        {
          name: "代收订单",
        },
      ],
      tabidx: 0,
      queryParams: {
        pageSize: 20,
        pageNumber: 1,
      },
      tableData: [],
      dialogVisible: false,
      Googlecode: "",
      shenheitem: "",
      refuseMsg: "",
      status: 1,
      pldialogVisible: false,
      plGooglecode: "",
      multipleSelection: [],
      value2: [],
           detaildialog: false,
      activeName: "first",
      orderdetailitem: "",
      baowendetail: "",
    };
  },
  created() {
    this.getList();
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
        filterauditStatus(value) {
      var value = parseInt(value);
      switch (value) {
        case 0:
          return "未审核";
          break;
        case 1:
          return "已审核";
          break;
        case 2:
          return "拒绝";
          break;
        default:
          break;
      }
    },
  },
  methods: {
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
      }else{
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
    getlinks() {
      this.$router.push("/abnormalorder");
    },
    selectable(row, index) {
      if (row.auditStatus == 0) {
        return true; //通过某个值来进行判断，规定那一行的选择框被禁用
      }
    },
    getinit() {
      this.value2 = [];
      this.queryParams = {
        pageNumber: this.queryParams.pageNumber,
        pageSize: this.queryParams.pageSize,
      };
      this.getList();
      setTimeout(() => {
        this.$message({
          message: "刷新成功",
          type: "success",
        });
      }, 1000);
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    getispiliang() {
      if (this.multipleSelection.length > 0) {
        this.pldialogVisible = true;
      } else {
        this.$message.error("请选择要批量审核的订单！");
      }
    },
    getplshenhe() {
      this.status = 1;
      for (let index = 0; index < this.multipleSelection.length; index++) {
        if (this.multipleSelection[index].status == 0) {
          this.shenheitem = this.multipleSelection[index];
          this.getplsh();
          if (index == 0) {
            this.$message({
              message: "正在处理请稍等！",
              duration: "800",
            });
          }
          if (index + 1 == this.multipleSelection.length) {
            setTimeout(() => {
              this.$message.success("处理成功，请等待审核！");
            }, 1000);
            this.getList();
          }
        }
      }
    },
    getplsh() {
      if (this.plGooglecode == "") {
        this.$message.error("请填写谷歌验证码");
        return false;
      }
      var data = {
        id: this.shenheitem.auditId,
        status: this.status,
        refuseMsg: this.refuseMsg,
        googleSign: this.plGooglecode,
      };
      this.$apiFun.payOrdercollectionaudit(data).then((res) => {
        if (res.code == 200) {
          this.pldialogVisible = false;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    getshenhe() {
      if (this.Googlecode == "") {
        this.$message.error("请填写谷歌验证码");
        return false;
      }
      var data = {
        id: this.shenheitem.auditId,
        status: this.status,
        refuseMsg: this.refuseMsg,
        googleSign: this.Googlecode,
      };
      this.$apiFun.payOrdercollectionaudit(data).then((res) => {
        if (res.code == 200) {
          this.$message.success("请求成功，请等待审核！");
          this.getList();
          this.dialogVisible = false;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    resetQuery() {
      this.value2 = [];
      this.queryParams = {
        pageNumber: this.queryParams.pageNumber,
        pageSize: this.queryParams.pageSize,
      };
      this.handleQuery();
    },
    handleQuery() {
      this.queryParams.pageNumber = 1;
      this.getList();
    },
    getList() {
      this.queryParams.startDate = this.value2[0];
      this.queryParams.endDate = this.value2[1];
      this.$apiFun.payOrdercollectionlist(this.queryParams).then((result) => {
        if (result.code == 200) {
          this.tableData = result.data.records;
          this.queryParams.total = result.data.total;
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
.tabswitch {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50%;
  margin: 40px auto;
  font-size: 14px;
  border-radius: 10px;
  background: #eee;
  cursor: pointer;
  .left {
    width: 50%;
    text-align: center;
    padding: 8px 0;
  }
  .right {
    width: 50%;
    text-align: center;
    padding: 8px 0;
  }
  .lefttabactive {
    background: #05aa69;
    color: #fff;
    border-radius: 10px 0 0 10px;
  }
  .righttabactive {
    background: #05aa69;
    color: #fff;
    border-radius: 0 10px 10px 0;
  }
}
.caozuopaiban .el-button {
  margin: 5px;
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
