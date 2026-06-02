import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Vant, { Loading } from 'vant'
import 'vant/lib/index.css'

import App from './App.vue'
import router from './router'
import { useUserStore } from './stores/user'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(Vant)
app.use(Loading)

const userStore = useUserStore()
userStore.loadFromStorage()

app.mount('#app')