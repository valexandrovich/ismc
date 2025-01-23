<script setup>


import { onMounted, reactive } from 'vue'
import axios from 'axios'
import ProgressTableComponent from "@/components/ProgressTableComponent.vue";

// const fetchProgress = () => {
//     axios.get(import.meta.env.VITE_API_PREFIX + '/cpms/jobs')
//       .then((response) => {
//         state.jobs = response.data
//         console.log(response.data)
//       })
//       .catch(err => console.log(err))
// }
//
// const state = reactive({
//   jobs: []
// })
//
// onMounted(() => {
//   setInterval(fetchProgress, 500)
// })

</script>

<template>
  <div>

    <div class="mb-3">
    <span class="text-5xl font-exo font-black text-gray-300">
      <font-awesome-icon icon="bars-progress" />
      Прогрес</span>
    </div>

  <progress-table-component/>
  </div>
<!--<h1>ProgressView</h1>-->
<!--  {{state}}-->
<!--  <div v-for="j in state.jobs" :key="j.id">-->
<!--    <span>{{j.id}} - {{j.name}} - {{j.description}} - {{j.sourceNickName}}</span>-->

<!--    <div v-for="s in j.steps" :key="s.id">{{s}}</div>-->






<!--  </div>-->
</template>

<style scoped>

</style>