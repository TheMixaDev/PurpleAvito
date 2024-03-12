import { NetworkService } from "./NetworkService";

export const HistoryService = {
    getHistory: (page, limit, success, fail) => {
        NetworkService.ClassicRequest("GET",
            `history?page=${page}&limit=${limit}`,
            null, response => {
                success(response.data);
            }, fail
        )
    }
};