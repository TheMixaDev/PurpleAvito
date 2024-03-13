import { NetworkService } from "./NetworkService";

export const PriceServersService = {
    /**
     * Retrieves the list of services.
     *
     * @param {function} success - callback function to handle successful response
     * @param {function} fail - callback function to handle failed response
     */
    getServices: (success, fail) => {
        NetworkService.ClassicRequest("GET", `services`,
            null, response => {
            success(response.data);
        }, fail);
    },
    /**
     * Generate the price based on location, category, and user ID.
     *
     * @param {integer} locationId - The ID of the location.
     * @param {integer} microCategoryId - The ID of the micro category.
     * @param {integer} userId - The ID of the user.
     * @param {function} success - callback function to handle successful response
     * @param {function} fail - callback function to handle failed response
     */
    getPrice: (locationId, microCategoryId, userId, success, fail) => {
        NetworkService.RootRequest("POST", `price`,
            {locationId, microCategoryId, userId}, response => {
            success(response.data);
        }, fail);
    }
}