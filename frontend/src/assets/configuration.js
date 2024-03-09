export const configuration = {
    serverUrl: import.meta.env.MODE === 'development' ? `http://localhost:80/api/v1/` : '/api/v1/',
}