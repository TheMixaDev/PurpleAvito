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
import { MatrixService } from '@/services/MatrixService';
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
                    <UIButton class="ml-2" color="danger" @click="parentMatrix = 0" :disabled="!parentMatrix">
                        Сбросить матрицу
                    </UIButton>
                    <UIButton class="ml-2" color="secondary" @click="parentMatrix = SettingsStore.baseline">
                        Установить главную
                    </UIButton>
                </p>
                <UIDropdownWithSearch
                    v-model="parentMatrix"
                    :options="SettingsStore.matrices"
                    >
                    Выберите родительскую матрицу
                </UIDropdownWithSearch>
            </div>
            <TableComponent
                @searchApply="NewMatrixStore.applySearch"
                v-model:search="NewMatrixStore.search"
                :pagination="NewMatrixStore.pagination"
                :clearEnabled="true"
                :clearState="NewMatrixStore.items.length > 0"
                @clearClick="NewMatrixStore.clear"
                @creationClick="NewMatrixStore.createElement"
                @pageChange="NewMatrixStore.handleSegmentsPageChange"
                :loadCount="NewMatrixStore.pagination.itemsPerPage"
                :columns="['ID категории', 'ID локации', 'Цена', 'Действие']"
                >
                <UITableRow v-for="(item, index) in NewMatrixStore.display" :key="index"
                    :class="!item.microcategory_id || item.microcategory_id.length < 1 || item.microcategory_id == '0' ||
                            !item.location_id || item.location_id.length < 1 || item.location_id == '0' ? `bg-danger-200` : ``">
                    <UITableCell>
                        <UILabeledInput
                            v-model="item.microcategory_id"
                            type="number"
                            :disabled="matrixPublishing"
                            />
                    </UITableCell>
                    <UITableCell>
                        <UILabeledInput
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
                <p class="text-center">
                    {{ (parentMatrix != 0 ? `Измененных` : `Всего`) }} строк матрицы - {{ NewMatrixStore.items.length }}
                </p>
                <UIButton color="primary" class="mt-4 w-full z-0" @click="selectFile" :disabled="fileLoading || matrixPublishing">
                    <font-awesome-icon :icon="['fas', 'download']"/>
                    Загрузить из файла
                </UIButton>
                <UIButton color="secondary" class="mt-4 w-full z-0" :disabled="NewMatrixStore.items.length < 1 || fileLoading || matrixPublishing" @click="createMatrix">
                    <font-awesome-icon :icon="['fas', 'plus']"/>
                    {{ (parentMatrix != 0 ? `Клонировать` : `Создать`) }} матрицу ({{ (parentMatrix != 0 && SettingsStore.matrices[parentMatrix].startsWith("baseline") ? `Основная` : `Скидочная`) }})
                </UIButton>
            </div>
        </div>
    </section>
</template>

<script>
const SettingsStore = useSettingsStore();
const NewMatrixStore = useNewMatrixStore();
export default {
    name: "PanelView",
    data() {
        return {
            loading: false,
            fileLoading: false,
            matrixPublishing: false,

            parentMatrix: 0
        }
    },
    computed: {
        FrontendService() {
            return FrontendService
        },
        SettingsStore() {
            return SettingsStore
        }
    },
    methods: {
        selectFile() {
            let input = document.createElement('input');
            input.type = 'file';
            input.onchange = e => {
                this.fileLoading = true;
                let file = e.target.files[0];
                let reader = new FileReader();
                reader.onload = e => {
                    let contents = e.target.result;
                    let parsed = [];
                    let lines = 0;
                    let sucessful = 0;
                    let errors = [];
                    try {
                        let json = JSON.parse(contents);
                        for(let item of json) {
                            let values = [];
                            lines++;
                            for(let key in item) {
                                values.push(FrontendService.valueParser(item[key]));
                                if(values.length == 3)
                                    break;
                            }
                            if(values.length >= 3) {
                                sucessful++;
                                parsed.push(new MatrixItem(values[0], values[1], values[2]));
                            } else {
                                errors.push(values);
                                // TODO errors notification?
                            }
                        }
                    }
                    catch {
                        const regex = /\(\s*(\d+),\s*(\d+),\s*(\d+)\s*\)|(\d+),\s*(\d+),\s*(\d+)/;
                        for(let line of contents.split('\n')) {
                            const matches = line.match(regex);
                            if(matches) {
                                lines++;
                                try {
                                    let microcategory_id = FrontendService.valueParser(matches[1] || matches[4]);
                                    let location_id = FrontendService.valueParser(matches[2] || matches[5]);
                                    let price = FrontendService.valueParser(matches[3] || matches[6]);
                                    parsed.push(new MatrixItem(microcategory_id, location_id, price));
                                    sucessful++;
                                } catch {
                                    errors.push(line);
                                    // TODO errors notification?
                                }
                            }
                        }
                    }
                    this.$notify({type: 'success', text: `Загружено ${sucessful} из ${lines} элементов`});
                    if(NewMatrixStore.items.length > 0) {
                        FrontendService.showFileModal(
                            () => {
                                NewMatrixStore.addItems(parsed, () => this.fileLoading = false);
                            }, () => {
                                NewMatrixStore.clear();
                                NewMatrixStore.addItems(parsed, () => this.fileLoading = false);
                        });
                    } else {
                        NewMatrixStore.addItems(parsed, () => this.fileLoading = false);
                    }
                };
                reader.readAsText(file);
            };
            input.click();
        },
        redirectToError(index) {
            this.$notify({type: 'error', text: `Некорректное значение в строке ${index * 1 + 1}`});
            NewMatrixStore.updateSearch("");
            NewMatrixStore.applySearch();
            NewMatrixStore.pagination.updatePage(Math.floor(index * 1 / 10) + 1);
            NewMatrixStore.handleSegmentsPageChange();
            this.matrixPublishing = false;
        },
        createMatrix() {
            this.matrixPublishing = true;
            setTimeout((async () => {
                let result = [];
                for(let index in NewMatrixStore.items) {
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
                MatrixService.createMatrix(this.parentMatrix, result.join("\n"), data => {
                    this.$notify({type: 'success', text: data.message});
                    SettingsStore.dropUpdate();
                    SettingsStore.updateSettings(() => {
                        NewMatrixStore.clear();
                        this.parentMatrix = data.matrixName;
                        this.matrixPublishing = false;
                    }, () => {
                        this.$notify({type: 'error', text: 'Произошла ошибка при обновлении данных'});
                        this.matrixPublishing = false;
                    });
                }, () => {
                    this.$notify({type: 'error', text: 'Произошла ошибка при создании матрицы'});
                    this.matrixPublishing = false;
                })
            }), 0);
        }
    },
    beforeCreate() {
        SettingsStore.updateSettings();
    }
}
</script>