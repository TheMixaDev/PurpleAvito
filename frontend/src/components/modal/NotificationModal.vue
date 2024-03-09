<script setup>
import CheckIcon from '@/assets/icons/CheckIcon.vue'
import CrossIcon from '@/assets/icons/CrossIcon.vue'
import ExclamationIcon from '@/assets/icons/ExclamationIcon.vue'
import CloseCrossIcon from '@/assets/icons/CloseCrossIcon.vue'
</script>
<template>
    <div class="flex items-center w-full max-w-xs p-4 mb-4 text-gray-500 bg-white rounded-lg shadow dark:text-gray-400 dark:bg-gray-800" role="alert">
        <div :class="['inline-flex items-center justify-center flex-shrink-0 w-8 h-8 rounded-lg',
            data.item.type === 'success' ? 'text-green-500 bg-green-100 dark:bg-green-800 dark:text-green-200' :
            (data.item.type === 'error' ? 'text-red-500 bg-red-100 dark:bg-red-800 dark:text-red-200' :
            'text-yellow-500 bg-yellow-100 dark:bg-yellow-800 dark:text-yellow-200')
            ]">
            <CheckIcon v-if="data.item.type === 'success'"/>
            <CrossIcon v-if="data.item.type === 'error'"/>
            <ExclamationIcon v-if="data.item.type === 'warn'"/>
            <span class="sr-only">Icon</span>
        </div>
        <div class="ms-3 text-sm font-normal">{{ data.item.text }}</div>
        <button type="button" class="ms-auto -mx-1.5 -my-1.5 bg-white text-gray-400 hover:text-gray-900 rounded-lg focus:ring-2 focus:ring-gray-300 p-1.5 hover:bg-gray-100 inline-flex items-center justify-center h-8 w-8 dark:text-gray-500 dark:hover:text-white dark:bg-gray-800 dark:hover:bg-gray-700"
        aria-label="Close" @click="data.close">
            <span class="sr-only">Закрыть</span>
            <CloseCrossIcon />
        </button>
    </div>
</template>

<script>
export default {
    name: "NotificationModal",
    props: ['data']
}
</script>