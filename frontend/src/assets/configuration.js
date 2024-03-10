export const configuration = {
    serverUrl: import.meta.env.MODE === 'development' ? `http://localhost:8080/api/` : '/api/v1/',
    debugMode: true,
    settingsUpdate: 60 * 1000,
    minLoadingTimer: 200 // Increase if animations looks too fast, set to 0 to remove fake ping
}