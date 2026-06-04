module.exports = {
  publicPath: '/',
  outputDir: 'dist',
  lintOnSave: false,
  devServer: {
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:8080/pet-home',
        changeOrigin: true
      }
    }
  }
}
