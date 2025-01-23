<script setup>

import {onMounted} from "vue";
import {useUserStore} from "@/stores/user.js";

const userStore = useUserStore()

onMounted(()=>{
  const savedUser = JSON.parse(localStorage.getItem("user"));
  if (savedUser) {
    userStore.user = savedUser
  }
})

</script>

<template>
<!--  <div style="background-color:#b7cebb;">-->
<!--      <button @click="userStore.logout()">Logout</button>-->
<!--      <NavbarComponent/>-->
<!--  </div>-->
  <RouterView />
</template>

<style scoped>

</style>
