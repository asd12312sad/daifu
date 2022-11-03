<template>
  <div>
    <el-card class="box-card">
      <div class="titles_search">
        <el-form
          :model="queryParams"
          ref="queryForm"
          :inline="true"
          label-width="90px"
        >
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="状态" prop="haveUsdt">
                <el-select
                  v-model="queryParams.status"
                  placeholder="请选择状态"
                  clearable
                  size="small"
                >
                  <el-option :key="1" label="成功" :value="1" />
                  <el-option :key="0" label="失败" :value="0" />
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
          <el-form-item>
            <el-button
              type="primary"
              icon="el-icon-search"
              size="mini"
              @click="handleQuery"
              >搜索
            </el-button>
            <el-button
              icon="el-icon-refresh"
              type="primary"
              size="mini"
              @click="getinit"
              >刷新</el-button
            >
          </el-form-item>
        </el-form>
      </div>
      <div class="tablemain">
        <el-table
          ref="singleTable"
          :data="tableData"
          highlight-current-row
          style="width: 100%"
          tooltip-effect="dark"
        >
          <el-table-column
            label="操作名称"
            align="center"
            prop="name"
          ></el-table-column>
          <el-table-column
            label="操作者"
            align="center"
            prop="merchantUserName"
          ></el-table-column>
          <el-table-column label="请求时间" align="center" prop="requestTime" />
          <el-table-column label="IP" align="center" prop="ip">
            <template slot-scope="scope">
              <span>
                {{ scope.row.ip }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" prop="status">
            <template slot-scope="scope">
              <span>
                {{ scope.row.status | filterstatus }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="类型" align="center" prop="type">
            <template slot-scope="scope">
              <span>
                {{ scope.row.type | filtertype }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" prop="buttonFlag">
            <template slot-scope="scope">
              <div class="caozuopaiban">
                <el-button
                  @click="openDetailFlag(scope.row)"
                  size="mini"
                  type="primary"
                >
                  详情
                </el-button>
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
    <el-dialog title="请求日志详情" :visible.sync="detailFlag" width="50%">
      <div>
        <div v-if="details">
          <div class="rizhititle">请求</div>
          <el-table :data="details.requestData" style="width: 100%" border>
            <el-table-column prop="label" label="字段键值" align="center">
            </el-table-column>
            <el-table-column prop="value" label="内容值" align="center">
            </el-table-column>
          </el-table>
        </div>
        <div v-if="details" style="margin-top:30px;">
          <div class="rizhititle">响应</div>
          <el-table :data="details.responseData" style="width: 100%" border>
            <el-table-column prop="label" label="字段键值" align="center">
            </el-table-column>
            <el-table-column prop="value" label="内容值" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.label != 'records'">
                  {{ scope.row.value && scope.row.value != '' ? scope.row.value : '' }}
                </span>
                <span v-if="scope.row.label == 'records'"> list </span>
              </template>
            </el-table-column>
          </el-table>
          <!-- <div
            class="everylists"
            v-for="(item, index) in details.responseData"
            :key="index"
            v-if="index != 1"
          >
            <div>{{ item.label }}</div>
            <div>{{ item.value }}</div>
          </div>
          <div
            class="everylists"
            v-for="(item, i) in getguolv(details.responseData[1].value)"
            :key="i"
            v-if="item.value && item.value != ''"
          >
            <div>{{ item.label }}</div>
            <div>{{ item.value }}</div>
          </div> -->
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="detailFlag = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import axios from "axios";
export default {
  name: "Orderlist",
  data() {
    return {
      queryParams: {
        pageSize: 20,
        pageNumber: 1,
      },
      tableData: [],
      pldialogVisible: false,
      value2: [],
      detailFlag: false,
      details: "",
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
          return "失败";
          break;
        case 1:
          return "成功";
          break;
        default:
          break;
      }
    },
    filtertype(value) {
      var value = parseInt(value);
      switch (value) {
        case 1:
          return "操作";
          break;
        case 2:
          return "API调用";
          break;
        default:
          break;
      }
    },
  },
  methods: {
    getguolv(item) {
      let obj = item;
      let objNew = [];
      for (let i in obj) {
        objNew.push({
          label: i,
          value: obj[i],
        });
      }
      return objNew;
    },
    openDetailFlag(row) {
      this.details = row;
      let objNew = [];
      let objNews = [];
      let objNews1 = [];
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

      if (row.responseLog && objNews[1].label == "data") {
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
        var newarray = objNews;
      }

      this.details = {
        requestData: objNew && objNew != "" ? objNew : [],
        responseData: newarray && newarray != "" ? newarray : [],
      };
      this.detailFlag = true;
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
    handleQuery() {
      this.queryParams.pageNumber = 1;
      this.getList();
    },
    getList() {
      this.queryParams.startDate = this.value2[0];
      this.queryParams.endDate = this.value2[1];
      this.$apiFun.loglist(this.queryParams).then((result) => {
        if (result.code == 200) {
          this.tableData = result.data.records;
          this.filtersip();
          this.queryParams.total = result.data.total;
        }
      });
    },
    filtersip() {
      for (let index = 0; index < this.tableData.length; index++) {
        axios({
          url:
            "http://opendata.baidu.com/api.php?query=" +
            this.tableData[index].ip +
            "&co=&resource_id=6006&oe=utf8",
          methods: "get",
          data: {},
        })
          .then((res) => {
            setTimeout(() => {
              this.tableData[index].ip = res.data.data[0].location;
            }, 10);
          })
          .catch(function () {});
      }
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
.rizhititle {
  text-align: center;
  margin: 10px 0;
  font-size: 20px;
  font-weight: 600;
  color: #459f75;
}
.everylists {
  display: flex;
  align-items: center;
  justify-content: space-between;
  div {
    border: 1px solid #eeeeee;
    width: 50%;
    display: flex;
    justify-content: center;
    padding: 12px;
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
