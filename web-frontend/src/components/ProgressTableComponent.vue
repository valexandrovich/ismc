<script setup>
import {computed, onMounted, reactive} from "vue";
import axios from "axios";

const state = reactive({
  jobs: [],
  isLoading: true,
  isCollapse: true,
  // openedJobs: [2, 5],
  jobsVisibility: {}
})

const fetchJobs = () => {
  // axios.get(import.meta.env.VITE_API_PREFIX + '/cpms/jobs')
  axios.get('/api/cpms/jobs')
      .then(resp => {
        state.jobs = resp.data

        const inProcessJobIds = state.jobs
            .filter(job => job.steps.some(step => step.status === 'IN_PROGRESS'))
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
  setInterval(fetchJobs, 500)
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

  let yearShort =  year.toString().slice(-2)

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

</script>

<template>


  <div
      class="flex flex-row  bg-green-700 font-semibold text-white rounded-xl  py-1.5">
    <div class="flex flex-col w-2p text-center">
      <!--      <span>Деталі</span>-->
    </div>
    <div class="flex flex-col w-10p text-center">
      <span>ID</span>
    </div>
    <div class="flex flex-col w-45p">
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

    <div class="flex flex-col w-15p  ">
      <span>Статус</span>
    </div>
  </div>


  <div class="flex w-full bg-gray-100  justify-center py-12 rounded-xl mt-2" v-if="state.isLoading">
    <div class="loader justify-center text-center "></div>



  </div>


  <div v-if="!state.isLoading && state.jobs.length == 0" class="text-center flex w-full bg-gray-100  justify-center py-12">
    <span class="text-gray-300 uppercase text-2xl font-black ">Не знайдено задач</span>
  </div>

  <template v-for="(job, index) in sortedJobs" :key="job.id" >

    <div class="flex flex-row py-1 cursor-pointer text-md rounded-xl  my-2  text-gray-700 font-bold  hover:bg-gray-300 "
         @click="changeJobStepsVisibility(job.id)"
         :class="[index % 2 == 0 ? 'bg-gray-100': 'bg-gray-100', state.jobsVisibility[job.id] ? 'rounded-b-none bg-gray-300 text-gray-700' : 'bg-gray-100'  ]">
      <!--         :class="{-->
      <!--         'text-red-700': job.steps.some(step => step.status === 'FAILED'),-->
      <!--          'bg-green-50': job.steps.some(step => step.status === 'IN_PROCESS'),-->
      <!--          'bg-green-50': job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')}"-->
      <!--    >-->
      <div class="flex flex-col w-2p   text-end justify-center text-green-700">
          <span v-if="state.jobsVisibility[job.id]"><font-awesome-icon :icon="['fas', 'chevron-down']"
                                                                       class="fa-fw"/></span>
        <span v-else><font-awesome-icon :icon="['fas', 'chevron-right']" class="fa-fw"/></span>

        <!--        <span class="text-green-400 font-extrabold">-->
        <!--          {{state.jobsVisibility[job.id] ? '' : '>'}}-->

        <!--        </span>-->
      </div>
      <div class="flex flex-col w-10p  text-center  justify-center">
        <span>  {{ job.id }}</span>
      </div>
      <div class="flex flex-col w-45p  justify-center ">
        <span>{{ job.name }}</span>
      </div>
      <!--      <div class="flex flex-col w-30p  justify-center">-->
      <!--        <span>{{ job.storedJob.description }}</span>-->
      <!--      </div>-->
      <div class="flex flex-col w-15p  text-center">
        <span>{{ job.startedAt == null ? 'невідомий' : formatDate(job.startedAt) }}</span>
      </div>

      <div class="flex flex-col w-15p  text-center ">
        <span>{{ job.finishedAt == null ? 'невідомий' : formatDate(job.finishedAt) }}</span>
      </div>

      <div class="flex flex-col w-15p    justify-center text-center">
        <span class="">{{ job.initiatorName }}</span>
      </div>


<!--               'text-red-700': job.steps.some(step => step.status === 'FAILED'),-->
      <!--          'bg-green-50': job.steps.some(step => step.status === 'IN_PROCESS'),-->
      <!--          'bg-green-50': job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')}"-->

      <div class="flex flex-col w-15p    justify-center ">
        <div v-if="job.status === 'SKIPPED'">
          <span class="text-amber-600"><font-awesome-icon :icon="['fas', 'circle-exclamation']" class="mr-2 fa-fw"/> Не запущено</span>
          <!--          <span v-if="job.status === 'FAILED'" class="text-red-500"><font-awesome-icon :icon="['fas', 'circle-exclamation']" class="mr-2 fa-fw"/> Помилка</span>-->
          <!--          <span v-if="job.status === 'IN_PROGRESS'" class="text-blue-500"><font-awesome-icon :icon="['fas', 'spinner']" class="mr-2 fa-fw"/> Обробка</span>-->
          <!--          <span v-if="job.status === 'FINISHED' " class="text-emerald-700"><font-awesome-icon :icon="['fas', 'circle-check']" class="mr-2 fa-fw"/> Завершено</span>-->
        </div>
        <div v-else>
            <span v-if="job.steps.some(step => step.status === 'FAILED')" class="text-red-500"><font-awesome-icon
                :icon="['fas', 'circle-exclamation']" class="mr-2 fa-fw"/> Помилка</span>
          <span v-if="job.steps.some(step => step.status === 'IN_PROGRESS')" class="text-blue-500"><font-awesome-icon
              :icon="['fas', 'spinner']" class="mr-2 fa-fw"/> Обробка</span>
          <span v-if="job.steps.every(step => step.status === 'FINISHED' || step.status === 'SKIPPED')"
                class="text-green-700"><font-awesome-icon :icon="['fas', 'circle-check']" class="mr-2 fa-fw"/> Завершено</span>
        </div>

      </div>

<!--      <div class="flex flex-col w-15p text-center">-->
<!--        <span>&#45;&#45;</span>-->
<!--      </div>-->


    </div>

    <div class="flex flex-col mt-[-8px]  text-md  " v-show="state.jobsVisibility[job.id]">
      <div class="flex flex-row bg-white text-gray-700 font-bold  border-b gap-2 py-1 pl-6">
        <div class="flex flex-col w-5p   ">ID</div>
<!--        <div class="flex flex-col w-5p text-center whitespace-nowrap">Крок</div>-->
        <div class="flex flex-col w-10p  ">Сервіс</div>
        <div class="flex flex-col w-10p  mr-4 ">Прогресс</div>
        <div class="flex flex-col w-10p  ">Старт</div>
        <div class="flex flex-col w-10p  ">Фініш</div>
        <div class="flex flex-col w-10p   ">Статус</div>
        <div class="flex flex-col w-45p ">Коментар</div>
      </div>
      <div
          class="flex flex-row  bg-white text-md text-gray-600  py-1 pl-6  last:mb-4  last:rounded-b-xl gap-2 border-b  "
          v-for="step in job.steps" :key="step.id"   >



        <div class="flex flex-col w-5p   ">{{ step.id }}</div>
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

        <div class="flex flex-col w-10p  ">
          {{ step.startedAt == null ? 'невідомий' : formatDate(step.startedAt) }}
        </div>
        <div class="flex flex-col w-10p  ">
          {{ step.finishedAt == null ? 'невідомий' : formatDate(step.finishedAt) }}
        </div>
<!--        <div class="flex flex-col text-emerald-700 font-semibold  w-15p">-->
<!--          {{ step.status }}-->
<!--        </div>-->
        <div v-if="step.status==='FINISHED'" class="flex flex-col text-green-700 font-semibold  w-10p    ">
          <span>  <font-awesome-icon :icon="['fas', 'circle-check']" class="mr-2 fa-fw"/>Завершено</span>
        </div>

        <div v-if="step.status==='NEW'"
             class="flex flex-col text-amber-500 font-semibold   w-10p  ">
          <span><font-awesome-icon :icon="['fas', 'pause']" class="mr-2 fa-fw"/> Очікування</span>
        </div>

        <div v-if="step.status==='IN_PROGRESS'"
             class="flex flex-col text-sky-600 font-semibold   w-10p  ">
          <span><font-awesome-icon :icon="['fas', 'spinner']" class="mr-2 fa-fw"/> Обробка</span>
        </div>

        <div v-if="step.status==='FAILED'" class="flex flex-col text-red-500 font-semibold   w-10p  ">
          <span> <font-awesome-icon :icon="['fas', 'circle-exclamation']" class="mr-2 fa-fw"/>Помилка</span>
        </div>
        <div v-if="step.status==='SKIPPED'" class="flex flex-col text-orange-500 font-semibold   w-10p  ">Пропущено
        </div>
        <!--        <div class="flex flex-col w-25p  "> {{ step.comment }}</div>-->
        <div class="flex flex-col w-45p   ">
          <span v-if="step.comment" class="break-words  block text-wrap">
<!--          {{ step.comment}}-->
          {{  step.comment.length > 255 ? step.comment.substring(0, 255) : step.comment }}
        </span></div>
      </div>
    </div>
  </template>
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

.progress-container {
  width: 100%; /* Full width */
  //background-color: #ddd; /* Grey background */
  @apply bg-gray-400 rounded-lg overflow-hidden;
  position: relative; /* Positioning context for the text */
}

.progress-bar {
  height: 20px;
  /*background-color: #4396bd; /* Green background */
  /*background-color: #4396bd; /* Green background */
  /*@apply bg-gradient-to-r from-green-700 to-green-600 ;*/
  @apply bg-gradient-to-r from-green-600 to-green-800;

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