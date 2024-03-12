<template>
    <div class="flex items-start">
        <div class="flex items-center h-5">
            <input :checked="modelValue"
                @change="$emit('update:modelValue', $event.target.checked)"
                :id="property"
                aria-describedby="remember"
                type="checkbox"
                class="w-4 h-4 border border-gray-300 rounded bg-gray-50 focus:ring-3 focus:ring-primary-300 dark:bg-gray-700 dark:border-gray-600 dark:focus:ring-primary-600 dark:ring-offset-gray-800 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed transition-opacity"
                :disabled="disabled">
        </div>
        <div class="ml-3 text-sm">
            <label :for="property" :class="`text-gray-500 dark:text-gray-300 ${disabled ? 'opacity-50' : 'cursor-pointer'}`">
                <slot></slot>
            </label>
        </div>
    </div>
</template>

<script>
export default {
    name: "UILabeledCheckbox",
    props: {
        modelValue: Boolean,
        property: {
            type: String,
            default: 'checkbox'
        },
        disabled: {
            type: null,
            default: false
        }
    },
    emits: ['update:modelValue']
}
</script>