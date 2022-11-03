<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="玩家名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入玩家名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="游戏名称" prop="gameName">
        <el-input
          v-model="queryParams.gameName"
          placeholder="请输入游戏名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="下单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择下单状态" clearable size="small">
          <el-option
            v-for="dict in statusOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="结果" prop="result">
        <el-select v-model="queryParams.result" placeholder="请选择结果" clearable size="small">
          <el-option
            v-for="dict in resultOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="反水金额" prop="fanshuiAmount">
        <el-input
          v-model="queryParams.fanshuiAmount"
          placeholder="请输入反水金额"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="U盾手续费" prop="fee">
        <el-input
          v-model="queryParams.fee"
          placeholder="请输入U盾手续费"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="daterangeCreateTime"
          size="small"
          style="width: 240px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="反水状态" prop="fanshui">
        <el-input
          v-model="queryParams.fanshui"
          placeholder="请输入反水状态"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="推广返佣状态" prop="tuiguang">
        <el-select v-model="queryParams.tuiguang" placeholder="请选择推广返佣状态" clearable size="small">
          <el-option
            v-for="dict in tuiguangOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="一级代理名称" prop="proxyOneName">
        <el-input
          v-model="queryParams.proxyOneName"
          placeholder="请输入一级代理名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="二级代理名称" prop="proxyTwoName">
        <el-input
          v-model="queryParams.proxyTwoName"
          placeholder="请输入二级代理名称"
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
          v-hasPermi="['erp:order:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['erp:order:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['erp:order:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-checkbox v-model="checkedRefresh">自动刷新</el-checkbox>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="表ID" align="center" prop="id"/>
      <el-table-column label="用户名称" align="center" prop="userName"/>
      <el-table-column label="到账地址" align="center" prop="payAddress" show-overflow-tooltip/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          {{ statusFormat(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="提币数量" align="center" prop="amount"/>
      <el-table-column label="创建时间" align="center" prop="createDate" width="180">
      </el-table-column>
      <el-table-column label="交易HASH" align="center" prop="txId" show-overflow-tooltip/>
      <el-table-column label="类型" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['erp:order:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['erp:order:remove']"
          >删除
          </el-button>
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

    <!-- 添加或修改下注记录对话框 -->
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
import {listfanbi, getOrder, delOrder, addOrder, updateOrder, exportOrder} from "@/api/erp/order";

export default {
  name: "Order",
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
      // 下注记录表格数据
      orderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      checkedRefresh: true,
      // 下单状态字典
      statusOptions: [],
      // 结果字典
      resultOptions: [],
      // 创建时间时间范围
      daterangeCreateTime: [],
      // 推广返佣状态字典
      tuiguangOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: null,
        gameName: null,
        status: null,
        result: null,
        fanshuiAmount: null,
        fee: null,
        createTime: null,
        fanshui: null,
        tuiguang: null,
        proxyOneName: null,
        proxyTwoName: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {}
    };
  },
  created() {
    this.getList();
    this.getDicts("order_type").then(response => {
      this.statusOptions = response.data;
    });
    this.getDicts("order_result").then(response => {
      this.resultOptions = response.data;
    });
    this.getDicts("amount_status").then(response => {
      this.tuiguangOptions = response.data;
    });
    //如果 checkedRefresh 为true，则每两秒刷新一次list
    setInterval(() => {
      if (this.checkedRefresh) {
        this.getList();
      }
    }, 2000);
  },
  methods: {
    /** 查询下注记录列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeCreateTime && '' != this.daterangeCreateTime) {
        this.queryParams.params["beginCreateTime"] = this.daterangeCreateTime[0];
        this.queryParams.params["endCreateTime"] = this.daterangeCreateTime[1];
      }
      listfanbi(this.queryParams).then(response => {
        this.orderList = response.rows;
        console.log(this.orderList);
        this.total = response.total;
        this.loading = false;
      });
    },
    // 下单状态字典翻译
    statusFormat(row) {
      console.log(row.status)
      return this.selectDictLabel(this.statusOptions, row.status);
    },
    // 结果字典翻译
    resultFormat(row) {
      var date = this.selectDictLabel(this.resultOptions, row.result);
      console.log(date);
      return date;
    },
    selectDictLabel(datas, value) {
      for (let i = 0; i < datas.length; i++) {
        if (datas[i].dictValue == value + "") {
          return datas[i].dictLabel;
        }
      }
    },
    // 推广返佣状态字典翻译
    tuiguangFormat(row, column) {
      return this.selectDictLabel(this.tuiguangOptions, row.tuiguang);
    },
    // 推广返佣状态字典翻译
    danshuiFormat(row, column) {
      return this.selectDictLabel(this.tuiguangOptions, row.fanshui);
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
        userName: null,
        payAddress: null,
        gameName: null,
        status: null,
        result: null,
        userResult: null,
        systemResult: null,
        amount: null,
        proxyOneAmount: null,
        proxyTwoAmount: null,
        fanshuiAmount: null,
        fee: null,
        address: null,
        createTime: null,
        txId: null,
        tradeId: null,
        tradeType: null,
        gameId: null,
        userId: null,
        proxyOneId: null,
        proxyTwoId: null,
        fanshui: null,
        tuiguang: null,
        proxyOneName: null,
        proxyTwoName: null
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
      this.daterangeCreateTime = [];
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
      this.title = "添加下注记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getOrder(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改下注记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateOrder(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrder(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除下注记录编号为"' + ids + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function () {
        return delOrder(ids);
      }).then(() => {
        this.getList();
        this.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有下注记录数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.exportLoading = true;
        return exportOrder(queryParams);
      }).then(response => {
        this.download(response.msg);
        this.exportLoading = false;
      }).catch(() => {
      });
    }
  }
};
</script>
