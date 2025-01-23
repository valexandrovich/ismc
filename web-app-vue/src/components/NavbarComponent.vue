<script setup>

import {useUserStore} from "@/stores/user.js";
import {computed} from "vue";
import routes from "@/utils/menuItems.js";

const userStore = useUserStore()

const filteredMenuItems = computed(() => {
  return routes.filter(item =>
        item.isInMenu &&
        userStore.user?.roles.some(role => item.meta.roles.includes(role))

  );
});


</script>

<template>
  <div>
    <p>LOGO</p>
    <ul v-if="userStore.user">
      <li v-for="item in filteredMenuItems" :key="item.path">
        <router-link :to="item.path">{{item.label}}</router-link>
      </li>
    </ul>

    <button @click="userStore.logout()">Logout</button>

  </div>

</template>

<style scoped>

</style>