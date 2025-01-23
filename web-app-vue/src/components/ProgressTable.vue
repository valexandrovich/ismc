<script setup>


import {computed, onMounted, onUnmounted, reactive, ref} from "vue";
import axios from "axios";
import {Icon} from "@iconify/vue";
import customAxios from "@/utils/customAxios.js";

const state = reactive({
  jobs: [],
  isLoading: true,
  isCollapse: true,
  // openedJobs: [2, 5],
  jobsVisibility: {}
})

const fetchJobs = () => {
  // axios.get(import.meta.env.VITE_API_PREFIX + '/cpms/jobs')
  customAxios.get('/cpms/jobs')
      .then(resp => {
        state.jobs = resp.data

        const inProcessJobIds = state.jobs

        // job.steps.some(step => step.status === 'NEW') && !job.steps.some(step => step.status === 'FAILED')

            .filter(job => (job.steps.some(step => step.status === 'IN_PROGRESS' || step.status === 'NEW') && !job.steps.some(step => step.status === 'FAILED')))
            .map(job => job.id);

        inProcessJobIds.forEach(j => {
          if (state.jobsVisibility[j] === undefined) {
            state.jobsVisibility[j] = true
          }
        })
        state.isLoading = false

      })
      .catch(err => {
        console.log(err)
      })
}

onMounted(() => {
  intervalId.value = setInterval(fetchJobs, 500);
})

const sortedJobs = computed(() => {
  return [...state.jobs].sort((a, b) => {
    // Convert startedAt to Date objects for accurate comparison
    const dateA = new Date(a.startedAt);
    const dateB = new Date(b.startedAt);

    // Compare the two dates for descending order
    return dateB - dateA;
  });
})

const formatDate = (timestamp) => {

  const date = new Date(timestamp)
  const day = date.getDate().toString().padStart(2, '0')
  const month = (date.getMonth() + 1).toString().padStart(2, '0') // Note: months are zero-based
  const year = date.getFullYear()
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2, '0')
  if (year == '1970') {
    return ''
  }

  let yearShort = year.toString().slice(-2)

  return `${day}.${month} - ${hours}:${minutes}:${seconds}`
}

const changeJobStepsVisibility = (jobId) => {
  if (state.jobsVisibility[jobId]) {
    state.jobsVisibility[jobId] = false
  } else if (!state.jobsVisibility[2]) {
    state.jobsVisibility[jobId] = true
  } else {
    state.jobsVisibility[jobId] = true
  }
}

const intervalId = ref(null)



onUnmounted(() => {
  if (intervalId.value !== null) {
    clearInterval(intervalId.value);
  }
});

</script>

<template>

  <div
      class="flex  gap-2   rounded-lg  py-2 text-sm bg-teal-600  dark:bg-opacity-70 text-white">
    <div class="flex flex-col w-5p text-center">
      <!--      <span>Деталі</span>-->
    </div>
    <div class="flex flex-col w-5p text-center">
      <span>ID</span>
    </div>
    <div class="flex flex-col w-35p">
      <span>Назва</span>
    </div>
    <!--    <div class="flex flex-col w-30p">-->
    <!--      <span>Опис</span>-->
    <!--    </div>-->
    <div class="flex flex-col w-15p text-center">
      <span>Старт</span>
    </div>

    <div class="flex flex-col w-15p text-center">
      <span>Фініш</span>
    </div>

    <div class="flex flex-col w-15p text-center">
      <span>Оператор</span>
    </div>

    <div class="flex flex-col w-10p    ">
      <span>Статус</span>
    </div>
  </div>


  <div class="flex w-full   justify-center py-12 rounded-xl mt-2" v-if="state.isLoading">
    <div class="loader justify-center text-center "></div>
  </div>


  <div v-if="!state.isLoading && state.jobs.length == 0" class="text-center flex w-full   justify-center py-12">
    <span class=" uppercase text-2xl opacity-50  ">Не знайдено задач</span>
  </div>

  <template v-for="(job, index) in sortedJobs" :key="job.id"  >

    <div
        class="flex flex-row items-center py-1 cursor-pointer min-h-10  gap-2  text-sm  mt-2 rounded-lg  hover:dark:bg-gray-500  hover:bg-slate-200 "
        @click="changeJobStepsVisibility(job.id)"

        :class="[index % 2 == 0 ? 'bg-slate-50 dark:bg-opacity-5 ': 'bg-slate-50 dark:bg-opacity-5 ',   index == state.jobs.length - 1 ? 'rounded-b-xl' : '' , state.jobsVisibility[job.id] ? 'rounded-b-none' : ''   ]">
      <!--         :class="{-->
      <!--         'text-red-700': job.steps.some(step => step.status === 'FAILED'),-->
      <!--          'bg-green-50': job.steps.some(step => step.status === 'IN_PROCESS'),-->
      <!--          'bg-green-50': job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')}"-->
      <!--    >-->
      <div class="flex flex-col w-5p   text-end justify-center text-teal-600">
          <span v-if="state.jobsVisibility[job.id]">
          <Icon icon="mdi:chevron-up" width="24" class="ml-3" />
