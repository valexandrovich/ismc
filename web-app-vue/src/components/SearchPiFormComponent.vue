<script setup>


import {reactive} from "vue";
import customAxios from "@/utils/customAxios.js";
import {useSearchPpResultsStore} from "@/stores/search.pp.results.js";
import Toastify from 'toastify-js';
import 'toastify-js/src/toastify.css';

const store = useSearchPpResultsStore();

const state = reactive({
  isLoading: false,
  searchForm: {
    params: {
      passportType: '1',
      isSimpleName: false
    },
    simpleName: '',
    lastName: '',
    firstName: '',
    patronymicName: '',
    birthday: '',
    inn: '',
    localPassportSerial: '',
    localPassportNumber: '',
    idPassportNumber: '',
    idPassportRecordNumber: '',
    intPassportSerial: '',
    intPassportNumber: '',
    otherPassportNumber: '',
    address: '',
    phone: ''
  },
})

const clearForm = () => {

  const currentParams = state.searchForm.params;

  state.searchForm = {
    params: {
      passportType: '1',
      isSimpleName: currentParams.isSimpleName
    },
    simpleName: '',
    lastName: '',
    firstName: '',
    patronymicName: '',
    birthday: '',
    inn: '',
    localPassportSerial: '',
    localPassportNumber: '',
    idPassportRecordNumber: '',
    idPassportNumber: '',
    intPassportSerial: '',
    intPassportNumber: '',
    otherPassportNumber: '',
    address: '',
    phone: ''
  }
}

const convertDate = (dateStr) => {
  const parts = dateStr.split('.');
  if (parts.length !== 3 || !parts[0] || !parts[1] || !parts[2] || isNaN(Date.parse(`${parts[2]}-${parts[1]}-${parts[0]}`))) {
    return "";
  }
  const converted = `${parts[2]}-${parts[1]}-${parts[0]}`;
  return converted;
}

