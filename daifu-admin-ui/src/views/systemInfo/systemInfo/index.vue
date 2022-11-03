<template>
  <div class="app-container">


    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['systemInfo:systemInfo:edit']">修改</el-button>
      </el-col>

    </el-row>

    <el-table v-loading="loading" :data="systemInfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="代收默认费率" align="center" prop="oneAgent" />
      <el-table-column label="代付默认单笔手续费" align="center" prop="twoAgent" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['systemInfo:systemInfo:edit']">修改</el-button>

        </template>
      </el-table-column>
    </el-table>



    <!-- 添加或修改系统设置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="160px">
        <el-form-item label="代收默认费率" prop="oneAgent">
          <el-input v-model="form.oneAgent" placeholder="代收默认费率" />
        </el-form-item>
        <el-form-item label="代付默认单笔手续费" prop="twoAgent">
          <el-input v-model="form.twoAgent" placeholder="代付默认单笔手续费" />
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
  </div>
</template>

<script>
import { listSystemInfo, getSystemInfo, delSystemInfo, addSystemInfo, updateSystemInfo, exportSystemInfo } from "@/api/systemInfo/systemInfo";

export default {
  name: "SystemInfo",
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
      // 系统设置表格数据
      systemInfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        key: null,
        kefu: null,
        telegram: null,
        whatsapp: null,
        backwater: null,
        oneAgent: null,
        twoAgent: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      adminGoogleSign:''
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询系统设置列表 */
    getList() {
      this.loading = true;
      listSystemInfo(this.queryParams).then(response => {
        this.systemInfoList = response.rows;
        this.total = response.total;
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
        id: null,
        key: null,
        kefu: null,
        telegram: null,
        whatsapp: null,
        backwater: null,
        oneAgent: null,
        twoAgent: null,
        searchValue: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
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
      this.title = "添加系统设置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getSystemInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改系统设置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSystemInfo(this.form, { adminGoogleSign: this.adminGoogleSign }).then(response => {
              this.$message.success("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSystemInfo(this.form, { adminGoogleSign: this.adminGoogleSign }).then(response => {
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
      this.$confirm('是否确认删除系统设置编号为"' + ids + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function () {
        return delSystemInfo(ids);
      }).then(() => {
        this.getList();
        this.$message.success("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有系统设置数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.exportLoading = true;
        return exportSystemInfo(queryParams);
      }).then(response => {
        this.download(response.msg);
        this.exportLoading = false;
      }).catch(() => { });
    }
  }
};
</script>
