<script setup>
import UIButton from '@/components/ui/UIButton.vue';
import UIDropdownWithSearch from '@/components/ui/UIDropdownWithSearch.vue';
import UISetupButton from '@/components/ui/UISetupButton.vue';

import UITableRow from '@/components/ui/table/UITableRow.vue';
import UITableCell from '@/components/ui/table/UITableCell.vue';

import TableComponent from '@/components/TableComponent.vue';
import { Pagination } from '@/models/pagination';
import { Segment } from '@/models/segment';
</script>
<template>
    <ul class="text-sm font-medium text-center text-gray-500 rounded-lg shadow sm:flex dark:divide-gray-700 dark:text-gray-400">
        <UISetupButton :selected="selectedPanel == 0" @click="selectPanel(0)">
            Основная матрица цен
        </UISetupButton>
        <UISetupButton  :selected="selectedPanel == 1" @click="selectPanel(1)">
            Сегментные скидочные матрицы
        </UISetupButton>
    </ul>
    <div class="p-4">
        <div v-if="selectedPanel == 0">
            <UIDropdownWithSearch
                v-model="currentMatrix"
                :options="matrixes"
                >
                Матрица не выбрана
            </UIDropdownWithSearch>
            <UIButton class="mt-2 w-full">
                Применить изменения
            </UIButton>
        </div>
        <div v-if="selectedPanel == 1">
            <TableComponent
                @searchApply="loadData"
                :creationEnabled="false"
                :pagination="pagination"
                :isLoading="false"
                @pageChange="loadData"
                :loadCount="pagination"
                :columns="['ID сегмента', 'Матрица цен', 'Действия']"
                >
                <UITableRow v-for="(item, index) in items" :key="index">
                    <UITableCell class="w-[110px]">
                        {{ item.id }}
                    </UITableCell>
                    <UITableCell>
                        <UIDropdownWithSearch
                            v-model="item.name"
                            :options="matrixesDiscount"
                            @changed="registerChange(item)"
                            >
                            Матрица не выбрана
                        </UIDropdownWithSearch>
                    </UITableCell>
                    <UITableCell class="min-w-[160px] w-[160px]">
                        <UIButton color="primary" @click="useCategory(category)" :disabled="!item.changed">
                            <font-awesome-icon :icon="['fas', 'bolt']"/>
                            Применить
                        </UIButton>
                    </UITableCell>
                </UITableRow>
            </TableComponent>
            <div class="mx-auto max-w-screen-xl p-3 px-4 lg:px-12">
                <UIButton color="primary" class="w-full z-0" :disabled="!changed">
                    <font-awesome-icon :icon="['fas', 'bolt']"/>
                    Применить все
                </UIButton>
            </div>
        </div>
    </div>
</template>

<script>
let pagination = new Pagination(0, 10, 0);
pagination.total = 3;
pagination.itemsPerPage = 10;
export default {
    name: "SetupView",
    data() {
        return {
            loading: false,
            selectedPanel: 0,

            currentMatrix: 0,
            matrixes: {
                "baseline_matrix_1": "baseline_matrix_1",
                "baseline_matrix_2": "baseline_matrix_2",
            },
            currentSegmentMatrix: 0,
            matrixesDiscount: {
                "discount_matrix_1": "discount_matrix_1",
                "discount_matrix_2": "discount_matrix_2",
                "discount_matrix_3": "discount_matrix_3",
                "discount_matrix_4": "discount_matrix_4",
                "discount_matrix_5": "discount_matrix_5",
                "discount_matrix_6": "discount_matrix_6",
                "discount_matrix_7": "discount_matrix_7",
                "discount_matrix_8": "discount_matrix_8",
                "discount_matrix_9": "discount_matrix_9",
                "discount_matrix_10": "discount_matrix_10",
            },
            items: [
                new Segment(1, "discount_matrix_1"),
                new Segment(2, "discount_matrix_2"),
                new Segment(3, null),
            ],
            changed: false
        }
    },
    methods: {
        loadData() {
            this.loading = true;
            /*FrontendService.runDataUpdater(CategoryStore.updateCategories, this, () => {
                this.loading = false;
            });*/
        },
        selectPanel(id) {
            this.selectedPanel = id;
        },
        registerChange(item) {
            item.changed = true;
            this.changed = true;
        }
    },
    mounted() {
        this.loadData();
    }
}
</script>