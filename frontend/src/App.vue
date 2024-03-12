<script setup>
import { RouterView } from 'vue-router'
import NotificationModal from '@/components/modal/NotificationModal.vue'
import PieIcon from './assets/icons/sidepanel/PieIcon.vue';
import PageIcon from './assets/icons/sidepanel/PageIcon.vue';
import AddIcon from './assets/icons/sidepanel/AddIcon.vue';
import LockIcon from './assets/icons/sidepanel/LockIcon.vue';
import UISideButton from './components/ui/UISideButton.vue';
import UIButton from './components/ui/UIButton.vue';
import { ModalsContainer } from 'vue-final-modal';
import UILoading from './components/ui/UILoading.vue';
</script>

<template>
  <div class="dark:bg-gray-900" style="position: static; height: 100vh;">
    <div class="antialiased dark:bg-gray-900">
      <nav class="bg-white border-b border-gray-200 px-4 py-2.5 dark:bg-gray-800 dark:border-gray-700 fixed left-0 right-0 top-0 z-50">
        <div class="flex flex-wrap justify-between items-center">
          <div class="flex justify-start items-center">
            <button
              data-drawer-target="drawer-navigation"
              data-drawer-toggle="drawer-navigation"
              aria-controls="drawer-navigation"
              class="p-2 mr-2 text-gray-600 rounded-lg cursor-pointer md:hidden hover:text-gray-900 hover:bg-gray-100 focus:bg-gray-100 dark:focus:bg-gray-700 focus:ring-2 focus:ring-gray-100 dark:focus:ring-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
            >
              <svg
                
                class="w-6 h-6"
                fill="currentColor"
                viewBox="0 0 20 20"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  fill-rule="evenodd"
                  d="M3 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zM3 10a1 1 0 011-1h6a1 1 0 110 2H4a1 1 0 01-1-1zM3 15a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z"
                  clip-rule="evenodd"
                ></path>
              </svg>
              <span class="sr-only">Toggle sidebar</span>
            </button>
            <a class="flex items-center justify-between mr-4">
              <img
                src="/logo.png"
                class="mr-3 h-8"
                alt="Avito Logo"
              />
              <span class="self-center text-2xl font-semibold whitespace-nowrap dark:text-white">Avito панель администратора</span>
              <UIButton
                @click="updateData()"
                class="ml-2">
                Обновить данные
              </UIButton>
            </a>
          </div>
        </div>
      </nav>

      <!-- Sidebar -->

      <aside
        class="fixed top-0 left-0 z-40 w-64 h-screen pt-14 transition-transform -translate-x-full bg-white border-r border-gray-200 md:translate-x-0 dark:bg-gray-800 dark:border-gray-700"
        aria-label="Sidenav"
        id="drawer-navigation"
      >
        <div class="overflow-y-auto py-5 px-3 h-full bg-white dark:bg-gray-800">
          <ul class="space-y-2">
            <UISideButton title="Главная" to="/">
              <PieIcon/>
            </UISideButton>
            <UISideButton title="Настройки цен" to="/setup">
              <LockIcon/>
            </UISideButton>
            <UISideButton title="Создание матрицы" to="/create">
              <AddIcon/>
            </UISideButton>
            <UISideButton title="Просмотр матриц" to="/">
              <PageIcon/>
            </UISideButton>
          </ul>
        </div>
      </aside>

      <main class="p-4 md:ml-64 h-auto pt-20">
        <RouterView />
      </main>
    </div>
    <UILoading v-if="SettingsStore" v-model="SettingsStore.loading" style="position: fixed"/>
    <ModalsContainer />
    <notifications class="p-3" position="top right">
        <template #body="props">
            <NotificationModal :data="props"/>
        </template>
    </notifications>
  </div>
</template>

<script>
import { useSettingsStore } from '@/stores/settings';
import { usePriveServersStore } from './stores/priceServers';
import { useTreeStore } from './stores/tree';

export default {
    name: "App",
    data() {
      return {
        SettingsStore: null,
        PriceServersStore: null,
        TreeStore: null
      }
    },
    methods: {
        updateData() {
            this.SettingsStore.dropUpdate();
            this.SettingsStore.updateSettings(
              () => this.$notify({type: 'success', text: 'Данные обновлены'}),
              () => this.$notify({type: 'error', text: 'Произошла ошибка при обновлении данных'})
            );
            this.PriceServersStore.get();
        }
    },
    mounted() {
        this.SettingsStore = useSettingsStore();
        this.PriceServersStore = usePriveServersStore();
        this.TreeStore = useTreeStore();
        this.TreeStore.get();
    }
}
</script>