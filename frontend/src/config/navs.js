import icons from "@/utils/icons";

export default [
  {
    path: "/dashboard",
    icon: icons.dashboard,
    name: "Dashboard",
    type: "Analytics",
    role: "Super Admin",
    privilage: ['CREATE_USER'], // admin-only
  },
  {
    path: "/customer",
    icon: icons.dashboard,
    name: "Dashboard",
    type: "Customer",
    role: "Customer",
  },
  {
    path: "/service/product",
    icon: icons.order ,
    name: "Service",
    type: "Service",
    role: "Super Admin",
    privilage: ['CREATE_USER'], // admin-only
  },
  {
    path: "/service/order",
    icon: icons.service,
    name: "Service-Order",
    type: "Service",
    roles: ["Customer", "Super Admin"],
  },
  {
    path: "/cashier",
    icon: icons.dashboard,
    name: "Dashboard",
    type: "Cashier",
    role: "Cashier",

  },
  {
    path: "/cashier/report",
    icon: icons.report,
    name: "Report",
    type: "Cashier",
    role: "Cashier",
  },
  {
    path: "/users",
    name: "Users",
    icon: icons.users,
    type: "Settings",
    role: "Super Admin",
    privilage: ['CREATE_USER'], // admin-only
  },
  {
    path: "/privileges",
    name: "Privileges",
    icon: icons.privilege,
    type: "Settings",
    role: "Super Admin",
    privilage: ['CREATE_USER'], // admin-only
  },
  {
    path: "/roles",
    name: "Roles",
    icon: icons.role,
    type: "Settings",
    role: "Super Admin",
    privilage: ['CREATE_USER'], // admin-only
  },
];
