import { NetworkService } from "./NetworkService";

export const TreeService = {
    getMicrocategories: (success, fail) => {
        NetworkService.ClassicRequest("GET", `tree/microcategories`,
            null, response => {
            success(response.data);
        }, fail);
    },
    getLocations: (success, fail) => {
        NetworkService.ClassicRequest("GET", `tree/locations`,
            null, response => {
            success(response.data);
        }, fail);
    }
}