import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor to handle errors
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            const { status, data } = error.response;

            if (status === 401) {
                // Token expired or invalid
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login';
                return Promise.reject(new Error('Session invalide, veuillez s\'authentifier'));
            }

            if (status === 403) {
                return Promise.reject(new Error('Vous n\'avez pas le droit d\'accéder à cette fonctionnalité. Veuillez contacter votre administrateur'));
            }

            if (data && data.message) {
                return Promise.reject(new Error(data.message));
            }
        }

        return Promise.reject(error);
    }
);

// Auth API
export const authAPI = {
    login: (credentials) => api.post('/auth/login', credentials),
    changePassword: (data) => api.post('/auth/change-password', data),
};

// Client API (AGENT_GUICHET)
export const clientAPI = {
    create: (clientData) => api.post('/clients', clientData),
    getAll: () => api.get('/clients'),
    getByIdentity: (identityNumber) => api.get(`/clients/identity/${identityNumber}`),
};

// Account API
export const accountAPI = {
    create: (accountData) => api.post('/accounts/create', accountData),
    getById: (id) => api.get(`/accounts/${id}`),
    getByRib: (rib) => api.get(`/accounts/rib/${rib}`),
};

// Dashboard API (CLIENT)
export const dashboardAPI = {
    get: (accountId) => api.get('/dashboard', { params: { accountId } }),
    getTransactions: (accountId, page = 0, size = 10) =>
        api.get(`/dashboard/accounts/${accountId}/transactions`, { params: { page, size } }),
};

// Transfer API (CLIENT)
export const transferAPI = {
    execute: (transferData) => api.post('/transfers', transferData),
};

export default api;
