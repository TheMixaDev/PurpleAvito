<script setup>
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/css/index.css';
</script>
<template>
    <loading v-model:active="mutatedModel"
        :can-cancel="false"
        :is-full-page="false"
        :height="75"
        :width="160"
        color="#fff"
        loader="bars"
        transition="none"
        class="animate-fade animate-once animate-duration-100 animate-ease-out"
        background-color="#000"
        />
</template>

<script>
export default {
    name: "UILoading",
    props: ['modelValue'],
    computed: {
        mutatedModel: {
            get() {
                return this.modelValue
            }
        }
    }
}
</script>