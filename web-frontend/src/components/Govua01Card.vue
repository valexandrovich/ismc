<script setup>
import {isoDateStrToShortDateStr, isoDateTimeStrToShortDateStr} from "@/utils/convertor.js";
import {computed} from "vue";
import {useSearchPpResultsStore} from "@/stores/search.pp.results.js";

const props = defineProps({
  g: {
    type: Object,
    required: true
  }
})

const store = useSearchPpResultsStore();

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
  <div   class="flex flex-col   w-[480px]">
    <div class="flex flex-row justify-end">
      <span v-if="isDisabled" class="bg-gray-400 z-70 uppercase px-3 pr-[30px] font-semibold text-gray-50 rounded-t-2xl">Неактивно</span>
      <span class="bg-sky-700 z-80 uppercase px-3 py-0.5 ml-[-20px]  text-gray-50 rounded-t-2xl">Банкрутство</span>
    </div>

    <div class="relative h-full bg-white rounded-2xl rounded-tr-none p-3 shadow-xl">
      <table class="pb" :class="isDisabled ? 'text-gray-400':''">
        <tbody>
        <tr><th>Код:</th><td>{{g.firmEdrpou}}</td></tr>
        <tr><th>Назва:</th><td>{{g.firmName}}</td></tr>
        <tr><th>Тип:</th><td>{{g.type}}</td></tr>
        <tr><th>Номер справи:</th><td>{{g.caseNumber}}</td></tr>
        <tr ><th   class="pb-4">Назва суду:</th><td class="pb-4">{{g.courtName}}</td></tr>
        <tr class="border-t-4 border-gray-200 border-dotted"><th  class="pt-4">Дата події:</th><td class="pt-4">{{ isoDateStrToShortDateStr(g.date)}}</td></tr>
        <tr><th class="text-nowrap">Дата створення:</th><td>{{isoDateTimeStrToShortDateStr(g.createDate)}}</td></tr>
        <tr><th class="text-nowrap">Дата закінчення:</th><td>{{ isoDateTimeStrToShortDateStr(g.disableDate)}}</td></tr>
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