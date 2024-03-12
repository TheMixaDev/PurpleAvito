import { NetworkService } from "./NetworkService";

export const PriceServersService = {
    getServices: (success, fail) => {
        NetworkService.ClassicRequest("GET", `services`,
            null, response => {
            success(response.data);
        }, fail);
    },
}