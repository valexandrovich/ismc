<script setup>
import { defineProps } from 'vue'

// Define the color and width props
const props = defineProps({
  colorClass: {
    type: String,
    default: 'text-red-500' // Default Tailwind CSS class for color
  },
  width: {
    type: String,
    default: "100" // Default Tailwind CSS class for width
  }
})
</script>

<template>
  <div :class="[colorClass, width]">
    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" zoomAndPan="magnify"
         viewBox="0 0 768 767.999994" :height="width" preserveAspectRatio="xMidYMid meet" version="1.0">
      <defs>
        <clipPath id="852997ca5c">
          <path d="M 103.332031 170 L 664.332031 170 L 664.332031 768 L 103.332031 768 Z M 103.332031 170 "
                clip-rule="nonzero"/>
        </clipPath>
        <clipPath id="080b2775bc">
          <path
              d="M 256.191406 234.984375 L 295.738281 234.984375 L 295.738281 276.25 L 256.191406 276.25 Z M 256.191406 234.984375 "
              clip-rule="nonzero"/>
        </clipPath>
      </defs>
      <g clip-path="url(#852997ca5c)">
        <path fill="currentColor"
              d="M 383.996094 768.472656 L 103.480469 515.949219 L 103.480469 170.230469 L 245.277344 170.230469 L 212.210938 229.4375 L 163.152344 229.4375 L 163.152344 489.496094 L 383.996094 688.347656 L 604.835938 489.496094 L 604.835938 229.4375 L 555.777344 229.4375 L 522.867188 170.230469 L 664.507812 170.230469 L 664.507812 515.949219 Z M 383.996094 768.472656 "
              fill-opacity="1" fill-rule="nonzero"/>
      </g>
      <path fill="currentColor"
            d="M 112.863281 488.265625 L 383.996094 -0.476562 L 655.125 488.265625 L 603.453125 517.023438 L 383.996094 121.9375 L 164.84375 517.023438 Z M 112.863281 488.265625 "
            fill-opacity="1" fill-rule="nonzero"/>
      <path fill="currentColor"
            d="M 202.214844 573.464844 L 383.996094 245.585938 L 565.773438 573.464844 L 514.101562 602.222656 L 383.996094 368.15625 L 254.195312 602.222656 Z M 202.214844 573.464844 "
            fill-opacity="1" fill-rule="nonzero"/>
      <g clip-path="url(#080b2775bc)">
        <path fill="currentColor"
              d="M 256.191406 234.984375 L 295.738281 234.984375 L 295.738281 276.230469 L 256.191406 276.230469 Z M 256.191406 234.984375 "
              fill-opacity="1" fill-rule="nonzero"/>
      </g>
    </svg>
  </div>
</template>

<style scoped>
/* No additional styles needed */
</style>
