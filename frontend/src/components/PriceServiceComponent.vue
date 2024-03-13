<script setup>
import UIIndicatorGray from './ui/indicators/UIIndicatorGray.vue';
import UIIndicatorGreen from './ui/indicators/UIIndicatorGreen.vue';
import UIIndicatorRed from './ui/indicators/UIIndicatorRed.vue';
import UIIndicatorYellow from './ui/indicators/UIIndicatorYellow.vue';

import { FrontendService } from '@/services/FrontendService';
</script>
<template>
    <div class="max-w-sm bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 inline-flex m-4">
        <div class="p-5">
            <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                {{ name }}
            </h5>
            <p class="mb-1 font-normal text-gray-700 dark:text-gray-400">
                Адрес: <span class="font-semibold text-blue-600 dark:text-blue-500">{{ url.replace("http://", "").replace("https://", "") }}</span><br>
                Пинг: {{ ping }} мс<br>
                Обновлено: {{ FrontendService.msToTime(updated) }}
            </p>
            <UIIndicatorGreen v-if="status == 'actual'">
                Доступен
            </UIIndicatorGreen>
            <UIIndicatorYellow v-if="status == 'available'">
                Ожидание базы данных
            </UIIndicatorYellow>
            <UIIndicatorRed v-if="status == 'failed'">
                Не доступен
            </UIIndicatorRed>
            <UIIndicatorGray v-if="status == 'pending'">
                Обновление...
            </UIIndicatorGray>
        </div>
    </div>
</template>

<script>
export default {
    name: "PriceServiceComponent",
    props: ['name', 'ping', 'updated', 'status', 'url'],
    computed: {
        FrontendService() {
            return FrontendService;
        }
    }
}
</script>