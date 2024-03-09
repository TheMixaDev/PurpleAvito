import { NetworkService } from "./NetworkService"

export const AuthService = {
    /**
     * Logs in the user with the provided credentials.
     *
     * @param {string} username - The username or email address of the user.
     * @param {string} password - The password of the user.
     * @param {function} success - The callback function to be called on successful login.
     * @param {function} fail - The callback function to be called on failed login.
     */
    login(username, password, success, fail) {
        NetworkService.ClassicRequest("POST", "login", { username, password }, (response) => {
            if(response.data.token)
                success(response.data.token);
            else fail();
        }, fail)
    }
}