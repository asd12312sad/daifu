const Mock = require('mockjs');
const Random = Mock.Random;

const DAY_HAS_SECONDS = 24 * 60 * 60 * 1000;

// echarts-简单pie的数据
Mock.mock('/chart-simple/pie', 'get', {
  'data|5': [{
    'value|20-200': 1,
    name: '@CNAME'
  }]
});

// echarts-简单line的数据
Mock.mock('/chart-simple/line', 'get', function () {

  let [xData, yData] = [['周一', '周二', '周三', '周四', '周五', '周六', '周日'], []];
  for (let i = 0; i < 7; i++) {

    yData.push(Random.integer(20, 200));
  }

  return {
    xData,
    yData
  }
});

// echarts-简单bar的数据
Mock.mock('/chart-simple/bar', 'get', function () {

  let tempDate = new Date(Random.date()).getTime();
  let [xData, yData] = [[], []];
  for (let i = 0; i < 7; i++) {

    xData.push(new Date(tempDate + i * DAY_HAS_SECONDS).Format('yyyy-MM-dd'));
    yData.push(Random.integer(20, 300));
  }

  return {
    xData,
    yData
  }
});

// echarts-简单gauge的数据
Mock.mock('/chart-simple/gauge', 'get', {
  'value|0-99.2': 1
});

// echarts-简单radar的数据
Mock.mock('/chart-simple/radar', 'get', function () {

  let [budget, spending] = [[], []];
  for (let i = 0; i < 6; i++) {

    budget.push(Random.integer(20, 16000));
    spending.push(Random.integer(20, 16000));
  }

  return {
    budget,
    spending
  }
});

// echarts-复杂line的数据
Mock.mock('/chart-complex/line2', 'get', {
  'chart|7': [{
    'value|30-500': 1,
    'ratio|0-99.2': 1,
    'month|+1': 5
  }]
});
