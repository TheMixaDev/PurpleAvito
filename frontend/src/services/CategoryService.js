import { NetworkService } from "./NetworkService";
import { configuration } from "@/assets/configuration";
import { responses } from "@/assets/responses";

export const CategoryService = {
    /**
     * Retrieves all categories.
     *
     * @param {Object} cookies - The cookies to be used for authentication.
     * @param {function} success - The callback function to execute on success.
     * @param {function} fail - The callback function to execute on failure.
     */
    getCategories: (cookies, success, fail) => {
        if(configuration.debugMode) return success(responses.categories);
        NetworkService.PrefabAuthRequest("GET", `categories/`,
            null, cookies, null, fail, response => {
            success(response.data.data);
        });
    },
}