<script setup lang="ts">
// Renders the expected output of a code sample as a terminal-style block.
withDefaults(defineProps<{ label?: string }>(), { label: 'Saída' })
</script>

<template>
  <div class="neo-output">
    <div class="neo-output__bar">
      <span class="neo-output__dot" />
      <span class="neo-output__dot" />
      <span class="neo-output__dot" />
      <span class="neo-output__label">{{ label }}</span>
    </div>
    <pre class="neo-output__body"><slot /></pre>
  </div>
</template>

<style scoped>
.neo-output {
  margin: 16px 0;
  border: 1px solid var(--vp-c-divider);
  border-radius: 10px;
  overflow: hidden;
  background: var(--vp-c-bg-alt);
}
.neo-output__bar {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: var(--vp-c-bg-soft);
  border-bottom: 1px solid var(--vp-c-divider);
}
.neo-output__dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--vp-c-divider);
}
.neo-output__dot:nth-child(1) { background: #ff5f56; }
.neo-output__dot:nth-child(2) { background: #ffbd2e; }
.neo-output__dot:nth-child(3) { background: #27c93f; }
.neo-output__label {
  margin-left: 8px;
  font-size: 12px;
  color: var(--vp-c-text-2);
  font-family: var(--vp-font-family-mono);
}
.neo-output__body {
  margin: 0;
  padding: 14px 16px;
  font-family: var(--vp-font-family-mono);
  font-size: 13.5px;
  line-height: 1.6;
  color: var(--vp-c-text-1);
  white-space: pre-wrap;
  overflow-x: auto;
}
</style>
