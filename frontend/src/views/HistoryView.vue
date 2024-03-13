<script setup>
import UITableRow from '@/components/ui/table/UITableRow.vue';
import UITableCell from '@/components/ui/table/UITableCell.vue';
import UISetupButton from '@/components/ui/UISetupButton.vue';
import UIButton from '@/components/ui/UIButton.vue';

import TableComponent from '@/components/TableComponent.vue';

import { useHistoryStore } from '@/stores/historyStore';
import { FrontendService } from '@/services/FrontendService';
import { useMatrixItemsStore } from '@/stores/matrixItemsStore';
</script>

<template>
    <ul class="text-sm font-medium text-center text-gray-500 rounded-lg shadow sm:flex dark:divide-gray-700 dark:text-gray-400">
        <UISetupButton :selected="true">
            История изменений
        </UISetupButton>
    </ul>
    <TableComponent
        :searchEnabled="false"
        :creationEnabled="false"
        :pagination="HistoryStore.pagination"
        :pageSelectorEnabled="true"
        v-model:pageSelector="pageSelector"
        @psProceed="pageSelectorProceed"
        :isLoading="loading"
        @pageChange="loadData"
        :loadCount="count"
        :columns="['Сегмент', 'Матрица', 'Дата и время', 'Действие']"
        >
        <UITableRow v-for="(value, key) in HistoryStore.items" :key="key">
            <UITableCell>
                {{ value.segmentId ? value.segmentId : 'Основная матрица' }}
            </UITableCell>
            <UITableCell>
                {{ value.name ? value.name : 'Сброшена' }}
            </UITableCell>
            <UITableCell>
                {{ FrontendService.formatDate(value.timestamp, true) }}
            </UITableCell>
            <UITableCell class="w-[180px]">
                <UIButton :disabled="!value.name" @click="goTo(value.name)">
                    <font-awesome-icon :icon="['fas', 'eye']"/>
                    Просмотреть
                </UIButton>
            </UITableCell>
        </UITableRow>
    </TableComponent>
</template>

<script>
const HistoryStore = useHistoryStore();
const MatrixItemsStore = useMatrixItemsStore();
export default {
    name: "HistoryView",
    data() {
        return {
            loading: false,
            count: -1,
            pageSelector: 1
        }
    },
    watch: {
        "HistoryStore.pagination.page"() {
            this.pageSelector = HistoryStore.pagination.page + 1;
        }
    },
    computed: {
        FrontendService() {
            return FrontendService;
        },
        HistoryStore() {
            return HistoryStore;
        }
    },
    methods: {
        loadData(ping = true) {
            this.loading = true;
            FrontendService.runDataUpdater(HistoryStore.updateItems, this, () => {
                this.loading = false;
            }, ping);
        },
        pageSelectorProceed() {
            if(!this.pageSelector) this.pageSelector = 1;
            HistoryStore.pagination.page = this.pageSelector - 1;
            this.loadData();
        },
        goTo(name) {
            MatrixItemsStore.matrix = name;
            MatrixItemsStore.updateItems(() => {
                this.$router.push({ name: 'matrix' });
            }, () => this.$notify({type: 'error', text: 'Произошла ошибка при получении матрицы'}));
        }
    },
    mounted() {
        this.loadData();
    }
}
</script>