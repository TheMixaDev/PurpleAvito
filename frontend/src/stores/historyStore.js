import { defineStore } from "pinia";
import { ref } from "vue";

import { Pagination } from "@/models/pagination";
import { HistoryService } from "@/services/HistoryService";

export const useHistoryStore = defineStore('history', () => {
    const items = ref([])
    const pagination = ref(new Pagination(0, 15, 0));

    /**
     * Update items based on pagination values.
     *
     * @param {function} success - Callback function to be executed on success
     * @param {function} fail - Callback function to be executed on failure
     */
    function get(success = () => {}, fail = () => {}) {
        if(pagination.value.page >= pagination.value.maxPage && pagination.value.maxPage != 0)
            pagination.value.page = pagination.value.maxPage - 1;
        HistoryService.getHistory(pagination.value.page, pagination.value.itemsPerPage, data => {
            pagination.value.total = data.total;
            items.value = data.history;
            success();
        }, fail);
    }

    return {
        items,
        pagination,
        get
    }
});