<script setup>

import {reactive} from "vue";
import axios from "axios";
import TextInput from "@/components/TextInput.vue";

import {useSearchPpResultsStore} from "@/stores/search.pp.results.js";
import Toastify from "toastify-js";

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
  },
})


const convertDate = (dateStr) => {
  const parts = dateStr.split('.');
  if (parts.length !== 3 || !parts[0] || !parts[1] || !parts[2] || isNaN(Date.parse(`${parts[2]}-${parts[1]}-${parts[0]}`))) {
    return "";
  }
  const converted = `${parts[2]}-${parts[1]}-${parts[0]}`;
  return converted;
}


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
    address: '',
    phone: ''
  }
}



const search = () => {


  state.isLoading = true;
  state.searchForm.birthday = convertDate(state.searchForm.birthday)



  console.log(state.searchForm)

  axios.post('/api/importer/search-red/pp', state.searchForm)
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


  <div class="flex relative">

    <div class="flex flex-col absolute bg-white bg-opacity-75 w-full h-full z-10 justify-center items-center" v-if="state.isLoading">
      <div class="loader justify-center text-center "></div>
      <span class="text-gray-400 font-bold text-2xl">Завантаження</span>
    </div>

    <div class="flex flex-col gap-4 flex-1 relative py-8 px-6">

      <div class="flex flex-row gap-3 flex-wrap">

        <div class="flex flex-row justify-center items-center space-x-2">
          <input type="checkbox" id="chck" class="chck" v-model="state.searchForm.params.isSimpleName"/>
          <label for="chck" class="chck-label text-nowrap">ПІБ разом</label>
        </div>


        <div v-if="state.searchForm.params.isSimpleName">
          <TextInput v-model="state.searchForm.simpleName" placeholder="ПІБ разом" mask="Іванов Іван Іванович"
                     class="w-[500px]" @keydown.enter="search"/>
        </div>

        <div v-else class="flex flex-row gap-3 ">
          <TextInput v-model="state.searchForm.lastName" placeholder="Прізвищє" mask="Іванов" @keydown.enter="search"/>
          <TextInput v-model="state.searchForm.firstName" placeholder="Ім'я" mask="Іван" @keydown.enter="search"/>
          <TextInput v-model="state.searchForm.patronymicName" placeholder="По-батькові" mask="Іванович" @keydown.enter="search"/>
        </div>


        <TextInput v-model="state.searchForm.birthday" placeholder="Дата народження" mask="дд.мм.рррр" @keydown.enter="search"/>
        <TextInput v-model="state.searchForm.inn" placeholder="РНОКПП (ІПН)" mask="1234567890" @keydown.enter="search"/>
      </div>

      <div class="flex flex-row gap-3 flex-wrap">

        <select class="slc"
                v-model="state.searchForm.params.passportType">
          <option value='1'>Паспорт книжка</option>
          <option value='2'>Паспорт ID картка</option>
          <option value='3'>Паспорт для виїзду за кородон</option>
        </select>

        <div v-if="state.searchForm.params.passportType === '1'" class=" flex flex-row gap-2">
          <TextInput v-model="state.searchForm.localPassportSerial" placeholder="Серія" mask="АА"
                     class="max-w-[100px]" @keydown.enter="search"/>
          <TextInput v-model="state.searchForm.localPassportNumber" placeholder="Номер" mask="123456"
                     class="max-w-[150px]" @keydown.enter="search"/>
        </div>
        <div v-if="state.searchForm.params.passportType === '2'" class=" flex flex-row gap-2">
          <TextInput v-model="state.searchForm.idPassportNumber" placeholder="Номер" mask="123456789"
                     class="max-w-[150px]" @keydown.enter="search"/>
          <TextInput v-model="state.searchForm.idPassportRecordNumber" placeholder="Номер запису у реєстрі"
                     mask="12345678-12345" class="max-w-[250px]" @keydown.enter="search"/>
        </div>
        <div v-if="state.searchForm.params.passportType === '3'" class=" flex flex-row gap-2">
          <TextInput v-model="state.searchForm.intPassportSerial" placeholder="Серія" mask="АА" class="max-w-[100px]" @keydown.enter="search"/>
          <TextInput v-model="state.searchForm.intPassportNumber" placeholder="Номер" mask="123456"
                     class="max-w-[150px]" @keydown.enter="search"/>
        </div>

        <TextInput placeholder="Телефон" mask="+380XXYYYXXYY" class="max-w-[250px]" @keydown.enter="search"/>
        <TextInput placeholder="Адреса" mask="м. Київ, вул. Хрещатик, 1" class="min-w-[500px]" @keydown.enter="search"/>
      </div>

      <div class="flex flex-row gap-3 mt-6 ">


        <button class="btn bg-green-700 text-white hover:bg-green-600 uppercase" @click="search">
          <font-awesome-icon icon="magnifying-glass" class="mr-1"/>
          Пошук
        </button>
        <button class="btn bg-sky-600 text-white hover:bg-sky-500 uppercase" @click="clearForm">
          <font-awesome-icon icon="trash-can" class="mr-1"/>
          Очистити
        </button>
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

.loader {
  border: 5px solid #f3f3f3; /* Light grey background */
  border-top: 5px solid #37a61f; /* Blue color */
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