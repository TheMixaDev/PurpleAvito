<script setup>
import PriceServiceComponent from '@/components/PriceServiceComponent.vue';
import { usePriveServersStore } from '@/stores/priceServers';
</script>

<template>
    <h1 class="text-3xl font-semibold">Сервисы отдачи цен</h1>
    <div>
        <PriceServiceComponent
            v-for="(value, key) in PriceServersStore.servers" :key="key"
            :name="key" :ping="value.ping" :updated="value.updated" :status="value.status" :url="key"/>
    </div>
</template>

<script>
const PriceServersStore = usePriveServersStore();
export default {
    name: "MainView",
    computed: {
        PriceServersStore() {
            return PriceServersStore;
        }
    },
    beforeCreate() {
        PriceServersStore.get();  
    }
}
</script>