<script setup>
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/css/index.css';
</script>
<template>
    <loading v-model:active="mutatedModel"
        :can-cancel="false"
        :is-full-page="true"
        :height="75"
        :width="160"
        color="#fff"
        loader="bars"
        transition="fade"
        class="animate-fade animate-duration-500 animate-ease-out"
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