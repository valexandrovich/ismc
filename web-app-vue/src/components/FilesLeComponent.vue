<script setup>

import {onMounted} from "vue";
import customAxios from "@/utils/customAxios.js";
import {Icon} from "@iconify/vue";
import {dateToShortDateStr, isoDateTimeStrToShortDateStr} from "@/utils/convertor.js";
import Toastify from "toastify-js";
import { useFilesLeStore } from '@/stores/files.le.js'

const filesLeStore = useFilesLeStore();
const isLoading = false


onMounted(()=>{
  filesLeStore.fetchData();
})

const uploadFile = (id) => {
  customAxios.post("/uploader/le/upload/" + id)
      .then(resp => {
        console.log(resp)

        Toastify({
          text: 'Обробку файлу запущено ',
          duration: 3000,
          newWindow: true,
          gravity: "top", // `top` or `bottom`
          position: 'right', // `left`, `center` or `right`
          stopOnFocus: true, // Prevents dismissing of toast on hover
          style: {
            background: "rgb(24,114,105)",
            color: "#e7e7e7",
            "border-radius": '15px',

          },
        }).showToast();

        filesLeStore.fetchData();
      })
      .catch(err => {
        console.log(err)
      })
}


const deleteFile = (id) => {
  customAxios.delete("/uploader/le/delete/" + id)
      .then(resp => {
        Toastify({
          text: 'Файл видалено ',
          duration: 3000,
          newWindow: true,
          gravity: "top", // `top` or `bottom`
          position: 'right', // `left`, `center` or `right`
          stopOnFocus: true, // Prevents dismissing of toast on hover
          style: {
            background: "rgb(203,147,147)",
            color: "#e7e7e7",
            "border-radius": '15px',

          },
        }).showToast();
        filesLeStore.fetchData();
      })
      .catch(err => {
        console.log(err)
      })
}

</script>

<template>

  <div class="relative">




  <div v-if="isLoading" class="absolute top-0 left-0 bg-white dark:bg-slate-600 bg-opacity-80 z-40 w-full rounded-lg">
    <div class=" justify-center flex flex-col items-center py-12">
      <div class="loader justify-center text-center "></div>
      <span>Завантаження</span>
    </div>
  </div>

    <div class="">
      <table class="w-full">
        <thead>
        <th>ID файлу</th>
        <th>Назва файлу</th>
        <th>Автор</th>
        <th>Кількість записів</th>
        <th>Новий файл</th>
        <th>Дата створення</th>
        <th>Завантажити</th>
        <th>Видалити</th>
        </thead>
        <tbody>
        <tr v-for="file in filesLeStore.sortedFiles" :key="file.id">
          <td class="text-xs">{{file.id}}</td>
          <td>{{file.fileName}}</td>
          <td>{{file.author}}</td>
          <td>{{file.rowsCount}}</td>
          <td><span v-if="file.isNew" class="text-green-400"><Icon icon="material-symbols:check" width="24" /></span> </td>
          <td>{{isoDateTimeStrToShortDateStr(file.createDate)}} </td>
          <td><button @click="uploadFile(file.id)" class="bg-teal-600 text-slate-50 px-3 py-1 rounded-lg hover:bg-teal-500 disabled:bg-slate-400" :disabled="!file.isNew">Завантажити</button></td>
          <td><button @click="deleteFile(file.id)" class="bg-transparent text-red-500 px-3 py-1 rounded-lg hover:bg-red-200">
            <Icon icon="ph:trash" />
          </button></td>
        </tr>
        </tbody>
      </table>
      
    </div>

  </div>


</template>

<style scoped>
.loader {
  border: 5px solid rgba(243, 243, 243, 0.21); /* Light grey background */
  border-top: 5px solid #14b7a5; /* Blue color */
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 2s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

th {
  @apply text-start  p-2  ;
}

td {
  @apply text-start  p-2 ;
}
</style>