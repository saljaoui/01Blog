// Locally the backend runs on its own port (localhost:8080).
// Everywhere else nginx serves the app and proxies /api and /auth on the
// same origin, so relative URLs are used and no rebuild is needed per host.
const LOCAL_HOSTS = ['localhost', '127.0.0.1'];

export const API_BASE =
  typeof window !== 'undefined' && LOCAL_HOSTS.includes(window.location.hostname)
    ? `http://${window.location.hostname}:8080`
    : '';
