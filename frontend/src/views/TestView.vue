<template>
    <div></div>
</template>
<script>
import { TreeService } from '@/services/TreeService';
import { responses } from '@/assets/responses';
export default {
    name: "TestView",
    mounted() {
        TreeService.formTree(responses.categories);
    }
}
</script>