import './index.css'
import 'vue-final-modal/style.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import VueCookies from 'vue-cookies'
import Notifications from '@kyvg/vue3-notification'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VueCookies, { expires: '30d' })
app.use(Notifications)

app.mount('#app')
