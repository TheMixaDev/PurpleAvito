<script setup>
import UIButton from '@/components/ui/UIButton.vue';
import UIDropdownWithSearch from '@/components/ui/UIDropdownWithSearch.vue';

import { useSettingsStore } from '@/stores/settings';

import { FrontendService } from '@/services/FrontendService';
import { MatrixService } from '@/services/MatrixService';
</script>

<template>
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
</template>

<script>
const SettingsStore = useSettingsStore();
export default {
    name: "SetupMainComponent",
    data() {
        return {
            mainChanged: false
        }
    },
    computed: {
        SettingsStore() {
            return SettingsStore;
        }
    },
    methods: {
        changeMain() {
            FrontendService.showWarningModal(`Вы уверены, что хотите изменить текущую главную ценовую матрицу?`, () => {
                MatrixService.setBaseline(SettingsStore.baseline, () => {
                    this.forceUpdate();
                    this.$notify({type: 'success', text: 'Главная ценовая матрица обновлена'});
                }, () => this.$notify({type: 'error', text: 'Произошла ошибка при обновлении главной ценовой матрицы'}));
            })
        },
        registerChangeMain() {
            this.mainChanged = true;
        },
        forceUpdate() {
            SettingsStore.dropUpdate();
            SettingsStore.updateSettings();
            this.mainChanged = false;
        }
    }
}
</script>