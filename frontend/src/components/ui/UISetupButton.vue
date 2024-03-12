<script setup>
const SELECTED_CLASS = `inline-block w-full p-4 text-gray-900 bg-gray-100 border-r border-gray-200 dark:border-gray-700 rounded-sm focus:ring-4 focus:ring-blue-300 active focus:outline-none dark:bg-gray-700 dark:text-white`;
const BASE_CLASS = `inline-block w-full p-4 bg-white border-r border-gray-200 dark:border-gray-700 hover:text-gray-700 hover:bg-gray-50 focus:ring-4 focus:ring-blue-300 focus:outline-none dark:hover:text-white dark:bg-gray-800 dark:hover:bg-gray-700`;
</script>
<template>
    <li class="w-full focus-within:z-10">
        <a href="#" :class="[selected ? SELECTED_CLASS : BASE_CLASS, 'text-xl']" aria-current="page">
            <slot></slot>
        </a>
    </li>
</template>

<script>
export default {
    name: "UISetupButton",
    props: ['selected']
}
</script>