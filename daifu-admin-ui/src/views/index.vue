<template xmlns="http://www.w3.org/1999/html">
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="24">
        <blockquote class="text-info" style="font-size: 20px">
          截至：{{ dateTime }} 总计
          <hr />
          代收：--{{sumData.collectCount}}-- 笔 / 收款成功: --{{sumData.collectEndCount}}-- 笔/{{sumData.collectSumAmount}}USDT/回调成功: -- {{sumData.collectReturnCount}}-- 笔<hr />
          代付：--{{sumData.payCount}}-- 笔 / 收款成功: --{{sumData.payEndCount}}-- 笔/{{sumData.paySumAmount}}USDT/回调成功: -- {{sumData.payReturnCount}}-- 笔<hr />
        </blockquote>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col>
         <div id="main" class="main" style="width: 100%;height: 300px;"></div>
      </el-col>
    </el-row>
    <el-divider />
  </div>
</template>

<script>
import { sumApi,weekSumApi } from "@/api/homeApi";
import * as echarts from 'echarts';


export default {

  name: "Index",
  data() {
    return {
      sumData:{},
      sumWeek:{},
      // 版本号
      version: "3.8.2",
      dateTime:"",
      timer:null
    };
  },
  created() {
    this.getSum()

  },
  mounted() {
    this.weekSum()

    this.timer = setInterval(() => {
      setTimeout(   this.dateTime =  this.getDateTime(), 0)
    },  1000)
    },
  methods: {
    weekSum(){
      weekSumApi().then(res=>{

        console.log(res.data)
        this.openWeekData(res.data)
      })
    },
    openWeekData(weekData){
      var chartDom = document.getElementById('main');
      var myChart = echarts.init(chartDom);
      var option;

      option = {
        title: {
          text: '最近一周情况'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {},
        toolbox: {
          show: true,
          feature: {
            dataZoom: {
              yAxisIndex: 'none'
            },
            dataView: { readOnly: false },
            magicType: { type: ['line', 'bar'] },
            restore: {},
            saveAsImage: {}
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: weekData.dateList
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value} '
          }
        },
        series: [
          {
            name: '生成代付数',
            type: 'line',
            data: weekData.payCountList,
            markPoint: {
              data: [
                { type: 'max', name: 'Max' },
                { type: 'min', name: 'Min' }
              ]
            },
            markLine: {
              data: [{ type: 'average', name: 'Avg' }]
            }
          },
          {
            name: '代付成功数',
            type: 'line',
            data: weekData.payEndCountList,
            markPoint: {
              data: [{ name: '周最低', value: -2, xAxis: 1, yAxis: -1.5 }]
            },
            markLine: {
              data: [
                { type: 'average', name: 'Avg' },
                [
                  {
                    symbol: 'none',
                    x: '90%',
                    yAxis: 'max'
                  },
                  {
                    symbol: 'circle',
                    label: {
                      position: 'start',
                      formatter: 'Max'
                    },
                    type: 'max',
                    name: '最高点'
                  }
                ]
              ]
            }
          },

          {
            name: '代付成功回调数',
            type: 'line',
            data: weekData.PayReturnCountList,
            markPoint: {
              data: [{ name: '周最低', value: -2, xAxis: 1, yAxis: -1.5 }]
            },
            markLine: {
              data: [
                { type: 'average', name: 'Avg' },
                [
                  {
                    symbol: 'none',
                    x: '90%',
                    yAxis: 'max'
                  },
                  {
                    symbol: 'circle',
                    label: {
                      position: 'start',
                      formatter: 'Max'
                    },
                    type: 'max',
                    name: '最高点'
                  }
                ]
              ]
            }
          },
          {
            name: '生成代收数',
            type: 'line',
            data: weekData.collectCountList,
            markPoint: {
              data: [
                { type: 'max', name: 'Max' },
                { type: 'min', name: 'Min' }
              ]
            },
            markLine: {
              data: [{ type: 'average', name: 'Avg' }]
            }
          },
          {
            name: '代付成功数',
            type: 'line',
            data: weekData.collectEndCountList,
            markPoint: {
              data: [{ name: '周最低', value: -2, xAxis: 1, yAxis: -1.5 }]
            },
            markLine: {
              data: [
                { type: 'average', name: 'Avg' },
                [
                  {
                    symbol: 'none',
                    x: '90%',
                    yAxis: 'max'
                  },
                  {
                    symbol: 'circle',
                    label: {
                      position: 'start',
                      formatter: 'Max'
                    },
                    type: 'max',
                    name: '最高点'
                  }
                ]
              ]
            }
          },

          {
            name: '代付成功回调数',
            type: 'line',
            data: weekData.collectReturnCountList,
            markPoint: {
              data: [{ name: '周最低', value: -2, xAxis: 1, yAxis: -1.5 }]
            },
            markLine: {
              data: [
                { type: 'average', name: 'Avg' },
                [
                  {
                    symbol: 'none',
                    x: '90%',
                    yAxis: 'max'
                  },
                  {
                    symbol: 'circle',
                    label: {
                      position: 'start',
                      formatter: 'Max'
                    },
                    type: 'max',
                    name: '最高点'
                  }
                ]
              ]
            }
          }

        ]
      };

      option && myChart.setOption(option);
    },

    getSum() {
      sumApi().then(response => {
        this.sumData = response.data
      });
    },
    getDateTime(){
      var date = new Date()
      var year = date.getFullYear()
      var month = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1
      var day = date.getDate() < 10 ? '0' + date.getDate() : date.getDate()
      var hours = date.getHours() < 10 ? '0' + date.getHours() : date.getHours()
      var minutes = date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()
      var seconds = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
      return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + ':' + seconds

    },

    goTarget(href) {
      window.open(href, "_blank");
    },
  },
};
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>

