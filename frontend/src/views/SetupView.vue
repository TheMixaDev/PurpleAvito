<script setup>
import UISetupButton from '@/components/ui/UISetupButton.vue';

import SetupMainComponent from '@/components/SetupMainComponent.vue';
import SetupSegmentsComponent from '@/components/SetupSegmentsComponent.vue';

import { useSettingsStore } from '@/stores/settings';
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
        <SetupMainComponent v-if="selectedPanel == 0" class="animate-fade animate-once animate-duration-500 animate-ease-out" />
        <SetupSegmentsComponent v-if="selectedPanel == 1" />
    </div>
</template>

<script>
const SettingsStore = useSettingsStore();
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
        selectPanel(id) {
            this.selectedPanel = id;
        }
    },
    beforeCreate() {
        SettingsStore.updateSettings();
    }
}
</script>