<template>
  <div>
    <el-row>
      <el-col :span="24">
        <div id="line1" class="chart"></div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="8">
        <div id="liquid" class="chart"></div>
      </el-col>
      <el-col :span="8">
        <div id="line2" class="chart"></div>
      </el-col>
      <el-col :span="8">
        <div id="pie" class="chart"></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import Util from '@/assets/js/util';
  import 'echarts-liquidfill';
  export default {
    name: "EChartsComplex",
    computed: {
      chartLine1() {

        return this.$echarts.init(Util.getDom('line1'));
      },
      chartLine2() {

        return this.$echarts.init(Util.getDom('line2'))
      },
      chartPie() {

         return this.$echarts.init(Util.getDom('pie'))
      },
      chartLiquid() {

        return this.$echarts.init(Util.getDom('liquid'))
      },
    },
    methods: {
      drawLine1()
      {

        let title = "今日和昨日访问量";
        let option = {
          title: Object.assign({}, Util.defaultEchartsOpt.title, {text: title}),
          grid: {
            top: 60,
            left: 60,
            right: 80,
            bottom: 20,
            containLabel: true
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              lineStyle: {
                color: '#ddd'
              }
            },
            backgroundColor: 'rgba(255,255,255,1)',
            padding: [5, 10],
            textStyle: {
              color: '#999',
            },
            extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
          },
          legend: {
            top: 15,
            right: 20,
            orient: 'vertical',
            textStyle: {
              color: "#666"
            }
          },
          xAxis: {
            type: 'category',
            data: ['00:00','2:00','4:00','6:00','8:00','10:00','12:00','14:00','16:00','18:00','20:00',"22:00"],
            boundaryGap: false,
            splitLine: {
              show: false,
              interval: 'auto',
              lineStyle: {
                color: ['#D4DFF5']
              }
            },
            axisTick: {
              show: false
            },
            axisLine: {
              lineStyle: {
                color: '#999'
              }
            },
            axisLabel: {
              margin: 10,
              textStyle: {
                fontSize: 14
              }
            }
          },
          yAxis: {
            type: 'value',
            splitLine: {
              lineStyle: {
                color: ['#D4DFF5']
              }
            },
            axisTick: {
              show: false
            },
            axisLine: {
              lineStyle: {
                color: '#999'
              }
            },
            axisLabel: {
              margin: 10,
              textStyle: {
                fontSize: 14
              }
            }
          },
          series: [{
            name: '今日',
            type: 'line',
            smooth: true,
            showSymbol: false,
            symbol: 'circle',
            symbolSize: 4,
            data: ['1200', '1400', '1008', '1411', '1026', '1288', '1300', '800', '1100', '1000', '1118', '1322'],
            areaStyle: {
              normal: {
                color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                  offset: 0,
                  color: 'rgba(199, 237, 250,0.5)'
                }, {
                  offset: 1,
                  color: 'rgba(199, 237, 250,0.2)'
                }], false)
              }
            },
            itemStyle: {
              normal: {
                color: 'rgba(154, 116, 179, 0.7)'
              }
            },
            lineStyle: {
              normal: {
                width: 2
              }
            }
          }, {
            name: '昨日',
            type: 'line',
            smooth: true,
            showSymbol: false,
            symbol: 'circle',
            symbolSize: 4,
            data: ['1200', '1400', '808', '811', '626', '488', '1600', '1100', '500', '300', '1998', '822'],
            areaStyle: {
              normal: {
                color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                  offset: 0,
                  color: 'rgba(216, 244, 247,1)'
                }, {
                  offset: 1,
                  color: 'rgba(216, 244, 247,1)'
                }], false)
              }
            },
            itemStyle: {
              normal: {
                color: 'rgba(126, 237, 238, 0.7)'
              }
            },
            lineStyle: {
              normal: {
                width: 2
              }
            }
          }]
        };
        this.chartLine1.setOption(option);
        return this;
      },
      drawLine2() {

        this.$axios.get('/chart-complex/line2').then(res => {

          let data = res.data;
          let xAxisMonth = [], barData = [], lineData = [];
          for (let i = 0; i < data.chart.length; i++) {

            xAxisMonth.push(data.chart[i].month + '月');
            barData.push({
              "name": xAxisMonth[i],
              "value": data.chart[i].value
            });
            lineData.push({
              "name": xAxisMonth[i],
              "value": data.chart[i].ratio
            });
          }

          let option = {
            title: Object.assign({}, Util.defaultEchartsOpt.title, {text: "训练月统计"}),
            grid: {
              top: 45,
              left: 20,
              right: 20,
              bottom: 0,
              containLabel: true
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'none'
              },
              formatter: function (params) {
                return params[0]["data"].name + "<br/>" + '训练人次: ' + params[1]["data"].value + "<br/>" + '合格率: ' + params[0]["data"].value;
              }
            },
            xAxis: [
              {
                type: 'category',
                show: false,
                data: xAxisMonth,
                axisLabel: {
                  textStyle: {
                    color: '#b6b5ab'
                  }
                }
              },
              {
                type: 'category',
                position: "bottom",
                data: xAxisMonth,
                boundaryGap: true,
                axisTick: {
                  show: false
                },
                axisLine: {
                  show: false
                },
                axisLabel: {
                  textStyle: {
                    color: '#b6b5ab'
                  }
                }
              }
            ],
            yAxis: [
              {
                show: true,
                offset: 52,
                splitLine: {
                  show: false,
                  lineStyle: {
                    color: "rgba(255,255,255,0.2)"
                  }
                },
                axisTick: {
                  show: false
                },
                axisLine: {
                  show: true
                },
                axisLabel: {
                  show: true,
                  color: '#b6b5ab'
                }
              },
              {
                show: false,
                type: "value",
                name: "合格率(%)",
                nameTextStyle: {
                  color: '#ccc'
                },
                axisLabel: {
                  color: '#ccc'
                },
                splitLine: {
                  show: false
                },
                axisLine: {
                  show: false
                },
                axisTick: {
                  show: false
                }
              }
            ],
            color: ['#e54035'],
            series: [
              {
                name: '训练人次',
                type: 'pictorialBar',
                xAxisIndex: 1,
                barCategoryGap: '-80%',
                symbol: 'path://d="M150 50 L130 130 L170 130  Z"',
                itemStyle: {
                  normal: {
                    color: function (params) {

                      let colorList = [
                        'rgba(13,177,205,0.8)', 'rgba(29,103,182,0.6)',
                        'rgba(13,177,205,0.8)', 'rgba(29,103,182,0.6)',
                        'rgba(13,177,205,0.8)', 'rgba(29,103,182,0.6)'
                      ];
                      return colorList[params.dataIndex];
                    }
                  },
                  emphasis: {
                    opacity: 1
                  }
                },
                data: barData,
              },
              {
                symbol: 'image://static/chart-icon.png',
                symbolSize: 42,
                name: "完成率",
                type: "line",
                yAxisIndex: 1,
                data: lineData,
                itemStyle: {
                  normal: {
                    borderWidth: 5,
                    color: {
                      colorStops: [{
                        offset: 0,
                        color: '#821eff'
                      },

                        {
                          offset: 1,
                          color: '#204fff'
                        }
                      ],
                    }
                  }
                }
              }
            ]
          };
          this.chartLine2.setOption(option);
        });
        return this;
      },
      drawPie() {

        let dataStyle = {
          normal: {
            label: {
              show: false
            },
            labelLine: {
              show: false
            },
            shadowBlur: 40,
            shadowColor: 'rgba(40, 40, 40, 0.5)'
          }
        };
        let placeHolderStyle = {
          normal: {
            color: 'rgba(0,0,0,0)',
            label: {
              show: false
            },
            labelLine: {
              show: false
            }
          },
          emphasis: {
            color: 'rgba(0,0,0,0)'
          }
        };
        // 5个圆环的样式
        let series = [];
        let colors = ['#85b6b2', '#6d4f8d','#cd5e7e', '#e38980','#f7db88'];
        for (let i = 0; i < 5; i++) {

          series.push({
            name: `产品${i + 1}`,
            type: 'pie',
            clockWise: false,
            center: ["50%", "56%"],
            radius: [110 - i * 20, 130 - i * 20],
            itemStyle: dataStyle,
            hoverAnimation: false,
            data: [
              {
                value: (Math.random() * 100).toFixed(2),
                name: `产品${i + 1}`,
                itemStyle: {
                  color: colors[i]
                }
              },
              {
                value: (Math.random() * 100).toFixed(2),
                name:'',
                itemStyle : placeHolderStyle
              }
            ]
          });
        }

        let option = {
          title: Object.assign({}, Util.defaultEchartsOpt.title, {text: "多产品完成度"}),
          tooltip : {
            show: true,
            formatter: "{a} <br/>完成度 : {d}%"
          },
          color: colors,
          legend: {
            itemGap: 12,
            left: 20,
            top: 50,
            textStyle: {
              color: "#666"
            }
          },
          series
        };

        this.chartPie.setOption(option);
        return this;
      },
      drawLiquid() {

        let rate = Math.random().toFixed(2);
        this.chartLiquid.setOption({
          title: Object.assign({}, Util.defaultEchartsOpt.title, {text: "项目总完成度"}),
          series: [{
            type: 'liquidFill',
            data: [rate > 0.6 ? rate : 0.6, 0.5, 0.4, 0.3],
            radius: "70%",
            center: ["50%", "55%"],
          }]
        });
        return this;
      },
      resizeChart() {

        window.addEventListener("resize", () => {

          this.chartLine1.resize();
          this.chartLine2.resize();
          this.chartPie.resize();
          this.chartLiquid.resize();
        });
      }
    },
    mounted() {

      this.drawLine1().drawLine2().drawPie().drawLiquid().resizeChart();
    }
  }
</script>

<style scoped lang="less">
  .el-row {
    margin-top: 20px;
  }
  .chart {
    width: 100%;
    height: 350px;
    .border-radius(8px);
    background-color: @boxBgColor;
    box-shadow: 0 0 5px transparent;
    &:hover {
      box-shadow: 0 0 5px @mainColor;
    }
  }
</style>
