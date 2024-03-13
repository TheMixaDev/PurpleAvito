import { ref } from 'vue'
import { defineStore } from 'pinia'

import { Pagination } from '@/models/pagination';

export const useSegmentsStore = defineStore('segments', () => {
    const segments = ref([]);
    const pagination = ref(new Pagination(0, 10, 0));
    const display = ref([]);
    const filtered = ref([]);
    const search = ref("");

    function updateSegments(newSegments) {
        segments.value = newSegments;
        pagination.value.total = segments.value.length;
        pagination.value.itemsPerPage = 10;
        handleSegmentsPageChange();
    }

    function updateSearch(newSearch) {
        search.value = newSearch;
    }

    /**
     * Filters the items based on the search value. 
     */
    function applySearch() {
        filtered.value = segments.value
            .filter(item => item.id.toString().includes(search.value) ||
                            item.name?.toString().includes(search.value));
        pagination.value.total = filtered.value.length;
        if(pagination.value.maxPage <= pagination.value.page)
            pagination.value.page = 0;
        display.value = filtered.value
            .slice(pagination.value.start, pagination.value.end);
    }

    function handleSegmentsPageChange() {
        applySearch();
    }

    return {
        display,
        pagination,
        search,
        segments,
        updateSearch,
        updateSegments,
        applySearch,
        handleSegmentsPageChange
    }
});