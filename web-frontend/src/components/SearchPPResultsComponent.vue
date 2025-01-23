<script setup>
import {isoDateStrToShortDateStr, isoDateTimeStrToShortDateStr} from "@/utils/convertor.js";
import {useSearchPpResultsStore} from "@/stores/search.pp.results.js";
import Govua01Card from "@/components/Govua01Card.vue";
import Govua08Card from "@/components/Govua08Card.vue";
import Govua09Card from "@/components/Govua09Card.vue";
import Govua06Card from "@/components/Govua06Card.vue";
import Govua07Card from "@/components/Govua07Card.vue";
import Govua10Card from "@/components/Govua10Card.vue";
import Govua12Card from "@/components/Govua12Card.vue";
import Govua11Card from "@/components/Govua11Card.vue";
import Govua13Card from "@/components/Govua13Card.vue";
const store = useSearchPpResultsStore();
</script>

<template>

<!--  {{  store.isResultsEmpty }}-->

<!--  <div v-if="store.results && store.isResultsEmpty">-->
<!--    {{  store.isResultsEmpty }}-->
<!--      <span  class="text-gray-400 font-bold text-2xl">Немає результатів пошуку</span>-->

<!--  </div>-->

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

  <div v-if="store.results.govua08List.length > 0" class="mt-4">
    <span class=" text-2xl text-gray-400 font-black">Розшук МВС:</span>
    <div class="flex flex-row flex-wrap gap-4  mt-4" >
      <Govua08Card v-for="g in store.results.govua08List" :key="g.hash"   :g="g" />
    </div>
  </div>

  <div v-if="store.results.govua09List.length > 0" class="mt-4">
    <span class=" text-2xl text-gray-400 font-black">Реєстр безвістно зниклих:</span>
    <div class="flex flex-row flex-wrap gap-4  mt-4" >
      <Govua09Card v-for="g in store.results.govua09List" :key="g.hash"   :g="g" />
    </div>
  </div>


  <div v-if="store.results.govua10List.length > 0" class="mt-4">
    <span class=" text-2xl text-gray-400 font-black">ДМСУ Недійсні паспорти Громадянина України:</span>
    <div class="flex flex-row flex-wrap gap-4  mt-4" >
      <Govua10Card v-for="g in store.results.govua10List" :key="g.hash"   :g="g" />
    </div>
  </div>

  <div v-if="store.results.govua11List.length > 0" class="mt-4">
    <span class=" text-2xl text-gray-400 font-black">ДМСУ Недійсні паспорти Громадянина України для виїзду за кордон:</span>
    <div class="flex flex-row flex-wrap gap-4  mt-4" >
      <Govua11Card v-for="g in store.results.govua11List" :key="g.hash"   :g="g" />
    </div>
  </div>


  <div v-if="store.results.govua12List.length > 0" class="mt-4">
    <span class=" text-2xl text-gray-400 font-black">МВС Недійсні паспорти Громадянина України:</span>
    <div class="flex flex-row flex-wrap gap-4  mt-4" >
      <Govua12Card v-for="g in store.results.govua12List" :key="g.hash"   :g="g" />
    </div>
  </div>


  <div v-if="store.results.govua13List.length > 0" class="mt-4">
    <span class=" text-2xl text-gray-400 font-black">МВС Недійсні паспорти Громадянина України для виъзду за кордон:</span>
    <div class="flex flex-row flex-wrap gap-4  mt-4" >
      <Govua13Card v-for="g in store.results.govua13List" :key="g.hash"   :g="g" />
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