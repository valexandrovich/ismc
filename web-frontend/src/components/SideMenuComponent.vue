<script setup>

import {routes} from "@/router/routes.js";
import circles from '@/assets/img/circles.png'

const backgroundStyle = computed(() => ({
  backgroundImage: `url(${circles})`,
  backgroundSize: 'cover',
  backgroundPosition: 'top',
}))

import { version } from '../../package.json';

import Logo from "@/components/icons/Logo.vue";
import {computed} from "vue";
</script>

<template>
  <aside class="w-64 bg-green-800  text-white flex flex-col justify-between"
         :style="backgroundStyle"
    >
    <div class="p-4 pr-0">


      <div class="text-center flex flex-col mb-5">
        <div class="flex flex-row  justify-center pr-12">

          <img src="@/assets/img/side_logo.png" alt="">
          
<!--          <logo/>-->
        </div>
      </div>
      <div class="flex flex-col mt-0 gap-1">
        <router-link v-for="route in routes.filter(r => r.isSideMenu)" :key="route.name" :to="route.path"
                     class="px-4 py-1 rounded-l-xl hover:bg-green-600 hover:text-white text-lg font-exo font-medium  transition-all duration-200 ease-in-out  ">
          <font-awesome-icon :icon="route.icon" class="mr-2"/>
          {{ route.label }}
        </router-link>
      </div>
    </div>


    <div class="flex flex-col p-4" style="position: fixed; bottom: 0; width: 16rem;">
      <div class="flex flex-row items-center justify-center gap-3 flex-wrap">
        <div class="font-semibold text-lg flex flex-row flex-nowrap items-center">
          <font-awesome-icon icon="user"  class="mr-2" />
          <span class="text-wrap">BieloienkoV</span>
        </div>

<!--        <button class="bg-amber-600  font-semibold px-2 py-1 rounded-lg hover:bg-amber-500">Вихід</button>-->

        </div>
      <div class="flex flex-row justify-center text-sm mt-2">
        <span>Версія {{ version }}</span>
      </div>
    </div>

  </aside>
</template>

<style scoped>
.router-link-active {
  /*@apply bg-gradient-to-r from-gray-200 to-white text-green-700 shadow-xl;*/
  @apply bg-gradient-to-r from-green-600 from-20%  text-white transition-all duration-300;
}


.router-link-active:hover {
  /* @apply bg-lime-600 text-white !important; */


}
</style>