<script setup>


import {onMounted, reactive} from 'vue'
import axios from 'axios'

const state = reactive({
  // testUrl: window.RUNTIME_ENV.VITE_API_PREFIX + '/scheduler/init-stored-job?id=1'
  // testUrl: window.RUNTIME_ENV.VITE_API_PREFIX + '/sandbox/tst'
  testUrl:  '/api/sandbox/tst'
})


const sendRequest = () => {
  axios.get(state.testUrl)
    .then(resp => {
      console.log(resp)
    })
    .catch(err => {
      console.log(err)
    })
}

const sendPostRequest = () => {
  axios.post(state.testUrl)
    .then(resp => {
      console.log(resp)
    })
    .catch(err => {
      console.log(err)
    })
}

onMounted(() => {
  console.log('On mount')
  console.log(import.meta.env.VITE_API_PREFIX)
  console.log(import.meta.env)

  console.log(window.RUNTIME_ENV.VITE_API_PREFIX)
  console.log(window.RUNTIME_ENV)
})

</script>

<template>
  <div>
    <span class="font-black uppercase text-gray-400">
      <font-awesome-icon icon="arrow-up-from-bracket" class="mr-2" />TEST
    </span>


    <input class="w-[400px] outline outline-1 outline-gray-400 p-2 rounded-2xl" type="text" placeholder="Test url" v-model="state.testUrl" />
    <br>
    <button class="bg-green-700 px-4 py-2 text-white rounded-2xl" @click="sendRequest">Send request</button>
    <button class="bg-green-700 px-4 py-2 text-white rounded-2xl" @click="sendPostRequest">Send POST request</button>

  </div>
</template>

<style scoped>

</style>