<script setup>
import InputParent from "../new_form_builder/InputParent.vue";
import InputLayout from "./NewInputLayout.vue";
</script>
<template>
  <InputParent v-slot="{ setRef, validation, error, value, changeValue }">
    <InputLayout :validation="validation" :error="error" :label="$attrs?.label">
      <div class="input-wrapper">
        <slot name="left" />
        <input :ref="setRef" />
        <slot name="right" />
      </div>
    </InputLayout>
  </InputParent>
</template>

