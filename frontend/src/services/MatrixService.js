import { NetworkService } from "./NetworkService";

export const MatrixService = {
    /**
     * A function to retrieve setup information.
     *
     * @param {function} success - callback function for successful response
     * @param {function} fail - callback function for failed response
     */
    getSetup: (success, fail) => {
        NetworkService.ClassicRequest("GET", `matrices/setup`,
            null, response => {
            success(response.data);
        }, fail);
    },
    /**
     * A function to retrieve matrices information.
     *
     * @param {function} success - callback function for successful response
     * @param {function} fail - callback function for failed response
     */
    getMatrices: (success, fail) => {
        NetworkService.ClassicRequest("GET", `matrices`,
            null, response => {
            success(response.data);
        }, fail);
    },
    /**
     * A function to set current baseline matrix.
     *
     * @param {string} name - name of the matrix
     * @param {function} success - callback function for successful response
     * @param {function} fail - callback function for failed response
     */
    setBaseline: (name, success, fail) => {
        NetworkService.ClassicRequest("POST", `matrices/setup/baseline`,
            { name }, response => {
            success(response.data);
        }, fail);
    },
    /**
     * A function for setting current discount matrix.
     *
     * @param {integer} id - segment id
     * @param {string} name - name of the matrix
     * @param {function} success - callback function for successful response
     * @param {function} fail - callback function for failed response
     */
    setSegment: (id, name, success, fail) => {
        MatrixService.setSegments([{ id, name }], success, fail);
    },
    /**
     * A function for setting all changed discount matrices.
     *
     * @param {array} segments - array of segments
     * @param {function} success - callback function for successful response
     * @param {function} fail - callback function for failed response
     */
    setSegments: (segments, success, fail) => {
        NetworkService.ClassicRequest("POST", `matrices/setup/segments`,
            {discountSegments: segments}, response => {
            success(response.data);
        }, fail);
    },
    /**
     * Create a matrix using the provided parent and file of changes.
     *
     * @param {string} parent - parent matrix name
     * @param {File} file - file data
     * @param {function} success - callback function for successful response
     * @param {function} fail - callback function for failed response
     * @param {function} uploadProgress - upload update function
     */
    createMatrix: (parent, file, success, fail, uploadProgress) => {
        if(!parent) parent = "discount_matrix_new";
        NetworkService.ClassicFileRequest(`matrices/${parent}`, file, response => {
            success(response.data);
        }, fail, uploadProgress);
    },
    /**
     * Retrieves matrix items based on the provided name, offset, and limit.
     *
     * @param {string} name - The name of the matrix.
     * @param {number} offset - The starting index for retrieving items.
     * @param {number} limit - The maximum number of items to retrieve.
     * @param {function} success - The callback function to handle successful response.
     * @param {function} fail - The callback function to handle failed response.
     */
    getMatrixItems: (name, offset, limit, success, fail) => {
        NetworkService.ClassicRequest("GET",
            `matrices/${name}?offset=${offset}&limit=${limit}`,
            null, response => {
                success(response.data);
            }, fail
        )
    }
}