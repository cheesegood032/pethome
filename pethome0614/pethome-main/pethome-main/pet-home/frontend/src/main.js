import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI, { Message } from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI)

// 全局设置 Element UI Message 提示的显示时长（单位：毫秒）
const originalMessage = Message
const shortMessage = function (options) {
  if (typeof options === 'string') {
    options = { message: options }
  }
  if (!options.duration && options.duration !== 0) {
    options.duration = 1500
  }
  return originalMessage(options)
}
  ;['success', 'warning', 'info', 'error'].forEach(type => {
    shortMessage[type] = function (options) {
      if (typeof options === 'string') {
        options = { message: options }
      }
      options.type = type
      if (!options.duration && options.duration !== 0) {
        options.duration = 1500
      }
      return originalMessage(options)
    }
  })
shortMessage.close = originalMessage.close
shortMessage.closeAll = originalMessage.closeAll
Vue.prototype.$message = shortMessage

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
