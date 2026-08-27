import type {
  AdminSummary,
  ApiResponse,
  AuthPayload,
  CreditCard,
  Dashboard,
  Expense,
  Role,
  User
} from './types';
import { demoApi, isDemoMode } from './demoApi';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

let accessToken: string | null = null;

export function setAccessToken(token: string | null) {
  accessToken = token;
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers);
  if (!(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }
  if (accessToken) {
    headers.set('Authorization', `Bearer ${accessToken}`);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
    credentials: 'include'
  });
  const json = (await response.json().catch(() => null)) as ApiResponse<T> | null;
  if (!response.ok || !json?.success) {
    throw new Error(json?.message || `API failed: ${response.status}`);
  }
  return json.data;
}

const liveApi = {
  login: (username: string, password: string) =>
    request<AuthPayload>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    }),
  refresh: () => request<AuthPayload>('/api/auth/refresh', { method: 'POST' }),
  logout: () => request<void>('/api/auth/logout', { method: 'POST' }),
  completeFirstLogin: (payload: {
    currentPassword: string;
    newPassword: string;
    fullName: string;
    email: string;
  }) =>
    request<User>('/api/auth/complete-first-login', {
      method: 'POST',
      body: JSON.stringify(payload)
    }),
  dashboard: (month?: string) => request<Dashboard>(`/api/dashboard${month ? `?month=${month}` : ''}`),
  expenses: (params?: { from?: string; to?: string; category?: string; creditCardId?: number | '' }) => {
    const search = new URLSearchParams();
    if (params?.from) search.set('from', params.from);
    if (params?.to) search.set('to', params.to);
    if (params?.category) search.set('category', params.category);
    if (params?.creditCardId) search.set('creditCardId', String(params.creditCardId));
    return request<Expense[]>(`/api/expenses${search.toString() ? `?${search}` : ''}`);
  },
  createExpense: (payload: Omit<Expense, 'id' | 'creditCardLabel'>) =>
    request<Expense>('/api/expenses', { method: 'POST', body: JSON.stringify(payload) }),
  updateExpense: (id: number, payload: Omit<Expense, 'id' | 'creditCardLabel'>) =>
    request<Expense>(`/api/expenses/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteExpense: (id: number) => request<void>(`/api/expenses/${id}`, { method: 'DELETE' }),
  creditCards: () => request<CreditCard[]>('/api/credit-cards'),
  createCreditCard: (payload: Omit<CreditCard, 'id' | 'currentMonthExpense' | 'currentCyclePaid'>) =>
    request<CreditCard>('/api/credit-cards', { method: 'POST', body: JSON.stringify(payload) }),
  updateCreditCard: (id: number, payload: Omit<CreditCard, 'id' | 'currentMonthExpense' | 'currentCyclePaid'>) =>
    request<CreditCard>(`/api/credit-cards/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteCreditCard: (id: number) => request<void>(`/api/credit-cards/${id}`, { method: 'DELETE' }),
  setCardPaid: (id: number, paid: boolean) =>
    request<CreditCard>(`/api/credit-cards/${id}/payment-status`, {
      method: 'PATCH',
      body: JSON.stringify({ paid })
    }),
  updateProfile: (payload: { fullName: string; email: string }) =>
    request<User>('/api/users/me', { method: 'PUT', body: JSON.stringify(payload) }),
  changePassword: (payload: { currentPassword: string; newPassword: string }) =>
    request<void>('/api/users/me/password', { method: 'POST', body: JSON.stringify(payload) }),
  adminSummary: () => request<AdminSummary>('/api/admin/summary'),
  adminUsers: () => request<User[]>('/api/admin/users'),
  createUser: (payload: {
    username: string;
    email: string;
    fullName: string;
    temporaryPassword: string;
    role: Role;
  }) => request<User>('/api/admin/users', { method: 'POST', body: JSON.stringify(payload) }),
  updateUserRole: (id: number, role: Role) =>
    request<User>(`/api/admin/users/${id}/role`, { method: 'PATCH', body: JSON.stringify({ role }) }),
  updateUserStatus: (id: number, active: boolean) =>
    request<User>(`/api/admin/users/${id}/status`, { method: 'PATCH', body: JSON.stringify({ active }) })
};

export const api = isDemoMode ? demoApi : liveApi;
