<script setup>
import UITableRow from '@/components/ui/table/UITableRow.vue';
import UITableCell from '@/components/ui/table/UITableCell.vue';
import UIButton from '@/components/ui/UIButton.vue';

import TableComponent from '@/components/TableComponent.vue';

import { useCategoryStore } from '@/stores/category';
import { FrontendService } from '@/services/FrontendService';
</script>
<template>
    <section>
        <TableComponent
            v-model:search="CategoryStore.search"
            @searchApply="loadData"
            :pagination="CategoryStore.pagination"
            :isLoading="loading"
            @pageChange="loadData"
            :loadCount="CategoryStore.pagination.total"
            :columns="['ID', 'Название', 'Выбрать']"
            >
            <UITableRow v-for="category in CategoryStore.display.children" :key="category.ID">
                <UITableCell class="w-[45px]">{{ category.id }}</UITableCell>
                <UITableCell>{{ category.name }}</UITableCell>
                <UITableCell class="w-[140px]">
                    <UIButton color="primary" @click="useCategory(category)">
                        <font-awesome-icon :icon="['fas', 'bolt']"/>
                        Выбрать
                    </UIButton>
                </UITableCell>
            </UITableRow>
        </TableComponent>
    </section>
</template>

<script>
const CategoryStore = useCategoryStore();

export default {
    name: "PanelView",
    data() {
        return {
            loading: false,

            count: -1
        }
    },
    computed: {
        CategoryStore() {
            return CategoryStore
        },
        FrontendService() {
            return FrontendService
        }
    },
    watch: {
        "CategoryStore.search"() {
            CategoryStore.setSearch(CategoryStore.search);
        }
    },
    methods: {
        loadData() {
            this.loading = true;
            FrontendService.runDataUpdater(CategoryStore.updateCategories, this, () => {
                this.loading = false;
            });
        }
    },
    mounted() {
        FrontendService.validateUser(this.$cookies, this.loadData);
    }
}
</script>