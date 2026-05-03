import api from '../lib/axios'
import type { TokenPair, UserProfile } from '../types'

export const authApi = {
  register: (email: string, username: string, password: string, city?: string) =>
    api.post<TokenPair>('/auth/register', { email, username, password, city }),

  login: (email: string, password: string) =>
    api.post<TokenPair>('/auth/login', { email, password }),

  refresh: (refreshToken: string) =>
    api.post<TokenPair>('/auth/refresh', { refreshToken }),

  logout: (refreshToken: string) =>
    api.post('/auth/logout', { refreshToken }),

  getMe: () => api.get<UserProfile>('/users/me'),
}
