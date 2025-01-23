<script setup>
import {useUploadPpStore} from "@/stores/upload.pp.js";
import * as XLSX from "xlsx";

import {useDropZone} from "@vueuse/core";

const uploadPpStore = useUploadPpStore();
const userStore = useUserStore();
import {v4 as uuidv4} from 'uuid';
import {computed, ref} from "vue";
import UploadPiCard from "@/components/UploadPiCard.vue";
import TextInput from "@/components/controls/TextInput.vue";
import {useUserStore} from "@/stores/user.js";
import Toastify from "toastify-js";

const dropZone = ref(null);
const fileInput = ref(null);


const handleDrop = (event) => {
  const files = event.dataTransfer.files;
  if (files.length) {
    processFile(files[0]);
  }
};

const handleFileSelect = (event) => {
  const files = event.target.files;
  if (files.length) {
    processFile(files[0]);
  }
};

const triggerFileSelect = () => {
  fileInput.value.click();
};

// const processFile = (file) => {
//   const reader = new FileReader();
//   reader.onload = (e) => {
//     const data = new Uint8Array(e.target.result);
//     const workbook = XLSX.read(data, { type: 'array' });
//     const sheetName = workbook.SheetNames[0];
//     const worksheet = workbook.Sheets[sheetName];
//
//     const range = XLSX.utils.decode_range(worksheet['!ref']);
//     range.s.r = Math.min(range.s.r + 3, range.e.r); // Skip first 3 rows
//     worksheet['!ref'] = XLSX.utils.encode_range(range);
//     const json = XLSX.utils.sheet_to_json(worksheet);
//     const dataWithUUID = json.map(row => ({
//       id: uuidv4(),
//       ...row
//     }));
//     uploadPpStore.data = dataWithUUID;
//   };
//   reader.readAsArrayBuffer(file);
// };


const processFile = async (file) => {
  isLoading.value = true
  await uploadPpStore.loadData(file)
  isLoading.value = false
};

useDropZone(dropZone, handleDrop);

const isLoading = ref(false)

const firstPage = () => {
  uploadPpStore.setPage(1);
};

const previousPage = () => {
  if (uploadPpStore.currentPage > 1) {
    uploadPpStore.setPage(uploadPpStore.currentPage - 1);
  }
};

const nextPage = () => {
  if (uploadPpStore.currentPage < uploadPpStore.totalPages) {
    uploadPpStore.setPage(uploadPpStore.currentPage + 1);
  }
};

const lastPage = () => {
  uploadPpStore.setPage(uploadPpStore.totalPages);
};

const changeItemsPerPage = (event) => {
  uploadPpStore.setItemsPerPage(parseInt(event.target.value));
};

