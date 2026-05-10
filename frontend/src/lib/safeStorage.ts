import type { StateStorage } from 'zustand/middleware'

/** localStorage с защитой от повреждённых данных (иначе Zustand persist падает при старте → белый экран). */
export function createSafeLocalStorage(): StateStorage {
  return {
    getItem: (name: string): string | null => {
      try {
        const raw = localStorage.getItem(name)
        if (!raw) return null
        JSON.parse(raw)
        return raw
      } catch {
        try {
          localStorage.removeItem(name)
        } catch {
          /* ignore */
        }
        return null
      }
    },
    setItem: (name: string, value: string) => {
      try {
        localStorage.setItem(name, value)
      } catch {
        /* quota / private mode */
      }
    },
    removeItem: (name: string) => {
      try {
        localStorage.removeItem(name)
      } catch {
        /* ignore */
      }
    },
  }
}
