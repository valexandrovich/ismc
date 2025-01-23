import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import FontAwesomeIcon from './fontawesome';

const app = createApp(App)

window.RUNTIME_ENV = {
  VITE_API_PREFIX: '/api'
};

app.use(createPinia())
app.use(router)
app.component('font-awesome-icon', FontAwesomeIcon);
app.mount('#app')
