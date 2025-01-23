<script setup>

import {Icon} from "@iconify/vue";
import LogoIcon from "@/components/LogoIcon.vue";
import {computed} from "vue";
import menuItems from "@/utils/menuItems.js";

const userStore = useUserStore();

import {version} from '../../package.json';
import {useUserStore} from "@/stores/user.js";

const asideItems = computed(() => {
  const user = useUserStore().user;

  const isDevMode = import.meta.env.DEV;

  // if (isDevMode) {
  //   return menuItems.filter(menuItem => {return menuItem.isInMenu})
  // } else {
    return menuItems.filter(item => {
      if (!item.isInMenu) return false;

      // console.log(item)
      // console.log(user)

      if (user && user.roles && item.meta.roles){
        return item.meta.roles.some(role => user.roles.includes(role));
      } else {
        return false
      }

      // if (!item.meta ) return false;
      // if (item.meta.requiresAuth === true) {
      //   if (user && user.roles && item.meta.roles) {
      //     return item.meta.roles.some(role => user.roles.includes(role));
      //   }
      //   return false;
      // }
      // return true;
    });
  // }


});


</script>

<template>
  <aside class="relative flex flex-col min-w-[280px] h-screen  py-7">


    <div class="flex justify-center items-center gap-1 text-gray-600 dark:text-gray-300  select-none">
      <LogoIcon color-class="text-teal-600 dark:text-teal-500 mr-1" width="36"/>
      <span class="text-2xl font-bold ">AFS</span>
      <span class="text-2xl font-normal ">| Антіфрод</span>

    </div>
    <div class="flex justify-center text-xs uppercase font-exo text-slate-500">
      <span>Версія: {{ version }}</span>
    </div>
    <div class="overflow-y-scroll custom-scrollbar mt-8">
      <ul class="flex flex-col gap-1">
        <li v-for="item in asideItems" :key="item.path" class=" px-6">
          <router-link :to="item.path"
                       class="flex gap-3 w-52 items-center py-1 pl-8 pr-2 rounded-lg   hover:bg-slate-300 hover:dark:bg-slate-700">
            <Icon :icon="item.icon" width="18"/>
            <span>{{ item.label }}</span></router-link>
        </li>
      </ul>
    </div>

    <div class="absolute opacity-20 dark:opacity-10 top-0 left-[-430px] flex justify-center items-center -z-10">
      <!--          <DotsTexture color-class="text-slate-500 dark:text-white dark:opacity-10 opacity-15" width="300px"/>-->
      <!--        <LogoIcon color-class="text-slate-900 dark:text-white dark:opacity-10 opacity-15" width="700px"/>-->
    </div>


  </aside>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  @apply bg-black bg-opacity-15 rounded-3xl;
  @apply dark:bg-white dark:bg-opacity-15 dark:rounded-3xl;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  @apply bg-black bg-opacity-35 rounded-3xl;
  @apply dark:bg-white dark:bg-opacity-35 dark:rounded-3xl;
}

.router-link-active {
  /*@apply bg-slate-600 bg-opacity-10;*/
  /*@apply bg-gradient-to-r from-gray-200 to-white text-green-700 shadow-xl;*/
  @apply bg-gradient-to-r from-teal-500 to-slate-300 from-5% to-5%;
  @apply dark:bg-gradient-to-r dark:from-teal-600 dark:to-teal-900 dark:from-5% dark:to-5%;
}


.router-link-active:hover {
  /* @apply bg-lime-600 text-white !important; */


}
</style>