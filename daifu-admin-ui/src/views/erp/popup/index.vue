<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="按钮标题" prop="buttonTitle">
        <el-input
          v-model="queryParams.buttonTitle"
          placeholder="请输入按钮标题"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="按钮跳转地址" prop="buttonHerf">
        <el-input
          v-model="queryParams.buttonHerf"
          placeholder="请输入按钮跳转地址"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="按钮是否启用" prop="buttonFlag">
        <el-select v-model="queryParams.buttonFlag" placeholder="请选择按钮是否启用" clearable size="small">
          <el-option
            v-for="dict in buttonFlagOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="弹窗是否启用" prop="popupFlag">
        <el-select v-model="queryParams.popupFlag" placeholder="请选择弹窗是否启用" clearable size="small">
          <el-option
            v-for="dict in popupFlagOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="语言" prop="lang">
        <el-input
          v-model="queryParams.lang"
          placeholder="请输入语言"
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
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['erp:popup:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['erp:popup:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['erp:popup:remove']"
        >删除</el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="popupList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column label="内容" align="center" prop="content" />
      <el-table-column label="按钮标题" align="center" prop="buttonTitle" />
      <el-table-column label="按钮跳转地址" align="center" prop="buttonHerf" />
      <el-table-column label="按钮是否启用" align="center" prop="buttonFlag"  >
        <template slot-scope="scope">
              <el-tag type="success" v-if="scope.row.buttonFlag === 1">启用</el-tag>
              <el-tag type="danger" v-else>禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="弹窗是否启用" align="center" prop="popupFlag"  >
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.popupFlag === 1">启用</el-tag>
          <el-tag type="danger" v-else>禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="语言" align="center" prop="lang" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['erp:popup:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['erp:popup:remove']"
          >删除</el-button>
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

    <!-- 添加或修改弹窗对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="按钮标题" prop="buttonTitle">
          <el-input v-model="form.buttonTitle" placeholder="请输入按钮标题" />
        </el-form-item>
        <el-form-item label="按钮跳转地址" prop="buttonHerf">
          <el-input v-model="form.buttonHerf" placeholder="请输入按钮跳转地址" />
        </el-form-item>
        <el-form-item label="按钮是否启用">
          <el-radio-group v-model="form.buttonFlag">
            <el-radio
              v-for="dict in buttonFlagOptions"
              :key="dict.dictValue"
              :label="parseInt(dict.dictValue)"
            >{{dict.dictLabel}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="弹窗是否启用">
          <el-radio-group v-model="form.popupFlag">
            <el-radio
              v-for="dict in popupFlagOptions"
              :key="dict.dictValue"
              :label="parseInt(dict.dictValue)"
            >{{dict.dictLabel}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="语言" prop="lang">
          <el-input v-model="form.lang" placeholder="请输入语言" />
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
import { listPopup, getPopup, delPopup, addPopup, updatePopup, exportPopup } from "@/api/erp/popup";

export default {
  name: "Popup",
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
      // 弹窗表格数据
      popupList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 按钮是否启用字典
      buttonFlagOptions: [],
      // 弹窗是否启用字典
      popupFlagOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        content: null,
        buttonTitle: null,
        buttonHerf: null,
        buttonFlag: null,
        popupFlag: null,
        lang: null,
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
    this.getDicts("openOrdis").then(response => {
      this.buttonFlagOptions = response.data;
    });
    this.getDicts("openOrdis").then(response => {
      this.popupFlagOptions = response.data;
    });
  },
  methods: {
    /** 查询弹窗列表 */
    getList() {
      this.loading = true;
      listPopup(this.queryParams).then(response => {
        this.popupList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 按钮是否启用字典翻译
    buttonFlagFormat(row, column) {
      return this.selectDictLabel(this.buttonFlagOptions, row.buttonFlag);
    },
    // 弹窗是否启用字典翻译
    popupFlagFormat(row, column) {
      return this.selectDictLabel(this.popupFlagOptions, row.popupFlag);
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
        title: null,
        content: null,
        buttonTitle: null,
        buttonHerf: null,
        buttonFlag: 0,
        popupFlag: 0,
        lang: null,
        createTime: null
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加弹窗";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPopup(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改弹窗";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePopup(this.form).then(response => {
              this.$message.success("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPopup(this.form).then(response => {
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
      this.$confirm('是否确认删除弹窗编号为"' + ids + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delPopup(ids);
        }).then(() => {
          this.getList();
          this.$message.success("删除成功");
        }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有弹窗数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.exportLoading = true;
          return exportPopup(queryParams);
        }).then(response => {
          this.download(response.msg);
          this.exportLoading = false;
        }).catch(() => {});
    }
  }
};
</script>
