<script setup>
import UITableRow from '@/components/ui/table/UITableRow.vue';
import UITableCell from '@/components/ui/table/UITableCell.vue';
import UIButton from '@/components/ui/UIButton.vue';
import UIDropdownWithSearch from '@/components/ui/UIDropdownWithSearch.vue';
import UISetupButton from '@/components/ui/UISetupButton.vue';
import UILabeledCheckbox from '@/components/ui/UILabeledCheckbox.vue';

import TableComponent from '@/components/TableComponent.vue';

import { useMatrixItemsStore } from '@/stores/matrixItemsStore';
import { useSettingsStore } from '@/stores/settings';
import { useTreeStore } from '@/stores/tree';
import { FrontendService } from '@/services/FrontendService';
import { useNewMatrixStore } from '@/stores/newMatrix';
import { MatrixItem } from '@/models/matrixItem';
</script>

<template>
    <ul class="text-sm font-medium text-center text-gray-500 rounded-lg shadow sm:flex dark:divide-gray-700 dark:text-gray-400">
        <UISetupButton :selected="true">
            Просмотр матриц
        </UISetupButton>
    </ul>
    <div class="mx-auto max-w-screen-xl p-3 px-4 lg:px-12">
        <UIDropdownWithSearch
            v-model="MatrixItemsStore.matrix"
            :options="SettingsStore.matrices"
            @changed="loadData"
            >
            Матрица не выбрана
        </UIDropdownWithSearch>
        <UILabeledCheckbox class="mt-2" v-model="showReadable">
            Отображать названия категорий и локаций
        </UILabeledCheckbox>
    </div>
    <TableComponent
        :searchEnabled="false"
        :creationEnabled="false"
        :pagination="MatrixItemsStore.pagination"
        :pageSelectorEnabled="true"
        v-model:pageSelector="pageSelector"
        @psProceed="pageSelectorProceed"
        :isLoading="loading"
        @pageChange="loadData"
        :loadCount="count"
        :columns="[showReadable ? 'Категория' : 'ID категории', showReadable ? 'Локация' : 'ID локации', 'Цена', 'Действие']"
        >
        <UITableRow v-for="(value, key) in MatrixItemsStore.items" :key="key">
            <UITableCell>
                {{ showReadable ? TreeStore.microcategories[value.microcategory_id] : value.microcategory_id }}
            </UITableCell>
            <UITableCell>
                {{ showReadable ? TreeStore.locations[value.location_id] : value.location_id }}
            </UITableCell>
            <UITableCell>
                {{ value.price }}
            </UITableCell>
            <UITableCell class="w-[150px]">
                <UIButton color="warning" @click="change(value)">
                    <font-awesome-icon :icon="['fas', 'pencil']"/>
                    Изменить
                </UIButton>
            </UITableCell>
        </UITableRow>
    </TableComponent>
</template>

<script>
const MatrixItemsStore = useMatrixItemsStore();
const SettingsStore = useSettingsStore();
const TreeStore = useTreeStore();
const NewMatrixStore = useNewMatrixStore();
export default {
    name: "MatrixView",
    data() {
        return {
            loading: false,
            count: -1,
            showReadable: true,
            pageSelector: 1,
        }
    },
    watch: {
        "MatrixItemsStore.pagination.page"() {
            this.pageSelector = this.MatrixItemsStore.pagination.page + 1;
        }
    },
    computed: {
        MatrixItemsStore() {
            return MatrixItemsStore;
        },
        SettingsStore() {
            return SettingsStore;
        },
        TreeStore() {
            return TreeStore;
        }
    },
    methods: {
        loadData(ping = true) {
            this.loading = true;
            FrontendService.runDataUpdater(MatrixItemsStore.updateItems, this, () => {
                this.loading = false;
            }, ping);
        },
        pageSelectorProceed() {
            MatrixItemsStore.pagination.page = this.pageSelector - 1;
            this.loadData();
        },
        change(value) {
            NewMatrixStore.addItems([new MatrixItem(value.microcategory_id, value.location_id, value.price)], () => {
                NewMatrixStore.parentMatrix = MatrixItemsStore.matrix;
                this.$router.push({ name: 'panel' });
            }, () => {});
        }
    },
    beforeCreate() {
        SettingsStore.updateSettings();
    }
}
</script>