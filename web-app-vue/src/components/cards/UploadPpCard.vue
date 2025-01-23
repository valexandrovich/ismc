<script setup>
import {isoDateStrToShortDateStr, isoDateTimeStrToShortDateStr} from "@/utils/convertor.js";
import {computed} from "vue";
import {Icon} from "@iconify/vue";

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

  <div   class="flex flex-col  h-full  w-[750px]">
<!--    {{g}}-->
    <div class="flex flex-row justify-end">
      <span v-if="isDisabled" class="bg-gray-400 z-70 uppercase px-3 pr-[30px] font-semibold text-gray-50 rounded-t-2xl">Неактивно</span>
      <span class="bg-sky-700 z-80 uppercase px-3 py-0.5 ml-[-20px] text-sm text-gray-50 rounded-t-2xl">Ручне завантаження</span>
    </div>

    <div class="relative h-full text-sm bg-white dark:bg-opacity-10 rounded-2xl rounded-tr-none p-3 shadow-xl">
      <table class="pb" :class="isDisabled ? 'text-gray-400':''">
        <tbody>
        <tr><th>ID:</th><td>{{g.id}}</td></tr>
        <tr><th class="flex gap-2 text-nowrap"><Icon icon="twemoji:flag-ukraine" width="18px" /> </th><td>{{g.lastNameUa}} {{g.firstNameUa}} {{g.patronymicNameUa}}</td></tr>
        <tr><th class="flex gap-2 text-nowrap"><Icon icon="twemoji:flag-russia" width="18px" /> </th><td>{{g.lastNameRu}} {{g.firstNameRu}} {{g.patronymicNameRu}}</td></tr>
        <tr><th class="flex gap-2 text-nowrap"><Icon icon="twemoji:flag-united-states" width="18px" /> </th><td>{{g.lastNameEn}} {{g.firstNameEn}} {{g.patronymicNameEn}}</td></tr>


        <tr><th  class="text-nowrap">Дата народження: </th><td>{{isoDateStrToShortDateStr(g.birthday)}}</td></tr>
        <tr><th class="text-nowrap">РНОКПП (ІПН):</th><td>{{g.inn}}</td></tr>

        <tr><th>Паспорт книжка:</th><td>{{g.localPassSerial}}{{g.localPassNum}} від {{isoDateStrToShortDateStr(g.localPassIssueDate)}} виданий {{g.localPassIssuer}}</td></tr>
        <tr><th>Паспорт ID картка:</th><td>{{g.idPassNumber}} запис  {{g.idPassRecord}} від {{isoDateStrToShortDateStr(g.idPassIssueDate)}} виданий {{g.idPassIssuerCode}}</td></tr>

        <tr><th>Код мітки:</th><td>{{g.markId}}</td></tr>
        <tr><th>Дата події:</th><td>{{isoDateStrToShortDateStr(g.markEventDate)}}</td></tr>
        <tr><th>Дата початку:</th><td>{{isoDateStrToShortDateStr(g.markStartDate)}}</td></tr>
        <tr><th>Дата закінчення:</th><td>{{isoDateStrToShortDateStr(g.markEndDate)}}</td></tr>

<!--        <tr><th>Ім`я:</th><td>{{g.markTextValue}}</td></tr>-->
<!--        <tr><th>Ім`я:</th><td>{{g.markNumberValue}}</td></tr>-->
<!--        <tr><th>Ім`я:</th><td>{{g.markComment}}</td></tr>-->
        <tr><th>Джерело:</th><td>{{g.source}}</td></tr>


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