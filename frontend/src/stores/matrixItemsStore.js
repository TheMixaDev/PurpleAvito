import { defineStore } from "pinia";
import { ref } from "vue";

import { Pagination } from "@/models/pagination";
import { MatrixService } from "@/services/MatrixService";

export const useMatrixItemsStore = defineStore('matrixItems', () => {
    const matrix = ref("");
    const loadedMatrix = ref("");
    const items = ref([])
    const pagination = ref(new Pagination(0, 10, 0));

    function updateItems(success, fail) {
        if(matrix.value.length < 1) return success();
        if(loadedMatrix.value != matrix.value || pagination.value.page < 0)
            pagination.value.page = 0;
        if(pagination.value.page >= pagination.value.maxPage && pagination.value.maxPage != 0)
            pagination.value.page = pagination.value.maxPage - 1;
        MatrixService.getMatrixItems(matrix.value, pagination.value.page * pagination.value.itemsPerPage, pagination.value.itemsPerPage, data => {
            pagination.value.total = data.total;
            items.value = data.rows;
            loadedMatrix.value = matrix.value;
            success();
        }, fail);
    }

    return {
        matrix,
        items,
        pagination,
        updateItems
    }
});