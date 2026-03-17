<!-- <script setup lang="ts">
import Input from '@/components/new_form_elements/Input.vue';
import InputPassword from '@/components/new_form_elements/InputPassword.vue';
import Select from '@/components/new_form_elements/Select.vue';
import Form from '@/new_form_builder/Form.vue';
</script>

<template>
    <Form class="grid grid-cols-3 gap-5 mt-3 p-6" id="userform" :inner="false">
        <Input name="email" validation="required" label="Email" :value="user?.email || ''" :attributes="{
            placeholder: 'Enter User Email',
        }" />
        <InputPassword name="password" label="Password" validation="required|pass"
        :attributes="{ placeholder: 'Password' }" />
        <Select name="title" label="Title" validation="required" :value="user?.title || ''"
            :options="['Mr.', 'Ms.', 'Dr.', 'Prof.']" :attributes="{
                type: 'text',
                placeholder: 'title',
            }">
        </Select>
        <Input name="firstName" validation="required" :value="user?.firstName || ''" label="First Name" :attributes="{
            placeholder: 'Enter firstname',
        }" />
        <Input name="fatherName" validation="required" :value="user?.fatherName || ''" label="Father Name" :attributes="{
            placeholder: 'Enter fathername',
        }" />
        <Input name="grandFatherName" validation="required" :value="user?.grandFatherName || ''"
            label="Grandfather Name" :attributes="{
                placeholder: 'Enter grandfathername',
            }" />
        <Select name="gender" label="Gender" :value="user?.gender || ''" validation="required"
            :options="['Female', 'Male']" :attributes="{
                type: 'text',
                placeholder: '',
            }">
        </Select>

        <Input name="mobilePhone" label="Mobile Phone" :value="user?.mobilePhone || ''" validation="required|phone"
            :attributes="{
                placeholder: 'mobile phone',
            }" />
        <Select :value="user?.roleUuid || ''" :obj="true" name="roleUuid" label="Role" validation="required"
            :options="(roles || []).map(role => ({ label: role.roleName, value: role.roleUuid }))" :attributes="{
                placeholder: 'Select Role',
            }">
        </Select>
        <Select name="userStatus" label="User Status" :value="user?.userStatus || ''" validation="required"
            :options="['Active', 'Inactive']" :attributes="{
                type: 'text',
                placeholder: 'status',
            }"></Select>
        <Input name="userType" label="User Type" :value="user?.userType || ''" validation="required" :attributes="{
            placeholder: 'user type',
        }" />

    </Form>
</template> -->