export default (type = 'GET', url = '', data = {}, async = true) => {

  // 定义一个promise
  return new Promise((resolve, reject) => {

    type = type.toUpperCase();
    let requestObj = window.XMLHttpRequest ? new XMLHttpRequest() : new new ActiveXObject("Microsoft.XMLHTTP");

    if (type == 'GET') {

      // url地址带请求参数
      let dataStr = '?'; //数据拼接字符串
      for (let key in data) {

        dataStr += `${key}=${data[key]}`;
      }
      dataStr = dataStr.substr(0, dataStr.lastIndexOf('&'));
      url += dataStr;

      // 发送ajax get请求
      requestObj.open(type, url, async);
      requestObj.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
      requestObj.send();
    }else if (type == 'POST') {

      // 发送ajax post请求
      requestObj.open(type, url, async);
      requestObj.setRequestHeader("Content-type", "application/json");
      requestObj.send(JSON.stringify(data));
    }else {

      reject('error type');
    }

    // 当readyState的值改变的时候 处理函数
    requestObj.onreadystatechange = () => {

      if (4 !== requestObj.readyState) return;

      if (200 == requestObj.status) {

        let obj = requestObj.response;
        typeof obj !== 'object' && (obj = JSON.parse(obj));
        resolve(obj);
      } else {

        reject(requestObj);
      }
    }
  });
}
