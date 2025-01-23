<script setup>
import {computed, ref} from "vue";
import FloatingLabelInput from "@/components/controls/FloatingLabelInput.vue";
import menuItems from "@/utils/menuItems.js";
import ThemeSwitcher from "@/components/controls/ThemeSwitcher.vue";
import LogoIcon from "@/components/LogoIcon.vue";
import DotsTexture from "@/components/DotsTexture.vue";
import {Icon} from "@iconify/vue";
import {useUserStore} from "@/stores/user.js";
import SidebarComponent from "@/components/SidebarComponent.vue";
import SearchPiFormComponent from "@/components/SearchPiFormComponent.vue";
import SearchLeFormComponent from "@/components/SearchLeFormComponent.vue";
import SearchResultsPiComponent from "@/components/SearchResultsPiComponent.vue";
import SearchResultsLeComponent from "@/components/SearchResultsLeComponent.vue";
import * as XLSX from "xlsx";
import {useDropZone} from "@vueuse/core";
import {useUploadPpStore} from "@/stores/upload.pp.js";
import UploaderPiComponent from "@/components/UploaderPiComponent.vue";
import FilesPiComponent from "@/components/FilesPiComponent.vue";
import FilesLeComponent from '@/components/FilesLeComponent.vue'


const userStore = useUserStore()

const currentTab = ref(1)

const changeTab = (tab) => {
  currentTab.value = tab;
}






</script>

<template>

  <div class="flex ">
    <SidebarComponent/>
    <main class="flex flex-col items-center w-full  h-screen  overflow-y-scroll custom-scrollbar">
      <div class="flex flex-col pt-8 max-w-[1620px] w-full">
        <div class="flex justify-between  px-4">
          <div>
            <span class="text-2xl flex items-center gap-2 font-bold   uppercase     ">
              <Icon icon="material-symbols:upload" width="28"/>
            Файли
              </span>
          </div>
          <div class="flex gap-6 items-center font-semibold">
            <ThemeSwitcher/>
            <span>{{ userStore.user?.username }}</span>
            <Icon icon="material-symbols:logout" class="hover:text-teal-500 cursor-pointer" width="18"
                  @click="userStore.logout()"/>
          </div>
        </div>
        <div class="flex flex-col px-4 pt-10 gap-4">
          <div class="flex  flex-col gap-4 bg-white bg-opacity-0 dark:bg-opacity-0  rounded-xl  ">
            <div class="flex flex-row">
              <button @click="currentTab = 1" class=" px-4 py-0.5  rounded-l-lg"
                      :class="currentTab === 1 ? 'bg-teal-600 text-white' : 'bg-slate-300 text-gray-400 dark:bg-opacity-20'">
                Фізичні особи
              </button>
              <button @click="currentTab = 2" class=" px-4 py-0.5  rounded-r-lg"
                      :class="currentTab === 2 ? 'bg-teal-600 text-white' : 'bg-slate-300 text-gray-400 dark:bg-opacity-20'">
                Юридичні особи
              </button>
            </div>
            <div v-if="currentTab === 1" class="flex flex-col bg-white dark:bg-opacity-10 rounded-lg">
              <FilesPiComponent/>
            </div>
          <div v-if="currentTab === 2" class="flex flex-col bg-white dark:bg-opacity-10 rounded-lg">
            <FilesLeComponent/>
          </div>
        </div>


      </div>


  </div>


  </main>
  </div>


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


</style>