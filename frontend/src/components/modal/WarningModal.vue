<script setup>
import CloseCrossIcon from "@/assets/icons/CloseCrossIcon.vue";

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
                <button type="button"
                    class="text-gray-400 absolute top-2.5 right-2.5 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm p-1.5 ml-auto inline-flex items-center dark:hover:bg-gray-600 dark:hover:text-white"
                    @click="$emit('close')">
                    <CloseCrossIcon />
                    <span class="sr-only">Закрыть</span>
                </button>
                <font-awesome-icon :icon="['fas', 'triangle-exclamation']" class="text-yellow-500 text-5xl m-3"/>
                <p class="mb-4 text-gray-500 dark:text-gray-300">{{ text }}</p>
                <div class="flex justify-center items-center space-x-4">
                    <UIButton color="gray" classExtension="dark:text-white text-black" @click="$emit('close')">
                        Отмена
                    </UIButton>
                    <UIButton color="secondary" @click="$emit('proceed')">
                        Продолжить
                    </UIButton>
                </div>
            </div>
        </div>
    </VueFinalModal>
</template>

<script>
export default {
    name: "WarningModal",
    props: ['text'],
    emits: ['proceed', 'close'],
    methods: {
        close() {
            this.$emit('close');
        }
    }
}
</script>