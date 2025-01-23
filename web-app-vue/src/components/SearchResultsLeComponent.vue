<script setup>
import {useSearchLeResultsStore} from "@/stores/search.le.results.js";
import Govua01Card from "@/components/cards/Govua01Card.vue";
import Govua06Card from "@/components/cards/Govua06Card.vue";
import Govua07Card from "@/components/cards/Govua07Card.vue";
import UploadLeCard from '@/components/cards/UploadLeCard.vue'

const store = useSearchLeResultsStore()
</script>

<template>

  <div class="flex flex-col gap-4">

    <div  class="flex justify-center" v-if="store.isResultsEmpty">

      <span class="text-2xl text-slate-500">Немає результатів пошуку</span>


    </div>

    <div class="flex flex-col gap-4">
      <div class="flex flex-wrap gap-12 h-full ">
        <div v-for="item in store.results.govua01List" :key="item.hash">
          <Govua01Card :g="item"/>
        </div>
        <div v-for="item in store.results.govua06List" :key="item.hash">
          <Govua06Card :g="item"/>
        </div>
        <div v-for="item in store.results.govua07List" :key="item.hash">
          <Govua07Card :g="item"/>
        </div>
        <div v-for="item in store.results.uploadLeList" :key="item.hash">
          <UploadLeCard :g="item"/>
        </div>
      </div>
    </div>




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