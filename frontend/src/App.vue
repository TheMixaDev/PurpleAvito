<script setup>
import { RouterView } from 'vue-router'
import NotificationModal from '@/components/modal/NotificationModal.vue'
</script>

<template>
  <RouterView />
  <notifications class="p-3" position="top left">
      <template #body="props">
          <NotificationModal :data="props"/>
      </template>
  </notifications>
</template>

