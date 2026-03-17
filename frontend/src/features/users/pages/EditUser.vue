<script setup>
import { useRoute, useRouter } from 'vue-router';
import { useApiRequest } from '@/composables/useApiRequest';
import { getUserById, updateUserById } from '../Api/UserApi';
import { getAllRole } from '../../roles/Api/RoleApi';
import { allRequest, toasted } from '@/utils/utils.js';
import { useUsers } from '../store/userStore';
import NewFormParent from '../../roles/components/NewFormParent.vue';
import { ref } from 'vue';
import Button from '@/components/Button.vue';
import { useForm } from '@/components/new_form_builder/useForm';
import UserForm from '../form/userForm.vue';

const route = useRoute()
const userStore = useUsers()
const userUuid = route.params.userUuid
const user = ref(userStore.users.find((el) => el.userUuid == userUuid) || {});
const { submit } = useForm('userform')

const userReq = useApiRequest()
const updateReq = useApiRequest();

userReq.send(
    () => allRequest({
        user: getUserById(userUuid),
        roles: getAllRole({ page: 1, limit: 500 })
    })
)

function update({ values }) {
    console.log(values);
    updateReq.send(
        () => updateUserById(userUuid, values),
        (res) => {
            toasted(res.success, 'Successfully Updated', res.error);
            if (res.success) {
                userStore.update(userUuid, { ...user, ...values });
            }
        }
    );
}
const router = useRouter()

const goBack = () => {
    router.go(-1);
};

</script>
<template>
    <button @click.prevent="goBack">
        <div class="flex gap-2 p-4 item-center ">

            <span class="item-center mt-1">
                <svg width="7" height="13" viewBox="0 0 7 13" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path opacity="0.6" fill-rule="evenodd" clip-rule="evenodd"
                        d="M5.82539 1.0134C6.03505 1.20471 6.05933 1.54072 5.87962 1.76391L2.15854 6.38525L5.87962 11.0066C6.05933 11.2298 6.03505 11.5658 5.82539 11.7571C5.61572 11.9484 5.30007 11.9226 5.12036 11.6994L1.12037 6.73164C0.959876 6.53232 0.959876 6.23819 1.12037 6.03887L5.12036 1.07113C5.30008 0.847943 5.61572 0.822096 5.82539 1.0134Z"
                        fill="#263558" stroke="#263558" stroke-linecap="round" />
                </svg>
            </span>
            <h3>Go Back</h3>


        </div>
    </button>
    {{ console.log(userReq.response.value) }}
    <NewFormParent size="xl" title="Update Users">
        <UserForm v-if="!userReq.pending.value" :roles="userReq.response.value?.roles?.content"
            :user="userReq.response.value?.user" />
        <Button size="xl" type="primary" class="flex justify-center items-center mt-3 gap-3 p-2 bg-primary"
            :pending="updateReq.pending.value" @click.prevent="submit(update)">
            Update User
        </Button>
    </NewFormParent>
</template>