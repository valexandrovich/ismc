import {defineStore} from 'pinia'
import Papa from 'papaparse';
import customAxios from "@/utils/customAxios.js";
import * as XLSX from "xlsx";
import {v4 as uuidv4} from "uuid";
import {dateToShortDateStr, excelDateToDate} from "@/utils/convertor.js";
import {
    containsCyrillicLetters,
    containsDigits,
    containsLatinLetters,
    containsRuLetters,
    containsSpaces,
    containsSpecialCharsName,
    containsUaLetters, idPassportNumberFormat, idPassportRecordFormat,
    innFormat, intPassportNumberFormat, intPassportSerialFormat,
    localPassportNumberFormat,
    localPassportSerialFormat, passportIssuerCodeFormat,
    shortDateFormat,
} from "@/utils/data.validation.rules.js";

export const useUploadLeStore = defineStore('uploadLeStore', {
    state: () => ({
        dateFields: ['foundationDate',  'markEventDate', 'markStartDate', 'markEndDate'],

        validationRules: {
            edrpou: [
                innFormat
            ],

            foundationDate: [
                shortDateFormat
            ],

            markEventDate: [shortDateFormat],
            markStartDate: [shortDateFormat],
            markEndDate: [shortDateFormat]
        },


        fileName: '',
        author: '',
        currentPage: 1,
        itemsPerPage: 5,
        isFiltered: false,
        data: [
        ],
        tagTypes: []
    }),
    actions: {

        async loadData(file) {
            const tagTypes = await customAxios.get('/enricher/tag-types')
            this.tagTypes = tagTypes.data

            return new Promise((resolve, reject) => {
                const reader = new FileReader();

                reader.onload = (e) => {
                    try {





                        const data = new Uint8Array(e.target.result);
                        const workbook = XLSX.read(data, {type: 'array'});
                        const sheetName = workbook.SheetNames[0];
                        const worksheet = workbook.Sheets[sheetName];

                        // Read the JSON data as a 2D array
                        const jsonData = XLSX.utils.sheet_to_json(worksheet, {header: 1});

                        const keys = jsonData[0];

                        // Skip excel header rows
                        const dataRows = jsonData.slice(3).map(row => {
                            const rowObject = {};
                            keys.forEach((key, index) => {
                                rowObject[key] = row[index] === undefined || row[index] === null ? '' : row[index];
                            });
                            return rowObject;
                        });


                        // Converting Excel dates to dd.mm.yyyy
                        dataRows.forEach(row => {
                            this.dateFields.forEach(field => {
                                if (row[field]) {
                                    const dateStr = dateToShortDateStr(excelDateToDate(row[field]))
                                    if (!dateStr.includes('NaN')) {
                                        row[field] = dateStr;
                                    }
                                }
                            })
                        });

                        // Converting all number values to string
                        dataRows.forEach(row => {
                            for (const key in row) {
                                if (typeof row[key] === 'number') {
                                    row[key] = row[key].toString();
                                }
                            }
                        });


                        // Adding uuid to rows
                        dataRows.forEach(row => {
                            row['id'] = uuidv4()
                        });


                        // Adding errors[] to rows
                        dataRows.forEach(row => {
                            row['errors'] = {}
                        });


                        // validate data
                        dataRows.forEach(row => {
                            this.validateRow(row);
                            // row.errors = this.validateRow(row)
                        })



                        this.data = dataRows





                        resolve();
                    } catch (error) {
                        reject(error);
                    }
                };
                reader.readAsArrayBuffer(file);
            });
        },

        validateRow(row) {
            row.errors = {};
            const keys = Object.keys(row);
            const errors = row.errors;
            keys.forEach(key => {
                if (row[key] === '') {
                    return;
                }
                if (this.validationRules[key]) {
                    this.validationRules[key].forEach(({rule, message, mask}) => {
                        const isValid = rule.test(row[key]); // not mask FALSE is OK   MASK - TRUE IS OK
                        if ((mask && !isValid) || (!mask && isValid)) {
                            if (!errors[key]) {
                                errors[key] = [];
                            }
                            errors[key].push(message);
                        }
                    });
                }
            });

            // validete mk code
            if (row.markId) {
                // console.log('validating markId')
                // console.log(row.markId)
                // console.log(this.tagTypes)
                const markType = this.tagTypes.find(tagType => tagType.code === row.markId);
                if (!markType) {
                    if (!errors['markId']) {
                        errors['markId'] = [];
                    }
                    // console.log('MKID ERROR')
                    errors['markId'].push('Такого типу мітки не існує!');
                }
            }

        },

        // validateRow(row) {
        //     const keys = Object.keys(row);
        //     const errors = row.errors
        //     keys.forEach(key => {
        //         if (this.validationRules[key]) {
        //             this.validationRules[key].forEach(({rule, message, mask}) => {
        //                 const isValid = rule.test(row[key]);  // not mask FALSE is OK   MASK - TRUE IS OK
        //                 if ((mask && !isValid) || (!mask && isValid)) {
        //                     if (!row.errors[key]) {
        //                         row.errors[key] = [];
        //                     }
        //                     row.errors[key].push(message);
        //                 }
        //             })
        //         }
        //     })
        //     return errors
        // },

        removeById(id) {
            this.data = this.data.filter(item => item.id !== id);
        },


        generateCSV() {
            const dataWithoutValidationStatuses = this.data.map(({errors, ...item}) => item);
            return Papa.unparse(dataWithoutValidationStatuses);
        },
        async uploadData() {
            try {

                // Generate CSV
                const csv = this.generateCSV();

                // Create a Blob from the CSV string
                const blob = new Blob([csv], {type: 'text/csv'});

                // Create a FormData object and append the Blob
                const formData = new FormData();
                formData.append('author', this.author);
                formData.append('rowsCount', this.data.length);
                formData.append('file', blob, this.fileName + '.csv');

                // const body = {
                //     "userName": "test",
                //     "fileName": "testFile",
                //     rows: this.data,
                // }

                // Send data to backend
                await customAxios.post('/uploader/le/upload', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });
            } catch (error) {
                console.error('Error sending data to backend:', error);
            }
        },
        setPage(page) {
            this.currentPage = page;
        },
        setItemsPerPage(itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
        },
        updateRow(rowId, updatedRow) {
            const index = this.data.findIndex(item => item.id === rowId);
            if (index !== -1) {
                this.data[index] = {...this.data[index], ...updatedRow};
                this.validateRow(this.data[index]); // Re-validate the updated row
                return this.data[index]; // Return the updated row
            }
            return null;
        }
    },
    getters: {


        totalRows(state) {
            if (state.isFiltered) {
                return state.data.filter(row => Object.keys(row.errors).length > 0).length;
            } else {
                return state.data.length;
            }
        },

        paginatedData(state) {
            return () => {

                // state.isFiltered = filterOnlyInvalid


                let filteredData = state.data;


                if (state.isFiltered) {
                    filteredData = state.data.filter(row =>
                        Object.keys(row.errors).length > 0
                    );
                }

                // Apply pagination after filtering
                const start = (state.currentPage - 1) * state.itemsPerPage;
                const end = start + state.itemsPerPage;

                return filteredData.slice(start, end);
            };
        },

        //
        totalPages(state) {
            return Math.ceil(this.totalRows / state.itemsPerPage);
        },

        errors(state) {
            return state.data.filter(row => Object.keys(row.errors).length > 0).length;
        },

        allIsValid(state) {
            return state.data.every(row => Object.keys(row.errors).length === 0);
        }

    }
});