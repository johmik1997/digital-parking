import { computed, provide, ref, watch } from "vue";
import { useTablePagination } from "./useTablePagination";
import { useApiRequest } from "./useApiRequest";

export function usePaginations(options = {}) {
  const paginationOptions = ref({
    cb: (f) => f,
    store: null,
    auto: true,
    perPage: 25,
    cache: false,
    ...(options || {}),
  });

  const search = ref("");
  const perPage = ref(paginationOptions.value.perPage);

  const req = useApiRequest(); // This has req.pending

  const searching = ref(false);
  const searchPagination = useTablePagination(perPage.value);
  const pagination = useTablePagination(perPage.value);

  function getPaginationData(next = true, current = false) {
    if (searching.value) {
      return JSON.parse(
        JSON.stringify({
          searchKey: search.value || undefined,
          search: search.value || undefined,
          page: next
            ? !current
              ? ++searchPagination.page.value
              : searchPagination.page.value
            : --searchPagination.page.value,
          size: searchPagination.limit.value || 25,
        })
      );
    } else {
      return JSON.parse(
        JSON.stringify({
          searchKey: search.value || undefined,
          search: search.value || undefined,
          page: next
            ? !current
              ? ++pagination.page.value
              : pagination.page.value
            : --pagination.page.value,
          size: pagination.limit.value || 25,
        })
      );
    }
  }

  function fetch(next = true, current = false, cache = false) {
    if (req.pending.value || (next && pagination.done.value)) return;

    // if(cache && paginationOptions.value.store && paginationOptions.value.store.getAll()?.length) return

    req.send(
      () => paginationOptions.value.cb(getPaginationData(next, current)),
      (res) => {
        
        if (paginationOptions.value.store && res.success) {
          console.log(res);
            paginationOptions.value.store.set(res.data?.response || res.data || []);
        }

        pagination.totalPages.value = res.data?.totalPages || 1;
        if (
          res.success &&
          res.data?.length < pagination.limit.value
        ) {
          pagination.done.value = true;
        }
      },
      true
    );
  }

  let controller;
  let timeout;
  function fetchSearch(next = true, current = false) {
    if (next && searchPagination.done.value) return;

    if (controller) controller.abort();
    if (timeout) clearTimeout(timeout);
    controller = new AbortController();

    timeout = setTimeout(() => {
      req.send(
        () =>
          paginationOptions.value.cb(getPaginationData(next, current), {
            signal: controller.signal,
          }),
        (res) => {
          searchPagination.totalPages.value = res.data?.totalPages || 1;
          if (
            res?.success &&
            res.data?.length < searchPagination.limit.value
          ) {
            searchPagination.done.value = true;
          }
        },
        true
      );
    }, 20);
  }

  function next() {
    if (searching.value) {
      fetchSearch();
    } else {
      fetch(true, false, paginationOptions.value.cache);
    }
  }

  function previous() {
    if (searching.value && searchPagination.page.value == 1) return;
    if (!searching.value && pagination.page.value == 1) return;

    if (searching.value) {
      fetchSearch(false);
      searchPagination.done.value = false;
    } else {
      pagination.done.value = false;
      fetch(false, false, paginationOptions.value.cache);
    }
  }

  watch(search, () => {
    searchPagination.done.value = false;
    searchPagination.page.value = 0;
    if (search.value) {
      searching.value = true;
      fetchSearch(true, false);
    } else if (!search.value && paginationOptions.value.auto) {
      searching.value = false;
      pagination.done.value = false;
      fetch(true, true, paginationOptions.value.cache);
    }
  });

  const auto = computed(() => paginationOptions.value.auto);
  watch(auto, fetch, {
    immediate: paginationOptions.value.auto,
  });

  watch(perPage, () => {
    pagination.reset(perPage.value);
    searchPagination.reset(perPage.value);
    if (search.value) {
      searching.value = true;
      fetchSearch(true, true);
    } else {
      searching.value = false;
      fetch(true, true, paginationOptions.value.cache);
    }
  });

  provide("next", next);
  provide("previous", previous);
  provide("searchPage", searchPagination.page);
  provide("searchTotalPages", searchPagination.totalPages);
  provide("page", pagination.page);
  provide("totalPages", pagination.totalPages);
  provide("searching", searching);

  const page = computed(() => {
    return searching.value
      ? searchPagination.page.value
      : pagination.page.value;
  });
 async function send() {
    try {
      // Use req.pending instead of pending
      req.pending.value = true;
      
      const response = await paginationOptions.value.cb(
        {
          page: pagination.page.value, // Use pagination.page.value instead of currentPage
          size: perPage.value, // Use 'size' instead of 'limit' to match your API
          search: search.value || undefined,
          ...paginationOptions.value.params
        },
        paginationOptions.value.config
      );
      
      // Handle paginated response format
      if (response && response.data) {
        // Extract pagination metadata - adjust based on your API response structure
        const { totalElements, totalPages, size, number, content } = response.data;
        
        // Update pagination state
        pagination.total.value = totalElements || 0;
        pagination.totalPages.value = totalPages || 1;
        perPage.value = size || perPage.value;
        pagination.page.value = number || 0;
        
        // Set the response data to the store
        if (paginationOptions.value.store && typeof paginationOptions.value.store.set === 'function') {
          paginationOptions.value.store.set(content || response.data.response || response.data);
        }
      }
      
      return response;
    } catch (error) {
      console.error('Pagination error:', error);
      req.error.value = error; // Set error on req
      return { success: false, error: error.message };
    } finally {
      req.pending.value = false; // Use req.pending instead of pending
    }
  }

  return {
    page: computed(() => searching.value ? searchPagination.page.value : pagination.page.value),
    search,
    send,
    perPage,
    totalPages: computed(() => searching.value ? searchPagination.totalPages.value : pagination.totalPages.value),
    data: computed(() => {
      if (paginationOptions.value.store && !searching.value) {
        return paginationOptions.value.store.getAll();
      }
      return req.response.value?.data?.response || req.response.value?.data || [];
    }),
    error: req.error,
    pending: req.pending, // Return req.pending
    dirty: req.dirty,
    next,
    previous,
  };
}