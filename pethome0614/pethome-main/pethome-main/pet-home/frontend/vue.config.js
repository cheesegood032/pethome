module.exports = {
  publicPath: '/',
  outputDir: 'dist',
  lintOnSave: false,
  devServer: {
    port: 9090,
    proxy: {
      '/api': {
        target: 'http://localhost:8080/pet_home',
        changeOrigin: true
      }
    }
  }
}
