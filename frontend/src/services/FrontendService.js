import { configuration } from "@/assets/configuration";

import { useModal } from "vue-final-modal";
import PromptFileModal from "@/components/modal/PromptFileModal.vue";
import WarningModal from "@/components/modal/WarningModal.vue";

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
    /**
     * A function to run a data updater with optional ping.
     *
     * @param {function} updater - the updater function to run
     * @param {object} component - the component object
     * @param {function} loadingToggler - function to toggle loading state
     * @param {boolean} [ping=true] - flag to enable/disable ping
     */
    runDataUpdater(updater, component, loadingToggler, ping = true) {
        let startTime = new Date().getTime();
        updater(() => { // All updaters require [success; fail]
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
    },
    /**
     * Displays a warning modal with the given text and callbacks for proceeding or canceling.
     *
     * @param {string} text - The text to display in the warning modal.
     * @param {function} proceed - The callback function to execute when the user chooses to proceed.
     * @param {function} cancel - The callback function to execute when the user chooses to cancel.
     */
    showWarningModal(text, proceed = () => {}, cancel = () => {}) {
        const modal = useModal({
            component: WarningModal,
            attrs: {
                text,
                onClose() {
                    modal.close();
                    cancel();
                },
                onProceed() {
                    modal.close();
                    proceed();
                }
            }
        });
        modal.open();
    },
    /**
     * Show a file modal and perform actions based on user input.
     *
     * @param {function} add - Function to be called when user adds a file
     * @param {function} clone - Function to be called when user clones a file
     */
    showFileModal(add, clone) {
        const modal = useModal({
            component: PromptFileModal,
            attrs: {
                onAdd() {
                    modal.close();
                    add();
                },
                onClone() {
                    modal.close();
                    clone();
                }
            }
        });
        modal.open();
    },
    /**
     * Parses the input value into an integer if possible, otherwise returns null.
     *
     * @param {type} value - input value to be parsed
     * @return {type} parsed integer value or null
     */
    valueParser(value) {
        if(value) value = parseInt(value);
        if(!value) value = null;
        return value;
    },
    /**
     * A function to format file size based on the input in bytes.
     *
     * @param {number} fileSizeInBytes - the size of the file in bytes
     * @return {string} the formatted file size with appropriate units
     */
    formatFileSize(fileSizeInBytes) {
        if (fileSizeInBytes < 1024) {
            return fileSizeInBytes + ' байта';
        } else if (fileSizeInBytes < 1024 * 1024) {
            return (fileSizeInBytes / 1024).toFixed(1) + ' Кб';
        } else if (fileSizeInBytes < 1024 * 1024 * 1024) {
            return (fileSizeInBytes / (1024 * 1024)).toFixed(1) + ' Мб';
        } else {
            return (fileSizeInBytes / (1024 * 1024 * 1024)).toFixed(1) + ' Гб';
        }
    },
    /**
     * Trigger UI updation asynchronously.
     */
    async updateUI() {
        await new Promise(resolve => setTimeout(resolve, 0));
    },
    /**
     * Convert a timestamp to a time string in the format "hh:mm:ss".
     *
     * @param {number} timestamp - The timestamp to convert to a time string.
     * @return {string} The time string in the format "hh:mm:ss".
     */
    msToTime(timestamp) {
        const currentDate = new Date(timestamp);
        const hours = currentDate.getHours().toString().padStart(2, '0');
        const minutes = currentDate.getMinutes().toString().padStart(2, '0');
        const seconds = currentDate.getSeconds().toString().padStart(2, '0');
        return `${hours}:${minutes}:${seconds}`;
    }
}