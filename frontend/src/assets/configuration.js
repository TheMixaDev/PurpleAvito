export const configuration = {
    serverUrl: import.meta.env.MODE === 'development' ? `http://localhost:80/api/v1/` : '/api/v1/',
    debugMode: true,
    minLoadingTimer: 200 // Increase if animations looks too fast, set to 0 to remove fake ping
}