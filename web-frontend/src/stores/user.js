import {defineStore} from 'pinia'

export const useUserStore = defineStore('userStore', {
    state: () => ({
        
    }),
    actions: {
        saveResults(newArray) {
            this.results = newArray;
        }
    },
    getters: {

        isResultsEmpty: (state) => {
            if (!state.results || Object.keys(state.results).length === 0) {
                return true;
            }
            return Object.values(state.results).every(array => Array.isArray(array) && array.length === 0);
        },

        getResults: (state) => state.results
    }
});