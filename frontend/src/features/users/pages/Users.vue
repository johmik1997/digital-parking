<script setup>
import { ref, computed } from 'vue';
import Table from '@/components/Table.vue';
import { useUsers } from '../store/userStore';
import { useApiRequest } from '@/composables/useApiRequest';
import { getAllUser, removeUserById } from '../Api/UserApi';
import { toasted } from '@/utils/utils';
import { openModal } from '@customizer/modal-x';
import BaseIcon from '@/components/base/BaseIcon.vue';
import { mdiPencil, mdiDeleteAlert, mdiCheckDecagram } from '@mdi/js';
import { usePaginations } from '@/composables/usePaginationTemp';

const usersStore = useUsers();
const selectedUser = ref(null);

// Pagination setup
const pagination = usePaginations({
  store: usersStore,
  cb: async (params) => {
    try {
      const response = await getAllUser(params);

      if (response && response.data?.content) {
        usersStore.set(response.data);
        return response.data;
      } else if (response?.content) {
        usersStore.set(response);
        return response;
      } else if (Array.isArray(response)) {
        usersStore.set({ content: response, totalElements: response.length });
        return { content: response, totalElements: response.length };
      }
      return response;
    } catch (error) {
      console.error("Error fetching users:", error);
      toasted(false, 'Failed to load users', error.message);
      throw error;
    }
  },
  initialPage: 0,
  initialSize: 10,
  immediate: true
});

// Remove user
const removeReq = useApiRequest();
function remove(id) {
  openModal(
    'Confirmation',
    { title: 'Remove User', message: 'Are you sure you want to delete this user?' },
    async (confirm) => {
      if (confirm) {
        await removeReq.send(
          () => removeUserById(id),
          async (res) => {
            if (res.success) {
              usersStore.remove(id);
              toasted(true, 'Removed Successfully');
              await pagination.cb(pagination.params.value);
            } else {
              toasted(false, 'Remove Failed', res.error);
            }
          }
        );
      }
    }
  );
}

// Open Add User Modal
function openAddUserModal() {
  openModal('AddUser', {}, (result) => {
    if (result) pagination.cb(pagination.params.value);
  });
}

// Computed formatted users
const formattedUsers = computed(() => {
  if (!usersStore.users || !Array.isArray(usersStore.users)) return [];
  return usersStore.users.map(user => ({
    ...user,
    fullname: `${user.title || ''} ${user.firstName || ''} ${user.fatherName || ''} ${user.grandFatherName || ''}`.trim()
  }));
});

// Counts
const activeUsersCount = computed(() =>
  usersStore.users.filter(u => u.userStatus === 'ACTIVE').length
);

// Pagination info
const totalElements = computed(() =>
  usersStore.pagination?.totalElements || usersStore.users.length || 0
);
const currentPage = computed(() =>
  usersStore.pagination?.pageNumber || 0
);
const totalPages = computed(() =>
  usersStore.pagination?.totalPages ||
  Math.ceil(totalElements.value / (usersStore.pagination?.pageSize || 10)) ||
  1
);
</script>

<template>
  <div class="p-4 sm:p-7">
    <!-- Header Stats -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
      <div class="bg-white rounded-lg shadow p-4 border-l-4 border-blue-500">
        <p class="text-sm text-gray-500">Total Users</p>
        <p class="text-2xl font-bold text-gray-800">{{ totalElements }}</p>
      </div>
      <div class="bg-white rounded-lg shadow p-4 border-l-4 border-green-500">
        <p class="text-sm text-gray-500">Active Users</p>
        <p class="text-2xl font-bold text-gray-800">{{ activeUsersCount }}</p>
      </div>
    </div>

    <!-- Add User Button -->
    <div class="flex flex-col sm:flex-row justify-between items-center mb-6 gap-4">
      <div class="text-sm text-gray-600">
        <span class="font-medium">User Management</span> - Manage system users and their permissions
      </div>
      <button
        @click="openAddUserModal"
        class="bg-gradient-to-r from-blue-600 to-cyan-500 text-white px-6 py-2.5 rounded-lg flex items-center gap-2 sm:w-auto justify-center hover:from-blue-700 hover:to-cyan-600 transition-all shadow-md hover:shadow-lg"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        <span>Add New User</span>
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="pagination.pending.value" class="bg-white rounded-xl shadow-lg p-8">
      <div class="flex justify-center items-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <span class="ml-3 text-gray-600">Loading users...</span>
      </div>
    </div>

    <!-- Users Table -->
    <div v-else class="bg-white rounded-xl shadow-lg overflow-hidden">
      <div class="overflow-x-auto">
        <Table
          :headers="{ head: ['Full Name','Email','Mobile Phone','Role','Gender','Status','Actions'], row: ['fullname','email','mobilePhone','roleName','gender','userStatus'] }"
          :rows="formattedUsers"
        >
          <!-- Status Slot -->
          <template #userStatus="{ row }">
            <span
              :class="[
                'px-2 py-1 rounded-full text-xs font-medium',
                row.userStatus === 'ACTIVE' ? 'bg-green-100 text-green-800' :
                row.userStatus === 'INACTIVE' ? 'bg-gray-100 text-gray-800' :
                row.userStatus === 'SUSPENDED' ? 'bg-red-100 text-red-800' :
                'bg-yellow-100 text-yellow-800'
              ]"
            >
              {{ row.userStatus || 'INACTIVE' }}
            </span>
          </template>

            <template #actions="{ row }">
          <div class="flex flex-col sm:flex-row gap-2">
            <button
              class="bg-gray-600 text-white px-3 py-1 rounded flex justify-center items-center gap-1"
                @click="$router.push('/edit_user/' + row.userUuid)"
            >
              <BaseIcon :path="mdiPencil" />
              <span class=" sm:inline">Edit</span>
            </button>
            <button
              class="bg-[#FF4C4C] text-white px-3 py-1 rounded flex justify-center items-center gap-1"
                @click="remove(row.userUuid)"
            >
              <BaseIcon :path="mdiDeleteAlert" />
              <span class=" sm:inline">Delete</span>
            </button>
          </div>
        </template>
         

          <!-- Empty Slot -->
          <template #empty>
            <div class="text-center py-12">
              <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              <h3 class="mt-2 text-sm font-medium text-gray-900">No users found</h3>
              <p class="mt-1 text-sm text-gray-500">Get started by adding a new user.</p>
              <div class="mt-6">
                <button
                  @click="openAddUserModal"
                  class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700"
                >
                  <svg class="-ml-1 mr-2 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                  </svg>
                  Add User
                </button>
              </div>
            </div>
          </template>
        </Table>
      </div>

      <!-- Pagination Info -->
      <div v-if="totalElements > 0" class="px-6 py-4 border-t border-gray-200 bg-gray-50">
        <div class="flex items-center justify-between">
          <div class="text-sm text-gray-700">
            Showing <span class="font-medium">{{ formattedUsers.length }}</span> of 
            <span class="font-medium">{{ totalElements }}</span> total users
          </div>
          <div class="text-sm text-gray-500">
            Page {{ currentPage + 1 }} of {{ totalPages }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
