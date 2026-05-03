import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import { listingsApi } from '../api/listings'

const TYPES = [
  { value: 'GIVE_AWAY', label: 'Отдам' },
  { value: 'SWAP', label: 'Обмен' },
  { value: 'SELL', label: 'Продам' },
]

export default function CreateListingPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({
    type: 'SWAP', title: '', description: '', city: '',
    priceAmount: '', priceCurrency: 'RUB',
  })
  const [photos, setPhotos] = useState<File[]>([])
  const [error, setError] = useState('')

  const set = (field: string) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) =>
      setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const mutation = useMutation({
    mutationFn: async () => {
      const { data: listing } = await listingsApi.create({
        type: form.type,
        title: form.title,
        description: form.description || undefined,
        city: form.city || undefined,
        priceAmount: form.type === 'SELL' ? Number(form.priceAmount) : undefined,
        priceCurrency: form.type === 'SELL' ? form.priceCurrency : undefined,
      })
      for (const file of photos) {
        await listingsApi.uploadPhoto(listing.id, file)
      }
      return listing
    },
    onSuccess: (listing) => navigate(`/listings/${listing.id}`),
    onError: (err: unknown) => {
      const msg = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message
      setError(msg ?? 'Ошибка при создании объявления')
    },
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!form.title.trim()) { setError('Введите название'); return }
    if (form.type === 'SELL' && !form.priceAmount) { setError('Укажите цену'); return }
    setError('')
    mutation.mutate()
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Новое объявление</h1>
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 space-y-5">
        {/* Тип */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Тип объявления</label>
          <div className="flex gap-2">
            {TYPES.map((t) => (
              <button key={t.value} type="button"
                onClick={() => setForm((p) => ({ ...p, type: t.value }))}
                className={`flex-1 py-2 rounded-lg text-sm font-medium border transition-colors ${
                  form.type === t.value
                    ? 'bg-brand-600 text-white border-brand-600'
                    : 'border-gray-200 text-gray-600 hover:border-brand-400'
                }`}
              >
                {t.label}
              </button>
            ))}
          </div>
        </div>

        {/* Название */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Название <span className="text-red-400">*</span></label>
          <input value={form.title} onChange={set('title')} required
            placeholder="Монстера делициоза, черенок"
            className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
          />
        </div>

        {/* Описание */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Описание</label>
          <textarea value={form.description} onChange={set('description')} rows={4}
            placeholder="Расскажите подробнее о растении..."
            className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 resize-none"
          />
        </div>

        {/* Цена (только SELL) */}
        {form.type === 'SELL' && (
          <div className="flex gap-3">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">Цена <span className="text-red-400">*</span></label>
              <input value={form.priceAmount} onChange={set('priceAmount')} type="number" min="0" step="1"
                placeholder="500"
                className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
              />
            </div>
            <div className="w-24">
              <label className="block text-sm font-medium text-gray-700 mb-1">Валюта</label>
              <select value={form.priceCurrency} onChange={set('priceCurrency')}
                className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
              >
                <option>RUB</option><option>USD</option><option>EUR</option>
              </select>
            </div>
          </div>
        )}

        {/* Город */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Город</label>
          <input value={form.city} onChange={set('city')} placeholder="Москва"
            className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
          />
        </div>

        {/* Фото */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Фотографии (до 10)</label>
          <input type="file" accept="image/*" multiple
            onChange={(e) => setPhotos(Array.from(e.target.files ?? []).slice(0, 10))}
            className="w-full text-sm text-gray-500 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-sm file:font-medium file:bg-brand-50 file:text-brand-700 hover:file:bg-brand-100"
          />
          {photos.length > 0 && (
            <p className="text-xs text-gray-400 mt-1">Выбрано: {photos.length} фото</p>
          )}
        </div>

        {error && <p className="text-red-500 text-sm">{error}</p>}

        <button type="submit" disabled={mutation.isPending}
          className="w-full bg-brand-600 text-white py-2.5 rounded-lg font-medium hover:bg-brand-700 disabled:opacity-60 transition-colors"
        >
          {mutation.isPending ? 'Публикуем...' : 'Опубликовать'}
        </button>
      </form>
    </div>
  )
}
