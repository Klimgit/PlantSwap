import api from '../lib/axios'
import type { TokenPair, UserProfile } from '../types'

export const authApi = {
  /** Бэкенд возвращает профиль без JWT — после регистрации нужно вызвать login */
  register: (username: string, email: string, password: string, city?: string) =>
    api.post<UserProfile>('/auth/register', { username, email, password, city }),

  login: (email: string, password: string) =>
    api.post<TokenPair>('/auth/login', { email, password }),

  refresh: (refreshToken: string) =>
    api.post<TokenPair>('/auth/refresh', { refreshToken }),

  logout: (refreshToken: string) =>
    api.post('/auth/logout', { refreshToken }),

  getMe: () => api.get<UserProfile>('/users/me'),
}
