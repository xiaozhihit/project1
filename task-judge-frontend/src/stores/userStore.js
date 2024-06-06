import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
    state: () => ({
        username: '',
        roles:[],
        refreshOnNavigate: false,
    }),
    getters: {
        isLoggedIn: (state) => {
            return state.username !== '' && state.roles.length > 0;
        },
    },
    actions: {
        setUsername(newUsername) {
            this.username = newUsername;
        },
        setRoles(newRoles){
            this.roles=newRoles;
        },
        setRefreshOnNavigate(value){
            this.refreshOnNavigate=value;
        }
    },
});