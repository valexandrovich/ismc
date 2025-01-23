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

  <div   class="flex flex-col h-full  w-[350px]">
    <div class="flex flex-row justify-end">
      <span v-if="isDisabled" class="bg-gray-400 z-70 uppercase px-3 pr-[30px] font-semibold text-gray-50 rounded-t-2xl">Неактивно</span>
      <span class="bg-sky-700 z-80 uppercase px-3 py-0.5 ml-[-20px] text-sm  text-gray-50 rounded-t-2xl">Недійсний паспорт (ДМС)</span>
    </div>

    <div class="relative h-full text-sm bg-white dark:bg-opacity-10 rounded-2xl rounded-tr-none p-3 shadow-xl">

      <table class="pb" :class="isDisabled ? 'text-gray-400':''">
        <tbody>
        <tr><td colspan="2"  class=" pb-2 text-gray-400 font-semibold  border-b-2 border-gray-200 dark:border-slate-500 border-dotted"><span>Паспорт громадянина України для виїзду за кордон</span></td></tr>
        <tr class="text-lg"><th class="pt-2">Серія та номер:</th><td class="text-nowrap pt-2 font-semibold">{{g.series}} {{g.number}}</td></tr>
        <tr ><th>Статус:</th><td>{{g.status}}</td></tr>
        <tr class="border-t-2 border-gray-200 dark:border-slate-500 border-dotted"><th class="pt-4">Дата події:</th><td class="pt-4">{{isoDateStrToShortDateStr(g.dateEdit)}}</td></tr>
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