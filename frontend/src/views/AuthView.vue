<script setup>
import UIButton from '@/components/ui/UIButton.vue';
import UILabeledCheckbox from '@/components/ui/UILabeledCheckbox.vue';
import UILabeledInput from '@/components/ui/UILabeledInput.vue';
import UILoading from '@/components/ui/UILoading.vue';

import { useAuthStore } from '@/stores/user';
import { FrontendService } from '@/services/FrontendService';

import router from '@/router';
</script>
<template>
    <section>
        <div class="flex flex-col items-center justify-center px-6 py-8 mx-auto md:h-screen lg:py-0">
            <div class="w-full bg-white rounded-lg shadow dark:border md:mt-0 sm:max-w-md xl:p-0 dark:bg-gray-800 dark:border-gray-700 vl-parent animate-fade animate-once animate-duration-500 animate-ease-out">
                <div class="p-6 space-y-4 md:space-y-6 sm:p-8">
                    <h1 class="text-xl font-bold leading-tight tracking-tight text-gray-900 md:text-2xl dark:text-white">
                        Авторизация
                    </h1>
                    <div class="space-y-4 md:space-y-6">
                        <UILabeledInput
                            v-model="login"
                            type="text"
                            property="org"
                            placeholder="Avito"
                            @keyup.enter="auth">
                            Логин
                        </UILabeledInput>
                        <UILabeledInput
                            v-model="password"
                            type="password"
                            property="password"
                            placeholder="••••••••"
                            @keyup.enter="auth">
                            Пароль
                        </UILabeledInput>
                        <div class="flex items-center justify-between">
                            <UILabeledCheckbox
                                v-model="rememberMe"
                                property="rememberMe"
                                @keyup.enter="auth">
                                Запомнить меня
                            </UILabeledCheckbox>
                        </div>
                        <UIButton @click="auth" :disabled="loading || !isInputsSet" classExtension="w-full px-5 py-2.5">
                            Войти
                        </UIButton>
                    </div>
                </div>
                <UILoading v-model="loading"/>
            </div>
        </div>
    </section>
</template>

<script>
export default {
    name: 'AuthView',
    data() {
        return {
            login: '',
            password: '',
            rememberMe: false,

            loading: false
        };
    },
    computed: {
        isInputsSet() {
            return this.login.length > 0 && this.password.length > 0;
        }
    },
    methods: {
        redirect() {
            router.push({ name: 'panel' });
        },
        auth() {
            this.loading = true;
            const AuthStorage = useAuthStore();
            AuthStorage.login(this.$cookies, this.login, this.password, this.rememberMe, () => {
                this.$notify({text:"Успешная авторизация", type: "success"});
                this.redirect();
            }, () => {
                setTimeout(() => {
                    this.$notify({text:"Неверный логин или пароль", type: "error"});
                    this.loading = false;
                }, 100);
            });
        }
    },
    mounted() {
        FrontendService.validateUser(this.$cookies, this.redirect, false);
    }
}
</script>