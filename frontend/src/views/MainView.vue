<script setup>
import PriceServiceComponent from '@/components/PriceServiceComponent.vue';
import UISetupButton from '@/components/ui/UISetupButton.vue';
import { usePriveServersStore } from '@/stores/priceServers';
import { VMap, VMapArcGisAeroTileLayer, VMapZoomControl, VMapDivMarker, VMapGoogleTileLayer } from 'vue-map-ui';
</script>

<template>
    <ul class="text-sm font-medium text-center text-gray-500 rounded-lg shadow sm:flex dark:divide-gray-700 dark:text-gray-400">
        <UISetupButton :selected="true">
            Сервисы отдачи цен
        </UISetupButton>
    </ul>
    <div>
        <VMap style="height: 300px" ref="map" theme="dark"
            :zoom="3"
            :min-zoom="3"
            :center="[61.060230, 84.826744]"
            class="z-0 rounded-lg mt-2"
            v-if="!selected_station">
            <VMapGoogleTileLayer :opacity="1" />
            <VMapArcGisAeroTileLayer :opacity="0.35" />
            <VMapZoomControl />
            <VMapDivMarker
                v-for="(value, key) in PriceServersStore.servers"
                v-model:latlng="value.coords"
                :icon-size="[20, 20]"
                :icon-anchor="[10, 10]"
                :key="key"
                @click="selectStation(station)">
                <div :class="['marker', `marker_${value.status}`]"></div>
            </VMapDivMarker>
        </VMap>
        <PriceServiceComponent
            v-for="(value, key) in PriceServersStore.servers" :key="key"
            :name="value.name" :ping="value.ping" :updated="value.updated" :status="value.status" :url="key"/>
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

<style>
.marker {
    width: 20px;
    height: 20px;
    border-radius: 50%;
}
.marker_penging {
    background-color: gray;
}
.marker_actual {
    background-color: green;
}
.marker_available {
    background-color: yellow;
}
.marker_failed {
    background-color: red;
}
</style>