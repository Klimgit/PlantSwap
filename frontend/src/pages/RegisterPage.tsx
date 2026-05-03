import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../api/auth'
import { useAuthStore } from '../store/authStore'

export default function RegisterPage() {
  const navigate = useNavigate()
  const { setTokens, setUser } = useAuthStore()
  const [form, setForm] = useState({ email: '', username: '', password: '', city: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const set = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await authApi.register(
        form.email, form.username, form.password, form.city || undefined,
      )
      setTokens(data.accessToken, data.refreshToken)
      const { data: profile } = await authApi.getMe()
      setUser(profile.id, profile.username)
      navigate('/')
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message
      setError(msg ?? 'Ошибка регистрации. Проверьте данные.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 w-full max-w-sm">
        <div className="text-center mb-6">
          <span className="text-4xl">🌿</span>
          <h1 className="mt-2 text-2xl font-bold text-gray-900">Создать аккаунт</h1>
          <p className="text-gray-500 text-sm mt-1">Присоединяйтесь к сообществу</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {[
            { label: 'Email', field: 'email', type: 'email', placeholder: 'you@example.com', required: true },
            { label: 'Имя пользователя', field: 'username', type: 'text', placeholder: 'greenthumb', required: true },
            { label: 'Пароль', field: 'password', type: 'password', placeholder: '••••••••', required: true },
            { label: 'Город (необязательно)', field: 'city', type: 'text', placeholder: 'Москва', required: false },
          ].map(({ label, field, type, placeholder, required }) => (
            <div key={field}>
              <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
              <input
                type={type} required={required} value={form[field as keyof typeof form]}
                onChange={set(field)} placeholder={placeholder}
                className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
              />
            </div>
          ))}
          {error && <p className="text-red-500 text-sm">{error}</p>}
          <button
            type="submit" disabled={loading}
            className="w-full bg-brand-600 text-white py-2 rounded-lg font-medium hover:bg-brand-700 disabled:opacity-60 transition-colors"
          >
            {loading ? 'Регистрируем...' : 'Зарегистрироваться'}
          </button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-4">
          Уже есть аккаунт?{' '}
          <Link to="/login" className="text-brand-600 hover:underline">Войти</Link>
        </p>
      </div>
    </div>
  )
}
