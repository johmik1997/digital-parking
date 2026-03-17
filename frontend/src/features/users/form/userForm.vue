<script setup>
import Input from '@/components/new_form_elements/Input.vue';
import InputPassword from '@/components/new_form_elements/InputPassword.vue';
import Select from '@/components/new_form_elements/Select.vue';
import { useApiRequest } from '@/composables/useApiRequest';
import { getAllRole } from '../../roles/Api/RoleApi';
import Form from '@/components/new_form_builder/Form.vue';


const props = defineProps({
    roles: {
        type: Array,
        required: true,
    },
    user: {
        type: Object,
        required: true,
    }
})

console.log('user', props.user);
console.log(props.roles);


</script>
<template>
    <Form class="grid grid-cols-3 gap-5 mt-3 p-6" id="userform" :inner="false">
        <Input name="email"  label="Email" :value="user?.email || ''" :attributes="{
            placeholder: 'Enter User Email',
        }" />
        <Select name="title" label="Title" :value="user?.title || ''"
            :options="['Mr.', 'Ms.', 'Dr.', 'Prof.']" :attributes="{
                type: 'text',
                placeholder: 'title',
              
            }">
        </Select>
        <Input name="firstName" validation="required|alpha" :value="user?.firstName || ''" label="First Name"
            :attributes="{
                placeholder: 'Enter firstname',
                required
            }" />
        <Input name="fatherName" validation="required|alpha" :value="user?.fatherName || ''" label="Father Name"
            :attributes="{
                placeholder: 'Enter fathername',
                required
            }" />
        <Input name="grandFatherName"  :value="user?.grandFatherName || ''"
            label="Grandfather Name" :attributes="{
                placeholder: 'Enter grandfathername',
            }" />
        <Select name="gender" label="Gender" :value="user?.gender || ''" validation="required"
            :options="['Female', 'Male']" :attributes="{
                placeholder: 'select gender',
                required
            }">
        </Select>

        <Input name="mobilePhone" label="Mobile Phone" :value="user?.mobilePhone || ''" validation="required|phone"
            :attributes="{
                placeholder: 'mobile phone',
                required
            }" />
        <Select :obj="true" name="roleUuid" label="Role" validation="required" :value="user?.roleUuid || ''"
            :options="(roles || []).map(role => ({ label: role.roleName, value: role.roleUuid }))" :attributes="{
                placeholder: 'Select Role',
                required
            }">
        </Select>
         <Select name="userType" label="User Type" validation="required"
          :options="['Admin', 'Client', 'Insurance', 'Bank', 'Agent']" :value="user?.userType || ''"
          :attributes="{ placeholder: 'Select User Type',
            required
           }" />
        <Select name="userStatus" label="User Status" :value="user?.userStatus || ''" validation="required"
            :options="['ACTIVE', 'HISTORY', 'SUSPENDED', 'PENDING']" :attributes="{
                type: 'text',
                placeholder: 'status',
            }"></Select>
   

    </Form>
</template>