<!--            <font-awesome-icon :icon="['fas', 'chevron-down']"  class="fa-fw"/>-->
          </span>
        <span v-else>
          <Icon icon="mdi:chevron-down" width="24" class="ml-3" />

<!--          <font-awesome-icon :icon="['fas', 'chevron-right']" class="fa-fw"/>-->
        </span>

        <!--        <span class="text-green-400 font-extrabold">-->
        <!--          {{state.jobsVisibility[job.id] ? '' : '>'}}-->

        <!--        </span>-->
      </div>
      <div class="flex flex-col w-5p  text-center   justify-center">
        <span>  {{ job.id }}</span>
      </div>
      <div class="flex flex-col w-35p  justify-center ">
        <span>{{ job.name }}</span>
      </div>
      <!--      <div class="flex flex-col w-30p  justify-center">-->
      <!--        <span>{{ job.storedJob.description }}</span>-->
      <!--      </div>-->
      <div class="flex flex-col w-15p  text-center font-exo">
        <span>{{ job.startedAt == null ? 'невідомий' : formatDate(job.startedAt) }}</span>
      </div>

      <div class="flex flex-col w-15p  text-center font-exo ">
        <span>{{ job.finishedAt == null ? 'невідомий' : formatDate(job.finishedAt) }}</span>
      </div>

      <div class="flex flex-col w-15p     text-center">
        <span class="">{{ job.initiatorName }}</span>
      </div>
      <div class="flex flex-col w-10p   ">

        <div class="flex items-center" v-if="job.steps.every(step => step.status === 'NEW')">
                    <Icon icon="zondicons:exclamation-outline" class="text-amber-600 mr-2" width="14"/><span>Очікування</span>
        </div>

        <div class="flex items-center" v-else-if="job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')">
          <Icon icon="lets-icons:check-fill" class="text-teal-500 mr-2" width="18" /><span>Завершено</span>
        </div>

        <div class="flex items-center" v-else-if="job.steps.some(step => step.status === 'FAILED')">
          <Icon icon="material-symbols:error" class="text-red-500 mr-2" width="18"/><span>Помилка</span>
        </div>

        <div class="flex items-center" v-else-if="job.steps.some(step => step.status === 'IN_PROGRESS')">
          <Icon icon="ph:spinner-bold" class="text-sky-500 mr-2" width="18"/><span>Обробка</span>
        </div>

        <div class="flex items-center" v-else-if="job.steps.some(step => step.status === 'NEW') && !job.steps.some(step => step.status === 'FAILED')">
          <Icon icon="ph:spinner-bold" class="text-sky-500 mr-2" width="18"/><span>Обробка</span>
        </div>
      </div>
    </div>

    <div class="flex flex-col  px-0    bg-slate-50 shadow-md dark:bg-opacity-5   rounded-b-lg  " v-show="state.jobsVisibility[job.id]">
      <div class="flex flex-row min-h-8 items-center  gap-2 font-semibold   border-t-gray-300 dark:border-opacity-10 border-t-2 border-dashed   text-sm ">
        <div class="flex flex-col w-5p items-center   ">ID</div>
        <!--        <div class="flex flex-col w-5p text-center whitespace-nowrap">Крок</div>-->
        <div class="flex flex-col w-10p  ">Сервіс</div>
        <div class="flex flex-col w-10p items-center mr-4 ">Прогресс</div>
        <div class="flex flex-col w-15p items-center  ">Старт</div>
        <div class="flex flex-col w-15p  items-center ">Фініш</div>

        <div class="flex flex-col w-35p ">Коментар</div>
        <div class="flex flex-col w-10p    ">Статус</div>
      </div>
      <div
          class="flex flex-row  items-center py-0  text-sm last:mb-4    min-h-8  gap-2   border-b-gray-300  border-dashed  "
          v-for="(step, index) in job.steps" :key="step.id">


        <div class="flex flex-col w-5p items-center  ">{{ step.id }}</div>
        <!--        <div class="flex flex-col w-5p text-center  ">{{ step.stepOrder }}</div>-->
        <div class="flex flex-col w-10p ">{{ step.service }}</div>
        <!--        <div class="flex flex-col w-10p text-center ">{{ Number((step.progress * 100).toFixed(2)) }} %</div>-->
        <div class="flex flex-col w-10p  mr-4 ">
          <div class="progress-container">
            <div class="progress-bar" :style="{ width: (step.progress * 100).toFixed(2) + '%' }"></div>
            <div class="progress-text text-nowrap">
              {{ Number((step.progress * 100).toFixed(2)) }} %
            </div>
          </div>
        </div>

        <div class="flex flex-col w-15p font-exo items-center ">
          {{ step.startedAt == null ? 'невідомий' : formatDate(step.startedAt) }}
        </div>
        <div class="flex flex-col w-15p   font-exo items-center">
          {{ step.finishedAt == null ? 'невідомий' : formatDate(step.finishedAt) }}
        </div>

        <div class="flex flex-col w-35p   ">
          <span v-if="step.comment" class="break-words  block text-wrap">
