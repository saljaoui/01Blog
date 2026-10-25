import { API_BASE } from './api-base';

export const environment = {
  production: false,
  apiBase: API_BASE,
  apiUrl: `${API_BASE}/api`,
  api: {
    posts: `${API_BASE}/api/posts`,
    likes: `${API_BASE}/api/likes`,
    comments: `${API_BASE}/api/comments`,
    auth: `${API_BASE}/auth`,
    users: `${API_BASE}/api/users`,
    saved: `${API_BASE}/api/saveds`,
    notifications: `${API_BASE}/api/notifications`,
    reports: `${API_BASE}/api/reports`
  }
};
