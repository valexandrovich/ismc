<script setup>
import {useSearchPpResultsStore} from "@/stores/search.pp.results.js";
import Govua01Card from "@/components/cards/Govua01Card.vue";
import Govua06Card from "@/components/cards/Govua06Card.vue";
import Govua07Card from "@/components/cards/Govua07Card.vue";
import Govua08Card from "@/components/cards/Govua08Card.vue";
import Govua09Card from "@/components/cards/Govua09Card.vue";
import Govua10Card from "@/components/cards/Govua10Card.vue";
import Govua11Card from "@/components/cards/Govua11Card.vue";
import Govua12Card from "@/components/cards/Govua12Card.vue";
import Govua13Card from "@/components/cards/Govua13Card.vue";
import UploadPpCard from "@/components/cards/UploadPpCard.vue";

const store = useSearchPpResultsStore()
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
        <div v-for="item in store.results.govua08List" :key="item.hash">
          <Govua08Card :g="item"/>
        </div>
        <div v-for="item in store.results.govua09List" :key="item.hash">
          <Govua09Card :g="item"/>
        </div>
        <div v-for="item in store.results.govua10List" :key="item.hash">
          <Govua10Card :g="item"/>
        </div>
        <div v-for="item in store.results.govua11List" :key="item.hash">
          <Govua11Card :g="item"/>
        </div>
        <div v-for="item in store.results.govua12List" :key="item.hash">
          <Govua12Card :g="item"/>
        </div>
        <div v-for="item in store.results.govua13List" :key="item.hash">
          <Govua13Card :g="item"/>
        </div>
        <div v-for="item in store.results.uploadPpList" :key="item.hash">
          <UploadPpCard :g="item"/>
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