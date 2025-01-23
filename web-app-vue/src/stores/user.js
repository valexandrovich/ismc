import {ref} from 'vue'
import {defineStore} from 'pinia'
import {useRouter} from "vue-router";

export const useUserStore = defineStore('user', () => {

    const router = useRouter();
    const user = ref(null)

    function logout() {
        this.user = null
        localStorage.removeItem('user');
        // router.push({ name: 'login', query: { redirect: currentRoute } });
        router.push({name: 'login'})
        ;
    }

        function login(user) {
            this.user = user
            localStorage.setItem('user', JSON.stringify(user));
            router.push('/');
        }

        return {user, logout, login}
    }

)
