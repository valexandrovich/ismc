import {createRouter, createWebHistory} from 'vue-router';
import {useUserStore} from '@/stores/user.js';
import routes from "@/utils/menuItems.js";


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

router.beforeEach((to, from, next) => {
    const userStore = useUserStore();
    const user = userStore.user; // Get the current user

    if (to.matched.some((record) => record.meta.requiresAuth)) {
        if (!user) {
            next({ name: 'login', query: { redirect: to.fullPath } });
        } else {
            const requiredRoles = to.meta.roles;
            if (requiredRoles && requiredRoles.length > 0) {
                const userRoles = user.roles || [];
                const hasRole = requiredRoles.some((role) => userRoles.includes(role));
                if (!hasRole) {
                    next({ path: '/login' });
                    // next(false);
                } else {
                    next();
                }
            } else {
                next();
            }
        }
    } else {
        next();
    }
});

// router.beforeEach((to, from, next) => {
//     const userStore = useUserStore();
//     const user = userStore.user; // Get the current user
//
//     if (to.matched.some((record) => record.meta.requiresAuth)) {
//
//         if (!user) {
//             alert('REDIRECT TO LOGIN !user')
//             next({name: 'login'});
//         } else {
//             const requiredRoles = to.meta.roles;
//             if (requiredRoles && requiredRoles.length > 0) {
//                 const userRoles = user.roles || [];
//                 const hasRole = requiredRoles.some((role) => userRoles.includes(role));
//                 if (!hasRole) {
//                     next(false);
//                 } else {
//                     next();
//                 }
//             } else {
//                 next();
//             }
//         }
//     } else {
//         next();
//     }
// });

export default router;
