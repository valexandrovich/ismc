<script setup>

import ScheduleTableComponent from "@/components/ScheduleTable.vue";
import {Icon} from "@iconify/vue";
import SidebarComponent from "@/components/SidebarComponent.vue";
import ThemeSwitcher from "@/components/controls/ThemeSwitcher.vue";
import {useUserStore} from "@/stores/user.js";


const userStore = useUserStore()


</script>

<template>

  <div class="flex ">
    <SidebarComponent/>
    <main class="flex flex-col items-center w-full  h-screen  overflow-y-scroll custom-scrollbar">
      <div class="flex flex-col pt-8 max-w-[1620px] w-full">
        <div class="flex justify-between  px-4">
          <div >
            <span class="text-2xl flex items-center gap-2 font-bold   uppercase     ">
              <Icon icon="mdi:calendar-month-outline" width="28"/>
            Розклад
              </span>
          </div>
          <div class="flex gap-6 items-center font-semibold">
            <ThemeSwitcher/>
            <span>{{userStore.user?.username}}</span>
            <Icon icon="material-symbols:logout" class="hover:text-teal-500 cursor-pointer" width="18" @click="userStore.logout()"/>
          </div>
        </div>
        <div class="flex flex-col px-4 pt-10 gap-4">
          <div class="bg-white bg-opacity-0 dark:bg-opacity-0  rounded-xl ">
            <ScheduleTableComponent/>
          </div>
        </div>
      </div>
    </main>
  </div>


</template>

<style scoped>

</style>