import { ref } from "vue";
import { defineStore } from "pinia";

export const useUsers = defineStore("allUserStore", () => {
  const users = ref([]);
  const pagination = ref({
    totalElements: 0,
    totalPages: 0,
    pageNumber: 0,
    pageSize: 0
  });

  function set(data) {
    console.log("Setting user data:", data);
    
    // Handle the nested content structure from your API
    if (data && data.content && Array.isArray(data.content)) {
      users.value = data.content;
      pagination.value = {
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0,
        pageNumber: data.pageNumber || 0,
        pageSize: data.pageSize || 0
      };
      console.log("Updated pagination:", pagination.value);
    } else if (Array.isArray(data)) {
      // Fallback for direct array
      users.value = data;
    } else {
      users.value = [];
    }
  }

  function getAll() {
    return users.value;
  }
  
  function add(data) {
    users.value.push(data);
  }

  function update(id, data) {
    const idx = users.value.findIndex((el) => el.userUuid == id);
    if (idx == -1) return;

    users.value.splice(idx, 1, data);
  }
  
  function remove(id) {
    const idx = users.value.findIndex((el) => el.userUuid == id);
    if (idx == -1) return;
    users.value.splice(idx, 1);
  }

  function updateStatus(id, status) {
    const idx = users.value.findIndex((el) => el.userUuid == id);
    if (idx == -1) return;

    users.value[idx].userStatus = status;
  }
  
  function updateVerification(userUuid, isVerified) {
    const user = users.value.find(u => u.userUuid === userUuid);
    if (user) {
      // Update both userStatus and isVerified for consistency
      user.userStatus = isVerified ? 'ACTIVE' : 'INACTIVE';
      // Also add a computed property or direct property if needed
      user.isVerified = isVerified;
    }
  }

  return {
    users,
    pagination,
    getAll,
    update,
    remove,
    updateStatus,
    set,
    add,
    updateVerification,
  };
});