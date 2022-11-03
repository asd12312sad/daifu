<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable size="small"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="账号" prop="account">
        <el-input v-model="queryParams.account" placeholder="请输入账号" clearable size="small"
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['erp:agent:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['erp:agent:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['erp:agent:remove']">删除</el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="agentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="账号" align="center" prop="id" />
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="账号" align="center" prop="account" />
      <el-table-column label="邀请码" align="center" prop="inviteCode" />
      <el-table-column label="结算地址" align="center" prop="usdtAddress" />
      <el-table-column label="已结算金额" align="center" prop="withdrawAmount" />
      <el-table-column label="未结算金额" align="center" prop="unbalancedAmount" />


      <el-table-column label="登录次数" align="center" prop="loginCount" />
      <el-table-column label="最后登录日期" align="center" prop="lastLoginDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastLoginDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最后登录IP" align="center" prop="ip" />
      <el-table-column label="最低代付手续费率" align="center" prop="payFee" />
      <el-table-column label="最低代收手续费率" align="center" prop="fee" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['erp:agent:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['erp:agent:remove']">删除</el-button>

          <el-button size="mini" type="text" icon="el-icon-edit" @click="updateGoogleSignCode(scope.row)"
            v-hasPermi="['erp:merchant:edit']">重置谷歌验证器</el-button>

          <el-button size="mini" type="text" icon="el-icon-edit" @click="editpassopen = true, editpassitem = scope.row"
            v-hasPermi="['erp:merchant:edit']">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改代理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="账号" prop="account">
          <el-input v-model="form.account" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="结算地址" prop="remark">
          <el-input v-model="form.usdtAddress" placeholder="请输入结算地址" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="最低代付手续费率" prop="payFee">
          <el-input v-model="form.payFee" placeholder="请输入最低代付手续费率" />
        </el-form-item>
        <el-form-item label="最低代收手续费率" prop="fee">
          <el-input v-model="form.fee" placeholder="请输入最低代收手续费率" />
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

    <el-dialog title="重置密码" :visible.sync="editpassopen" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="密码" prop="password">
          <el-input v-model="passwords" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="谷歌验证码" prop="adminGoogleSigns">
          <el-input v-model="adminGoogleSigns" placeholder="请输入谷歌验证码" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="updatePassWord(editpassitem)">确 定</el-button>
        <el-button @click="editpassopen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAgent, getAgent, delAgent, addAgent, updateAgent, exportAgent, updateGoogleSignCodeApi } from "@/api/erp/agent";
import { updateMerchant } from "@/api/erp/merchant";


export default {
  name: "Agent",
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
      // 代理表格数据
      agentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        account: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      adminGoogleSign: '',
      editpassitem: '',
      editpassopen: false,
      passwords: '',
      adminGoogleSigns: ''
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询代理列表 */
    getList() {
      this.loading = true;
      listAgent(this.queryParams).then(response => {
        this.agentList = response.rows;
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
        id: null,
        name: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        account: null,
        password: null,
        loginCount: null,
        lastLoginDate: null,
        ip: null,
        payFee: null,
        fee: null
      };
      this.resetForm("form");
    },
    updateGoogleSignCode(row) {
      this.$prompt('请输入谷歌验证码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        updateGoogleSignCodeApi({ id: row.id, adminGoogleSign: value }).then(response => {
          this.$message.success("重置谷歌验证成功")
        })
      }).catch(() => {

      });

    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    updatePassWord(row) {
      var data = {
        id: row.id, 
        password: this.passwords
      }
      updateAgent(data,{adminGoogleSign:this.adminGoogleSigns}).then(response => {
        this.$message.success("修改成功")
      })
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
      this.title = "添加代理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAgent(id).then(response => {
        this.form = response.data;
        this.form.password = null;
        this.open = true;
        this.title = "修改代理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAgent(this.form, this.adminGoogleSign).then(response => {
              this.$message.success("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAgent(this.form, this.adminGoogleSign).then(response => {
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
      this.$prompt("请输入谷歌验证码", '是否确认删除代理编号为"' + ids + '"的数据项?', {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        // type: "warning"
      }).then(function ({ value }) {
        return delAgent(ids, value);
      }).then(() => {
        this.getList();
        this.$message.success("删除成功");
      }).catch(() => { })

    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有代理数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.exportLoading = true;
        return exportAgent(queryParams);
      }).then(response => {
        this.download(response.msg);
        this.exportLoading = false;
      }).catch(() => { });
    }
  }
};
</script>
