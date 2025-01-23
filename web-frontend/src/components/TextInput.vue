<script>
export default {
  props: {
    modelValue: String,
    placeholder: String,
    mask: String
  },
  data() {
    return {
      isFocused: false
    };
  },
  methods: {
    updateValue(value) {
      this.$emit('update:modelValue', value);
    },
    handleBlur() {
      if (!this.modelValue) {
        this.isFocused = false;
      }
      this.$emit('blur');
    }
  }
};
</script>

<!--<script setup>-->

<!--</script>-->

<template>
  <div class="relative">
    <input
        :type="mask === 'password' ? 'password' : 'text'"
        :value="modelValue"
        @input="updateValue($event.target.value)"
        @focus="isFocused = true"
        @blur="handleBlur"
        :placeholder="isFocused && mask !== 'password' ? mask : ''"
        class="custom-input"
    />
    <label
        :class="{ 'input-label-focus': isFocused || modelValue }"
        class="input-label"
    >
      {{ placeholder }}
    </label>
  </div>
</template>

<style scoped>
.relative {
  position: relative;
}

.custom-input {
  width: 100%;
  position: relative;
  z-index: 1;
}

.custom-input:focus {
  z-index: 0;
  outline: none;
  box-shadow: inset 0 0 0 2px #157f3d;
}

.input-label {
  position: absolute;
  left: 0.85rem;
  top: 0.25rem;
  pointer-events: none;
  transition: all 0.3s;
  @apply font-exo text-lg font-semibold text-gray-400;
  padding: 0 0.25rem;
  z-index: 2;
}

.input-label-focus {
  top: -0.9rem;
  left: 0.75rem;
  font-size: 0.95rem;
  @apply text-green-800;
}

.input-label-focus::after {
  content: '';
  position: absolute;
  bottom: 0.5rem;
  left: 0;
  width: 100%;
  height: 20%; /* Cover bottom 20% of the text */
  background-color: #e3e5e9; /* Match the input background color */
  z-index: -1; /* Place it behind the text */
}
</style>

