<script setup>
import {ref, computed, watch, defineProps, defineEmits, onMounted} from 'vue';

const props = defineProps({
  modelValue: {
    type: [String, null],
    required: true,
  },
  label: {
    type: String,
    required: true,
  },
  options: {
    type: Array,
    required: true,
  },
  isAllowedNull: {
    type: Boolean,
    required: true,
  }
});

// Define reactive properties for internal state and error message
const selectedValue = ref(props.modelValue);
const errorMessage = ref('');

// Computed property to set the initial value
const computedValue = computed(() => {
  if (props.modelValue === '') {
    return '';
  }
  if (props.options.includes(props.modelValue)) {
    return props.modelValue;
  }
  return '';
});

// Watch for changes in modelValue and options
watch(() => props.modelValue, (newValue) => {
  if (newValue === '') {
    selectedValue.value = '';
    errorMessage.value = '';
  } else if (props.options.includes(newValue)) {
    selectedValue.value = newValue;
    errorMessage.value = '';
  } else {
    errorMessage.value = 'Invalid option selected';
  }
}, { immediate: true });

watch(() => props.options, () => {
  validate();
  if (!props.options.includes(selectedValue.value)) {
    selectedValue.value = '';
    errorMessage.value = 'Invalid option selected';
  } else {
    errorMessage.value = '';
  }
});

const emit = defineEmits(['update:modelValue']);

const errors = ref([]);

const validate = () => {
  errors.value = []; // Reset errors
  if ((selectedValue.value === '' || selectedValue.value === null || selectedValue.value === undefined) && !props.isAllowedNull) {
    console.log('NO NULL ALLOWED!!')
    errors.value.push('NO NULL ALLOWED!!');
  }
};


onMounted(()=>{validate()})


// Emit the selected value when it changes
watch(selectedValue, (newValue) => {
  validate();
  // Here we should emit the updated value to the parent component
  // Since we're using <script setup>, the `emit` function is automatically defined as a function named `emit`
  emit('update:modelValue', newValue);
});
</script>

<template>
  <div class="relative flex flex-col">
    <label :for="label">{{ label }}</label>

    <select v-model="selectedValue">
      <option value="">-</option>
      <option v-for="option in options" :key="option" :value="option">{{ option }}</option>
    </select>

    <div v-if="errors" class="text-red-500">{{ errors }}</div>
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>