import { ref } from 'vue'
import { defineStore } from 'pinia'

import { Pagination } from '@/models/pagination';
import { MatrixItem } from '@/models/matrixItem';
import { FrontendService } from '@/services/FrontendService';

export const useNewMatrixStore = defineStore('newMatrix', () => {
    const parentMatrix = ref(0);
    const items = ref([]);
    const pagination = ref(new Pagination(0, 10, 0));
    const display = ref([]);
    const filtered = ref([]);
    const search = ref("");

    function updateSearch(newSearch) {
        search.value = newSearch;
    }

    function createElement() {
        items.value.push(new MatrixItem(0, 0, null));
        applySearch();
    }
    
    function deleteElement(index) {
        items.value.splice(index, 1);
        applySearch();
    }

    function compare(origin, value, strict) {
        return strict ? origin?.toString() == value : origin?.toString().includes(value);
    }

    function applySearch() {
        let prompt = search.value+"";
        let strict = prompt.startsWith("!");
        if(strict)
            prompt = prompt.slice(1);
        if(prompt.split(" ").length > 1) {
            let splitted = prompt.split(" ");
            filtered.value = items.value
                .filter(item => compare(item.microcategory_id, splitted[0], strict) &&
                                compare(item.location_id, splitted[1], strict));
        } else {
            filtered.value = items.value
                .filter(item => compare(item.microcategory_id, prompt, strict) ||
                                compare(item.location_id, prompt, strict));
        }
        pagination.value.total = filtered.value.length;
        if(pagination.value.maxPage <= pagination.value.page)
            pagination.value.page = 0;
        display.value = filtered.value
            .slice(pagination.value.start, pagination.value.end);
    }

    function handleSegmentsPageChange() {
        applySearch();
    }

    function clear() {
        items.value = [];
        applySearch();
    }

    function addItems(newItems, callback, progress) {
        setTimeout(async () => {
            progress(0);
            let total = newItems.length;
            let lastPercentUpdate = Date.now();
            for(let i in newItems) {
                if(Date.now() - lastPercentUpdate > 10 || i == total - 1) {
                    progress(i / total);
                    await FrontendService.updateUI();
                    lastPercentUpdate = Date.now();
                }
                items.value.push(newItems[i]);
            }
            applySearch();
            callback();
        }, 0);
    }

    return {
        items,
        pagination,
        search,
        display,
        parentMatrix,
        updateSearch,
        applySearch,
        handleSegmentsPageChange,
        createElement,
        deleteElement,
        clear,
        addItems
    }
});