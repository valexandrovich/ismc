<script setup>
import { ref, watch, defineProps, defineEmits, onMounted, defineExpose } from 'vue';

const props = defineProps({
  modelValue: {
    type: [String, null, Number],
    required: true,
  },
  placeholder: {
    type: String,
    required: true,
  },
  label: {
    type: String,
    required: true,
  },
  errorLabel: {
    type: [String, null, Number, Array],
    required: false,
  }

});

const emit = defineEmits(['update:modelValue']);

const inputValue = ref(props.modelValue);

const inputFocused = ref(false);



watch(inputValue, (newValue) => {
  emit('update:modelValue', newValue);
});

watch(() => props.modelValue, (newValue) => {
  inputValue.value = newValue;
});


const handleBlur = () => {
  inputFocused.value = false;

};




</script>

<template>
  <div class="relative flex flex-col">
    <label
        :class="[
        'absolute top-[-8px] left-2 transition-all duration-200 ease-in-out pointer-events-none',
        inputFocused ? 'dark:text-teal-400 text-teal-600' : 'text-slate-400',
      ]"
        :style="{
        transform: inputFocused || inputValue ? 'translate(-10px, -13px) scale(0.75)' : 'translate(8px, 12px) scale(1)',
      }"
    >
      {{ label }}
    </label>
    <input
        type="text"
        v-model="inputValue"
        @focus="inputFocused = true"
        @blur="handleBlur"
        :placeholder="inputFocused && inputValue === '' ? placeholder : ''"

    />

    <div class="text-xs text-red-400 flex flex-col">
      <span v-for="err in errorLabel" :key="err">{{err}}</span>
    </div>

  </div>
</template>

<style scoped>
.err-input {
  @apply outline outline-2 outline-red-400 outline-offset-0;
}
</style>