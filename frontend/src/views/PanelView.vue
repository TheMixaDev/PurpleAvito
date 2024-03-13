<script setup>
import UITableRow from '@/components/ui/table/UITableRow.vue';
import UITableCell from '@/components/ui/table/UITableCell.vue';
import UIButton from '@/components/ui/UIButton.vue';

import TableComponent from '@/components/TableComponent.vue';

import { FrontendService } from '@/services/FrontendService';
import UIDropdownWithSearch from '@/components/ui/UIDropdownWithSearch.vue';
import { MatrixItem } from '@/models/matrixItem';
import UILabeledInput from '@/components/ui/UILabeledInput.vue';
import UISetupButton from '@/components/ui/UISetupButton.vue';
import { useSettingsStore } from '@/stores/settings';
import { useNewMatrixStore } from '@/stores/newMatrix';
import { useTreeStore } from '@/stores/tree';
import { MatrixService } from '@/services/MatrixService';
import UIProgressBar from '@/components/ui/UIProgressBar.vue';
import UILabeledCheckbox from '@/components/ui/UILabeledCheckbox.vue';
import { Loader } from '@/models/loader';
</script>
<template>
    <section>
        <ul class="text-sm font-medium text-center text-gray-500 rounded-lg shadow sm:flex dark:divide-gray-700 dark:text-gray-400">
            <UISetupButton :selected="true">
                Создание новой матрицы
            </UISetupButton>
        </ul>
        <div class="p-4">
            <div class="mx-auto max-w-screen-xl p-3 px-4 lg:px-12">
                <p class="mb-2 text-center">
                    Отсутствие родительской матрицы создает новую пустую скидочную матрицу.
                    <UIButton class="ml-2" color="danger" @click="NewMatrixStore.parentMatrix = 0" :disabled="!NewMatrixStore.parentMatrix">
                        Сбросить матрицу
                    </UIButton>
                    <UIButton class="ml-2" color="secondary" @click="NewMatrixStore.parentMatrix = SettingsStore.baseline">
                        Установить главную
                    </UIButton>
                </p>
                <UIDropdownWithSearch
                    v-model="NewMatrixStore.parentMatrix"
                    :options="SettingsStore.matrices"
                    >
                    Выберите родительскую матрицу
                </UIDropdownWithSearch>
                <UILabeledCheckbox class="mt-2" v-model="showReadable">
                    Отображать названия категорий и локаций
                </UILabeledCheckbox>
            </div>
            <TableComponent
                @searchApply="NewMatrixStore.applySearch"
                :searchEnabled="originFile == null"
                v-model:search="NewMatrixStore.search"
                :pagination="NewMatrixStore.pagination"
                @clearClick="clear"
                :clearEnabled="true"
                :clearState="(NewMatrixStore.items.length > 0 || originFile != null) && (!fileLoading && !matrixPublishing)"
                @creationClick="NewMatrixStore.createElement"
                :creationEnabled="originFile == null"
                @pageChange="NewMatrixStore.handleSegmentsPageChange"
                :loadCount="NewMatrixStore.pagination.itemsPerPage"
                :columns="[showReadable ? 'Категория' : 'ID категории', showReadable ? 'Локация' : 'ID локации', 'Цена', 'Действие']"
                :emptyText="originFile != null ? 'Предпросмотр недоступен' : 'Данные не найдены'"
                >
                <UITableRow v-for="(item, index) in NewMatrixStore.display" :key="index"
                    :class="!FrontendService.valueParser(item.microcategory_id)
                            || FrontendService.valueParser(item.microcategory_id) < TreeStore.minMicrocategory
                            || FrontendService.valueParser(item.microcategory_id) > TreeStore.maxMicrocategory
                            || !FrontendService.valueParser(item.location_id)
                            || FrontendService.valueParser(item.location_id) < TreeStore.minLocation
                            || FrontendService.valueParser(item.location_id) > TreeStore.maxLocation
                            ? `bg-danger-200` : ``">
                    <UITableCell>
                        <UIDropdownWithSearch
                            :options="TreeStore.microcategories"
                            v-model="item.microcategory_id"
                            v-if="showReadable">
                            Не выбрано
                        </UIDropdownWithSearch>
                        <UILabeledInput
                            v-else
                            v-model="item.microcategory_id"
                            type="number"
                            :disabled="matrixPublishing"
                            />
                    </UITableCell>
                    <UITableCell>
                        <UIDropdownWithSearch
                            :options="TreeStore.locations"
                            v-model="item.location_id"
                            v-if="showReadable">
                            Не выбрано
                        </UIDropdownWithSearch>
                        <UILabeledInput
                            v-else
                            v-model="item.location_id"
                            type="number"
                            :disabled="matrixPublishing"
                            />
                    </UITableCell>
                    <UITableCell>
                        <UILabeledInput
                            v-model="item.price"
                            type="number"
                            :disabled="matrixPublishing"
                            />
                    </UITableCell>
                    <UITableCell>
                        <UIButton color="danger" @click="NewMatrixStore.deleteElement(index)" :disabled="matrixPublishing">
                            <font-awesome-icon :icon="['fas', 'trash']"/>
                            Удалить
                        </UIButton>
                    </UITableCell>
                </UITableRow>
            </TableComponent>
            <div class="mx-auto max-w-screen-xl p-3 px-4 lg:px-12">
                <UIButton color="primary" class=" w-full z-0" @click="selectFile" :disabled="fileLoading || originFile != null || matrixPublishing">
                    <font-awesome-icon :icon="['fas', 'download']"/>
                    Загрузить из файла
                </UIButton>
                <UIButton color="secondary" class="mt-4 w-full z-0" :disabled="((NewMatrixStore.items.length < 1 || fileLoading) && originFile == null) || matrixPublishing" @click="createMatrix">
                    <font-awesome-icon :icon="['fas', 'plus']"/>
                    {{ (NewMatrixStore.parentMatrix != 0 ? `Клонировать` : `Создать`) }} матрицу ({{ (NewMatrixStore.parentMatrix != 0 && SettingsStore.matrices[NewMatrixStore.parentMatrix].startsWith("baseline") ? `Основная` : `Скидочная`) }})
                </UIButton>
                <p class="text-center mt-2 text-xl">
                    {{ (NewMatrixStore.parentMatrix != 0 ? `Измененных` : `Всего`) }}
                    <span v-if="originFile == null">
                        строк матрицы - {{ NewMatrixStore.items.length }}
                    </span>
                    <span v-else>
                        данных - {{ FrontendService.formatFileSize(originFile.size) }}
                    </span>
                </p>
                <UIProgressBar
                    v-if="matrixPublishing"
                    :percent="percentLoading">
                    <span v-if="formingFile">
                        Формирование файла...
                    </span>
                    <span v-else>
                        {{ !updatingDb ? `Загрузка матрицы на сервер...` : `Обновление базы данных...` }}
                    </span>
                </UIProgressBar>
                <UIProgressBar
                    v-if="fileLoading"
                    :percent="percentLoading">
                    Загрузка файла...
                </UIProgressBar>
            </div>
        </div>
    </section>
