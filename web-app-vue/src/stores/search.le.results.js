import {defineStore} from 'pinia'

export const useSearchLeResultsStore = defineStore('searchLeResults', {
    state: () => ({
        searchForm: {
            "params": {
            },
            "name": null,
            "edrpou": null,
            "address": null,
            "phone": null
        },
        results: {
            "govua01List": [

            ],
            "govua06List": [

            ],
            "govua07List": [

            ]

        }
    }),
    actions: {
        saveResults(newArray) {
            this.results = newArray;
        },
        clearResults() {
            this.results.govua01List = [];
            this.results.govua06List = [];
            this.results.govua07List = [];
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