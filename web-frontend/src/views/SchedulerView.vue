<script setup>

import StoredJobsTable from "@/components/StoredJobsTable.vue";


</script>

<template>

  <div class="mb-3">
    <span class="text-5xl font-exo font-black text-gray-300">
      <font-awesome-icon icon="calendar-days" />
      Розклад</span>
  </div>

  <div class="flex flex-col  shadow-lg shadow-gray-300">
    <stored-jobs-table/>
  </div>
</template>

<style scoped>

</style>