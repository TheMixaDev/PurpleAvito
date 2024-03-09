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
</script>
<template>
    <section>
        <div>
            Создать новую матрицу:
            <UIDropdownWithSearch
                v-model="parentMatrix"
                :options="unitedMatrixes"
                >
                Выберите матрицу наследования
            </UIDropdownWithSearch>
            <TableComponent
                @searchApply="loadData"
                :pagination="pagination"
                :isLoading="false"
                @pageChange="loadData"
                :loadCount="pagination"
                :columns="['ID категории', 'ID локации', 'Цена', 'Действие']"
                >
                <UITableRow v-for="(item, index) in items" :key="index">
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
                        <UIButton color="danger" @click="useCategory(category)">
                            <font-awesome-icon :icon="['fas', 'trash']"/>
                            Удалить
                        </UIButton>
                    </UITableCell>
                </UITableRow>
            </TableComponent>
            <UIButton>Загрузить из файла</UIButton>
            <UIButton>Создать мартицу</UIButton>
        </div>
    </section>
</template>

<script>
let pagination = new Pagination(0, 10, 0);
pagination.total = 3;
pagination.itemsPerPage = 10;
export default {
    name: "PanelView",
    data() {
        return {
            loading: false,

            parentMatrix: 0,
            unitedMatrixes: {
                "none": "Пустая матрица",
                "baseline_matrix_1": "baseline_matrix_1",
                "baseline_matrix_2": "baseline_matrix_2",
                "discount_matrix_1": "discount_matrix_1",
                "discount_matrix_2": "discount_matrix_2"
            },
            items: [
                new MatrixItem(13, 14, 71),
                new MatrixItem(25, 22, 122)
            ]
        }
    },
    computed: {
        FrontendService() {
            return FrontendService
        }
    },
    methods: {
        loadData() {
            this.loading = true;
            /*FrontendService.runDataUpdater(CategoryStore.updateCategories, this, () => {
                this.loading = false;
            });*/
        }
    },
    mounted() {
        this.loadData();
    }
}
</script>