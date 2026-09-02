import axios from "axios";

const api = axios.create({ baseURL: "/api/v1" });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = "Bearer " + token;
  return config;
});

api.interceptors.response.use(
  (r) => r,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.clear();
      window.location.href = "/login";
    }
    return Promise.reject(err);
  }
);

// Auth
export const authApi = {
  login: (email: string, password: string) => api.post("/auth/login", { email, password }),
  register: (data: any) => api.post("/auth/register", data),
};

// Buses
export const busApi = {
  getAll: (page = 0, size = 20) => api.get(`/buses?page=${page}&size=${size}`),
  getById: (id: string) => api.get(`/buses/${id}`),
};

// Stops
export const stopApi = {
  getAll: (page = 0, size = 50) => api.get(`/stops?page=${page}&size=${size}`),
  getById: (id: string) => api.get(`/stops/${id}`),
};

// Routes
export const routeApi = {
  getAll: (page = 0, size = 20) => api.get(`/routes?page=${page}&size=${size}`),
  getById: (id: string) => api.get(`/routes/${id}`),
};

// Trips
export const tripApi = {
  getAll: (page = 0, size = 20) => api.get(`/trips?page=${page}&size=${size}`),
  getById: (id: string) => api.get(`/trips/${id}`),
};

// Fare
export const fareApi = {
  calculate: (data: { sourceStopId: string; destinationStopId: string; tripId: string }) =>
    api.post("/fares/calculate", data),
};

// Bookings
export const bookingApi = {
  create: (data: { tripId: string; sourceStopId: string; destinationStopId: string }, key: string) =>
    api.post("/bookings", data, { headers: { "Idempotency-Key": key } }),
  getAll: (page = 0, size = 20) => api.get(`/bookings?page=${page}&size=${size}`),
  getById: (id: string) => api.get(`/bookings/${id}`),
};

// Payments
export const paymentApi = {
  initiate: (bookingId: string, key: string) =>
    api.post(`/payments?bookingId=${bookingId}`, null, { headers: { "Idempotency-Key": key } }),
  verify: (paymentId: string) => api.post(`/payments/${paymentId}/verify`),
  getById: (id: string) => api.get(`/payments/${id}`),
};

// Tickets
export const ticketApi = {
  getAll: (page = 0, size = 20) => api.get(`/tickets?page=${page}&size=${size}`),
  getById: (id: string) => api.get(`/tickets/${id}`),
};

// Location
export const locationApi = {
  getBus: (id: string) => api.get(`/buses/${id}/location`),
  updateGps: (data: any) => api.post("/gps/update", data),
};

export default api;
