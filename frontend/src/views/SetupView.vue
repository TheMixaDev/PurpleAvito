<script setup>
import UIButton from '@/components/ui/UIButton.vue';
import UIDropdownWithSearch from '@/components/ui/UIDropdownWithSearch.vue';
import UISetupButton from '@/components/ui/UISetupButton.vue';

import UITableRow from '@/components/ui/table/UITableRow.vue';
import UITableCell from '@/components/ui/table/UITableCell.vue';

import TableComponent from '@/components/TableComponent.vue';

import { useSettingsStore } from '@/stores/settings';
import { MatrixService } from '@/services/MatrixService';
import { FrontendService } from '@/services/FrontendService';
</script>
<template>
    <ul class="text-sm font-medium text-center text-gray-500 rounded-lg shadow sm:flex dark:divide-gray-700 dark:text-gray-400">
        <UISetupButton :selected="selectedPanel == 0" @click="selectPanel(0)">
            Основная матрица цен
        </UISetupButton>
        <UISetupButton :selected="selectedPanel == 1" @click="selectPanel(1)">
            Сегментные скидочные матрицы
        </UISetupButton>
    </ul>
    <div class="p-4">
        <div v-if="selectedPanel == 0" class="animate-fade animate-once animate-duration-500 animate-ease-out">
            <UIDropdownWithSearch
                v-model="SettingsStore.baseline"
                :options="SettingsStore.baselineMatrices"
                @changed="registerChangeMain"
                >
                Матрица не выбрана
            </UIDropdownWithSearch>
            <UIButton class="mt-2 w-full" :disabled="!mainChanged" @click="changeMain">
                Применить изменения
            </UIButton>
        </div>
        <div v-if="selectedPanel == 1">
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
        </div>
    </div>
</template>

<script>
const SettingsStore = useSettingsStore();
let locked = true; // TODO make loading with it
export default {
    name: "SetupView",
    data() {
        return {
            selectedPanel: 0,
            changed: false,
            mainChanged: false
        }
    },
    computed: {
        SettingsStore() {
            return SettingsStore;
        }
    },
    methods: {
        registerChangeMain() {
            this.mainChanged = true;
        },
        changeMain() {
            FrontendService.showWarningModal(`Вы уверены, что хотите изменить текущую главную ценовую матрицу?`, () => {
                MatrixService.setBaseline(SettingsStore.baseline, () => {
                    this.forceUpdate();
                    this.$notify({type: 'success', text: 'Главная ценовая матрица обновлена'});
                }, () => this.$notify({type: 'error', text: 'Произошла ошибка при обновлении главной ценовой матрицы'}));
            })
        },
        selectPanel(id) {
            this.selectedPanel = id;
        },
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
            // TODO TEST
            MatrixService.setSegment(segment.id, segment.name, () => {
                segment.changed = false;
                this.checkIsChanged();
                this.$notify({type: 'success', text: 'Сегмент обновлен'});
            }, () => this.$notify({type: 'error', text: 'Произошла ошибка при обновлении сегмента'}));
        },
        setAllSegments() {
            // TODO TEST
            let updating = SettingsStore.segments.segments
                .filter(item => item.changed)
                .map(item => { item.id, item.name });
            if (updating.length > 0) {
                MatrixService.setSegments(updating, () => {
                    this.forceUpdate();
                    this.$notify({type: 'success', text: 'Сегменты обновлены'});
                }, () => this.$notify({type: 'error', text: 'Произошла ошибка при обновлении сегментов'}));
            }
        },
        forceUpdate() {
            SettingsStore.dropUpdate();
            SettingsStore.updateSettings();
            this.changed = false;
            this.mainChanged = false;
        }
    },
    beforeCreate() {
        SettingsStore.updateSettings(() => {
            locked = false;
        });
    }
}
</script>