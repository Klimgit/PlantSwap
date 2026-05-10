import { create } from 'zustand'
import { createJSONStorage, persist } from 'zustand/middleware'
import { createSafeLocalStorage } from '../lib/safeStorage'

interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  userId: string | null
  username: string | null
  setTokens: (accessToken: string, refreshToken: string) => void
  setUser: (userId: string, username: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      userId: null,
      username: null,
      setTokens: (accessToken, refreshToken) => set({ accessToken, refreshToken }),
      setUser: (userId, username) => set({ userId, username }),
      logout: () => set({ accessToken: null, refreshToken: null, userId: null, username: null }),
    }),
    {
      name: 'plantswap-auth',
      storage: createJSONStorage(() => createSafeLocalStorage()),
    },
  ),
)
