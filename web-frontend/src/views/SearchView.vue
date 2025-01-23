<script setup>
import { ref } from 'vue'
import SearchPPComponent from '@/components/SearchPPComponent.vue'
import SearchLEComponent from '@/components/SearchLEComponent.vue'
import SearchPPResultsComponent from "@/components/SearchPPResultsComponent.vue";
import SearchLeResultsComponent from "@/components/SearchLeResultsComponent.vue";

const activeTab = ref(1)
</script>

<template>
  <div class="mb-3">
    <span class="text-3xl font-exo font-black text-gray-300">
      <font-awesome-icon icon="magnifying-glass" />
      Пошук</span>
  </div>


  <div class="flex flex-col">

    <div class="flex flex-row   ">
      <div class="rounded-t-2xl overflow-hidden">
        <button
          @click="activeTab = 1"
          class="tab-btn"
          :class="{ 'tab-btn-active': activeTab === 1 }">
          <font-awesome-icon icon="user"  class="mr-2" />
          Фізичні особи
        </button>
        <button
          @click="activeTab = 2"
          class="tab-btn"
          :class="{ 'tab-btn-active': activeTab === 2 }">
          <font-awesome-icon icon="building"  class="mr-2" />
          Юридичні особи
        </button>
      </div>
    </div>

    <div class="bg-white  rounded-b-3xl rounded-tr-3xl shadow-lg shadow-gray-200 ">
    <div class="flex flex-col " v-if="activeTab === 1">
      <search-p-p-component/>
    </div>
    <div class="flex flex-col " v-if="activeTab === 2">
      <search-l-e-component/>
    </div>
    </div>


    <div class=" py-8 px-6 mt-4 rounded-xl ">
      <div class="flex flex-col " v-if="activeTab === 1">
        <search-p-p-results-component/>
      </div>
      <div class="flex flex-row " v-if="activeTab === 2">
        <search-le-results-component/>
      </div>
    </div>


  </div>

</template>

<style scoped>

</style>