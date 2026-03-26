import api from './client';

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  refresh: (refreshToken) => api.post('/auth/refresh', { refreshToken }),
};

export const usersApi = {
  getAll: (params) => api.get('/users', { params }),
  register: (data) => api.post('/users/register', data),
  createStaff: (data) => api.post('/users/admin/create', data),
  getById: (id) => api.get(`/users/${id}`),
  getByEmail: (email) => api.get('/users/by-email', { params: { email } }),
  updatePhone: (id, phone) => api.patch(`/users/${id}/phone`, { phone }),
  updatePassword: (id, data) => api.patch(`/users/${id}/password`, data),
};

export const doctorsApi = {
  register: (data) => api.post('/doctors/register', data),
  getMe: () => api.get('/doctors/me'),
  getById: (id) => api.get(`/doctors/${id}`),
  getByUserId: (userId) => api.get(`/doctors/by-user/${userId}`),
  getAll: () => api.get('/doctors'),
  getBySpecialization: (specialization) => api.get('/doctors/by-specialization', { params: { specialization } }),
  updateDepartment: (id, department) => api.patch(`/doctors/${id}/department`, null, { params: { department } }),
  updateSpecialization: (id, specialization) => api.patch(`/doctors/${id}/specialization`, specialization),
};

export const patientsApi = {
  register: (data) => api.post('/patients/register', data),
  getMe: () => api.get('/patients/me'),
  getAll: () => api.get('/patients'),           // ← add this line
  getById: (id) => api.get(`/patients/${id}`),
  getByUserId: (userId) => api.get(`/patients/by-user/${userId}`),
  updateAddress: (id, address) => api.patch(`/patients/${id}/address`, null, { params: { address } }),
  updateEmergencyContact: (id, contact) => api.patch(`/patients/${id}/emergency-contact`, null, { params: { contact } }),
};

export const appointmentsApi = {
  book: (data) => api.post('/appointments', data),
  getById: (id) => api.get(`/appointments/${id}`),
  getMyAppointments: () => api.get('/appointments/my'),
  getByStatus: (status) => api.get('/appointments/by-status', { params: { status } }),
  complete: (id) => api.patch(`/appointments/${id}/complete`),
  cancel: (id) => api.patch(`/appointments/${id}/cancel`),
  reschedule: (id, newAppointmentTime) => api.patch(`/appointments/${id}/reschedule`, { newAppointmentTime }),
};

export const medicinesApi = {
  add: (data) => api.post('/medicines', data),
  getAll: (params) => api.get('/medicines', { params }),
  getById: (id) => api.get(`/medicines/${id}`),
  getByName: (name) => api.get('/medicines/by-name', { params: { name } }),
  updatePrice: (id, price) => api.patch(`/medicines/${id}/price`, null, { params: { price } }),
  updateManufacturer: (id, manufacturer) => api.patch(`/medicines/${id}/manufacturer`, null, { params: { manufacturer } }),
};

export const inventoryApi = {
  create: (data) => api.post('/inventory', data),
  getByMedicine: (medicineId) => api.get(`/inventory/by-medicine/${medicineId}`),
  isLowStock: (medicineId) => api.get(`/inventory/by-medicine/${medicineId}/low-stock`),
  addStock: (medicineId, quantity) => api.patch(`/inventory/by-medicine/${medicineId}/add-stock`, { quantity }),
  deductStock: (medicineId, quantity) => api.patch(`/inventory/by-medicine/${medicineId}/deduct-stock`, { quantity }),
  updateReorderLevel: (medicineId, reorderLevel) => api.patch(`/inventory/by-medicine/${medicineId}/reorder-level`, null, { params: { reorderLevel } }),
};
