import {defineStore} from 'pinia'
import Papa from 'papaparse';
import customAxios from "@/utils/customAxios.js";
import {useUserStore} from "@/stores/user.js";

// const userStore = useUserStore()

export const useFilesLeStore = defineStore('filesLeStore', {
    state: () => ({
        files: [

        ]
    }),
    actions: {

        fetchData() {
            customAxios.get('/uploader/le/files')
                .then(res => {
                    this.files = res.data;
                })
        }


    },
    getters: {
        sortedFiles: (state) => {
            return state.files.slice().sort((a, b) => new Date(b.createDate) - new Date(a.createDate));
        }
    }
});