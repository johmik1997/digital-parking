<script setup>
import Button from '@/components/Button.vue';
import Input from '@/components/new_form_elements/Input.vue';
import InputPassword from '@/components/new_form_elements/InputPassword.vue';
import Select from '@/components/new_form_elements/Select.vue';
import Form from '@/components/new_form_builder/Form.vue';
import NewFormParent from '../../roles/components/NewFormParent.vue';
import { closeModal } from '@customizer/modal-x';
import { useApiRequest } from '@/composables/useApiRequest';
import { CreateUser } from '../Api/UserApi';
import { useUsers } from '../store/userStore';
import { allRequest, toasted } from '@/utils/utils';
import { useForm } from '@/components/new_form_builder/useForm';
import { getAllRole } from '../../roles/Api/RoleApi';

const { submit } = useForm('addform');

const user = useUsers();
const req = useApiRequest();
const rolereq = useApiRequest();

// Fetch roles
rolereq.send(() =>
  allRequest({
    roles: getAllRole({ page: 1, limit: 500 }),
  })
);

// Submit handler
function create({ values }) {
  req.send(
    () => CreateUser(values),
    (res) => {
      if (res.success) {
        user.add(res.data);
        toasted(true, 'User Created');
        closeModal();
      } else {
        toasted(false, '', res.error);
      }
    }
  );
}
</script>

<template>
  <div class="bg-black/50 min-h-full p-4 sm:p-6 md:p-10 grid place-items-center">
    <NewFormParent title="Add User" size="xs">
      <Form :inner="false" v-slot="{ submit }" id="addform"
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 p-4 sm:p-6">
        
        <Input name="email" validation="required|email" label="Email" :attributes="{ placeholder: 'Enter User Email' }" />
        
        <InputPassword name="password" label="Password" validation="required|pass"
          :attributes="{ placeholder: 'Password' }" />
        
        <Select name="title" label="Title" validation="required" 
          :options="['Mr.', 'Ms.', 'Dr.', 'Prof']"
          :attributes="{ placeholder: 'Title' }" />
        
        <Input name="firstName" validation="required|alpha" label="First Name"
          :attributes="{ placeholder: 'Enter First Name' }" />
        
        <Input name="fatherName" validation="required|alpha" label="Father Name"
          :attributes="{ placeholder: 'Enter Father Name' }" />
        
        <Input name="grandFatherName" validation="required|alpha" label="Grandfather Name"
          :attributes="{ placeholder: 'Enter Grandfather Name' }" />
        
        <Select name="gender" label="Gender" validation="required"
          :options="['Female', 'Male']" :attributes="{ placeholder: 'Select Gender',
            required
           }" />
        
        <Input name="mobilePhone" label="Mobile Phone" validation="required|phone"
          :attributes="{ placeholder: 'Enter Mobile Phone' }" />
        <Select :obj="true" name="roleUuid" label="Role" validation="required"
          :options="(rolereq.response.value?.roles?.content || []).map(role => ({
            label: role.roleName,
            value: role.roleUuid,
          }))"
          :attributes="{ placeholder: 'Select Role',
            required
           }" />
      </Form>

      <template #bottom>
        <div class="p-4">
          <Button type="secondary" :pending="req.pending.value" @click.prevent="submit(create)" class="w-full">
            Add User
          </Button>
        </div>
      </template>
    </NewFormParent>
  </div>
</template>
