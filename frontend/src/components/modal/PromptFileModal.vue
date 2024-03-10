<script setup>
import UIButton from "../ui/UIButton.vue";

import { VueFinalModal } from 'vue-final-modal'
</script>
<template>
    <VueFinalModal
        content-class="flex overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-modal md:h-full backdrop-blur-sm"
        overlay-transition="vfm-fade"
        content-transition="vfm-fade">
        <div class="relative p-4 w-full max-w-md h-full flex items-center justify-center">
            <div class="relative p-4 text-center w-full bg-white rounded-lg shadow dark:bg-gray-800 sm:p-5">
                <font-awesome-icon :icon="['fas', 'triangle-exclamation']" class="text-yellow-500 text-5xl m-3"/>
                <p class="mb-4 text-gray-500 dark:text-gray-300">
                    В таблице уже содержатся данные. Хотите ли вы добавить данные из файла или заменить ими таблицу?
                </p>
                <div class="flex justify-center items-center space-x-4">
                    <UIButton color="gray" classExtension="text-black dark:text-white" @click="$emit('clone')">
                        Заменить
                    </UIButton>
                    <UIButton color="secondary" @click="$emit('add')">
                        Добавить
                    </UIButton>
                </div>
            </div>
        </div>
    </VueFinalModal>
</template>

<script>
export default {
    name: "PromptFileModal",
    props: ['text'],
    emits: ['add', 'clone']
}
</script>