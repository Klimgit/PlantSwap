import { Link, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import { authApi } from '../api/auth'

export default function Layout({ children }: { children: React.ReactNode }) {
  const { username, refreshToken, logout } = useAuthStore()
  const navigate = useNavigate()

  const handleLogout = async () => {
    if (refreshToken) {
      try { await authApi.logout(refreshToken) } catch { /* игнорируем */ }
    }
    logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b border-gray-200 sticky top-0 z-10">
        <div className="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
          <Link to="/" className="font-bold text-brand-700 text-xl tracking-tight">
            🌿 PlantSwap
          </Link>
          <nav className="flex items-center gap-4 text-sm">
            <Link to="/" className="text-gray-600 hover:text-brand-700 transition-colors">
              Объявления
            </Link>
            {username ? (
              <>
                <Link to="/my-listings" className="text-gray-600 hover:text-brand-700 transition-colors">
                  Мои объявления
                </Link>
                <Link to="/deals" className="text-gray-600 hover:text-brand-700 transition-colors">
                  Сделки
                </Link>
                <Link to="/favorites" className="text-gray-600 hover:text-brand-700 transition-colors">
                  Избранное
                </Link>
                <span className="text-gray-400">|</span>
                <span className="text-gray-700 font-medium">{username}</span>
                <button
                  onClick={handleLogout}
                  className="text-gray-500 hover:text-red-600 transition-colors"
                >
                  Выйти
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="text-gray-600 hover:text-brand-700 transition-colors">
                  Войти
                </Link>
                <Link
                  to="/register"
                  className="bg-brand-600 text-white px-3 py-1.5 rounded-lg hover:bg-brand-700 transition-colors"
                >
                  Регистрация
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-4 py-6">{children}</main>
    </div>
  )
}
