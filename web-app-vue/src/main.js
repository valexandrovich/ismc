import './assets/main.css'

import {createApp} from 'vue'
import {createPinia} from 'pinia'


// import PrimeVue from 'primevue/config';
// import PrimeVueComponents from './primevue-components';
// import Aura from '@primevue/themes/aura';
// import Lara from '@primevue/themes/lara';
// import Nora from '@primevue/themes/nora';

import App from './App.vue'
import router from './router'
// import {definePreset} from "@primevue/themes";
// import Button from "primevue/button";

const app = createApp(App)

// const MyPreset = definePreset( Aura, {
    // components: {
    //     Button: {
    //         root: {class: 'bg-sky-500'}
    //     },
    // }
    // semantic: {
    //     primary: {
    //         50: '{indigo.50}',
    //         100: '{indigo.100}',
    //         200: '{indigo.200}',
    //         300: '{indigo.300}',
    //         400: '{indigo.400}',
    //         500: '{indigo.500}',
    //         600: '{indigo.600}',
    //         700: '{indigo.700}',
    //         800: '{indigo.800}',
    //         900: '{indigo.900}',
    //         950: '{indigo.950}'
    //     },
    //
    //     formField: {
    //         paddingX: '5px',
    //         paddingY: '12px',
    //         borderRadius: '5px',
    //         backgroundColor: 'red'
    //     },
    //
    //
    // }

// });

// app.use(PrimeVue, { unstyled: true });
// app.use(PrimeVue, {
//     theme: {
//         preset: MyPreset,
//         options: {
//             // prefix: 'p',
//             darkModeSelector: '.p-dark',
//             // darkModeSelector: 'system',
//             // darkModeSelector: '.my-app-dark',
//         }
//     },
    // unstyled: true,



    // UNSTYLED
    // pt: {
    //     inputtext: {
    //         root: {class: 'bg-red-200'},
    //         label: 'text-white font-bold text-xl'
    //     },
    //     button: {
    //         root: {class: 'bg-red-800'},
    //     }
    // }

    // pt: {
    //     button: {
    //         root: {class: 'bg-red-600 hover:bg-sky-500 cursor-pointer text-white px-4 py-1 rounded border-0 flex gap-2'},
    //         label: 'text-white font-bold text-xl',
    //         icon: 'text-sky-200  text-xl'
    //     },
    //     panel: {
    //         header: 'bg-primary text-primary-contrast border-primary',
    //         content: 'border-primary text-lg text-primary-700',
    //         title: 'bg-primary text-primary-contrast text-xl',
    //         toggler: 'bg-primary text-primary-contrast hover:text-primary hover:bg-primary-contrast'
    //     },
    //     'text-input': {
    //         root: {class: 'bg-green-700'},
    //         label: 'text-green-500',
    //     }
    // }


// });




import {OhVueIcon} from "oh-vue-icons";


app.component("v-icon", OhVueIcon);


app.use(createPinia())
app.use(router)
// app.use(PrimeVueComponents);
app.mount('#app')