const uploadData = async () => {

  isLoading.value = true;
  uploadPpStore.author = userStore.user.username
  await uploadPpStore.uploadData();

  Toastify({
    text: 'Файл завантажено у розділ "ФАЙЛИ"',
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

  uploadPpStore.data = []

  isLoading.value = false;
}

const isOnlyErrors = ref(false)


const isFiltered = computed({
  get: () => uploadPpStore.isFiltered,
  set: (value) => uploadPpStore.isFiltered = value
});

</script>

<template>

  <div class="relative">




    <div v-if="isLoading" class="absolute top-0 left-0 bg-white dark:bg-slate-600 bg-opacity-80 z-40 w-full rounded-lg">
      <div class=" justify-center flex flex-col items-center py-12">
        <div class="loader justify-center text-center "></div>
        <span>Завантаження</span>
      </div>

    </div>
    <div
        v-if="uploadPpStore.data.length == 0"
        ref="dropZone"
        @dragover.prevent
        @drop.prevent="handleDrop"
        class="drop-zone bg-white bg-opacity-70 dark:bg-opacity-10 py-4 text-center rounded-lg"
    >
      <p class="text-lg font-semibold mb-4">Виберіть файл для завантаження, або завантажте приклад файлу</p>
      <input type="file" ref="fileInput" @change="handleFileSelect" hidden/>
      <div class="flex gap-4 justify-center">
        <button @click="triggerFileSelect" class="bg-teal-600 text-white py-1 px-6 rounded-lg">Вибрати файл</button>
        <button class="bg-gray-600 text-white py-1 px-6 rounded-lg"><a href="/PP_LOADER.xlsx">Завантажити приклад</a></button>
      </div>


    </div>

    <div v-else class="my-4 flex bg-white bg-opacity-70 dark:bg-opacity-10 p-6  rounded-lg gap-4">
      <TextInput :validation-rules="[]" label="Назва файлу" placeholder="" v-model="uploadPpStore.fileName"
                 class="flex flex-grow"/>
      <button class="bg-teal-600 text-white py-1 px-6 rounded-lg disabled:bg-slate-400" @click="uploadData"
              :disabled="uploadPpStore.fileName === '' || !uploadPpStore.allIsValid">Завантажити
      </button>
      <button class="bg-gray-600 text-white py-1 px-6 rounded-lg" @click="uploadPpStore.data = []">Очистити</button>
    </div>


    <div v-if="uploadPpStore.data.length > 0" class="flex flex-col gap-6">

      <div class="flex gap-4 text-sm">
        <span>Записів: <strong>{{ uploadPpStore.data.length }}</strong></span>
        <span class="text-red-400">Записів з помилками: <strong>{{ uploadPpStore.errors }}</strong></span>
      </div>

      <!--      TOTAL ROWS: {{ uploadPpStore.totalRows }}-->
      <!--      FILETERD: {{uploadPpStore.isFiltered}}-->

      <div class="flex justify-between items-center">
        <div class="flex gap-2 items-center">
          <button @click="firstPage" :disabled="uploadPpStore.currentPage === 1"
                  class="outline outline-1 outline-offset-0 rounded-lg px-2 py-0 text-teal-600 font-semibold hover:bg-teal-600 hover:text-slate-50 disabled:bg-slate-300 disabled:outline-none disabled:text-slate-100 disabled:dark:bg-opacity-10">
            1
          </button>
          <button @click="previousPage" :disabled="uploadPpStore.currentPage === 1"
                  class="outline outline-1 outline-offset-0 rounded-lg px-2 py-0 text-teal-600 font-semibold hover:bg-teal-600 hover:text-slate-50 disabled:bg-slate-300 disabled:outline-none disabled:text-slate-100 disabled:dark:bg-opacity-10">
            Назад
          </button>
          <span>Сторінка {{ uploadPpStore.currentPage }} з {{ uploadPpStore.totalPages }}</span>
          <button @click="nextPage" :disabled="uploadPpStore.currentPage === uploadPpStore.totalPages"
                  class="outline outline-1 outline-offset-0 rounded-lg px-2 py-0 text-teal-600 font-semibold hover:bg-teal-600 hover:text-slate-50 disabled:bg-slate-300 disabled:outline-none disabled:text-slate-100 disabled:dark:bg-opacity-10">
            Вперед
          </button>
          <button @click="lastPage" :disabled="uploadPpStore.currentPage === uploadPpStore.totalPages"
                  class="outline outline-1 outline-offset-0 rounded-lg px-2 py-0 text-teal-600 font-semibold hover:bg-teal-600 hover:text-slate-50 disabled:bg-slate-300 disabled:outline-none disabled:text-slate-100 disabled:dark:bg-opacity-10">
            {{ uploadPpStore.totalPages }}
          </button>
        </div>

        <div class="flex gap-2">
          <span>Показати лише з помилками</span>
          <input type="checkbox" v-model="isFiltered">
        </div>
        <div class="flex gap-2 items-center">
          <label for="itemsPerPage">Записів на сторінку:</label>
          <select id="itemsPerPage" v-model="uploadPpStore.itemsPerPage" @change="changeItemsPerPage">
            <option v-for="n in [2, 5, 10, 20, 50]" :key="n" :value="n">{{ n }}</option>
          </select>
        </div>
      </div>

      <!--      <div v-for="row in uploadPpStore.data" :key="row">-->
      <!--        {{row}}-->

      <!--        <input type="text" v-model="row.lNameUa">-->
      <!--        <input type="text" v-model="row.fNameUa">-->

      <!--      </div>-->

      <div v-if="uploadPpStore.data.length > 0">

        <!--        {{row.id}}-->
        <UploadPiCard v-for="row in uploadPpStore.paginatedData()" :key="row.id" :row="row"/>
        <!---->
      </div>

      <!--    <UploadPiCard v-for="row in uploadPpStore.data" :key="row.id" :row="row"/>-->


      <!--      <span>{{row}}</span>-->
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


select {
  @apply p-1 rounded-lg bg-slate-50 dark:bg-opacity-10
}
</style>