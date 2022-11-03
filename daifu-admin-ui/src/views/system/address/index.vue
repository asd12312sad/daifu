<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="收款地址" prop="address">
        <el-input v-model="queryParams.address" placeholder="请输入收款地址" clearable size="small"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商户ID" prop="merchantId">
        <el-input v-model="queryParams.merchantId" placeholder="请输入商户ID" clearable size="small"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="回调地址" prop="returnAddress">
        <el-input v-model="queryParams.returnAddress" placeholder="请输入回调地址" clearable size="small"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="商户订单号" prop="merchantOrderNo">
        <el-input v-model="queryParams.merchantOrderNo" placeholder="请输入商户订单号" clearable size="small"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="addressList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="收款地址" align="center" prop="address" />
      <el-table-column label="商户ID" align="center" prop="merchantId" />
      <el-table-column label="回调地址" align="center" prop="returnAddress" />
      <el-table-column label="tex余额" align="center" prop="trxBalance" />
      <el-table-column label="usdt余额" align="center" prop="usdtBalance" />
      <el-table-column label="商户订单号" align="center" prop="merchantOrderNo" />
      <el-table-column label="日志记录" align="center" prop="message" />
      <el-table-column label="下单时间" align="center" prop="createTime" />
      <el-table-column label="回调是否成功" align="center" prop="buttonFlag">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.message === 'SUCCESS' || scope.row.message === 'success'">成功</el-tag>
          <el-tag type="danger" v-else>失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" prop="buttonFlag">
        <template slot-scope="scope">

          <el-button size="mini" type="text" icon="el-icon-edit" @click="huidiaoApi(scope.row)"
            v-hasPermi="['system:dept:edit']">重新回调</el-button>
        </template>

      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改商户付款地址对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAddress, huidiaoApi, getAddress, delAddress, addAddress, updateAddress, exportAddress } from "@/api/system/address";

export default {
  name: "Address",
  data() {
    return {
      // 遮罩层
      loading: true,
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
      // 商户付款地址表格数据
      addressList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        address: null,

        merchantId: null,

        returnAddress: null,

        merchantOrderNo: null,

      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    huidiaoApi(row) {
      this.$prompt('请输入谷歌验证码', '重新回调', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        var query = {
          adminGoogleSign: value,
          id:row.id,
        }
        huidiaoApi(query).then(response => {
          if (response.code == 200) {
            this.getList();
            this.$message.success("操作成功")
          } else {
            this.$message.error(response.msg)
          }
        })
      }).catch(() => {
      });
    },
    /** 查询商户付款地址列表 */
    getList() {
      this.loading = true;
      listAddress(this.queryParams).then(response => {
        this.addressList = response.rows;
        this.total = parseInt(response.total);
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        address: null,

        merchantId: null,

        hexAddress: null,

        returnAddress: null,

        privateKey: null,

        privateKeyBase: null,

        trxBalance: null,

        usdtBalance: null,

        merchantOrderNo: null,

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
      this.title = "添加商户付款地址";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const address = row.id || this.ids
      getAddress(address).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改商户付款地址";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAddress(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAddress(this.form).then(response => {
              this.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const addresss = row.id || this.ids;
      this.$confirm('是否确认删除商户付款地址编号为"' + addresss + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function () {
        return delAddress(addresss);
      }).then(() => {
        this.getList();
        this.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有商户付款地址数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.exportLoading = true;
        return exportAddress(queryParams);
      }).then(response => {
        this.download(response.msg);
        this.exportLoading = false;
      }).catch(() => { });
    }
  }
};
</script>
