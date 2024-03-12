<template>
    <div></div>
</template>
<script>
import { FrontendService } from '@/services/FrontendService';
import { useSettingsStore } from '@/stores/settings';
const SettingsStore = useSettingsStore();
export default {
    name: "TestView",
    mounted() {
        //SettingsStore.updateSettings();
        FrontendService.showWarningModal("asdasd", () => {alert("asddasasddsadas")});
    }
}
</script>