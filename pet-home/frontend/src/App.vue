<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
  background-color: #faf8f5;
  color: #333;
}

#app {
  min-height: 100vh;
}

a {
  text-decoration: none;
  color: inherit;
}

.btn-primary {
  background-color: #ff9f43;
  border-color: #ff9f43;
}

.btn-primary:hover {
  background-color: #ff8c1a;
  border-color: #ff8c1a;
}

.text-primary {
  color: #ff9f43;
}
</style>
