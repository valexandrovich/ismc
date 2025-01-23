<script setup>

import { onMounted, reactive, ref, watch } from 'vue'
import {useUserStore} from "@/stores/user.js";
import customAxios from "@/utils/customAxios.js";
import LogoIcon from "@/components/LogoIcon.vue";
import ThemeSwitcher from "@/components/controls/ThemeSwitcher.vue";
import {useRoute, useRouter} from "vue-router";

const userStore = useUserStore()
const router = useRouter();
const route = useRoute();


const loginError = ref(null)

const loginData = reactive({
  username: '',
  password: ''
})

watch(() => loginData.username, () => {
  loginError.value = null;
});

watch(() => loginData.password, () => {
  loginError.value = null;
});




const login = () => {
  customAxios.post("/login", loginData)
      .then(resp => {
        userStore.login(resp.data)
        const redirectPath = route.query.redirect || { name: 'search' };
        router.push(redirectPath);
      })
      .catch(err => {
        console.log(err)
        if (err.response.status ===  500) {
          loginError.value = 'Невідома помилка'
        } else if (err.response.status ===  404) {
          loginError.value = 'Користувача не знайдено';
        } else if (err.response.status ===  401) {
          loginError.value = 'Невірний пароль';
        } else {
          loginError.value = 'Невідома помилка';
        }

      })
}



</script>

<template>

  <div class="relative min-h-screen w-full flex justify-center items-center">

    <div class="absolute top-4 right-4">
      <ThemeSwitcher/>
    </div>

        <div class="bg-white bg-opacity-30 dark:bg-opacity-15 px-16 py-16 rounded-3xl flex flex-col gap-4">
          <div class="flex  items-center mb-6">
          <LogoIcon color-class="text-teal-500 dark:text-teal-600 mr-1" width="52"/>
          <span class="text-4xl font-bold mr-2">AFS</span>
          <span class="text-4xl font-normal ">| Антіфрод</span>
          </div>
            <input type="text" placeholder="Логін" v-model="loginData.username" @keyup.enter="login" >
            <input type="password" placeholder="Пароль" v-model="loginData.password" @keyup.enter="login">
          <transition
              enter-active-class="transition-opacity duration-500 ease-in-out"
              enter-from-class="opacity-0"
              enter-to-class="opacity-100"
              leave-active-class="transition-opacity duration-500 ease-in-out"
              leave-from-class="opacity-100"
              leave-to-class="opacity-0"
          >

          <span v-if="loginError" class="text-red-800 mt-4 dark:text-white  text-center ">{{loginError}}</span>
          </transition>
            <button @click="login" class="bg-teal-600 mt-4 py-1 text-white rounded-lg hover:bg-teal-500">Вхід</button>
        </div>
  </div>


</template>

<style scoped>
input {
  @apply bg-white;
}
</style>