</template>

<script>
const regex = /\(\s*(\d+),\s*(\d+),\s*(\d+|null)\s*\)|(\d+),\s*(\d+),\s*(\d+|null)/;
const SettingsStore = useSettingsStore();
const NewMatrixStore = useNewMatrixStore();
const TreeStore = useTreeStore();
export default {
    name: "PanelView",
    data() {
        return {
            fileLoading: false,
            matrixPublishing: false,
            formingFile: false,
            originFile: null,
            
            percentLoading: 0,
            showReadable: true,
            updatingDb: false
        }
    },
    computed: {
        FrontendService() {
            return FrontendService
        },
        SettingsStore() {
            return SettingsStore
        },
        TreeStore() {
            return TreeStore
        }
    },
    methods: {
        /**
         * Opens a file dialog for the user to select a file, handles different cases based on file size and type,
         * parses the file contents, and updates the UI accordingly.
         */
        selectFile() {
            let input = document.createElement('input');
            input.type = 'file';
            input.onchange = e => {
                this.fileLoading = true;
                let file = e.target.files[0];
                let unlockLoading = () => this.fileLoading = false;
                if(file.size / (1024 * 1024) > 16) {
                    if(file.name.endsWith(".csv")) {
                        let setFile = () => {
                            NewMatrixStore.clear();
                            this.originFile = file;
                            unlockLoading();
                        };
                        FrontendService.showWarningModal(`Размер загруженного файла матрицы превышает 16 МБ.
                                                        Предпросмотр и изменение файла внутри интерфейса будут недоступны,
                                                        сам .csv файл будет загружен напрямую на сервер.`, () => {
                            if(NewMatrixStore.items.length > 0)
                                FrontendService.showWarningModal(`После загрузки большого файла текущие строки изменений будут удалены. Продолжить?`, setFile);
                            else setFile();
                        });
                        unlockLoading();
                    } else {
                        let convertNotify = () => {
                            this.$notify({type: 'error', text: 'Преобразуйте файл в формат .csv для корректной отправки на сервер'});
                            unlockLoading();
                        }
                        FrontendService.showWarningModal(`Размер загруженного файла матрицы превышает 16 МБ и при этом не является файлом формата .csv. Преобразуйте файл в формат .csv для корректной отправки на сервер.`, convertNotify, convertNotify);
                    }
                } else {
                    let reader = new FileReader();
                    this.percentLoading = 0;
                    reader.onload = async e => {
                        let contents = e.target.result;
                        let parsed = [];
                        let lines = 0;
                        let sucessful = 0;
                        let errors = [];
                        let lastPercentUpdate = Date.now();
                        try {
                            let json = JSON.parse(contents);
                            let total = json.length;
                            this.percentLoading = 12.5;
                            await FrontendService.updateUI();
                            for(let item of json) {
                                if(Date.now() - lastPercentUpdate > 10 || lines == total - 1) {
                                    this.percentLoading = Math.round((lines * 1000) / total / 8 + 125) / 10 / 8;
                                    await FrontendService.updateUI();
                                    lastPercentUpdate = Date.now();
                                }
                                let values = [];
                                lines++;
                                for(let key in item) {
                                    values.push(FrontendService.valueParser(item[key]));
                                    if(values.length == 3) break;
                                }
                                if(values.length >= 3) {
                                    sucessful++;
                                    parsed.push(new MatrixItem(values[0], values[1], values[2]));
                                } else errors.push(values);
                            }
                        }
                        catch {
                            let splitted = contents.split('\n');
                            let total = splitted.length;
                            for(let line of splitted) {
                                if(Date.now() - lastPercentUpdate > 10 || lines == total - 1) {
                                    this.percentLoading = Math.round((lines * 1000) / total / 4) / 10;
                                    await FrontendService.updateUI();
                                    lastPercentUpdate = Date.now();
                                }
                                lines++;
                                const matches = line.match(regex);
                                if(matches) {
                                    try {
                                        let microcategory_id = FrontendService.valueParser(matches[1] || matches[4]);
                                        let location_id = FrontendService.valueParser(matches[2] || matches[5]);
                                        let price = FrontendService.valueParser(matches[3] || matches[6]);
                                        parsed.push(new MatrixItem(microcategory_id, location_id, price));
                                        sucessful++;
                                    } catch {
                                        errors.push(line);
                                    }
                                }
                            }
                        }
                        let updateProgress = progress => {
                            this.percentLoading = Math.round(progress * 1000 / 4 * 3 + 250) / 10;
                        }
                        let unlockAndNotify = () => {
                            unlockLoading();
                            this.$notify({type: 'success', text: `Загружено ${sucessful} из ${lines} элементов`});
                        }
                        if(NewMatrixStore.items.length > 0) {
                            FrontendService.showFileModal(
                                () => {
                                    NewMatrixStore.addItems(parsed, unlockAndNotify, updateProgress);
                                }, () => {
                                    NewMatrixStore.clear();
                                    NewMatrixStore.addItems(parsed, unlockAndNotify, updateProgress);
                            });
                        } else NewMatrixStore.addItems(parsed, unlockAndNotify, updateProgress);
                    };
                    reader.readAsText(file);
                }
            };
            input.click();
        },
        /**
         * Redirects to an error with notification to the specific row in the table.
         *
         * @param {number} index - the index used to determine the error page
         */
        redirectToError(index) {
            this.$notify({type: 'error', text: `Некорректное значение в строке ${index * 1 + 1}`});
            NewMatrixStore.updateSearch("");
            NewMatrixStore.applySearch();
            NewMatrixStore.pagination.updatePage(Math.floor(index * 1 / 10) + 1);
            NewMatrixStore.handleSegmentsPageChange();
            this.matrixPublishing = false;
        },
        /**
         * A function that creates form files from the matrix items with progress updates.
         *
         * @param {Function} progress - a callback function to update progress
         * @return {File} the processed file object
         */
        async formFile(progress) {
            let result = [];
            let total = NewMatrixStore.items.length;
            let lastPercentUpdate = Date.now();
            for(let index in NewMatrixStore.items) {
                let loading = Math.round((index * 1000) / total) / 10;
                if(Date.now() - lastPercentUpdate > 10 || index == total - 1) {
                    progress(loading);
                    await FrontendService.updateUI();
                    lastPercentUpdate = Date.now();
                }
                let item = NewMatrixStore.items[index];
                try {
                    let microcategory_id = FrontendService.valueParser(item.microcategory_id);
                    let location_id = FrontendService.valueParser(item.location_id);
                    let price = FrontendService.valueParser(item.price);
                    if(!microcategory_id || !location_id)
                        return this.redirectToError(index);
                    result.push(`${microcategory_id},${location_id},${price}`);
                } catch {
                    return this.redirectToError(index);
                }
            }
            return (new File([new Blob([result.join("\n")], { type: 'text/plain' })], 'matrix.csv', { type: 'text/plain' }));
        },
        /**
         * A method to create a matrix with various steps including file handling and database updates.
         */
        createMatrix() {
            this.percentLoading = 0;
            this.matrixPublishing = true;
            this.formingFile = true;
            this.updatingDb = false;
            setTimeout((async () => {
                let file = this.originFile;
                if(file == null) {
                    await FrontendService.updateUI();
                    file = await this.formFile(progress => this.percentLoading = progress);
                }
                this.formingFile = false;
                this.percentLoading = 0;

                let loader = new Loader(data => {
                    if(data.type == 'success') {
                        data = data.data;
                        this.$notify({type: 'success', text: data.message});
                        SettingsStore.dropUpdate();
                        SettingsStore.updateSettings(() => {
                            NewMatrixStore.clear();
                            NewMatrixStore.parentMatrix = data.matrixName;
                            this.updatingDb = false;
                            this.matrixPublishing = false;
                        }, () => {
                            this.$notify({type: 'error', text: 'Произошла ошибка при обновлении данных'});
                            this.updatingDb = false;
                            this.matrixPublishing = false;
                        });
                    } else if(data.type == 'error') {
                        let error = data.data;
                        if(error.response.data && error.response.data.showModal)
                            FrontendService.showWarningModal(error.response.data.message);
                        this.$notify({type: 'error', text: 'Произошла ошибка при создании матрицы'});
                        this.updatingDb = false;
                        this.matrixPublishing = false;
                    }
                }, percent => {
                    this.percentLoading = Math.round(percent * 10) / 10
                }, 0.001 * file.size);
                MatrixService.createMatrix(NewMatrixStore.parentMatrix, file, data => {
                    loader.end({type: 'success', data: data});
                }, error => {
                    loader.end({type: 'error', data: error});
                }, event => {
                    this.percentLoading = Math.round((event.loaded * 1000) / event.total) / 10
                    if(this.percentLoading > 99) {
                        this.updatingDb = true;
                        this.percentLoading = 0;
                        loader.start();
                    }
                });
            }), 0);
        },
        clear() {
            if(this.originFile == null) {
                NewMatrixStore.clear();
            } else {
                FrontendService.showWarningModal(`Загруженные данные (${FrontendService.formatFileSize(this.originFile.size)}) будут удалены. Продолжить?`, () => {
                    this.originFile = null;
                });
            }
        }
    },
    beforeCreate() {
        SettingsStore.updateSettings();
    }
}
</script>