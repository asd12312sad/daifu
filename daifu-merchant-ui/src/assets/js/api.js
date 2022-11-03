//引入刚才的http.js文件
import https from './http.js';

//设置个对象，就不用一个个暴露了，直接暴露对象
let apiFun = {};

/* 获取列表 */
//查询列表，详情就是get
/* /api/getlist是请求接口地址，有http.js里面的Ip加上，如：http:192.168.0.1:9090//api/getlist  */
apiFun.getlist = function (params) {
    return https.get('/api/getlist', params)
}

/* 新增保存检查计划 */
//保存都是post
apiFun.saveJcInfo = function (params) {
    return https.post('/api/saveJcInfo', params)
}
// 登录邮箱获取验证码
apiFun.loginCode= function(params){
  return https.get("/mer/sso/login/code",params)
}
// 邮箱获取验证码
apiFun.registerCode= function(params){
    return https.get("/mer/sso/register/code",params)
}
// 订单导出
apiFun.orderexport = function(params){
  return https.get("/mer/system/address/export",params)
}
// 注册
apiFun.register= function(params){
    return https.get("/mer/sso/register",params)
}
// 谷歌验证
apiFun.getGoogleSign= function(params){
    return https.get("/mer/sso/getGoogleSign",params)
}
// 登录
apiFun.login= function(params){
    return https.get("/mer/sso/login",params)
}
// 个人信息
apiFun.info= function(params){
    return https.get("/mer/sso/info",params)
}
// 修改密码
apiFun.resetpassword= function(params){
  return https.get("/mer/sso/reset/password",params)
}
// 控制台
apiFun.homesum = function(params){
  return https.get("/mer/home/sum",params)
}

apiFun.resetSign= function(params){
  return https.get("/mer/sso/resetSign",params)
}

apiFun.resetGoogleSign = function(data){
  return https.get("/mer/sso/resetGoogleSign",data)
}
apiFun.updateSetting= function(data){
  return https.post("/mer/sso/update/setting",data)
}
apiFun.getAddressList= function(params){
  return https.get("/mer/system/address/list",params)
}

apiFun.getUseAddressList= function(params){
  return https.get("/mer/system/address/collect/list",params)
}

apiFun.collectNotionalPooling = function (params){
  return https.get("/mer/system/address/collect/notionalPooling",params)
}
apiFun.getAddressHuiDiao= function(params){
  return https.get("/mer/system/address/huidiao",params)
}
apiFun.getPayOrderList= function(params){
  return https.get("/mer/payOrder/list",params)
}
apiFun.getPayOrderHuiDiao= function(params){
  return https.get("/mer/payOrder/huidiao",params)
}
// 财务列表
apiFun.getpayuseaddresslist = function(params){
  return https.get("/mer/pay/use/address/list",params)
}
// 新建代付地址
apiFun.getpayuseaddresscreate = function(params){
  return https.get("/mer/pay/use/address/create",params)
}
// 测试代收
apiFun.testMerchant = function(params){
  return https.get("/mer/home/test",params)
}
// 测试代付
apiFun.testPay = function(params){
  return https.get("/mer/home/testPay",params)
}
// 代付重新支付
apiFun.payOrderrealpay = function(params){
  return https.get("/mer/payOrder/real/pay",params)
}
// 代付修改金额
apiFun.payOrderupdateamount = function(params){
  return https.get("/mer/payOrder/update/amount",params)
}
// 修改邮箱
apiFun.ssoresetemail = function(params){
  return https.get("/mer/sso/reset/email",params)
}
// 获取api Key
apiFun.ssoapiKey = function(params){
  return https.get("/mer/sso/apiKey",params)
}
// 代收列表统计
apiFun.homecollectionheader = function(params){
  return https.get("/mer/home/collection/header",params)
}
// 代付列表统计
apiFun.homepayheader = function(params){
  return https.get("/mer/home/pay/header",params)
}
// 大额审查列表
apiFun.payOrderwholesalelist = function(params){
  return https.get("/mer/payOrder/wholesale/list",params)
}
// 异常订单列表
apiFun.payOrderabnormitylist = function(params){
  return https.get("/mer/payOrder/abnormity/list",params)
}
// 大额审查异常订单审核
apiFun.payOrderaudit = function(params){
  return https.get("/mer/payOrder/audit",params)
}

// 角色列表
apiFun.rolePage = function(params){
  return https.get("/mer/role/page",params)
}

apiFun.roleDetail = function(params){
  return https.get("/mer/role/detail",params)
}


apiFun.roleBuilding = function(data){
  return https.post("/mer/role/building",data)
}

apiFun.roleAdd = function(data){
  return https.post("/mer/role/add",data)
}

apiFun.roleRmove = function(data){
  return https.get("/mer/role/delete",data)
}



// 账号列表
apiFun.userPage = function(params){
  return https.get("/mer/user/page",params)
}

apiFun.userDetail = function(params){
  return https.get("/mer/user/detail",params)
}


apiFun.userBuilding = function(data){
  return https.post("/mer/user/building",data)
}

apiFun.userAdd = function(data){
  return https.post("/mer/user/add",data)
}

apiFun.userRmove = function(params){
  return https.get("/mer/user/delete",params)
}

apiFun.rolelist = function(params){
  return https.get("/mer/role/list",params)
}

apiFun.loglist = function(params){
  return https.get("/mer/log/list",params)
}

// 异常订单代收审核
apiFun.payOrdercollectionaudit = function(params){
  return https.get("/mer/payOrder/collection/audit",params)
}
// 异常订单代收审核列表
apiFun.payOrdercollectionlist = function(params){
  return https.get("/mer/payOrder/collection/list",params)
}
// 代付手动标记到账
apiFun.payOrdermarkPayment = function(params){
  return https.get("/mer/payOrder/markPayment",params)
}

// 代付手动标记放弃
apiFun.payOrderabandon = function(params){
  return https.get("/mer/payOrder/abandon",params)
}
// 代收补单
apiFun.systemaddressbudan = function(params){
  return https.get("/mer/system/address/budan",params)
}

//暴露出这个对象
export default apiFun;

