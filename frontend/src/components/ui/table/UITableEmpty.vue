<template>
    <div class="text-gray-500 text-center pt-4">
        {{ text }}
    </div>
</template>

<script>
export default {
    name: "UITableEmpty",
    props: {
        text: {
            type: String,
            default: "Данные не найдены"
        }
    }
}
</script>