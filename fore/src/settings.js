var Cookies = require('js-cookie')
module.exports = {
  title: '可爱鸟管理系统',

  /**
   * @type {boolean} true | false
   * @description 是否显示系统LOGO
   */
  sidebarLogo: Cookies.get('bird_sidebar_logo') || false
}
