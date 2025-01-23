<script setup>

import {reactive} from "vue";
import axios from "axios";
import TextInput from "@/components/TextInput.vue";
import {useSearchLeResultsStore} from "@/stores/search.le.results.js";
import Toastify from "toastify-js";

const store = useSearchLeResultsStore();

const state = reactive({
  isLoading: false,
  isNoResults: false,
  searchForm: {
    name: '',
    edrpou: '',

  },
})


const clearForm = () => {
  state.searchForm = {
    name: '',
    edrpou: '',

  }
}

const search = () => {
  state.isLoading = true;

  axios.post('/api/importer/search-red/le', state.searchForm)
      .then(resp => {
        state.isLoading = false;
        store.results = resp.data.searchResults
        store.searchForm = resp.data.legalEntitySearchDto

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

    <div class="flex flex-col absolute bg-white bg-opacity-75 w-full h-full z-10 justify-center items-center"
         v-if="state.isLoading">
      <div class="loader justify-center text-center "></div>
      <span class="text-gray-400 font-bold text-2xl">Завантаження</span>
    </div>

    <div class="flex flex-col gap-4 flex-1  py-8 px-6">
      <div class="flex flex-row gap-3 flex-wrap">
        <TextInput placeholder="Назва" mask="ТОВ Компанія" class="max-w-[250px]" v-model="state.searchForm.name"/>
        <TextInput placeholder="Код ЄДРПОУ" mask="12345678" class="max-w-[250px]" v-model="state.searchForm.edrpou"/>
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


  <!--<div>-->

  <!--  <input type="text" class="bg-green-200" placeholder="lastname" v-model="state.searchForm.name">-->
  <!--  <input type="text" class="bg-green-200" placeholder="first" v-model="state.searchForm.edrpou">-->
  <!--  <button @click="search">Search</button>-->

  <!--&lt;!&ndash;  <div class="flex flex-row gap-2 flex-wrap justify-between">&ndash;&gt;-->
  <!--&lt;!&ndash;  <person-card-component :person="person" v-for="person in state.searchResults" :key="person.id"/>&ndash;&gt;-->
  <!--&lt;!&ndash;  </div>&ndash;&gt;-->

  <!--  <div class="flex flex-row gap-2 flex-wrap justify-between" v-if="useLegalEntityStore().legalEntities">-->


  <!--    <legal-entity-card-component :legal-entity="legalEntity" v-for="legalEntity in useLegalEntityStore().legalEntities.slice(0, 10)" :key="legalEntity.id"/>-->
  <!--&lt;!&ndash;    <person-card-component :person="person" v-for="person in usePrivatePersonStore().privatePersons" :key="person.id"/>&ndash;&gt;-->
  <!--  </div>-->

  <!--&lt;!&ndash;  {{usePrivatePersonStore().privatePersons}}&ndash;&gt;-->

  <!--&lt;!&ndash;  <div v-for="person in state.searchResults" :key="person.id">&ndash;&gt;-->
  <!--&lt;!&ndash;    {{person}}&ndash;&gt;-->
  <!--&lt;!&ndash;  </div>&ndash;&gt;-->


  <!--</div>-->
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