<script setup>
import UITableRow from '@/components/ui/table/UITableRow.vue';
import UITableCell from '@/components/ui/table/UITableCell.vue';
import UIButton from '@/components/ui/UIButton.vue';

import TableComponent from '@/components/TableComponent.vue';

import { FrontendService } from '@/services/FrontendService';
import UIDropdownWithSearch from '@/components/ui/UIDropdownWithSearch.vue';
import { Pagination } from '@/models/pagination';
import { MatrixItem } from '@/models/matrixItem';
import UILabeledInput from '@/components/ui/UILabeledInput.vue';
import UISetupButton from '@/components/ui/UISetupButton.vue';
import { useSettingsStore } from '@/stores/settings';
import { useNewMatrixStore } from '@/stores/newMatrix';
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
                    Отсутствие родительской матрицы создает новую пустую матрицу.
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
                @clearClick="NewMatrixStore.clear"
                @creationClick="NewMatrixStore.createElement"
                @pageChange="NewMatrixStore.handleSegmentsPageChange"
                :loadCount="NewMatrixStore.pagination.itemsPerPage"
                :columns="['ID категории', 'ID локации', 'Цена', 'Действие']"
                >
                <UITableRow v-for="(item, index) in NewMatrixStore.display" :key="index">
                    <UITableCell>
                        <UILabeledInput
                            v-model="item.microcategory_id"
                            type="number"
                            />
                    </UITableCell>
                    <UITableCell>
                        <UILabeledInput
                            v-model="item.location_id"
                            type="number"
                            />
                    </UITableCell>
                    <UITableCell>
                        <UILabeledInput
                            v-model="item.price"
                            type="number"
                            />
                    </UITableCell>
                    <UITableCell>
                        <UIButton color="danger" @click="NewMatrixStore.deleteElement(index)">
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
                <UIButton color="primary" class="mt-4 w-full z-0" @click="selectFile">
                    <font-awesome-icon :icon="['fas', 'download']"/>
                    Загрузить из файла
                </UIButton>
                <UIButton color="secondary" class="mt-4 w-full z-0" :disabled="NewMatrixStore.items.length < 1">
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
let pagination = new Pagination(0, 10, 0);
pagination.total = 100;
pagination.itemsPerPage = 10;
let locked = true; // TODO make loading with it
export default {
    name: "PanelView",
    data() {
        return {
            loading: false,

            parentMatrix: 0,
            items: [
                new MatrixItem(13, 14, 71),
                new MatrixItem(25, 22, 122)
            ]
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
                            () => NewMatrixStore.addItems(parsed), () => {
                                NewMatrixStore.clear();
                                NewMatrixStore.addItems(parsed);
                        });
                    } else
                        NewMatrixStore.addItems(parsed);
                };
                reader.readAsText(file);
            };
            input.click();
        }
    },
    beforeCreate() {
        SettingsStore.updateSettings(() => {
            locked = false;
        });
    }
}
</script>