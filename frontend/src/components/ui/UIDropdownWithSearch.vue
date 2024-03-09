<script setup>
import DownArrowIcon from '@/assets/icons/DownArrowIcon.vue'
import MagnifierIcon from '@/assets/icons/MagnifierIcon.vue';

import UITableEmpty from './table/UITableEmpty.vue';
</script>
<template>
    <div class="relative w-full p-0 m-0">
        <div :class="['relative', showSelector ? 'z-20' : '']">
            <button
                class="flex-shrink-0 inline-flex items-center py-2.5 px-4 text-sm font-medium text-center text-gray-500 bg-gray-100 border border-gray-300 rounded-lg hover:bg-gray-200 focus:ring-4 focus:outline-none focus:ring-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 dark:focus:ring-gray-700 dark:text-white dark:border-gray-600 w-full disabled:opacity-50 disabled:cursor-not-allowed transition-opacity"
                type="button"
                @click="showSelector = !showSelector"
                :disabled="disabled">
                <slot v-if="!options || options.length <= 0 ||
                            !modelValue || modelValue.length === 0 || modelValue * 1 === 0"></slot>
                <span>
                    {{ options[modelValue] }}
                </span>
                <span class="ml-auto">
                    <DownArrowIcon/>
                </span>
            </button>
        </div>
        <div v-if="showSelector" class="z-10 bg-white rounded-lg shadow dark:bg-gray-700 w-full absolute animate-fade-down animate-once animate-duration-200 animate-ease-in">
            <div class="p-3">
                <label for="input-group-search" class="sr-only">Search</label>
                <div class="relative">
                    <div class="absolute inset-y-0 rtl:inset-r-0 start-0 flex items-center ps-3 pointer-events-none">
                        <MagnifierIcon />
                    </div>
                    <input type="text"
                            class="block w-full p-2 ps-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                            placeholder="Поиск"
                            v-model="search">
                </div>
            </div>
            <ul v-if="optionsFiltered.length > 0" class="max-h-48 px-3 pb-3 overflow-y-auto text-sm text-gray-700 dark:text-gray-200">
                <li v-for="key in optionsFiltered" :value="key" :key="key" @click="select(key)" class="cursor-pointer">
                    <div class="flex items-center ps-2 rounded hover:bg-gray-100 dark:hover:bg-gray-600">
                        <span class="py-2 ms-2 text-sm font-medium text-gray-900 rounded dark:text-gray-300">
                            {{ options[key] }}
                        </span>
                    </div>
                </li>
            </ul>
            <UITableEmpty v-else class="pb-8" />
        </div>
    </div>
</template>
<script>
export default {
    name: "UIDropdownWithSearch",
    props: {
        modelValue: null,
        options: {
            type: Object,
            default: () => ({})
        },
        disabled: {
            type: Boolean,
            default: false
        }
    },
    computed: {
        /**
         * Filters the options object based on the search string.
         */
        optionsFiltered() {
            return Object.keys(this.options)
                .filter(key => this.options[key].toLowerCase().includes(this.search.toLowerCase()))
        }
    },
    emits: ['update:modelValue', 'changed'],
    data() {
        return {
            showSelector: false,
            search: ""
        }
    },
    methods: {
        select(key) {
            this.$emit('update:modelValue', key);
            this.$emit('changed');
            this.showSelector = false;
        },
        /**
         * Handles the click event outside of the component.
         */
        handleClick() {
            const container = this.$el;
            if (!container.contains(event.target)) {
                this.showSelector = false;
            }
        }
    },
    mounted() {
        document.addEventListener("click", this.handleClick);
    },
    unmounted() {
        document.removeEventListener("click", this.handleClick);
    }
}
</script>