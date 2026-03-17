<script setup lang="ts">
import { useForm } from "./useForm";
const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  inner: {
    type: Boolean,
    default: true,
  },
  childrenName: {
    type: String,
  },
});

if (!props.id) {
  throw new Error("[id] is required for a new form");
}

type Submit = (cb: Function) => void;

interface UseForm {
  formEl: HTMLFormElement,
  submit: Submit,
  valid: boolean
}

var { formEl, submit, valid }: UseForm = useForm(
  props.id,
  props.inner,
  props.childrenName
);
</script>
<template>
  <form autocomplete="off" :id="id" ref="formEl" action="">
    <slot :valid="valid" :submit="submit"></slot>
  </form>
</template>
