<script setup>



import TextInput from "@/components/TextInput.vue";
import {computed, reactive} from "vue";
import circles from "@/assets/img/circles2.png";

const backgroundStyle = computed(() => ({
  backgroundImage: `url(${circles})`,
  backgroundSize: '25%',
  backgroundPosition: 'bottom left',
  backgroundRepeat: 'no-repeat'
}))

const state = reactive({
  loginForm: {
    login: '',
    password: ''
  }
})

</script>

<template>

  <div class="w-screen h-screen flex justify-center items-center bg-green-900" :style="backgroundStyle" >
    <img src="@/assets/img/side_logo.png" alt="">
  <div   class="w-[400px] h-[250px] bg-gray-50 rounded-3xl px-6 pt-6  flex flex-col justify-center gap-3 shadow-lg shadow-gray-900">
<!--    <div class="flex flex-row justify-center" >-->
<!--      <img src="@/assets/img/login_logo.png" class="w-[300px]">-->
<!--    </div>-->

    <TextInput  placeholder="Логін"  class="" v-model="state.loginForm.login" />
    <TextInput  placeholder="Пароль" mask="password"  class="" v-model="state.loginForm.password"/>

    <div class="flex flex-col justify-center items-center text-center" >
      <button class="w-[200px] btn bg-green-700 text-white hover:bg-green-600 uppercase">
        Вхід
        <font-awesome-icon icon="right-to-bracket"  class="ml-2" />
      </button>
      <a href="" class="mt-4 text-green-800 cursor-pointer hover:text-green-600">Як отримати доступ?</a>
    </div>

  </div>
  </div>

</template>

<style scoped>

</style>