import { useAuthStore } from "@/stores/user";

import router from "@/router";
import moment from "moment";

export const FrontendService = {
    /**
     * Validates the user using the provided cookies and redirects to the login page if validation fails.
     *
     * @param {Object} cookies - The cookies object containing user authentication information.
     * @param {function} success - The success callback function to be executed upon successful user validation.
     * @param {boolean} [redirect=true] - Optional. Specifies whether to redirect the user to the login page if validation fails. Defaults to true.
     */
    validateUser(cookies, success, redirect = true) {
        const store = useAuthStore();
        store.cookieLogin(cookies, success, () => {
            if(redirect)
                router.push({ name: "login" });
        })
    },
    /**
     * Formats the input date string into RU format.
     *
     * @param {Object | number | Date} input - The input date string to be formatted.
     * @param {boolean} includeTime - Whether to include the time in the formatted result. Defaults to false.
     * @return {string} The formatted date string.
     */
    formatDate(input, includeTime = false) {
        return moment(input).format(`YYYY.MM.DD${includeTime ? " HH:mm" : ""}`);
    },
}