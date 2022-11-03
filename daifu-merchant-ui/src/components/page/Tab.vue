<template>
  <div>
    <el-card class="box-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="`未读消息(${unread.length})`" name="first">
          <el-table
            :data="unread"
            :show-header="showHeader"
            :row-style="{ background: '#f5f5f5' }"
          >
            <el-table-column prop="date" label="日期" width="200">
              <template slot-scope="scope">
                <i class="el-icon-time"></i>
                <span style="margin-left: 5px">{{ scope.row.date }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="消息内容"> </el-table-column>
            <el-table-column width="120" label="操作" align="center">
              <template slot-scope="scope">
                <el-button @click="handleRead(scope.$index)" type="warning"
                  >标为已读</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <el-button @click="handleReadAll" type="danger" class="btn-batch"
            >全部标记为已读</el-button
          >
        </el-tab-pane>
        <el-tab-pane :label="`已读消息(${read.length})`" name="second">
          <el-table
            :data="read"
            :show-header="showHeader"
            :row-style="{ background: '#f5f5f5' }"
          >
            <el-table-column prop="date" label="日期" width="200">
              <template slot-scope="scope">
                <i class="el-icon-time"></i>
                <span style="margin-left: 5px">{{ scope.row.date }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="消息内容"> </el-table-column>
            <el-table-column width="100" label="操作" align="center">
              <template slot-scope="scope">
                <el-button @click="handleDel(scope.$index)" type="danger"
                  >删除</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <el-button @click="handleDelAll" type="danger" class="btn-batch"
            >删除全部</el-button
          >
        </el-tab-pane>
        <el-tab-pane :label="`回收站(${trash.length})`" name="third">
          <el-table
            :data="trash"
            :show-header="showHeader"
            :row-style="{ background: '#f5f5f5' }"
          >
            <el-table-column prop="date" label="日期" width="200">
              <template slot-scope="scope">
                <i class="el-icon-time"></i>
                <span style="margin-left: 5px">{{ scope.row.date }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="消息内容"> </el-table-column>
            <el-table-column width="100" label="操作" align="center">
              <template slot-scope="scope">
                <el-button @click="handleRestore(scope.$index)" type="default"
                  >还原</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <el-button @click="handleRestoreAll" class="btn-batch" type="warning"
            >恢复全部</el-button
          >
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "Tab",
  data() {
    return {
      activeTab: "first",
      showHeader: false,
      unread: [
        {
          date: "2018-08-20 20:00:00",
          title: "【系统通知】该系统将于次日凌晨2点到5点进行升级维护",
        },
        {
          date: "2018-08-19 21:00:00",
          title: "【天气预报】明日成都全市范围内会有大面积降雨",
        },
        {
          date: "2018-08-19 20:00:00",
          title: "【系统通知】该系统将于次日凌晨2点到5点进行升级维护",
        },
      ],
      read: [
        {
          date: "2018-08-17 20:00:00",
          title: "【系统通知】该系统将于次日凌晨2点到5点进行升级维护",
        },
      ],
      trash: [
        {
          date: "2018-08-17 21:00:00",
          title: "【天气预报】明日成都龙泉驿区将会出现罕见高温",
        },
      ],
    };
  },
  methods: {
    handleRead(index) {
      this.read.push(this.unread.splice(index, 1)[0]);
    },
    handleReadAll() {
      this.read = this.read.concat(this.unread.splice(0));
    },
    handleDel(index) {
      this.trash.push(this.read.splice(index, 1)[0]);
    },
    handleDelAll() {
      this.trash = this.trash.concat(this.read.splice(0));
    },
    handleRestore(index) {
      this.read.push(this.trash.splice(index, 1)[0]);
    },
    handleRestoreAll() {
      this.read = this.read.concat(this.trash.splice(0));
    },
  },
};
</script>

<style scoped lang="less">
.el-tab-pane {
  padding: 20px 0;
  .border-radius;
}
.btn-batch {
  margin: 20px 0 0 10px;
}
</style>
