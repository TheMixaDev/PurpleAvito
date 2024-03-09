<script setup>
import UIButton from '@/components/ui/UIButton.vue';
import UIDropdownWithSearch from '@/components/ui/UIDropdownWithSearch.vue';
</script>
<template>
    <div>
        Текущая матрица цен:
        <UIDropdownWithSearch
            v-model="currentMatrix"
            :options="matrixes"
            >
            Не выбрано
        </UIDropdownWithSearch>
        <UIButton>Применить изменения</UIButton>
    </div>
    <div>
        Скидочные матрицы:
        <UIDropdownWithSearch
            v-model="currentSegment"
            :options="segments"
            >
            Выберите сегмент матриц
        </UIDropdownWithSearch>
        <UIDropdownWithSearch
            v-model="currentSegmentMatrix"
            :options="matrixesDiscount"
            >
            Выберите матрицу
        </UIDropdownWithSearch>
        <UIButton>Применить изменения</UIButton>
    </div>
</template>

<script>
export default {
    name: "SetupView",
    data() {
        return {
            loading: false,

            currentMatrix: 0,
            matrixes: {
                1: "baseline_matrix_1",
                2: "baseline_matrix_2",
            },
            currentSegment: 0,
            segments: {
                1: "segment_1",
                2: "segment_2",
                43: "segment_43"
            },
            currentSegmentMatrix: 0,
            matrixesDiscount: {
                1: "discount_matrix_1",
                2: "discount_matrix_2",
            },
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