import { NetworkService } from "./NetworkService";

export const MatrixService = {
    getSetup: (success, fail) => {
        NetworkService.ClassicRequest("GET", `matrices/setup`,
            null, response => {
            success(response.data);
        }, fail);
    },
    getMatrices: (success, fail) => {
        NetworkService.ClassicRequest("GET", `matrices`,
            null, response => {
            success(response.data);
        }, fail);
    },
    setBaseline: (name, success, fail) => {
        NetworkService.ClassicRequest("POST", `matrices/setup`,
            { name }, response => {
            success(response.data);
        }, fail);
    },
    setSegment: (id, name, success, fail) => {
        MatrixService.setSegments([{ id, name }], success, fail);
    },
    setSegments: (segments, success, fail) => {
        NetworkService.ClassicRequest("POST", `matrices/setup/segments`,
            segments, response => {
            success(response.data);
        }, fail);
    }
}