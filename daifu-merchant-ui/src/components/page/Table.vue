<template>
  <div>
    <!--分页-->
    <el-pagination
      :page-sizes="[10, 20, 30, 40]"
      :page-size="10"
      :total="100"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
    <!--表格内容-->
    <el-table
      ref="list"
      :data="tableData"
      style="width: 100%"
      border
      stripe
      highlight-current-row
      :default-sort="{prop: 'date', order: 'descending'}"
      @row-click="handleRowClick"
      @select-all="handleCheckedAllAndCheckedNone"
      @select="handleCheckedAllAndCheckedNone">
      <el-table-column
        type="selection"
        width="45"
        align="center">
      </el-table-column>
      <el-table-column
        type="index"
        label="序号"
        width="50">
      </el-table-column>
      <el-table-column
        property="date"
        label="日期"
        width="180"
        sortable>
        <template slot-scope="scope">
          <i class="el-icon-time"></i>
          <span style="margin-left: 5px">{{scope.row.date}}</span>
        </template>
      </el-table-column>
      <el-table-column
        property="name"
        label="姓名"
        width="180">
      </el-table-column>
      <el-table-column
        property="address"
        label="地址">
      </el-table-column>
      <el-table-column
        label="操作"
        width="130"
        align="center">
        <template slot-scope="scope">
          <el-button circle icon="el-icon-edit-outline" type="primary" title="编辑" size="small"
            @click="rowEdit(scope.$index, scope.row)"></el-button>
          <el-button circle icon="el-icon-delete" type="danger" title="删除" size="small"
            @click="rowDel(scope.$index, scope.row, $event);"></el-button>
        </template>
      </el-table-column>
    </el-table>
    <!--编辑-弹出层-->
    <el-dialog
      title="编辑"
      :visible.sync="isShowEditDialog"
      width="430px"
      @close="dialogClose">
      <el-form
        ref="editForm"
        :model="formFileds"
        label-width="55px"

        :rules="rules">
        <el-form-item label="日期">
          <el-date-picker v-model="formFileds.date" value-format="yyyy-MM-dd" :editable="false" :clearable="false"></el-date-picker>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formFileds.name"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formFileds.address"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleEdit(formFileds.id)" class="pull-right margin-l-25">确定</el-button>
          <el-button @click="isShowEditDialog = false;" class="pull-right">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "Table",
    data() {
      return {
        formFileds: {
          date: '',
          name: '',
          address: '',
          id: ''
        },
        rules: {
          name: [
            {required: true, message: '姓名不能为空', trigger: 'blur, change'}
          ],
          address: [
            {required: true, message: '地址不能为空', trigger: 'blur, change'}
          ]
        },
        tableData: [{
          id: 0,
          date: '2016-05-02',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄'
        }, {
          id: 1,
          date: '2016-05-04',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1517 弄'
        }, {
          id: 2,
          date: '2016-05-01',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1519 弄'
        }, {
          id: 3,
          date: '2016-05-03',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1516 弄'
        }],
        isShowEditDialog: false
      }
    },
    methods: {
      handleRowClick(row, event, column) {

        // 仅选中当前行
        this.setCurRowChecked(row);
      },
      handleCheckedAllAndCheckedNone(selection) {

        // 当前选中仅一行时操作-（当前表格行高亮）
        1 != selection.length && this.$refs.list.setCurrentRow();
      },
      dialogClose() {

        // 清空编辑表单
        this.$refs.editForm.resetFields();
      },
      rowEdit(index, row) {

        this.setCurRowChecked(row);

        // 给编辑弹出层赋值
        // ***这里需要注意的是：因为加了排序 所以tableData的顺序和实际显示的行顺序不一样
        for (let key in this.formFileds) {

          this.formFileds[key] = row[key];
        }
        this.isShowEditDialog = true;
      },
      handleEdit(id) {

        this.$refs.editForm.validate(isValid => {

          if (!isValid) return;

          // 保存编辑后的数据
          Object.assign(this.tableData[id], this.formFileds);
          this.isShowEditDialog = false;

          // 考虑到可能编辑了日期-需要重新排序
          // ***注意：手动排序传参和表格的default-sort属性格式不太一样
          this.$refs.list.sort('date', 'descending');

          this.$message.success('编辑成功');
        });
      },
      rowDel(index, row, event) {

        // 让当前删除按钮失焦
        event.target.blur();

        this.$confirm('确定要删除当前行吗？', '删除', {
          comfirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(() => {

          this.tableData.splice(row.id, 1);
          this.$message.success('删除成功');
          return false;
        });
      },
      // 选中当前行-当前行的复选框被勾选
      setCurRowChecked(row) {

        this.$refs.list.clearSelection();
        this.$refs.list.toggleRowSelection(row);
      }
    }
  }
</script>

<style scoped lang="less">
.el-form {
  padding: 0 10px;
}
.el-date-editor {
  width: 100% !important;
}
</style>