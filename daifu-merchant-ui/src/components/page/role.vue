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
          <!-- <el-row :gutter="24">
            <el-col :span="12"> -->
          <el-form-item label="角色名称" prop="address">
            <el-input
              v-model="queryParams.roleName"
              placeholder="请输入角色名称"
              clearable
              size="small"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <!-- </el-col>
    
          </el-row>
          <el-row :gutter="24">
            <el-col :span="24"> -->
          <!-- <div style="margin-left: 32px"> -->
          <el-form-item>
            <el-button
              type="primary"
              icon="el-icon-search"
              size="mini"
              @click="handleQuery"
              >搜索</el-button
            >
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
              >重置</el-button
            >
            <el-button
              type="primary"
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
              >新增</el-button
            >
          </el-form-item>
          <!-- </div> -->
          <!-- </el-col>
          </el-row> -->
        </el-form>
      </div>
      <div class="tablemain">
        <el-table
          ref="singleTable"
          :data="tableData"
          highlight-current-row
          style="width: 100%"
        >
          <el-table-column label="角色名称" align="center" prop="roleName" />
          <el-table-column label="操作" align="center" prop="buttonFlag">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-sort"
                @click="huidiaoApi(scope.row)"
                >绑定菜单</el-button
              >
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="rowEdit(scope.row)"
                >修改</el-button
              >
              <el-button
                size="mini"
                type="text"
                icon="el-icon-close"
                @click="
                  rowRemove(scope.row);
                  delitem = scope.row;
                "
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            background
            layout="prev, pager, next, jumper"
            :total="queryParams.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :page-size="queryParams.pageSize"
            :current-page.sync="queryParams.pageNumber"
          >
          </el-pagination>
        </div>
      </div>
    </el-card>
    <el-dialog
      title="绑定权限"
      :visible.sync="dialogVisible1"
      width="30%"
      :show-close="false"
    >
      <div style="display: flex; justify-content: center">
        <el-form style="width: 100%">
          <el-form-item label="权限绑定">
            <el-select
              size="medium"
              v-model="selectValue"
              multiple
              placeholder="请选择"
            >
              <el-option
                v-for="item in this.roleDetail.bindingMerchantMenuList"
                :key="item.menuId"
                :label="item.menuName"
                :value="item.menuId"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="谷歌验证码">
            <el-input v-model="roleDetail.googleSign"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="gettestdaishou">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="新建角色"
      :visible.sync="addDialogVisible"
      width="30%"
      :show-close="false"
    >
      <el-form>
        <el-form-item label="角色名称">
          <el-input v-model="addData.roleName"></el-input>
        </el-form-item>
        <el-form-item label="谷歌验证码">
          <el-input v-model="addData.googleSign"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="(addData = {}), (addDialogVisible = false)"
          >取 消</el-button
        >
        <el-button type="primary" @click="addRoleApi">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="请输入谷歌验证码"
      :visible.sync="deldialogVisible"
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
        <el-button @click="deldialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="rowRemove(delitem)">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import axios from "axios";
import fileDownload from "js-file-download";
export default {
  name: "role",
  data() {
    return {
      queryParams: {
        pageSize: 10,
        pageNumber: 1,
      },
      roleDetail: {},
      selectValue: [],
      tableData: [],
      addData: {},
      addDialogVisible: false,
      dialogVisible1: false,
      Googlecode: "",
      tjdetail: "",
      tjdetails: "",
      deldialogVisible: false,
      delitem: "",
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
    getdaishoutj() {
      this.$apiFun.homecollectionheader().then((res) => {
        this.tjdetail = res.data.all;
        this.tjdetails = res.data.today;
      });
    },
    handleAdd() {
      this.addData = {};
      this.addDialogVisible = true;
    },
    rowEdit(row) {
      this.addData = row;
      this.addDialogVisible = true;
    },
    rowRemove(row) {
      if (this.deldialogVisible == false) {
        return (this.deldialogVisible = true);
      }
      if (this.Googlecode == '' || !this.Googlecode) {
        this.$message.error("请先输入谷歌验证码");
        return false;
      }
      
      this.$apiFun
        .roleRmove({ roleId: row.roleId, googleSign: this.Googlecode })
        .then((res) => {
          if (res.code == 200) {
            this.$message({
              message: "删除成功",
              type: "success",
            });

            this.getList();
            this.deldialogVisible = false;
          } else {
            this.$message({
              message: res.msg,
              type: "error",
            });
          }
          this.Googlecode = "";
        });
    },
    addRoleApi() {
      if (this.addData.googleSign == '' || !this.addData.googleSign) {
        this.$message.error("请先输入谷歌验证码");
        return false;
      }
      this.$apiFun.roleAdd(this.addData).then((res) => {
        if (res.code == 200) {
          this.$message({
            message: "新增成功",
            type: "success",
          });
        } else {
          this.$message({
            message: res.msg,
            type: "error",
          });
        }
        this.addData = {};
        this.addDialogVisible = false;
        this.getList();
      });
    },
    gettestdaishou() {
       if (this.roleDetail.googleSign == '' || !this.roleDetail.googleSign) {
        this.$message.error("请先输入谷歌验证码");
        return false;
      }
      this.roleDetail.menuIds = this.selectValue;
      this.$apiFun.roleBuilding(this.roleDetail).then((res) => {
        if (res.code == 200) {
          this.$message({
          message: "绑定成功",
          type: "success",
        });
        }else{
           this.$message({
            message: res.msg,
            type: "error",
          });
        }
        
        this.roleDetail = {};
        this.selectValue = [];

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
      this.queryParams = {
        pageNumber: this.queryParams.pageNumber,
        pageSize: this.queryParams.pageSize,
      };
      this.handleQuery();
    },
    getList() {
      this.$apiFun.rolePage(this.queryParams).then((result) => {
        this.tableData = result.data.records;
        this.queryParams.total = result.data.total;
      });
    },
    huidiaoApi(row) {
      this.$apiFun.roleDetail({ id: row.roleId }).then((result) => {
        this.roleDetail = result.data;
        this.selectValue = [];
        for (
          let index = 0;
          index < this.roleDetail.bindingMerchantMenuList.length;
          index++
        ) {
          this.selectValue.push(
            this.roleDetail.bindingMerchantMenuList[index].menuId
          );
        }
        this.roleDetail.bindingMerchantMenuList =
          this.roleDetail.bindingMerchantMenuList.concat(
            this.roleDetail.notBindingMerchantMenuList
          );
        this.dialogVisible1 = true;
      });
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val;
    },
    handleCurrentChange(val) {
      this.queryParams.pageNumber = val;
      this.getList();
    },
  },
};
</script>

<style scoped lang="less">
/deep/ .el-select {
  width: 100% !important;
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
