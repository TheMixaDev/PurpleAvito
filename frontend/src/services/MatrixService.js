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
        NetworkService.ClassicRequest("POST", `matrices/setup/baseline`,
            { name }, response => {
            success(response.data);
        }, fail);
    },
    setSegment: (id, name, success, fail) => {
        MatrixService.setSegments([{ id, name }], success, fail);
    },
    setSegments: (segments, success, fail) => {
        NetworkService.ClassicRequest("POST", `matrices/setup/segments`,
            {discountSegments: segments}, response => {
            success(response.data);
        }, fail);
    },
    createMatrixLegacy: (parent, content, success, fail) => {
        if(!parent) parent = "discount_matrix_new";
        NetworkService.ClassicRequest("POST", `matrices/${parent}`,
            content, response => {
            success(response.data);
        }, fail, "text/plain");
    },
    createMatrix: (parent, file, success, fail, uploadProgress) => {
        if(!parent) parent = "discount_matrix_new";
        NetworkService.ClassicFileRequest(`matrices/${parent}`, file, response => {
            success(response.data);
        }, fail, uploadProgress);
    },
    getMatrixItems: (name, offset, limit, success, fail) => {
        NetworkService.ClassicRequest("GET",
            `matrices/${name}?offset=${offset}&limit=${limit}`,
            null, response => {
                success(response.data);
            }, fail
        )
    }
}