import { configuration } from "@/assets/configuration";

import axios from "axios";

export const NetworkService = {
    /**
     * Executes a HTTP request using Axios.
     *
     * @param {string} method - The HTTP method to use for the request.
     * @param {string} url - The URL to send the request to.
     * @param {Object} data - The data to send with the request.
     * @param {function} success - The callback function to be executed if the request is successful.
     * @param {function} fail - The callback function to be executed if the request fails.
     */
    ClassicRequest(method, url, data, success, fail, contentType = "application/json") {
        (async () => {
            let response = await axios({
                method: method,
                url: `${configuration.serverUrl}${url}`,
                data: data,
                headers: {'Content-Type': contentType}
            }).catch(fail);
            if(response)
                success(response);
        })();
    },
    /**
     * A function to make a root request using the specified method, URL, data, success callback, fail callback, and optional content type.
     *
     * @param {string} method - The HTTP method to use for the request
     * @param {string} url - The URL to make the request to
     * @param {Object} data - The data to be sent with the request
     * @param {function} success - The callback function to be executed upon a successful request
     * @param {function} fail - The callback function to be executed upon a failed request
     * @param {string} contentType - The content type of the request (default is "application/json")
     */
    RootRequest(method, url, data, success, fail, contentType = "application/json") {
        (async () => {
            let response = await axios({
                method: method,
                url: `${configuration.rootUrl}${url}`,
                data: data,
                headers: {'Content-Type': contentType}
            }).catch(fail);
            if(response)
                success(response);
        })();
    },
    /**
     * ClassicFileRequest function to send a file via POST request using FormData.
     *
     * @param {string} url - The URL to send the file to.
     * @param {File} file - The file to be sent.
     * @param {function} success - The callback function to be executed upon a successful request
     * @param {function} fail - The callback function to be executed upon a failed request
     * @param {function} uploadProgress - The callback function for upload progress.
     */
    ClassicFileRequest(url, file, success, fail, uploadProgress) {
        const formData = new FormData();
        formData.append('file', file);
        (async () => {
            let response = await axios({
                method: "POST",
                url: `${configuration.serverUrl}${url}`,
                data: formData,
                headers: {'Content-Type': 'multipart/form-data'},
                onUploadProgress: uploadProgress
            }).catch(fail);
            if(response)
                success(response);
        })();
    }
}