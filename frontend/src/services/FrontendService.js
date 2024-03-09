import { configuration } from "@/assets/configuration";

import moment from "moment";

export const FrontendService = {
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
    runDataUpdater(updater, component, loadingToggler, ping = true) {
        let startTime = new Date().getTime();
        updater(component.$cookies, () => { // All updaters require [cookies; success; fail]
            let diffTime = new Date().getTime() - startTime;
            if(diffTime > 0)
                setTimeout(loadingToggler,
                    diffTime < configuration.minLoadingTimer && ping
                    ? configuration.minLoadingTimer - diffTime
                    : 0);
            else loadingToggler();
        }, () => {
            loadingToggler();
            component.$notify({type: 'error', text: 'Произошла ошибка при загрузке данных'});
        });
    }
}