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
              <el-form-item label="账号备注" prop="address">
                <el-input
                  v-model="queryParams.nickName"
                  placeholder="请输入账号备注"
                  clearable
                  size="small"
                  @keyup.enter.native="handleQuery"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="账号" prop="address">
                <el-input
                  v-model="queryParams.userName"
                  placeholder="请输入账号"
                  clearable
                  size="small"
                  @keyup.enter.native="handleQuery"
                />
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
                    size="mini"
                    @click="resetQuery"
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
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="tablemain">
        <el-table
          ref="singleTable"
          :data="tableData"
          highlight-current-row
          style="width: 100%"
        >
          <el-table-column label="账号" align="center" prop="userName" />
          <el-table-column label="备注" align="center" prop="nickName" />

          <el-table-column label="操作" align="center" prop="buttonFlag">
            <template slot-scope="scope">
              <!-- <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="huidiaoApi(scope.row)"
                >绑定角色</el-button
              > -->
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
        <el-select
          size="medium"
          v-model="selectValue"
          multiple
          placeholder="请选择"
        >
          <el-option
            v-for="item in this.roleDetail.bindingMerchantRoleList"
            :key="item.roleId"
            :label="item.roleName"
            :value="item.roleId"
          >
          </el-option>
        </el-select>
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
        <el-form-item label="账号">
          <el-input v-model="addData.userName"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="addData.password" type="password"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="addData.nickName"></el-input>
        </el-form-item>
        <el-form-item label="权限">
          <el-select
            size="medium"
            v-model="addData.roleIds"
            multiple
            placeholder="请选择"
          >
            <el-option
              v-for="item in addData.bindingMerchantRoleList"
              :key="item.roleId"
              :label="item.roleName"
              :value="item.roleId"
            >
            </el-option>
          </el-select>
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
export default {
  name: "user",
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
      jueselist: [],
      deldialogVisible: false,
      delitem: {},
      addedittype: 1,
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getjsList() {
      this.addData = {};
      this.$apiFun.rolelist().then((res) => {
        this.addData.bindingMerchantRoleList = res.data;
        this.addDialogVisible = true;
      });
    },
    // 新增
    handleAdd() {
      this.addedittype = 1;
      this.getjsList();
    },
    rowEdit(row) {
      this.addedittype = 2;
      this.$apiFun.userDetail({ id: row.userId }).then((result) => {
        this.addData = result.data;
        this.addData.roleIds = [];
        for (
          let index = 0;
          index < this.addData.bindingMerchantRoleList.length;
          index++
        ) {
          this.addData.roleIds.push(
            this.addData.bindingMerchantRoleList[index].roleId
          );
        }
        this.addData.bindingMerchantRoleList =
          this.addData.bindingMerchantRoleList.concat(
            this.addData.notBindingMerchantRoleList
          );
        this.addDialogVisible = true;
      });
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
        .userRmove({ userId: row.userId, googleSign: this.Googlecode })
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
      this.$apiFun.userAdd(this.addData).then((res) => {
        if (res.code == 200) {
          this.$message({
            message: this.addedittype == 1 ? "新增成功" : "修改成功",
            type: "success",
          });
          this.getList();
        } else {
          this.$message({
            message: res.msg,
            type: "error",
          });
        }

        this.addDialogVisible = false;
      });
    },
    gettestdaishou() {
      if (this.roleDetail.googleSign == '' || !this.roleDetail.googleSign) {
        this.$message.error("请先输入谷歌验证码");
        return false;
      }
      this.roleDetail.roleIds = this.selectValue;
      this.$apiFun.userBuilding(this.roleDetail).then((res) => {
        if (res.code == 200) {
          this.$message({
            message: "绑定成功",
            type: "success",
          });
        } else {
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
      this.$apiFun.userPage(this.queryParams).then((result) => {
        this.tableData = result.data.records;
        this.queryParams.total = result.data.total;
      });
    },
    huidiaoApi(row) {
      this.$apiFun.userDetail({ id: row.userId }).then((result) => {
        this.roleDetail = result.data;
        this.selectValue = [];
        for (
          let index = 0;
          index < this.roleDetail.bindingMerchantRoleList.length;
          index++
        ) {
          this.selectValue.push(
            this.roleDetail.bindingMerchantRoleList[index].roleId
          );
        }
        this.roleDetail.bindingMerchantRoleList =
          this.roleDetail.bindingMerchantRoleList.concat(
            this.roleDetail.notBindingMerchantRoleList
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
