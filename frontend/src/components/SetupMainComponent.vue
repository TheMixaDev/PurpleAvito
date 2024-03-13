<script setup>
import UIIndicatorGreen from './ui/indicators/UIIndicatorGreen.vue';

import UITableRow from '@/components/ui/table/UITableRow.vue';
import UITableCell from '@/components/ui/table/UITableCell.vue';

import TableComponent from '@/components/TableComponent.vue';

import UIButton from '@/components/ui/UIButton.vue';
import UIDropdownWithSearch from '@/components/ui/UIDropdownWithSearch.vue';
import UILabeledCheckbox from '@/components/ui/UILabeledCheckbox.vue';
import UILabeledInput from './ui/UILabeledInput.vue';

import { Pagination } from '@/models/pagination';

import { useSettingsStore } from '@/stores/settings';
import { useTreeStore } from '@/stores/tree';

import { FrontendService } from '@/services/FrontendService';
import { MatrixService } from '@/services/MatrixService';
import { PriceServersService } from '@/services/PriceServersService';
</script>

<template>
    <div class="mx-auto max-w-screen-xl p-3 px-4 lg:px-12">
        <h2 class="text-xl font-semibold mb-2 text-center">
            Установить основную ценовую матрицу
        </h2>
        <UIDropdownWithSearch
            v-model="SettingsStore.baseline"
            :options="SettingsStore.baselineMatrices"
            @changed="registerChangeMain"
            >
            Матрица не выбрана
        </UIDropdownWithSearch>
        <UIButton class="mt-2 w-full" :disabled="!mainChanged" @click="changeMain">
            Применить изменения
        </UIButton>
        <h2 class="text-xl font-semibold mt-2 text-center">
            Просмотреть цену
        </h2>
        <UILabeledCheckbox class="mt-2" v-model="showReadable">
            Отображать названия категорий и локаций
        </UILabeledCheckbox>

        <UILabeledInput
            class="mt-2"
            type="number"
            v-model="user_id">
            ID пользователя:
        </UILabeledInput>
    </div>
    <div>
        <TableComponent
            :searchEnabled="false"
            :pagination="new Pagination(1, 10, 1)"
            :clearEnabled="false"
            :creationEnabled="false"
            :footerEnabled="false"
            :showFooter="false"
            :columns="[showReadable ? 'Категория' : 'ID категории', showReadable ? 'Локация' : 'ID локации', 'Цена', 'Пинг', 'Отправить']"
            :emptyText="originFile != null ? 'Предпросмотр недоступен' : 'Данные не найдены'"
            >
            <UITableRow>
                <UITableCell>
                    <UIDropdownWithSearch
                        :options="TreeStore.microcategories"
                        v-model="microcategory_id"
                        v-if="showReadable">
                        Не выбрано
                    </UIDropdownWithSearch>
                    <UILabeledInput
                        v-else
                        v-model="microcategory_id"
                        type="number"
                        />
                </UITableCell>
                <UITableCell>
                    <UIDropdownWithSearch
                        :options="TreeStore.locations"
                        v-model="location_id"
                        v-if="showReadable">
                        Не выбрано
                    </UIDropdownWithSearch>
                    <UILabeledInput
                        v-else
                        v-model="location_id"
                        type="number"
                        />
                </UITableCell>
                <UITableCell>
                    {{ price }}
                </UITableCell>
                <UITableCell class="w-[120px]">
                    <UIIndicatorGreen v-if="ping >= 0">
                        {{ ping }} мс
                    </UIIndicatorGreen>
                </UITableCell>
                <UITableCell class="w-[120px]">
                    <UIButton @click="sendPrice" :disabled="waitPrice ||
                        !FrontendService.valueParser(microcategory_id)
                        || FrontendService.valueParser(microcategory_id) < TreeStore.minMicrocategory
                        || FrontendService.valueParser(microcategory_id) > TreeStore.maxMicrocategory
                        || !FrontendService.valueParser(location_id)
                        || FrontendService.valueParser(location_id) < TreeStore.minLocation
                        || FrontendService.valueParser(location_id) > TreeStore.maxLocation">
                        Отправить
                    </UIButton>
                </UITableCell>
            </UITableRow>
        </TableComponent>
    </div>
    <div class="mx-auto max-w-screen-xl p-3 px-4 lg:px-12">
        <p v-if="matrixName" class="text-center">
            Найдено в матрице: {{ matrixName }}<br>
            Категория: {{ TreeStore.microcategories[category] }}<br>
            Локация: {{ TreeStore.locations[location] }}
        </p>
    </div>
</template>

<script>
const SettingsStore = useSettingsStore();
const TreeStore = useTreeStore();
export default {
    name: "SetupMainComponent",
    data() {
        return {
            mainChanged: false,

            showReadable: true,
            microcategory_id: 0,
            location_id: 0,
            user_id: 1,

            waitPrice: false,
            ping: -1,

            price: "",
            matrixName: "",
            category: 1,
            location: 1
        }
    },
    computed: {
        SettingsStore() {
            return SettingsStore;
        },
        TreeStore() {
            return TreeStore;
        }
    },
    methods: {
        /**
         * Prompt changing of the main function.
         *
         */
        changeMain() {
            FrontendService.showWarningModal(`Вы уверены, что хотите изменить текущую главную ценовую матрицу?`, () => {
                MatrixService.setBaseline(SettingsStore.baseline, () => {
                    this.forceUpdate();
                    this.$notify({type: 'success', text: 'Главная ценовая матрица обновлена'});
                }, error => this.$notify({type: 'error', text: error.response.data.message}));
            })
        },
        registerChangeMain() {
            this.mainChanged = true;
        },
        forceUpdate() {
            SettingsStore.dropUpdate();
            SettingsStore.updateSettings();
            this.mainChanged = false;
        },
        /**
         * A method to send the price with retries if unsuccessful.
         *
         * @param {number} retries - the number of retries
         * @return {void} 
         */
        sendPrice(retries = 0) {
            this.waitPrice = true;
            let start = Date.now();
            PriceServersService.getPrice(this.location_id * 1, this.microcategory_id * 1, this.user_id * 1, data => {
                this.ping = Date.now()-start;
                this.price = data.price;
                this.category = data.microCategoryId;
                this.location = data.locationId;
                this.matrixName = data.matrixName;
                this.waitPrice = false;
            }, () => {
                if(retries < 10) {
                    this.sendPrice(retries+1);
                } else {
                    this.$notify({type: 'error', text: 'Не удалось получить цену'})
                    this.waitPrice = false;
                }
            })
        }
    }
}
</script>