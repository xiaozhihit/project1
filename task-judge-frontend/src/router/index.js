import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from "../stores/userStore";
import {get} from "../net";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/components/Login.vue')
                },
                {
                    path:'forget',
                    name:'welcome-forget',
                    component:()=>import('@/components/Forget.vue')
                },
                {
                    path: 'change-password',
                    name: 'change-password',
                    component:()=>import('@/components/ChangePassword.vue')
                }
            ]
         },
        {
            path: '/index',
            name:'index',
            component: () => import('@/views/IndexView.vue'),
            meta: { requiresAuth: true },
            children: [
                {
                    path: '/user/view-task-score',
                    name: 'view-task-score',
                    component: () => import('@/components/user/ViewTaskScore.vue'),
                    meta: { refreshOnNavigate: true },
                },
                {
                    path: '/user/submit-task',
                    name: 'submit-task',
                    component: () => import("@/components/user/SubmitTask.vue"),
                },

                {
                    path:'/judge/judge-task',
                    name:'judge-task',
                    component:()=>import('@/components/judge/JudgeTask.vue'),
                    meta: { refreshOnNavigate: true },
                },

                {
                    path: '/admin/reset-student-password',
                    name: 'reset-student-password',
                    component: () => import('@/components/admin/ResetStudentPassword.vue'),
                    meta: { refreshOnNavigate: true },
                },
                {
                    path: '/admin/assign-group',
                    name: 'assign-group',
                    component: () => import("@/components/admin/AssignGroup.vue"),
                    meta: { refreshOnNavigate: true },
                },
                {
                    path:'/admin/set-task-url',
                    name:'set-task-url',
                    component:()=>import('@/components/admin/SetTaskUrl.vue'),
                    meta: { refreshOnNavigate: true },
                },
                {
                    path: '/admin/register-student',
                    name: 'register-student',
                    component: () => import('@/components/admin/RegisterStudent.vue'),
                },
                {
                    path: '/admin/reset-judge-password',
                    name: 'reset-judge-password',
                    component: () => import("@/components/admin/ResetJudgePassword.vue"),
                    meta: { refreshOnNavigate: true },
                },
                {
                    path:'/admin/revoke-scoring',
                    name:'revoke-scoring',
                    component:()=>import('@/components/admin/RevokeScoring.vue'),
                    meta: { refreshOnNavigate: true },
                },
                {
                    path: '/admin/assign-judge',
                    name:'assign-judge',
                    component:()=>import('@/components/admin/AssignJudge.vue'),
                    meta: { refreshOnNavigate: true },
                }
            ],
        },
        {
            path: '/:pathMatch(.*)*',
            name: 'NotFound',
            component: () => import('@/views/404.vue'),
            meta:{requiresAuth: true}
        },

    ]
})

router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore();
    await get('/api/auth/check-login', (data) => {
        if (data.isLoggedIn) {
            userStore.setUsername(data.username);
            userStore.setRoles(data.roles);
        }
    })
    //检查要进入的路由是否需要登录
    if (to.matched.some(record => record.meta.requiresAuth)) {
        // 检查用户是否已登录
        if (userStore.isLoggedIn) {
            next();
        } else {
            // 如果用户未登录，重定向到登录页面
            next({path: '/'});
        }
    } else {
        next(); // 确保一定要调用 next()
    }

    if (to.meta.refreshOnNavigate) {
        router.afterEach((to, from) => {
            if (to.meta.refreshOnNavigate) {
                userStore.setRefreshOnNavigate(true);
            }
        });
    } else {
        userStore.setRefreshOnNavigate(false);
    }

});

export default router
