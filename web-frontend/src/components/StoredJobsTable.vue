<script setup>
import {computed, onMounted, reactive} from "vue";
import axios from "axios";


import Toastify from 'toastify-js';
import 'toastify-js/src/toastify.css';

const state = reactive({
  isLoading: true,
  storedJobs: []
})

const sortedJobs = computed(() => {
  return state.storedJobs.slice().sort((a, b) => a.id - b.id);
});

const fetchStoredJobs = () => {
  // axios.get(import.meta.env.VITE_API_PREFIX + '/scheduler/stored-jobs')
  axios.get('/api/scheduler/stored-job')
      .then(resp => {
        console.log(resp)
        state.storedJobs = resp.data





      })
      .catch(err => {
        console.log(err)
      })
  state.isLoading = false;
}

onMounted(() => {
  fetchStoredJobs()
})

const initStoredJob = (job) => {
  // axios.post(import.meta.env.VITE_API_PREFIX + '/scheduler/init-stored-job' + '?id=' + id)
  axios.post( '/api/scheduler/stored-job' + '?id=' + job.id)
      .then(resp => {
        console.log(resp)

        Toastify({
          text: "Завдання  " + job.shortName + "  запущене",
          duration: 3000,
          newWindow: true,
          gravity: "bottom", // `top` or `bottom`
          position: 'right', // `left`, `center` or `right`
          stopOnFocus: true, // Prevents dismissing of toast on hover
          style: {
            background: "#166434",
            color: "#e7e7e7",
            "border-radius": '15px',

          },
        }).showToast();

      })
      .catch(err => {
        console.log(err)
      })
}

</script>

<template>
  <div class="flex flex-col relative">



    <div class="flex flex-col items-center w-full bg-white rounded-xl   justify-center py-12" v-if="state.isLoading">
      <div class="loader justify-center text-center "></div>
      <span class="text-gray-300 font-semibold text-4xl">Завантаження</span>
    </div>

    <div class="flex flex-col rounded-xl" v-else>
      <div
          class="flex flex-row bg-green-700 font-semibold text-white text-lg rounded-t-2xl gap-12   py-1 ">

        <div class="flex flex-col w-10p text-center">ID</div>
        <div class="flex flex-col w-15p">Означення</div>
        <div class="flex flex-col w-35p">Назва</div>
        <div class="flex flex-col w-30p">Джерело</div>
        <div class="flex flex-col w-10p text-center">Дія</div>
      </div>
      <div class="flex flex-row  py-2  gap-12 text-md font-semibold  text-gray-700 last:rounded-b-xl"
           v-for="(job, index) in sortedJobs" :key="job.id"
           :class ="index % 2 == 0 ? 'bg-white': 'bg-gray-100' ">
        <div class="flex flex-col w-10p text-center">{{ job.id }}</div>
        <div class="flex flex-col w-15p">{{ job.shortName }}</div>
        <div class="flex flex-col w-35p">{{ job.name }}</div>
        <div class="flex flex-col w-30p">{{ job.source }}</div>
        <div class="flex flex-col w-10p px-2 pr-6 min-w-[200px] max-w-[200px]  justify-center">
          <button @click="initStoredJob(job)"
                  class="whitespace-nowrap bg-green-700 text-white px-6 py-1 rounded-xl hover:bg-green-600 ">
            <font-awesome-icon :icon="['fas', 'play']" class="mr-2 fa-fw"/>
            Старт
          </button>
        </div>
      </div>
    </div>
  </div>

  <!--  <table>-->
  <!--    <thead>-->
  <!--    <th>ID</th>-->
  <!--    <th>Означення</th>-->
  <!--    <th>Назва</th>-->
  <!--    <th>Опис</th>-->
  <!--    <th>Дія</th>-->
  <!--    </thead>-->
  <!--    <tbody>-->
  <!--    <tr v-for="job in state.storedJobs" :key="job.id">-->
  <!--      <td>{{ job.id }}</td>-->
  <!--      <td>{{ job.sourceNickName }}</td>-->
  <!--      <td>{{ job.name }}</td>-->
  <!--      <td>{{ job.description }}</td>-->
  <!--      <td><button>Click</button></td>-->
  <!--    </tr>-->
  <!--    </tbody>-->
  <!--  </table>-->
</template>

<style scoped>
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