import './index.css'
import 'vue-final-modal/style.css'
import '@/assets/styles/scrollbar.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createVfm } from 'vue-final-modal'

import App from './App.vue'
import router from './router'
import VueCookies from 'vue-cookies'
import Notifications from '@kyvg/vue3-notification'

import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import { faTrash, faPlus, faDownload, faPencil, faEye, faBolt, faCopy, faTriangleExclamation, faBan, faStop, faBuilding } from "@fortawesome/free-solid-svg-icons";
library.add(faTrash, faPlus, faDownload, faPencil, faEye, faBolt, faCopy, faTriangleExclamation, faBan, faStop, faBuilding);

const app = createApp(App)

app.component('font-awesome-icon', FontAwesomeIcon)
app.use(createPinia())
app.use(createVfm())
app.use(router)
app.use(VueCookies, { expires: '30d' })
app.use(Notifications)

app.mount('#app')
