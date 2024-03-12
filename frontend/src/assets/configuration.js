export const configuration = {
    serverUrl: import.meta.env.MODE === 'development' ? `http://localhost/api/` : '/api/',
    debugMode: true,
    settingsUpdate: 60 * 1000,
    minLoadingTimer: 200 // Increase if animations looks too fast, set to 0 to remove fake ping
}