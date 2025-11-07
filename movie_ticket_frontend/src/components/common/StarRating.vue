<template>
  <div class="star-rating" :style="{ '--star-size': size + 'px' }">
    <svg
      v-for="n in max"
      :key="n"
      :width="size"
      :height="size"
      viewBox="0 0 24 24"
      class="star"
      aria-hidden="true"
    >
      <defs>
        <clipPath :id="`star-clip-${uid}-${n}`">
          <rect x="0" y="0" :width="fillPercent(n) + '%'" height="24" />
        </clipPath>
      </defs>
      <!-- empty star -->
      <path
        class="star-empty"
        d="M12 .587l3.668 7.431L24 9.748l-6 5.848L19.335 24 12 19.897 4.665 24 6 15.596 0 9.748l8.332-1.73L12 .587z"
        fill="currentColor"
        opacity="0.25"
      />
      <!-- filled star clipped by percentage -->
      <path
        class="star-fill"
        d="M12 .587l3.668 7.431L24 9.748l-6 5.848L19.335 24 12 19.897 4.665 24 6 15.596 0 9.748l8.332-1.73L12 .587z"
        :fill="fillColor"
        :clip-path="`url(#star-clip-${uid}-${n})`"
      />
    </svg>
  </div>
</template>

<script setup>
// no runtime imports required

const props = defineProps({
  value: { type: Number, default: 0 },
  max: { type: Number, default: 5 },
  size: { type: Number, default: 18 },
  fillColor: { type: String, default: '#f5a623' },
})

// unique id per component instance for clip paths
const uid = Math.random().toString(36).slice(2, 9)

const fillPercent = (index) => {
  const val = Number(props.value) || 0
  const start = index - 1
  const pct = Math.max(0, Math.min(100, (val - start) * 100))
  return Math.round(pct)
}
</script>

<style scoped lang="scss">
.star-rating {
  display: inline-flex;
  align-items: center;
  .star {
    display: inline-block;
    vertical-align: middle;
    color: rgba(0,0,0,0.85);
    margin-right: 4px;
    overflow: visible;
  }
  .star-empty {
    transition: fill 0.2s;
  }
  .star-fill {
    transition: width 0.2s, opacity 0.2s;
  }
}
</style>
