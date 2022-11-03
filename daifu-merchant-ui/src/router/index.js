import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);

export default new Router({
  // mode: "history",
  routes: [
    {
      path: '/',
      redirect: '/index'
    },
    {
      path: '/',
      component: () => import('../components/common/Base.vue'),
      meta: {
        title: '公共部分'
      },
      children: [
        {
          path: '/index',
          component: () => import('../components/page/Home.vue'),
          meta: {
            title: '控制台'
          }
        },
        {
          path: '/orderlist',
          component: () => import('../components/page/Orderlist.vue'),
          meta: {
            title: '代收订单'
          }
        },
        {
          path: '/UseAddress',
          component: () => import('../components/page/UseAddress.vue'),
          meta: {
            title: '收款地址'
          }
        },
        {
          path: '/consumption',
          component: () => import('../components/page/Consumption.vue'),
          meta: {
            title: '代付订单'
          }
        },
        {
          path: '/examination',
          component: () => import('../components/page/Examination.vue'),
          meta: {
            title: '大额审查'
          }
        },
        {
          path: '/abnormalorder',
          component: () => import('../components/page/Abnormalorder.vue'),
          meta: {
            title: '异常订单'
          }
        },
        {
          path: '/ycdsOrder',
          component: () => import('../components/page/ycdsOrder.vue'),
          meta: {
            title: '异常订单'
          }
        },
        {
          path: '/finance',
          component: () => import('../components/page/finance.vue'),
          meta: {
            title: '财务管理'
          }
        },
        {
          path: '/apicloud',
          component: () => import('../components/page/Apicloud.vue'),
          meta: {
            title: 'API文档'
          }
        },
        {
          path: '/security',
          component: () => import('../components/page/Security.vue'),
          meta: {
            title: '安全中心'
          }
        },
        {
          path: '/journal',
          component: () => import('../components/page/Journal.vue'),
          meta: {
            title: '日志记录'
          }
        },
        {
          path: '/setup',
          component: () => import('../components/page/Setup.vue'),
          meta: {
            title: '设置'
          }
        },
        {
          path: '/role',
          component: () => import('../components/page/role.vue'),
          meta: {
            title: '角色设置'
          }
        },
        {
          path: '/user',
          component: () => import('../components/page/user.vue'),
          meta: {
            title: '用户设置'
          }
        },
        {
          path: '/chart-simple',
          component: () => import('../components/page/EchartsSimple.vue'),
          meta: {
            title: '简单图表'
          }
        },
        {
          path: '/chart-complex',
          component: () => import('../components/page/EchartsComplex.vue'),
          meta: {
            title: '复杂图表'
          }
        },
        {
          path: '/tab',
          component: () => import('../components/page/Tab.vue'),
          meta: {
            title: 'tab选项卡'
          }
        },
        {
          path: '/table',
          component: () => import('../components/page/Table.vue'),
          meta: {
            title: '表格'
          }
        }
      ]
    },
    {
      path: '/login',
      component: () => import('../components/page/Login.vue')
    },
    {
      path: '/forget',
      component: () => import('../components/page/Forget.vue')
    },
    {
      path: '/register',
      component: () => import('../components/page/register.vue')
    },
    {
      path: '/error',
      component: () => import('../components/page/Error.vue')
    },
    {
      path: '/404',
      component: () => import('../components/page/404.vue')
    }
  ]
})
