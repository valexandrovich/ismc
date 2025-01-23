import LoginView from "@/views/LoginView.vue";
import SearchView from "@/views/SearchView.vue";
import ScheduleView from "@/views/ScheduleView.vue";
import ProgressView from "@/views/ProgressView.vue";
import NotFoundView from "@/views/NotFoundView.vue";
import UploadView from "@/views/UploadView.vue";
import MonitoringView from "@/views/MonitoringView.vue";
import ReportsView from "@/views/ReportsView.vue";
import ToolsView from "@/views/ToolsView.vue";
import FilesView from "@/views/FilesView.vue";

// const isDevMode = import.meta.env.DEV;
const isDevMode = false;

const routes = [
    {
        path: '/login',
        name: 'login',
        label: 'Логін',
        component: LoginView,
        isInMenu: false,
        meta: {requiresAuth: false},
    },
    {
        path: '/',
        name: 'search',
        label: 'Пошук',
        component: SearchView,
        isInMenu: true,
        icon: 'material-symbols:search',
        meta: {
            requiresAuth: !isDevMode,
            roles:  ['ROLE_ADMIN', 'ROLE_SEARCH_BASE', 'ROLE_SEARCH_ADVANCED']
        },
    },
    {
        path: '/schedule',
        name: 'schedule',
        label: 'Розклад',
        component: ScheduleView,
        isInMenu: true,
        icon: 'mdi:calendar-month-outline',
        meta: {
            requiresAuth: !isDevMode,
            roles:  ['ROLE_ADMIN']
        },
    },
    {
        path: '/progress',
        name: 'progress',
        label: 'Прогрес',
        component: ProgressView,
        isInMenu: true,
        icon: 'material-symbols:progress-activity-sharp',
        meta: {
            requiresAuth: !isDevMode,
            roles:   ['ROLE_ADMIN']
        },
    },
    {
        path: '/upload',
        name: 'upload',
        label: 'Завантаження',
        component: UploadView,
        isInMenu: true,
        icon: 'material-symbols:upload',
        meta: {
            requiresAuth: !isDevMode,
            roles:   ['ROLE_ADMIN']
        },
    },

    {
        path: '/files',
        name: 'files',
        label: 'Файли',
        component: FilesView,
        isInMenu: true,
        icon: 'mdi:file-outline',
        meta: {
            requiresAuth: !isDevMode,
            roles:   ['ROLE_ADMIN']
        },
    },



    {
        path: '/monitoring',
        name: 'monitoring',
        label: 'Моніторинг',
        component: MonitoringView,
        isInMenu: true,
        icon: 'eos-icons:monitoring',
        meta: {requiresAuth: true, roles: ['ROLE_ADMIN']},
    },
    {
        path: '/reports',
        name: 'reports',
        label: 'Звіти',
        component: ReportsView,
        isInMenu: true,
        icon: 'carbon:document',
        meta: {requiresAuth: true, roles: ['ROLE_ADMIN']},
    },

    {
        path: '/tools',
        name: 'tools',
        label: 'Інструменти',
        component: ToolsView,
        isInMenu: true,
        icon: 'ph:wrench',
        meta: {requiresAuth: true, roles: ['ROLE_ADMIN']},
    },


    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: NotFoundView,
    },
]

export default routes