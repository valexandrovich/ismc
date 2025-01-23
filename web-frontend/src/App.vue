<script setup>
import SideMenuComponent from "@/components/SideMenuComponent.vue";
import {useRoute} from "vue-router";
import {computed} from "vue";

const route = useRoute();

// Computed property to check if the current route is /login
const showSideMenu = computed(() => route.path !== '/login');

</script>

<template>
<!--  TODO Scroll independed-->
  <div class="flex min-h-screen">
    <side-menu-component v-if="showSideMenu"/>
    <main :class="[showSideMenu ? 'flex-1  p-4 bg-gray-200' : 'w-full']"  >
      <router-view></router-view>
    </main>
  </div>

</template>

<style scoped>

</style>
