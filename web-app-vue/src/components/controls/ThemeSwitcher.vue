<script setup>

import {ref, watchEffect} from "vue";
import {Icon} from "@iconify/vue";

const theme = ref(localStorage.getItem('theme') || 'light')

const toggleTheme = () => {
  theme.value = theme.value === 'light' ? 'dark' : 'light'
  localStorage.setItem('theme', theme.value)
}

watchEffect(() => {
  if (theme.value === 'dark') {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
})

</script>

<template>
    <div class="">
      <Icon icon="ci:sun" width="18"  v-if="theme === 'light'" @click="toggleTheme" class="cursor-pointer hover:text-gray-400 "/>
      <Icon icon="ci:moon" width="18" v-if="theme === 'dark'" @click="toggleTheme" class="cursor-pointer hover:text-gray-400"/>
    </div>
</template>

<style scoped>

</style>