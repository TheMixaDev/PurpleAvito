<script setup>
import UIButton from '@/components/ui/UIButton.vue';
import UIDropdownWithSearch from '@/components/ui/UIDropdownWithSearch.vue';

import UITableRow from '@/components/ui/table/UITableRow.vue';
import UITableCell from '@/components/ui/table/UITableCell.vue';

import TableComponent from '@/components/TableComponent.vue';

import { useSettingsStore } from '@/stores/settings';
import { MatrixService } from '@/services/MatrixService';
</script>

<template>
    <TableComponent
        @searchApply="SettingsStore.segments.applySearch"
        v-model:search="SettingsStore.segments.search"
        :creationEnabled="false"
        :pagination="SettingsStore.segments.pagination"
        :isLoading="false"
        @pageChange="SettingsStore.segments.handleSegmentsPageChange"
        :loadCount="SettingsStore.segments.pagination.itemsPerPage"
        :columns="['ID сегмента', 'Матрица цен', 'Действия']"
        >
        <UITableRow v-for="(segment, index) in SettingsStore.segments.display" :key="index">
            <UITableCell class="w-[110px]">
                {{ segment.id }}
            </UITableCell>
            <UITableCell>
                <UIDropdownWithSearch
                    v-model="segment.name"
                    :options="SettingsStore.discountMatrices"
                    @changed="registerChange(segment)"
                    >
                    Матрица не выбрана
                </UIDropdownWithSearch>
            </UITableCell>
            <UITableCell class="min-w-[210px] w-[210px]">
                <UIButton color="primary" @click="setSegment(segment)" :disabled="!segment.changed">
                    <font-awesome-icon :icon="['fas', 'bolt']"/>
                    Применить
                </UIButton>
                <UIButton class="ml-2" color="danger" @click="removeMatrix(segment)" :disabled="!segment.name">
                    <font-awesome-icon :icon="['fas', 'trash']"/>
                </UIButton>
            </UITableCell>
        </UITableRow>
    </TableComponent>
    <div class="mx-auto max-w-screen-xl p-3 px-4 lg:px-12">
        <UIButton color="primary" class="w-full z-0" :disabled="!changed" @click="setAllSegments">
            <font-awesome-icon :icon="['fas', 'bolt']"/>
            Применить все
        </UIButton>
    </div>
</template>

<script>
const SettingsStore = useSettingsStore();
export default {
    name: "SetupSegmentsComponent",
    data() {
        return {
            changed: false
        }
    },
    computed: {
        SettingsStore() {
            return SettingsStore;
        }
    },
    methods: {
        registerChange(item) {
            item.changed = true;
            this.changed = true;
        },
        removeMatrix(segment) {
            segment.name = null;
            this.registerChange(segment);
        },
        checkIsChanged() {
            this.changed = SettingsStore.segments.segments.some(item => item.changed);
        },
        setSegment(segment) {
            MatrixService.setSegment(segment.id, segment.name, () => {
                segment.changed = false;
                this.checkIsChanged();
                this.$notify({type: 'success', text: 'Сегмент обновлен'});
            }, error => this.$notify({type: 'error', text: error.response.data.message}));
        },
        setAllSegments() {
            let updating = SettingsStore.segments.segments
                .filter(item => item.changed)
                .map(item => ({ id: item.id, name: item.name }));
            if (updating.length > 0) {
                MatrixService.setSegments(updating, () => {
                    this.forceUpdate();
                    this.$notify({type: 'success', text: 'Сегменты обновлены'});
                }, error => this.$notify({type: 'error', text: error.response.data.message}));
            }
        },
        forceUpdate() {
            SettingsStore.dropUpdate();
            SettingsStore.updateSettings();
            this.changed = false;
        }
    }
}
</script>