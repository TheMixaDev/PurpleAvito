import { NetworkService } from "./NetworkService";

export const PriceServersService = {
    getServices: (success, fail) => {
        NetworkService.ClassicRequest("GET", `services`,
            null, response => {
            success(response.data);
        }, fail);
    },
    getPrice: (locationId, microCategoryId, userId, success, fail) => {
        NetworkService.ClassicRequest("POST", `price`,
            {locationId, microCategoryId, userId}, response => {
            success(response.data);
        }, fail);
    }
}