<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="提币地址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入提币地址"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
          <el-option
            v-for="dict in statusOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="玩家ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入玩家ID"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提币数量" prop="amount">
        <el-input
          v-model="queryParams.amount"
          placeholder="请输入提币数量"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="玩家名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入玩家名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="remark">
        <el-input
          v-model="queryParams.remark"
          placeholder="请输入类型"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="auditList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="表ID" align="center" prop="id" />
      <el-table-column label="创建时间" align="center" prop="createDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="充币地址" align="center" prop="address" />
      <el-table-column label="交易HASH" align="center" prop="txId" />
      <el-table-column label="状态" align="center" prop="status" >
        <template slot-scope="scope">
          {{ statusFormat(scope.row) ? statusFormat(scope.row) : scope.row.status }}

        </template>
      </el-table-column>
      <el-table-column label="提币地址" align="center" prop="payAddress" />
      <el-table-column label="玩家ID" align="center" prop="userId" />
      <el-table-column label="提币数量" align="center" prop="amount" />
      <el-table-column label="玩家名称" align="center" prop="userName" />
      <el-table-column label="类型" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="auditOk(scope.row)"
            v-hasPermi="['erp:audit:edit']"
          >审核通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="auditNotOk(scope.row)"
            v-hasPermi="['erp:audit:edit']"
          >审核不通过</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改提币审核对话框 -->
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
import { listAudit, exportAudit,audit } from "@/api/erp/audit";

export default {
  name: "Audit",
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
      // 提币审核表格数据
      auditList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 状态  3 成功 4 失败字典
      statusOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        address: null,
        status: null,
        payAddress: null,
        userId: null,
        amount: null,
        userName: null,
        remark: null,
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
    this.getDicts("audit_status").then(response => {
      this.statusOptions = response.data;
    });
  },
  methods: {
    /** 查询提币审核列表 */
    getList() {
      this.loading = true;
      listAudit(this.queryParams).then(response => {
        this.auditList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    auditOk(row){
      row.status = 3
      audit(row).then(response=>{
        this.$message.success("审核成功")
        this.getList()
      })
    },
    auditNotOk(row){
      row.status = 2
      audit(row).then(response=>{
        this.$message.success("审核成功")
        this.getList()
      })
    },
    // 状态  3 成功 4 失败字典翻译
    statusFormat(row) {
      return this.selectDictLabel(this.statusOptions, row.status);
    },
    selectDictLabel(datas, value) {
      for (let i = 0; i < datas.length; i++) {
        if (datas[i].dictValue == value + "") {
          return datas[i].dictLabel;
        }
      }
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        createDate: null,
        address: null,
        txId: null,
        tradeId: null,
        tradeType: null,
        status: 0,
        payAddress: null,
        userId: null,
        amount: null,
        userName: null,
        remark: null,
        delFlag: null,
        type: null
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },


    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有提币审核数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.exportLoading = true;
          return exportAudit(queryParams);
        }).then(response => {
          this.download(response.msg);
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