const search = () => {
  // store.clearResults()
  state.isLoading = true;
  state.searchForm.birthday = convertDate(state.searchForm.birthday)



  console.log(state.searchForm)

  customAxios.post('/importer/search-red/pp', state.searchForm)
      .then(resp => {
        state.isLoading = false;
        store.searchForm = resp.data.personSearchDto
        store.results = resp.data.searchResults
      })
      .catch(err => {
        state.isLoading = false;
        Toastify({
          // text: 'Помилка при виконанні запиту:' + err,
          text:  err.response.status === 401 ?  'Час сеансу вичерпаний! Повторіть вхід' : 'Помилка при виконанні запиту:' + err,
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
      <div class="flex justify-between">
        <span>Тип пошуку</span>
        <div class="text-sm">
          <button @click="state.searchForm.params.isSimpleName = false" class=" px-4 py-0.5  rounded-l-lg"
                  :class="state.searchForm.params.isSimpleName === false ?  'bg-teal-600 text-white' : 'bg-slate-300 text-gray-400 dark:bg-opacity-20'">
            Окремо
          </button>
          <button @click="state.searchForm.params.isSimpleName = true" class=" px-4 py-0.5  rounded-r-lg"
                  :class="state.searchForm.params.isSimpleName === true ? 'bg-teal-600 text-white' : 'bg-slate-300 text-gray-400 dark:bg-opacity-20'">
            Разом
          </button>

          <!--      <button :class="!state.searchForm.params.isSimpleName ?  'bg-gray-500' : 'bg-teal-700'" @click="state.searchForm.params.isSimpleName = true">Разом</button>-->
          <!--      <button :class="state.searchForm.params.isSimpleName ?  'bg-gray-500' : 'bg-teal-700'" @click="state.searchForm.params.isSimpleName = false">Окремо</button>-->
        </div>
      </div>
      <div class="flex flex-col gap-4" v-if="state.searchForm.params.isSimpleName">
        <input type="text" placeholder="ПІБ Разом" v-model="state.searchForm.simpleName"  @keyup.enter="search">
      </div>
      <div class="flex flex-col gap-4" v-else>
        <input type="text" placeholder="Прізвищє" v-model="state.searchForm.lastName" @keyup.enter="search">
        <input type="text" placeholder="Ім`я" v-model="state.searchForm.firstName" @keyup.enter="search">
        <input type="text" placeholder="По-батькові" v-model="state.searchForm.patronymicName" @keyup.enter="search">
      </div>
            <input type="text" placeholder="Дата народження" v-model="state.searchForm.birthday" @keyup.enter="search">
            <input type="text" placeholder="РНОКПП (ІПН)" v-model="state.searchForm.inn" @keyup.enter="search">


    </div>
    <div class="flex flex-col  w-60p gap-4">
          <div class="flex flex-col bg-slate-50 dark:bg-opacity-10 rounded-lg p-4 gap-4">
            <div class="flex ">
              <span class="text-sm text-gray-400 uppercase font-semibold">Дані про паспорт:</span>
            </div>
            <div class="flex justify-between flex-wrap">
              <div><span>Тип</span></div>
                  <div class="text-sm">
                    <button @click="state.searchForm.params.passportType = '1'" class=" px-4 py-0.5  rounded-l-lg" :class="state.searchForm.params.passportType === '1' ?  'bg-teal-600 text-white' : 'bg-slate-300 text-gray-400 dark:bg-opacity-20'">Паспорт книжка</button>
                    <button @click="state.searchForm.params.passportType = '2'" class=" px-4 py-0.5  " :class="state.searchForm.params.passportType === '2' ? 'bg-teal-600 text-white' : 'bg-slate-300 text-gray-400 dark:bg-opacity-20'">ID картка</button>
                    <button @click="state.searchForm.params.passportType = '3'" class=" px-4 py-0.5  " :class="state.searchForm.params.passportType === '3'? 'bg-teal-600 text-white' : 'bg-slate-300 text-gray-400 dark:bg-opacity-20'">Закордоний паспорт</button>
                    <button @click="state.searchForm.params.passportType = '4'" class=" px-4 py-0.5  rounded-r-lg" :class="state.searchForm.params.passportType === '4'? 'bg-teal-600 text-white' : 'bg-slate-300 text-gray-400 dark:bg-opacity-20'">Інше</button>
                  </div>
            </div>
            <div class="flex gap-4 w-full" v-if="state.searchForm.params.passportType === '1'">
                    <input type="text" placeholder="Серія" v-model="state.searchForm.localPassportSerial" @keyup.enter="search">
                    <input type="text" placeholder="Номер" class="w-full" v-model="state.searchForm.localPassportNumber" @keyup.enter="search">
            </div>


            <div class="flex gap-4 w-full" v-if="state.searchForm.params.passportType === '2'">
              <input type="text" placeholder="Номер документа" v-model="state.searchForm.idPassportNumber" @keyup.enter="search">
              <input type="text" placeholder="Номер запису у реєстрі" class="w-full" v-model="state.searchForm.idPassportRecordNumber" @keyup.enter="search">
            </div>

            <div class="flex gap-4 w-full" v-if="state.searchForm.params.passportType === '3'">
              <input type="text" placeholder="Серія" v-model="state.searchForm.intPassportSerial" @keyup.enter="search">
              <input type="text" placeholder="Номер" class="w-full" v-model="state.searchForm.intPassportNumber" @keyup.enter="search">
            </div>

            <div class="flex gap-4 w-full" v-if="state.searchForm.params.passportType === '4'">
              <input type="text" placeholder="Номер документа" class="w-full" v-model="state.searchForm.otherPassportNumber" @keyup.enter="search" disabled>
            </div>

          </div>
          <div class="flex  flex-col bg-slate-50 dark:bg-opacity-10 rounded-lg p-4 gap-4">
            <div class="flex ">
              <span class="text-sm text-gray-400 uppercase font-semibold">Інше:</span>
            </div>
              <input type="text" placeholder="Телефон" v-model="state.searchForm.address" @keyup.enter="search" disabled>
              <input type="text" placeholder="Адреса" class="" v-model="state.searchForm.phone" @keyup.enter="search" disabled>

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