export const configuration = {
    rootUrl: import.meta.env.MODE === 'development' ? `http://localhost:8081/` : '/',
    serverUrl: import.meta.env.MODE === 'development' ? `http://localhost/api/` : '/api/',
    settingsUpdate: 60 * 1000, // Interval for updating settings not on force-update
    minLoadingTimer: 50 // Increase if animations looks too fast, set to 0 to remove fake ping
}