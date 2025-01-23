<script setup>
import {computed, onMounted, reactive} from "vue";
import customAxios from "@/utils/customAxios.js";
import {Icon} from "@iconify/vue";

import Toastify from 'toastify-js';
import 'toastify-js/src/toastify.css';
import {useUserStore} from "@/stores/user.js";

const state = reactive({
  isLoading: true,
  storedJobs: []
})

const sortedJobs = computed(() => {
  return state.storedJobs.slice().sort((a, b) => a.id - b.id);
});

const fetchStoredJobs = () => {
  // axios.get(import.meta.env.VITE_API_PREFIX + '/scheduler/stored-jobs')
  customAxios.get('/scheduler/stored-job')
      .then(resp => {
        state.storedJobs = resp.data
        state.isLoading = false;
      })
      .catch(err => {
        state.isLoading = false;
      })

}

onMounted(() => {
  fetchStoredJobs()
})


const initStoredJob = (job) => {
  // axios.post(import.meta.env.VITE_API_PREFIX + '/scheduler/init-stored-job' + '?id=' + id)

  // Toastify({
  //   text: "Завдання  " + job.shortName + "  запущене",
  //   duration: 3000,
  //   newWindow: true,
  //   gravity: "top", // `top` or `bottom`
  //   position: 'right', // `left`, `center` or `right`
  //   stopOnFocus: true, // Prevents dismissing of toast on hover
  //   style: {
  //     background: "rgb(24,114,105)",
  //     color: "#e7e7e7",
  //     "border-radius": '15px',
  //     'box-shadow': 'none !important'
  //   },
  // }).showToast();

  // return

  customAxios.post( '/scheduler/stored-job' + '?id=' + job.id  +  '&initiatorName=' + useUserStore().user.username)
      .then(resp => {
        console.log(resp)

        Toastify({
          text: "Завдання  " + job.shortName + "  запущене",
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

      })
      .catch(err => {
        console.log(err)
        Toastify({
          text: "Помилка! " + err,
          duration: 3000,
          newWindow: true,
          gravity: "top", // `top` or `bottom`
          position: 'right', // `left`, `center` or `right`
          stopOnFocus: true, // Prevents dismissing of toast on hover
          style: {
            background: "rgb(96,19,19)",
            color: "#e7e7e7",
            "border-radius": '15px',

          },
        }).showToast();
      })
}
</script>

<template>
  <div class="flex flex-col relative ">
    <div class="flex flex-col items-center w-full  rounded-xl   justify-center py-12" v-if="state.isLoading">
      <div class="loader justify-center text-center "></div>
      <span class="  text-2xl">Завантаження</span>



    </div>



    <div class="flex flex-col rounded-xl " v-else>




      <div
          class="flex flex-row  text-sm bg-teal-600  dark:bg-opacity-70 text-white  gap-12   rounded-lg py-2 ">

        <div class="flex flex-col w-10p text-center">ID</div>
        <div class="flex flex-col w-15p">Означення</div>
        <div class="flex flex-col w-35p">Назва</div>
        <div class="flex flex-col w-30p">Джерело</div>
        <div class="flex flex-col w-10p text-start">Дія</div>
      </div>

      <div v-if="!state.isLoading && state.storedJobs.length === 0" class="text-center flex w-full   justify-center py-12">
        <span class=" uppercase text-2xl opacity-50  ">Не знайдено задач</span>
      </div>

<!--      <div v-if="state.storedJobs.length === 0" class="flex justify-center py-12">-->
<!--          <span class="text-lg">Не знайдено завдань</span>-->
<!--      </div>-->

      <div class="flex flex-row  items-center py-1  gap-12 text-sm my-1   rounded-lg min-h-12"
           v-for="(job, index) in sortedJobs" :key="job.id"
           :class ="[index % 2 == 0 ? 'bg-slate-50 dark:bg-opacity-5': 'bg-slate-50 dark:bg-opacity-5' , index == sortedJobs.length - 1 ? 'rounded-b-2xl' : '' ]">
        <div class="flex flex-col w-10p text-center">{{ job.id }}</div>
        <div class="flex flex-col w-15p">{{ job.shortName }}</div>
        <div class="flex flex-col w-35p">{{ job.name }}</div>
        <div class="flex flex-col w-30p">{{ job.source }}</div>
        <div class="flex flex-col w-10p px-2 pr-6 min-w-[130px] max-w-[200px]  justify-center">
          <button @click="initStoredJob(job)"
                  class="whitespace-nowrap flex justify-center items-center  text-teal-600  outline outline-1 outline-teal-600 px-2 py-1 rounded-xl hover:bg-teal-600 hover:text-white ">
            <Icon icon="mdi:play" width="18" class="mr-1" />
            <span>Старт</span>
          </button>
        </div>
      </div>
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
</style>