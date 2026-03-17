export default [
  { path: '/users', name: 'Users', component: () => import('@/features/users/pages/Users.vue'), meta: { requiresAuth: true, permissions: [] } },
  { path: '/edit_user/:userUuid', name: 'edit user', component: () => import('@/features/users/pages/EditUser.vue'), meta: { requiresAuth: true, permissions: [] } },
  { path: '/usersForm', name: 'User Form', component: () => import('@/features/users/pages/AddUser.mdl.vue'), meta: { requiresAuth: true, permissions: [] } },
]
