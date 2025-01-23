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
import router from "@/router/index.js";


const userStore = useUserStore()

const currentTab = ref(1)

const changeTab = (tab) => {
  currentTab.value = tab;
}

const value = ref('')

const ptConfig = ref({
  root: {class: 'bg-red-400'}
});


</script>

<template>
  <div class="flex ">
    <SidebarComponent/>
    <main class="flex flex-col items-center w-full  h-screen  overflow-y-scroll custom-scrollbar">
      <div class="flex flex-col pt-8 max-w-[1620px] w-full">
        <div class="flex justify-end  px-4">
          <div class="flex gap-6 items-center font-semibold">
            <ThemeSwitcher/>
            <div v-if="userStore.user" class="flex items-center gap-2">
              <span>{{ userStore.user?.username }}</span>
              <Icon icon="material-symbols:logout" class="hover:text-teal-500 cursor-pointer" width="18"
                    @click="userStore.logout()"/>
            </div>
            <div v-else>
              <button class="bg-teal-600 px-4 py-1 rounded-lg hover:bg-teal-500 text-slate-50"
                      @click="router.push('/login')">Вхід
              </button>
            </div>
          </div>
        </div>
        <div class="flex flex-col px-4 pt-10 gap-4">
          <div v-if="userStore.user" class="bg-white bg-opacity-80 dark:bg-opacity-10 p-4 rounded-lg">

            <div class="flex justify-between items-center">
            <span class="flex items-center gap-2">
              <Icon icon="mdi:account" width="24"></Icon>
              Користувач:  <strong class="text-teal-500">{{userStore.user?.username}}</strong> </span>
              <button @click="userStore.logout()" class="bg-teal-600 px-4 py-1 text-white rounded-lg hover:bg-teal-500">Вихід</button>
            </div>

            <div v-if="userStore.user?.roles.length > 0" class="flex flex-col py-4">

              <span class="font-semibold text-lg">Доступні ролі:</span>
              <span v-for="role in userStore.user.roles" :key="role">
                - {{role}}
              </span>

            </div>
            <div v-else class="flex flex-col items-center py-12">
              <span class="text-red-400 text-xl font-semibold">Увага! Не знайдено жодної ролі для системи AFS!</span>
              <span class="text-md ">Зверніться до адміністраторів системи за допомогою листа на адресу <span class="font-bold font-exo">antifraud@otpbank.com.ua</span> для отримання інструкцій для отримання доступу</span>
            </div>

<!--            <span>Доступні ролі:</span>-->
<!--            <div v-for="role in userStore.user.roles" :key="role">-->
<!--              {{ role }}-->
<!--            </div>-->
          </div>

          <div class="bg-white bg-opacity-80 dark:bg-opacity-10 p-4 rounded-lg">
            <span>asdasda</span>
            <span>asdasda</span>
            <span>asdasda</span>
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