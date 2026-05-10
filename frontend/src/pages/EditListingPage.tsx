import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { listingsApi } from '../api/listings'
import { useAuthStore } from '../store/authStore'
import Spinner from '../components/Spinner'

const TYPES = [
  { value: 'GIVE_AWAY', label: 'Отдам' },
  { value: 'SWAP', label: 'Обмен' },
  { value: 'SELL', label: 'Продам' },
]

export default function EditListingPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const qc = useQueryClient()
  const { userId } = useAuthStore()
  const [form, setForm] = useState({
    type: 'SWAP',
    title: '',
    description: '',
    city: '',
    priceAmount: '',
    priceCurrency: 'RUB',
  })
  const [newPhotos, setNewPhotos] = useState<File[]>([])
  const [error, setError] = useState('')

  const { data: listing, isLoading, isError } = useQuery({
    queryKey: ['listing', id],
    queryFn: () => listingsApi.getById(id!).then((r) => r.data),
    enabled: !!id,
  })

  useEffect(() => {
    if (!listing) return
    setForm({
      type: listing.type,
      title: listing.title,
      description: listing.description ?? '',
      city: listing.city ?? '',
      priceAmount: listing.priceAmount != null ? String(listing.priceAmount) : '',
      priceCurrency: listing.priceCurrency ?? 'RUB',
    })
  }, [listing])

  const updateMutation = useMutation({
    mutationFn: async () => {
      await listingsApi.update(id!, {
        type: form.type,
        title: form.title,
        description: form.description || undefined,
        city: form.city || undefined,
        priceAmount: form.type === 'SELL' ? Number(form.priceAmount) : undefined,
        priceCurrency: form.type === 'SELL' ? form.priceCurrency : undefined,
      })
      for (const file of newPhotos) {
        await listingsApi.uploadPhoto(id!, file)
      }
    },
    onSuccess: async () => {
      await qc.invalidateQueries({ queryKey: ['listing', id] })
      await qc.invalidateQueries({ queryKey: ['my-listings'] })
      navigate(`/listings/${id}`)
    },
    onError: (err: unknown) => {
      const msg = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message
      setError(msg ?? 'Не удалось сохранить изменения')
    },
  })

  const removePhotoMutation = useMutation({
    mutationFn: (photoId: string) => listingsApi.removePhoto(id!, photoId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['listing', id] }),
  })

  const set =
    (field: string) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) =>
      setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!form.title.trim()) {
      setError('Введите название')
      return
    }
    if (form.type === 'SELL' && !form.priceAmount) {
      setError('Укажите цену')
      return
    }
    setError('')
    updateMutation.mutate()
  }

  if (isLoading) return <Spinner className="py-16" />
  if (isError || !listing) {
    return <p className="text-center py-16 text-gray-400">Объявление не найдено</p>
  }

  if (listing.ownerId !== userId) {
    return (
      <div className="max-w-lg mx-auto text-center py-16">
        <p className="text-gray-600 mb-4">Редактировать может только владелец объявления.</p>
        <Link to={`/listings/${id}`} className="text-brand-600 hover:underline">
          Вернуться к объявлению
        </Link>
      </div>
    )
  }

  return (
    <div className="max-w-2xl mx-auto">
      <Link to={`/listings/${id}`} className="text-sm text-gray-500 hover:text-brand-600 mb-4 inline-block">
        ← Назад к объявлению
      </Link>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Редактировать объявление</h1>
      <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 space-y-5">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Тип объявления</label>
          <div className="flex gap-2">
            {TYPES.map((t) => (
              <button
                key={t.value}
                type="button"
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

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Название <span className="text-red-400">*</span>
          </label>
          <input
            value={form.title}
            onChange={set('title')}
            required
            className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Описание</label>
          <textarea
            value={form.description}
            onChange={set('description')}
            rows={4}
            className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 resize-none"
          />
        </div>

        {form.type === 'SELL' && (
          <div className="flex gap-3">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Цена <span className="text-red-400">*</span>
              </label>
              <input
                value={form.priceAmount}
                onChange={set('priceAmount')}
                type="number"
                min="0"
                step="1"
                className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
              />
            </div>
            <div className="w-24">
              <label className="block text-sm font-medium text-gray-700 mb-1">Валюта</label>
              <select
                value={form.priceCurrency}
                onChange={set('priceCurrency')}
                className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
              >
                <option>RUB</option>
                <option>USD</option>
                <option>EUR</option>
              </select>
            </div>
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Город</label>
          <input
            value={form.city}
            onChange={set('city')}
            className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
          />
        </div>

        {listing.photos.length > 0 && (
          <div>
            <p className="block text-sm font-medium text-gray-700 mb-2">Текущие фото</p>
            <div className="flex flex-wrap gap-3">
              {listing.photos.map((ph) => (
                <div key={ph.id} className="relative w-24 h-24 rounded-lg overflow-hidden border border-gray-100">
                  <img src={ph.url} alt="" className="w-full h-full object-cover" />
                  <button
                    type="button"
                    onClick={() => removePhotoMutation.mutate(ph.id)}
                    disabled={removePhotoMutation.isPending}
                    className="absolute bottom-1 right-1 text-xs bg-red-600 text-white px-1.5 py-0.5 rounded"
                  >
                    ✕
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Добавить фото</label>
          <input
            type="file"
            accept="image/*"
            multiple
            onChange={(e) => setNewPhotos(Array.from(e.target.files ?? []).slice(0, 10))}
            className="w-full text-sm text-gray-500 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-sm file:font-medium file:bg-brand-50 file:text-brand-700 hover:file:bg-brand-100"
          />
          {newPhotos.length > 0 && (
            <p className="text-xs text-gray-400 mt-1">Будет загружено: {newPhotos.length}</p>
          )}
        </div>

        {error && <p className="text-red-500 text-sm">{error}</p>}

        <button
          type="submit"
          disabled={updateMutation.isPending}
          className="w-full bg-brand-600 text-white py-2.5 rounded-lg font-medium hover:bg-brand-700 disabled:opacity-60 transition-colors"
        >
          {updateMutation.isPending ? 'Сохраняем...' : 'Сохранить'}
        </button>
      </form>
    </div>
  )
}
