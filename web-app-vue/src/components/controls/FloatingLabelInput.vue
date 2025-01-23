<template>
  <div class="relative">

<!--    <input type="text" v-model="value" :id="inputId" class="bg-gray-300">-->

<!--    <input-->
<!--        v-model="value"-->
<!--        type="text"-->
<!--        :id="inputId"-->
<!--        class="block w-full px-2.5  pt-4 text-sm text-gray-900 bg-transparent border border-gray-300 rounded-lg appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"-->
<!--        placeholder=" "-->
<!--    />-->
<!--    <label-->
<!--        :for="inputId"-->
<!--        class="absolute text-sm text-gray-500 duration-300 transform -translate-y-4 scale-75 top-2 z-10 origin-[0] left-2.5 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4"-->
<!--    >-->
<!--      {{ label }}-->
<!--    </label>-->
  </div>
</template>

<script>
export default {
  props: {
    label: {
      type: String,
      required: true
    },
    modelValue: {
      type: String,
      required: true
    },
    inputId: {
      type: String,
      required: true
    }
  },
  computed: {
    value: {
      get() {
        return this.modelValue;
      },
      set(value) {
        this.$emit('update:modelValue', value);
      }
    }
  }
};
</script>

<style scoped>
</style>
