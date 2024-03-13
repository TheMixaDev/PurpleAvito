import { NetworkService } from "./NetworkService";

export const HistoryService = {
    /**
     * Retrieves the history data based on the specified page and limit.
     *
     * @param {number} page - The page number to retrieve.
     * @param {number} limit - The maximum number of items to retrieve.
     * @param {function} success - Callback function to handle successful response.
     * @param {function} fail - Callback function to handle failed response.
     */
    getHistory: (page, limit, success, fail) => {
        NetworkService.ClassicRequest("GET",
            `history?page=${page}&limit=${limit}`,
            null, response => {
                success(response.data);
            }, fail
        )
    }
};