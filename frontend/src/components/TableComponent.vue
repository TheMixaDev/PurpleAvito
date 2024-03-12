<script setup>
import LeftArrowIcon from '@/assets/icons/LeftArrowIcon.vue';
import RightArrowIcon from '@/assets/icons/RightArrowIcon.vue';

import UITableRow from './ui/table/UITableRow.vue';
import UITableCell from './ui/table/UITableCell.vue';
import UITableEmpty from './ui/table/UITableEmpty.vue';
import UIButton from '@/components/ui/UIButton.vue';
import UISearchField from '@/components/ui/UISearchField.vue';

import '@/assets/styles/loading.css';

import { Pagination } from '@/models/pagination';

const SELECTED_PAGE = "flex items-center justify-center text-sm z-10 py-2 px-3 leading-tight text-primary-600 bg-primary-50 border border-primary-300 hover:bg-primary-100 hover:text-primary-700 dark:border-gray-700 dark:bg-gray-700 dark:text-white cursor-pointer";
const PAGE = "flex items-center justify-center text-sm py-2 px-3 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white cursor-pointer";
</script>
<template>
    <div class="mx-auto max-w-screen-xl p-3 px-4 lg:px-12 animate-fade animate-once animate-duration-500 animate-ease-out">
        <div class="bg-white dark:bg-gray-800 relative shadow-md sm:rounded-lg vld-parent">
            <div class="flex flex-col md:flex-row items-center justify-between space-y-3 md:space-y-0 md:space-x-4 p-4" v-if="searchEnabled || creationEnabled || clearEnabled">
                <div class="w-full md:w-1/2">
                    <UISearchField v-if="searchEnabled" v-model="searchValue" @apply="checkSearch"/>
                </div>
                <div class="w-full md:w-auto flex flex-col md:flex-row space-y-2 md:space-y-0 items-stretch md:items-center justify-end md:space-x-3 flex-shrink-0">
                    <UIButton v-if="clearEnabled" @click="$emit('clearClick')" color="danger" :disabled="!clearState">
                        <font-awesome-icon :icon="['fas', 'trash']"/>&nbsp;
                        Очистить
                    </UIButton>
                    <UIButton v-if="creationEnabled" @click="$emit('creationClick')">
                        <font-awesome-icon :icon="['fas', 'plus']"/>&nbsp;
                        Создать
                    </UIButton>
                </div>
            </div>
            <div class="overflow-x-auto">
                <table class="w-full text-sm text-left text-gray-500 dark:text-gray-400">
                    <thead class="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                        <tr>
                            <th scope="col" class="px-4 py-3" v-for="(column, index) in columns" :key="index">{{ column }}</th>
                        </tr>
                    </thead>
                    <tbody v-if="!isLoading">
                        <slot></slot>
                    </tbody>
                    <tbody v-else>
                        <UITableRow v-for="index in loadCount > 0 ? loadCount : pagination.itemsPerPage" :key="index">
                            <UITableCell v-for="column in columns" :key="column">
                                <div class="loading-cell h-[30px] w-full bg-gradient-to-r
                                            from-gray-200 via-gray-300 to-gray-200
                                            dark:from-gray-500 dark:via-gray-600 dark:to-gray-500"></div>
                            </UITableCell>
                        </UITableRow>
                    </tbody>
                </table>
                <UITableEmpty v-if="pagination.total == 0 && !isLoading" class="pb-6" :text="emptyText"/>
            </div>
            <nav v-if="footerEnabled && !isLoading && pagination.total > 0" class="flex flex-col md:flex-row justify-between items-start md:items-center space-y-3 md:space-y-0 p-4" >
                <span class="text-sm font-normal text-gray-500 dark:text-gray-400">
                    Отображение
                    <span class="font-semibold text-gray-900 dark:text-white">
                        {{ pagination.start + 1 }}-{{ pagination.end }}
                    </span>
                    из
                    <span class="font-semibold text-gray-900 dark:text-white">
                        {{ pagination.total }}
                    </span>
                </span>
                <ul class="inline-flex items-stretch -space-x-px">
                    <li>
                        <a @click="changePage(pagination.page)" href="#" class="flex items-center justify-center h-full py-1.5 px-3 ml-0 text-gray-500 bg-white rounded-l-lg border border-gray-300 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">
                            <span class="sr-only">Предудыщая</span>
                            <LeftArrowIcon />
                        </a>
                    </li>
                    <li v-for="page in pages" :key="page">
                        <a
                            :class="page === pagination.page + 1 ? SELECTED_PAGE : PAGE"
                            :aria-current="page === pagination.page + 1 ? 'page' : false"
                            @click="page !== '...' ? changePage(page) : null"
                        >
                            {{ page }}
                        </a>
                    </li>
                    <li>
                        <a @click="changePage(pagination.page + 2)" href="#" class="flex items-center justify-center h-full py-1.5 px-3 text-gray-500 bg-white rounded-r-lg border border-gray-300 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">
                            <span class="sr-only">Следующая</span>
                            <RightArrowIcon />
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
    </div>
</template>

<script>
export default {
    name: "TableComponent",
    data() {
        return {
            lastSearch: ""
        }
    },
    props: {
        searchEnabled: {
            type: Boolean,
            default: true
        },
        creationEnabled: {
            type: Boolean,
            default: true
        },
        clearEnabled: {
            type: Boolean,
            default: false
        },
        clearState: {
            type: Boolean,
            default: true
        },
        columns: Array,
        footerEnabled: {
            type: Boolean,
            default: true
        },
        loadCount: {
            type: Number,
            default: 0
        },
        emptyText: {
            type: String,
            default: "Данные не найдены"
        },
        search: String,
        empty: Boolean,
        isLoading: Boolean,
        pagination: Pagination
    },
    emits: ['creationClick', 'pageChange', 'update:search', 'searchApply', 'clearClick'],
    computed: {
        /**
         * Generates an array of page numbers for pagination based on max page count and current page.
         */
        pages() {
            let pages = [];
            if (this.pagination.maxPage <= 5) {
                for (let i = 1; i <= this.pagination.maxPage; i++)
                    pages.push(i);
            } else {
                pages.push(1);
                if (this.pagination.page + 1 > 3)
                    pages.push('...');
                let start = this.pagination.page;
                let end = this.pagination.page + 2;
                if (start < 2) {
                    start = 2;
                    end = 3;
                }
                if (end >= this.pagination.maxPage) {
                    start = this.pagination.maxPage - 3;
                    end = this.pagination.maxPage - 1;
                }
                for (let i = start; i <= end; i++)
                    pages.push(i);
                if (this.pagination.page + 1 < this.pagination.maxPage - 2)
                    pages.push('...');
                pages.push(this.pagination.maxPage);
            }
            return pages;
        },
        searchValue: {
            get() {
                return this.search;
            },
            set(value) {
                this.$emit('update:search', value);
            }
        },
        isLoadingValue() {
            return this.isLoading;
        }
    },
    methods: {
        changePage(newPage) {
            this.pagination.updatePage(newPage);
            this.$emit('pageChange');
        },
        checkSearch() {
            if(this.lastSearch == this.search) return;
            this.lastSearch = this.search;
            this.$emit('searchApply');
        }
    },
    mounted() {
        this.lastSearch = this.search;
    }
}
</script>