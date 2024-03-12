import { ref } from 'vue'
import { defineStore } from 'pinia'
import { PriceServersService } from '@/services/PriceServersService';

export const usePriveServersStore = defineStore('priceServers', () => {
    const servers = ref({});

    function get() {
        for(let i in servers.value)
        servers.value[i].status = "pending";
        PriceServersService.getServices(data => {
            data = data.priceServices;
            for(let i of data) {
                servers.value[i.url] = {
                    ping: i.ping,
                    status: i.connected ? (i.actual ? `actual` : `available`) : `failed`,
                    updated: Date.now()
                }
            }
        })
    }

    return {
        servers,
        get
    }
});