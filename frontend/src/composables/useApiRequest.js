import { provide, ref } from "vue";

export function useApiRequest(provideValues = true) {
  const response = ref();
  const pending = ref(false);
  const error = ref("");
  const dirty = ref(false);

  if (provideValues) {
    provide("pending", pending);
    provide("error", error);
  }

  function send(
    request = (f) => f,
    cb = (f) => f,
    remove = false,
    beforeResolve = false
  ) {
    if (typeof request != "function")
      return console.error("can not be called. not a function");

    pending.value = true;
    error.value = "";

    if (remove) {
      response.value = null;
    }
    // return new Promise((resolve, reject) => {
    try {
      dirty.value = true;
      request().then((res) => {
        if (beforeResolve) cb(res);

        // setTimeout(() => {
        pending.value = false;
        if (!(typeof cb == "function")) return; //resolve(res);

        response.value = res?.data ?? res;
        error.value = res?.error;

        cb(res);
        // resolve(res);
        // }, 0);
      });
    } catch (err) {
      console.error(err);
      pending.value = false;
      error.value = err.message;
      // reject(error.value)
    }
    // });
  }

  return {
    response,
    send,
    pending,
    error,
    dirty,
  };
}
