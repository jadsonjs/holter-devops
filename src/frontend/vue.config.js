module.exports = {
    publicPath: process.env.VUE_APP_PUBLIC_PATH,

    pages: {

        // ao acessar http://localhost:8080, redirecione para index.html
        index: {
          entry: 'src/main.js',
          template: 'public/index.html',
          title: ''
        },
    
        // ao acessar http://localhost:8080/login, redirecione para login.html
        login: {
          entry: 'src/login.js',
          template: 'public/login.html',
          title: ''
        },
    
      },
    
      // https://forum.vuejs.org/t/vue-router-on-multi-page-app-and-page-reload/48405/2
      // create a proxy for /login/* to request login.html
      // to work in devServer
      // only for localhost:3000/holter.  In projection (inside the jar do not need this)
      devServer: {
        proxy: {
          '/login': {
            target: 'http://localhost:'+process.env.PORT+''+process.env.VUE_APP_PUBLIC_PATH+'',
            pathRewrite: {'^/.*' : '/login.html'}
          }
        }
      }

    
}