<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <div id="pie" class="chart"></div>
      </el-col>
      <el-col :span="8">
        <div id="line" class="chart"></div>
      </el-col>
      <el-col :span="8">
        <div id="bar" class="chart"></div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="8">
        <div id="gauge" class="chart"></div>
      </el-col>
      <el-col :span="8">
        <div id="radar" class="chart"></div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import Util from "@/assets/js/util";
export default {
  name: "EChartsSimple",
  computed: {
    chartPie() {
      return this.$echarts.init(Util.getDom("pie"));
    },
    chartLine() {
      return this.$echarts.init(Util.getDom("line"));
    },
    chartBar() {
      return this.$echarts.init(Util.getDom("bar"));
    },
    chartGauge() {
      return this.$echarts.init(Util.getDom("gauge"));
    },
    chartRadar() {
      return this.$echarts.init(Util.getDom("radar"));
    }
  },
  methods: {
    drawPie() {
      this.$axios.get("/chart-simple/pie").then(res => {

        let title = "中文名使用TOP5";
        this.chartPie.setOption({
          title: Object.assign({}, Util.defaultEchartsOpt.title, {text: title}),
          tooltip: {
            trigger: "item",
            formatter: "{a} <br/>{b} : {c} ({d}%)"
          },
          color: ["#af84cb", "#3acaa9", "#ebcc6f", "#67c4ed", "rgba(32, 254, 255, 0.5)"],
          series: [
            {
              name: title,
              type: "pie",
              radius: "55%",
              center: ["50%", "50%"],
              data: res.data.data,
              roseType: "radius",
              label: {
                normal: {
                  textStyle: {
                    color: "#777"
                  }
                }
              },
              labelLine: {
                normal: {
                  lineStyle: {
                    color: "#888"
                  },
                  smooth: 0.2,
                  length: 10,
                  length2: 20
                }
              },
              itemStyle: {
                normal: {
                  shadowBlur: 200,
                  shadowColor: "rgba(0, 0, 0, 0.5)"
                }
              },
              animationType: "scale",
              animationEasing: "elasticOut",
              animationDelay: function(idx) {
                return Math.random() * 200;
              }
            }
          ]
        });
      });
      return this;
    },
    drawLine() {
      this.$axios.get("/chart-simple/line").then(res => {

        let title = "用户周活跃度";
        this.chartLine.setOption({
          title: Object.assign({}, Util.defaultEchartsOpt.title, {text: title}),
          tooltip: {
            trigger: "axis",
            formatter: "{a} <br/>{b} : {c}"
          },
          xAxis: {
            type: "category",
            data: res.data.xData,
            boundaryGap: false,
            splitLine: {
              show: false,
              interval: 'auto',
              lineStyle: {
                color: ['#D4DFF5']
              }
            },
            axisLine: {
              lineStyle: {
                color: '#999'
              }
            },
            axisLabel: {
              margin: 10,
              textStyle: {
                fontSize: 12
              }
            }
          },
          yAxis: {
            type: "value",
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
                fontSize: 12
              }
            }
          },
          series: [
            {
              name: title,
              data: res.data.yData,
              type: "line",
              smooth: true,
              showSymbol: false,
              symbol: 'circle',
              symbolSize: 6,
              areaStyle: {
                normal: {
                  color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                    offset: 0,
                    color: 'rgba(199, 237, 250, 0.5)'
                  }, {
                    offset: 1,
                    color: 'rgba(199, 237, 250, 0.2)'
                  }], false)
                }
              },
              itemStyle: {
                normal: {
                  color: 'rgba(32, 254, 255, 0.5)'
                }
              },
              lineStyle: {
                normal: {
                  width: 2
                }
              }
            }
          ]
        });
      });
      return this;
    },
    drawBar() {
      this.$axios.get("/chart-simple/bar").then(res => {

        let title = "商品日销量";
        this.chartBar.setOption({
          title: Object.assign({}, Util.defaultEchartsOpt.title, {text: title}),
          tooltip: {
            trigger: "item",
            formatter: "{a} <br/>{b} : {c}"
          },
          xAxis: {
            type: "category",
            data: res.data.xData,
            axisLine: {
              lineStyle: {
                color: '#999'
              }
            },
            axisLabel: {
              margin: 10,
              textStyle: {
                fontSize: 12
              }
            }
          },
          yAxis: {
            type: "value",
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
                fontSize: 12
              }
            }
          },
          series: [
            {
              name: title,
              data: res.data.yData,
              type: "bar",
              symbol: "triangle",
              symbolSize: 20,
              lineStyle: {
                normal: {
                  color: "green",
                  width: 4,
                  type: "dashed"
                }
              },
              barWidth: 25,
              itemStyle: {
                normal: {
                    barBorderRadius: 30,
                    color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1,
                      [
                        {
                            offset: 0,
                            color: '#00feff'
                        },
                        {
                            offset: 0.5,
                            color: '#027eff'
                        },
                        {
                            offset: 1,
                            color: '#0286ff'
                        }
                      ])
                }
              },
            }
          ]
        });
      });
      return this;
    },
    drawGauge() {

      let title = "平均速度(km/h)";
      let option = {
        title: Object.assign({}, Util.defaultEchartsOpt.title, {text: title}),
        series : [
            {
              name: title,
              type: 'gauge',
              center : ['50%', '60%'],
              min: 0,
              max: 220,
              splitNumber: 11,
              radius: '80%',
              axisLine: {
                lineStyle: {
                  width: 7
                }
              },
              axisTick: {
                length: 12,
                lineStyle: {
                  color: 'auto'
                }
              },
              splitLine: {
                length: 15,
                lineStyle: {
                  color: 'auto'
                }
              },
              axisLine: {
                show: true,
                lineStyle: {
                  width: 20,
                  color: [
                    [
                      1,
                      new this.$echarts.graphic.LinearGradient(0, 0, 1, 0, [
                        {
                          offset: 0,
                          color: "#3acaa9"
                        },
                        {
                          offset: 1,
                          color: "#0286ff"
                        }
                      ])
                    ]
                  ]
                }
              },
              axisLabel: {
                backgroundColor: 'auto',
                borderRadius: 2,
                color: '#eee',
                padding: 3,
                textShadowBlur: 2,
                textShadowOffsetX: 1,
                textShadowOffsetY: 1,
                textShadowColor: '#222'
              },
              pointer: {
                width: 5
              },
              detail : {
                formatter: value => parseFloat(value).toFixed(2),
                fontWeight: 'bolder',
                borderRadius: 3,
                backgroundColor: '',
                shadowBlur: 5,
                shadowColor: '#333',
                shadowOffsetY: 3,
                borderWidth: 2,
                textShadowBlur: 2,
                textShadowColor: '#222e7d',
                width: 70,
                fontSize: 20,
                color: '#eee',
              },
              data:[{value: 40, name: ''}]
            }]
      };
      this.$axios.get("/chart-simple/gauge").then(res => {
        option.series[0].data[0].value = res.data.value;
        this.chartGauge.setOption(option);

        setInterval(() => {
          option.series[0].data[0].value = (Math.random() * 100).toFixed(2);
          this.chartGauge.setOption(option, true);
        }, 2000);
      });

      return this;
    },
    drawRadar() {
      this.$axios.get("/chart-simple/radar").then(res => {

        let title = "部门预算支出比";
        this.chartRadar.setOption({
          title: Object.assign({}, Util.defaultEchartsOpt.title, {text: title}),
          tooltip: {},
          radar: {
            name: {
              textStyle: {
                color: "#999",
              }
            },
            center: ["50%", "58%"],
            radius: "57%",
            indicator: [
              { name: "销售", max: 16000 },
              { name: "管理", max: 16000 },
              { name: "运营", max: 16000 },
              { name: "客服", max: 16000 },
              { name: "研发", max: 16000 },
              { name: "市场", max: 16000 }
            ]
          },
          series: [
            {
              name: "预算 vs 开销（Budget vs spending）",
              type: "radar",
              symbol: "circle",
              symbolSize: 10,
              data: [
                {
                  value: res.data.budget,
                  name: "预算分配（Allocated Budget）",
                  areaStyle: {
                      normal: {
                          color: "rgba(154, 116, 179, 0.7)"
                      }
                  },
                  itemStyle:{
                      color: 'rgba(154, 116, 179, 1)',
                      borderColor: 'rgba(154, 116, 179, 0.3)',
                      borderWidth: 10,
                  },
                  lineStyle: {
                      normal: {
                          color: "rgba(154, 116, 179, 1)",
                          width: 2
                      }
                  },
                },
                {
                  value: res.data.spending,
                  name: "实际开销（Actual Spending）",
                  areaStyle: {
                      normal: {
                          color: "rgba(126, 237, 238, 0.7)"
                      }
                  },
                  itemStyle:{
                      color: 'rgba(126, 237, 238, 1)',
                      borderColor: 'rgba(126, 237, 238, 0.3)',
                      borderWidth: 10,
                  },
                  lineStyle: {
                      normal: {
                          color: "rgba(126, 237, 238, 1)",
                          width: 2
                      }
                  }, 
                }
              ]
            }
          ]
        });
      });
      return this;
    },
    resizeChart() {

      window.addEventListener("resize", () => {
        this.chartPie.resize();
        this.chartLine.resize();
        this.chartBar.resize();
        this.chartGauge.resize();
        this.chartRadar.resize();
      });
    }
  },
  mounted() {

    this.drawPie().drawLine().drawBar().drawGauge().drawRadar().resizeChart();
  }
};
</script>

<style scoped lang="less">
.el-row {
  margin-top: 20px;
}
.chart {
  width: 100%;
  height: 330px;
  .border-radius(8px);
  background-color: @boxBgColor;
  box-shadow: 0 0 5px transparent;
  &:hover {
    box-shadow: 0 0 5px @mainColor;
  }
}
</style>
