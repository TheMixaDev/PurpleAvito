<template>
    <div class="flex justify-between mt-1 animate-once animate-fade animate-duration-500 animate-ease-in-out">
        <span class="text-base font-medium text-secondary-700 dark:text-white"><slot></slot></span>
        <span class="text-sm font-medium text-secondary-700 dark:text-white">{{ percent }}%</span>
    </div>
        <div class="w-full bg-gray-200 rounded-full h-2.5 dark:bg-gray-700">
        <div class="bg-secondary-600 h-2.5 rounded-full transition-all" :style="`width: ${percent}%`"></div>
    </div>
</template>

<script>
export default {
    name: "UIProgressBar",
    props: ['percent']
}
</script>