<!--          {{ step.comment}}-->
          {{ step.comment.length > 255 ? step.comment.substring(0, 255) : step.comment }}
        </span></div>

        <div v-if="step.status==='FINISHED'" class="flex   w-10p     ">
          <Icon icon="lets-icons:check-fill" class="text-teal-500 mr-1" width="18" /><span>Завершено</span>
        </div>

        <div v-if="step.status==='NEW'"
             class="flex items-center     w-10p  ">
          <Icon icon="zondicons:exclamation-outline" class=" mr-2" width="14"/><span>Очікування</span>
        </div>

        <div v-if="step.status==='IN_PROGRESS'"
             class="flex items-center    w-10p  ">
          <Icon icon="ph:spinner-bold" class="text-sky-500 mr-1" width="18"/><span>Обробка</span>
        </div>

        <div v-if="step.status==='FAILED'" class="flex items-center  w-10p   ">
          <Icon icon="material-symbols:error" class="text-red-500 mr-1" width="18"/><span>Помилка</span>
        </div>
        <div v-if="step.status==='SKIPPED'" class="flex items-center    w-10p  ">Пропущено
        </div>

      </div>
    </div>

  </template>
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

.progress-container {
  width: 100%; /* Full width */
  //background-color: #ddd; /* Grey background */
  @apply bg-slate-400 dark:bg-opacity-20 rounded-lg overflow-hidden;
  position: relative; /* Positioning context for the text */
}

.progress-bar {
  height: 20px;
  /*background-color: #4396bd; /* Green background */
  /*background-color: #4396bd; /* Green background */
  /*@apply bg-gradient-to-r from-green-700 to-green-600 ;*/
  @apply bg-gradient-to-r from-teal-600 to-teal-500;

}

.progress-text {
  position: absolute; /* Absolute positioning */
  top: 50%; /* Center vertically */
  left: 50%; /* Center horizontally */
  transform: translate(-45%, -50%); /* Adjust to exact center */
  //color: black; /* Text color */
  @apply text-red-50 font-semibold;
}
</style>