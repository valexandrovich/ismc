<script setup>
import {reactive} from "vue";
import customAxios from "@/utils/customAxios.js";
import {useSearchLeResultsStore} from "@/stores/search.le.results.js";
import Toastify from 'toastify-js';
import 'toastify-js/src/toastify.css';

const store = useSearchLeResultsStore();

const state = reactive({
  isLoading: false,
  searchForm: {
    params: {
    },
    name: '',
    edrpou: '',
    address: '',
    phone: ''
  },
})

const clearForm = () => {

  const currentParams = state.searchForm.params;

  state.searchForm = {
    params: {
    },
    name: '',
    edrpou: '',
    address: '',
    phone: ''
  }
}



const search = () => {
  // store.clearResults()
  state.isLoading = true;



  console.log(state.searchForm)

  customAxios.post('/importer/search-red/le', state.searchForm)
      .then(resp => {
        state.isLoading = false;
        store.searchForm = resp.data.personSearchDto
        store.results = resp.data.searchResults
      })
      .catch(err => {
        state.isLoading = false;
        Toastify({
          text: 'Помилка при виконанні запиту:' + err,
          duration: 5000,
          newWindow: true,
          gravity: "bottom",
          position: 'right',
          stopOnFocus: true,
          style: {
            background: "#811b1b",
            color: "#e7e7e7",
            "border-radius": '15px',

          },
          close: true,
        }).showToast();
        console.log(err)
      })
}
</script>

<template>
  <div class="flex gap-4 w-full relative" >

    <div v-if="state.isLoading" class="absolute top-0 left-0 bg-white bg-opacity-70  dark:bg-slate-800 dark:bg-opacity-70 rounded-lg h-full w-full flex flex-col justify-center items-center">
      <div class="loader justify-center text-center "></div>
      <span class="text-2xl font-bold uppercase  text-slate-400">Пошук</span>
    </div>

    <div class="flex flex-col bg-slate-50 dark:bg-opacity-10 w-40p p-4 rounded-lg gap-4">

      <div class="flex ">
        <span class="text-sm text-gray-400 uppercase font-semibold">Дані про особу:</span>
      </div>
      <div class="flex flex-col gap-4">
        <input type="text" placeholder="Назва" v-model="state.searchForm.name"  @keyup.enter="search">
      </div>
      <input type="text" placeholder="ЄДРПОУ" v-model="state.searchForm.edrpou"  @keyup.enter="search" >


    </div>
    <div class="flex flex-col  w-60p gap-4">
      <div class="flex  flex-col bg-slate-50 dark:bg-opacity-10 rounded-lg p-4 gap-4">
        <div class="flex ">
          <span class="text-sm text-gray-400 uppercase font-semibold">Інше:</span>
        </div>
        <input type="text" placeholder="Телефон" v-model="state.searchForm.address"  @keyup.enter="search" disabled>
        <input type="text" placeholder="Адреса" class="" v-model="state.searchForm.phone"  @keyup.enter="search" disabled>

      </div>


      <div class="bg-slate-50 bg-opacity-100 dark:bg-opacity-10 rounded-lg p-4 flex justify-center gap-4">

        <button @click="search" class="bg-teal-600 text-white px-6 py-1 rounded-lg hover:bg-teal-500">Пошук</button>
        <button @click="clearForm" class="outline outline-1  px-6 py-1 rounded-lg text-gray-500 dark:text-gray-300 outline-gray-400 hover:bg-gray-500 hover:text-slate-50">Очистити</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.loader {
  border: 5px solid rgba(204, 204, 204, 0.49); /* Light grey background */
  border-top: 5px solid #0d9488; /* Blue color */
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
</style>