import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui';
import '@/assets/css/common.less';
import '@/assets/css/variable.less';
import '@/assets/css/theme/459f75/index.css';
import '@/assets/css/theme/459f75/aliconfont.css';

// import 'element-ui/lib/theme-chalk/index.css';
// import axios from 'axios';
import echarts from 'echarts';
import apiFun from "@/assets/js/api.js";


// require('./mock');
Vue.prototype.$apiFun = apiFun;//请求接口api
Vue.config.productionTip = false;
Vue.use(ElementUI, { size: 'medium' });
Vue.prototype.$echarts = echarts;

Vue.prototype.copy = function (id) {
  var input = document.createElement("input");
  input.value = id;  // 将调用方法穿入进来的值赋给input
  document.body.appendChild(input);  // 将input添加到body中
  input.select(); // 选中文本
  document.execCommand("copy");  // 调用浏览器复制方法、复制成功
  this.$message({  // 利用element弹出成功提示
    type: 'success',
    message: '复制成功'
  });
  input.remove();  // 成功后删除input，防止影响其他元素
}
Vue.prototype.hashchaxun = function (url) {
  window.open('https://tronscan.org/#/address/' + url)
}
// 使用router.beforeEach注册一个全局前置守卫，对路由进行权限跳转
router.beforeEach((to, from, next) => {
  // console.log(to, from, next)

  // 未匹配到路由时 跳转到error页面
  if (0 === to.matched.length) {
    next('/error');
    return false;
  }
  // const role = localStorage.getItem('username');
  const role = localStorage.getItem("token");
  if (!role && to.path != '/login' && to.path != '/forget' && to.path != '/register') {
    next('/login');
  } else if (to.meta.permission) {
    // 如果是管理员权限则可进入，这里只是简单的模拟管理员权限而已
    role === 'admin' ? next() : next('/error');
  } else {
    // 简单的判断IE10及以下不进入富文本编辑器，该组件不兼容
    if (navigator.userAgent.indexOf('MSIE') > -1 && to.path === '/editor') {
      Vue.prototype.$alert('vue-quill-editor组件不兼容IE10及以下浏览器，请使用更高版本的浏览器查看', '浏览器不兼容通知', {
        confirmButtonText: '确定'
      });
    } else {
      next();
    }
  }
});

new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
});
