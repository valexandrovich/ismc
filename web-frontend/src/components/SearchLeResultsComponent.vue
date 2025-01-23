<script setup>
import {isoDateStrToShortDateStr, isoDateTimeStrToShortDateStr} from "@/utils/convertor.js";
import {useSearchLeResultsStore} from "@/stores/search.le.results.js";
import Govua01Card from "@/components/Govua01Card.vue";
import Govua06Card from "@/components/Govua06Card.vue";
import Govua07Card from "@/components/Govua07Card.vue";
import Govua08Card from "@/components/Govua08Card.vue";
import Govua09Card from "@/components/Govua09Card.vue";
const store = useSearchLeResultsStore();
</script>

<template>



  <div v-if="store.isResultsEmpty">
    <span  class="text-gray-400 font-bold text-2xl">Немає результатів пошуку</span>
  </div>






  <div v-if="!store.isResultsEmpty">


    <div v-if="store.results.govua01List.length > 0">
      <span class=" text-2xl text-gray-400 font-black">Банкрутство:</span>
      <div class="flex flex-row flex-wrap gap-4 mt-4" >
        <Govua01Card v-for="g1 in store.results.govua01List" :key="g1.hash"   :g="g1" />
      </div>
    </div>


    <div v-if="store.results.govua06List.length > 0" class="mt-4">
      <span class=" text-2xl text-gray-400 font-black">Автоматизована система виконавчих впроваджень:</span>
      <div class="flex flex-row flex-wrap gap-4  mt-4" >
        <Govua06Card v-for="g in store.results.govua06List" :key="g.hash"   :g="g" />
      </div>
    </div>


    <div v-if="store.results.govua07List.length > 0" class="mt-4">
      <span class=" text-2xl text-gray-400 font-black">Єдиний реєстр боржників:</span>
      <div class="flex flex-row flex-wrap gap-4  mt-4" >
        <Govua07Card v-for="g in store.results.govua07List" :key="g.hash"   :g="g" />
      </div>
    </div>

  </div>

</template>

<style scoped>
table {
  border-collapse: collapse; /* Ensures that the border is collapsed into a single border */
  width: 100%; /* Optional: Sets the table width */
}

table, th, td {
  border: 1px solid black; /* Sets the border style for the table and table cells */
}

th, td {
  padding: 8px; /* Adds some padding inside table cells for better readability */
  text-align: left; /* Aligns text to the left inside table cells */
}
</style>