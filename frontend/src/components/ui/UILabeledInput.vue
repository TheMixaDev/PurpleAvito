<template>
    <div>
        <label :for="property" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white">
            <slot></slot>
        </label>
        <input
            v-if="!textarea"
            :id="property"
            :value="modelValue"
            @input="$emit('update:modelValue', $event.target.value)"
            :type="type"
            :name="property"
            :placeholder="placeholder"
            :disabled="disabled"
            @blur="$emit('blur')"
            class="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-opacity"
        >
        <textarea
            v-else
            :id="property"
            :value="modelValue"
            @input="$emit('update:modelValue', $event.target.value)"
            :placeholder="placeholder"
            :disabled="disabled"
            @blur="$emit('blur')"
            rows="8"
            class="block p-2.5 w-full text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500 disabled:opacity-50 disabled:cursor-not-allowed transition-opacity"
        ></textarea>
    </div>
</template>

<script>
export default {
    name: "UILabeledInput",
    props: {
        modelValue: String,
        type: {
            type: String,
            default: "text"
        },
        property: {
            type: String,
            default: "input"
        },
        placeholder: {
            type: String,
            default: ""
        },
        textarea: {
            type: Boolean,
            default: false
        },
        disabled: {
            type: Boolean,
            default: false
        }
    },
    emits: ['update:modelValue', 'blur']
}
</script>