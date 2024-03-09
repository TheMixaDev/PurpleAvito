import { ref } from 'vue'
import { defineStore } from 'pinia'
import { CategoryService } from '@/services/CategoryService';
import { Pagination } from '@/models/pagination';
import { TreeService } from '@/services/TreeService';

export const useCategoryStore = defineStore('category', () => {
    const categories = ref([]);
    const pagination = ref(new Pagination(0, 10, 0));
    const tree = ref(null);
    const depth = ref([]);
    const display = ref([]);
    const search = ref("");

    /**
     * Updates the categories data with tree info.
     *
     * @param {Object} cookies - The cookies to be used for authentication.
     * @param {function} success - The callback function for successful retrieval.
     * @param {function} fail - The callback function for retrieval failure.
     */
    function updateCategories(cookies, success, fail) {
        if(categories.value.length > 0) return success();
        CategoryService.getCategories(cookies, data => {
            //pagination.value.total = data.total;
            categories.value = data;
            tree.value = TreeService.formTree(categories.value);
            display.value = tree.value.children[0];
            pagination.value.total = display.value.length;
            success();
        }, fail);
    }
    function setSearch(value) {
        search.value = value;
    }

    return {
        categories,
        pagination,
        tree,
        depth,
        display,
        search,
        updateCategories,
        setSearch
    }
})