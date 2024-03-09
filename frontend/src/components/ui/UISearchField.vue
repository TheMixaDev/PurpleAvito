<script setup>
import MagnifierIcon from '@/assets/icons/MagnifierIcon.vue'
</script>
<template>
    <label for="simple-search" class="sr-only">Поиск</label>
    <div class="relative w-full">
        <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
            <MagnifierIcon />
        </div>
        <input :value="modelValue"
            @input="$emit('update:modelValue', $event.target.value)"
            @keyup.enter="$emit('apply')"
            @keyup.esc="$emit('update:modelValue', '')"
            @blur="$emit('apply')"
            type="text"
            id="simple-search"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full pl-10 p-2 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500"
            placeholder="Поиск">
    </div>
</template>

<script>
export default {
    name: "UISearchField",
    props: ['modelValue'],
    emits: ['update:modelValue', 'apply']
}
</script>