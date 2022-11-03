// 根据id、class、tag获取第一个匹配到的值
function getDom(selector, type = 'id') {

  let getDomBy = {
    id(selector) {

      return document.getElementById(selector);
    },
    className(selector) {

      return document.getElementsByClassName(selector)[0];
    },
    tagName(selector) {

      return document.getElementsByTagName(selector)[0];
    }
  };

  return getDomBy[type](selector);
}

// echarts的title样式
const defaultEchartsOpt = {
  title: {
    text: "",
    left: "center",
    top: 20,
    textStyle: {
      color: "#666"
    }
  }
}

export default {
  getDom,
  defaultEchartsOpt
}

/**
 * 按指定格式-格式化时间 如：new Date().Format("yyyy-MM-dd hh:mm:ss");
 * hasweek用来表示是否显示星期
 */
Date.prototype.Format = function (fmt, hasweek) {

  var weekday = ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
  var o = {
    "M+": this.getMonth() + 1,
    "d+": this.getDate(),
    "h+": this.getHours(),
    "m+": this.getMinutes(),
    "s+": this.getSeconds(),
    "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
    "S": this.getMilliseconds() // 毫秒
  };
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
  for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));

  return fmt + (hasweek ? "&nbsp;&nbsp;&nbsp;&nbsp;" + weekday[this.getDay()] : "");
};
