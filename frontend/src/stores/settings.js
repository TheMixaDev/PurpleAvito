import { ref } from 'vue'
import { defineStore } from 'pinia'
import { MatrixService } from '@/services/MatrixService';
import { configuration } from '@/assets/configuration';
import { useSegmentsStore } from './segments';

export const useSettingsStore = defineStore('settings', () => {
    const matrices = ref({});
    const baselineMatrices = ref({});
    const discountMatrices = ref({});

    const baseline = ref("");
    const segments = ref(useSegmentsStore());
    const lastUpdate = ref(-1);
    const loading = ref(false);

    function getMatrices(success, fail) {
        MatrixService.getMatrices(
            response => {
                baselineMatrices.value = response.baselineMatrices.reduce((result, item) => {
                    result[item.name] = item.name;
                    return result;
                }, {});
                discountMatrices.value = response.discountMatrices.reduce((result, item) => {
                    result[item.name] = item.name;
                    return result;
                }, {});
                matrices.value = {...baselineMatrices.value, ...discountMatrices.value};
                loading.value = false;
                success();
            }, fail
        )
    }

    function getSetup(success, fail) {
        MatrixService.getSetup(
            response => {
                baseline.value = response.baselineMatrix.name;
                segments.value.updateSegments(response.discountSegments);
                success();
            }, fail
        )
    }

    function updateSettings(success, fail) {
        if (Date.now() - lastUpdate.value > configuration.settingsUpdate) {
            loading.value = true;
            lastUpdate.value = Date.now();
            return getMatrices(() => getSetup(success, fail), fail);
        }
        success();
    }

    function dropUpdate() {
        lastUpdate.value = 0;
    }

    return {
        matrices,
        baselineMatrices,
        discountMatrices,

        baseline,
        segments,

        loading,

        updateSettings,
        dropUpdate
    };
})