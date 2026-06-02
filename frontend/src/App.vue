<template>
  <div class="app">
    <router-view />
    <van-tabbar v-model="active" route v-if="userStore.isLoggedIn">
      <van-tabbar-item to="/" icon="location-o">地图</van-tabbar-item>
      <van-tabbar-item to="/borrow" icon="plus">租借</van-tabbar-item>
      <van-tabbar-item to="/return" icon="down">归还</van-tabbar-item>
      <van-tabbar-item to="/orders" icon="description">订单</van-tabbar-item>
      <van-tabbar-item to="/profile" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const active = ref(0)

const routeNameMap = {
  map: 0,
  borrow: 1,
  return: 2,
  orders: 3,
  profile: 4
}

watch(
  () => route.name,
  (newName) => {
    if (newName && routeNameMap[newName] !== undefined) {
      active.value = routeNameMap[newName]
    }
  },
  { immediate: true }
)
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
