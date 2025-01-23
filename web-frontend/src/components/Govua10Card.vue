<script setup>
import {isoDateStrToShortDateStr, isoDateTimeStrToShortDateStr} from "@/utils/convertor.js";
import {computed} from "vue";

const props = defineProps({
  g: {
    type: Object,
    required: true
  }
})

const isDisabled = computed(() =>{
  if (props.g.disableDate){
    const d = new Date(props.g.disableDate)
    const cd = new Date();
    if (d <= cd){
      return true
    }
  }
  return false
})

</script>

<template>

  <div   class="flex flex-col   w-[350px]">
    <div class="flex flex-row justify-end">
      <span v-if="isDisabled" class="bg-gray-400 z-70 uppercase px-3 pr-[30px] font-semibold text-gray-50 rounded-t-2xl">Неактивно</span>
      <span class="bg-sky-700 z-80 uppercase px-3 py-0.5 ml-[-20px]  text-gray-50 rounded-t-2xl">Недійсний паспорт</span>
    </div>

    <div class="relative h-full bg-white rounded-2xl rounded-tr-none p-3 shadow-xl">
      <table class="pb" :class="isDisabled ? 'text-gray-400':''">
        <tbody>

        <tr><td colspan="2"  class=" pb-2 text-gray-400 font-semibold text-lg border-b-4 border-gray-200 border-dotted"><span>Паспорт громадянина України</span></td></tr>
        <tr class="text-xl"><th>Серія та номер:</th><td>{{g.series}} {{g.number}}</td></tr>
        <tr class="border-t-4 border-gray-200 border-dotted"><th class="pt-4">Дата події:</th><td class="pt-4">{{isoDateStrToShortDateStr(g.dateEdit)}}</td></tr>
        <tr><th class="text-nowrap">Дата створення:</th><td>{{isoDateTimeStrToShortDateStr(g.createDate)}}</td></tr>
        <tr><th class="text-nowrap">Дата закінчення:</th><td>{{isoDateTimeStrToShortDateStr(g.disableDate)}}</td></tr>
        </tbody>
      </table>
    </div>

  </div>





</template>

<style scoped>

tr {
  vertical-align: top;
}

th {

  text-align: left;
  padding-right: 10px;
  padding-bottom: 5px;
}
</style>