import ProgressView from "@/views/ProgressView.vue";
import SearchView from "@/views/SearchView.vue";
import SchedulerView from "@/views/SchedulerView.vue";
import UploadView from "@/views/UploadView.vue";
import TestView from '@/views/TestView.vue'
import LoginView from "@/views/LoginView.vue";

export const routes = [
    {
        path: '/',
        name: 'search',
        component: SearchView,
        label: 'Пошук',
        icon: 'magnifying-glass',
        isSideMenu: true,
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView,
        label: 'Логін',
        icon: 'user',
        isSideMenu: false,
    },
    {
        path: '/scheduler',
        name: 'scheduler',
        component: SchedulerView,
        label: 'Розклад',
        icon: 'calendar-days',
        isSideMenu: true,
    },
    {
        path: '/upload',
        name: 'upload',
        component: UploadView,
        label: 'Завантаження',
        icon: 'arrow-up-from-bracket',
        isSideMenu: true,
    },
    {
        path: '/progress',
        name: 'progress',
        component: ProgressView,
        label: 'Прогрес',
        icon: 'bars-progress',
        isSideMenu: true,
    }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    //
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }
    // ,
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // },
    // {
    //     path: '/test',
    //     name: 'test',
    //     component: TestView,
    //     label: 'Test',
    //     icon: 'bars-progress'
    // }



]