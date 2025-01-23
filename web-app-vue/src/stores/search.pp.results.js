import {defineStore} from 'pinia'

export const useSearchPpResultsStore = defineStore('searchPpResults', {
    state: () => ({
        currentPage: 1,
        itemsPerPage: 2,
        searchForm: {
            "params": {
                "passportType": "1",
                "isSimpleName": false
            },
            "simpleName": null,
            "lastName": null,
            "firstName": null,
            "patronymicName": null,
            "birthday": null,
            "inn": "2538100753",
            "localPassportSerial": null,
            "localPassportNumber": null,
            "intPassportSerial": null,
            "intPassportNumber": null,
            "idPassportRecordNumber": null,
            "idPassportNumber": null,
            "address": null,
            "phone": null
        },
        results: {
            "govua01List": [

            ],
            "govua06List": [

            ],
            "govua07List": [

            ],
            "govua08List": [

            ],
            "govua09List": [

            ],
            "govua10List": [
],
            "govua11List": [

            ],
            "govua12List": [

            ],
            "govua13List": [

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
            this.results.govua08List = [];
            this.results.govua09List = [];
            this.results.govua10List = [];
            this.results.govua11List = [];
            this.results.govua12List = [];
            this.results.govua13List = [];
        },
        setPage(page) {
            this.currentPage = page;
        },
        setItemsPerPage(itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
        }
    },
    getters: {
        paginatedData(state) {
            const start = (state.currentPage - 1) * state.itemsPerPage;
            const end = start + state.itemsPerPage;
            return state.data.slice(start, end);
        },
        totalPages(state) {
            return Math.ceil(state.data.length / state.itemsPerPage);
        },

        isResultsEmpty: (state) => {
            if (!state.results || Object.keys(state.results).length === 0) {
                return true;
            }
            return Object.values(state.results).every(array => Array.isArray(array) && array.length === 0);
        },

        getResults: (state) => state.results
    }
});