// axiosInstance.js
import axios from 'axios';
import {useUserStore} from "@/stores/user.js";
import {useRouter} from "vue-router";

const customAxios = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true,
});


customAxios.interceptors.request.use(
    (config) => {
        const userStore = useUserStore();
        const token = userStore.user?.accessToken;

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

customAxios.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        // const router = useRouter();
        // const currentRoute = router.currentRoute.value.fullPath;

        // alert(currentRoute);

        const userStore = useUserStore();
        if (error.response && error.response.status === 401) {
            userStore.logout()
        }
        if (error.response && error.response.status === 400) {
            // userStore.logout()
        }
        return Promise.reject(error);
    }
);

export default customAxios;
