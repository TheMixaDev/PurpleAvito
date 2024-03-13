import { NetworkService } from "./NetworkService";

export const TreeService = {
    /**
     * Retrieves microcategories.
     *
     * @param {function} success - callback function for successful response
     * @param {function} fail - callback function for failed response
     */
    getMicrocategories: (success, fail) => {
        NetworkService.ClassicRequest("GET", `tree/microcategories`,
            null, response => {
            success(response.data);
        }, fail);
    },
    /**
     * Retrieves locations.
     *
     * @param {function} success - callback function for successful response
     * @param {function} fail - callback function for failed response
     */
    getLocations: (success, fail) => {
        NetworkService.ClassicRequest("GET", `tree/locations`,
            null, response => {
            success(response.data);
        }, fail);
    }
}