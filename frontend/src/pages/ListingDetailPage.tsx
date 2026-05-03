import { useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { listingsApi } from '../api/listings'
import { dealsApi } from '../api/deals'
import { useAuthStore } from '../store/authStore'
import Spinner from '../components/Spinner'

const TYPE_LABELS = { GIVE_AWAY: 'Отдам', SWAP: 'Обмен', SELL: 'Продам' }

export default function ListingDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const qc = useQueryClient()
  const { userId, accessToken } = useAuthStore()
  const [dealNote, setDealNote] = useState('')
  const [showDealForm, setShowDealForm] = useState(false)

  const { data: listing, isLoading } = useQuery({
    queryKey: ['listing', id],
    queryFn: () => listingsApi.getById(id!).then((r) => r.data),
    enabled: !!id,
  })

  const favMutation = useMutation({
    mutationFn: () =>
      listing?.isFavorite
        ? listingsApi.removeFavorite(id!)
        : listingsApi.addFavorite(id!),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['listing', id] }),
  })

  const dealMutation = useMutation({
    mutationFn: () => dealsApi.create(id!, listing!.ownerId, dealNote || undefined),
    onSuccess: () => navigate('/deals'),
  })

  const deleteMutation = useMutation({
    mutationFn: () => listingsApi.delete(id!),
    onSuccess: () => navigate('/my-listings'),
  })

  if (isLoading) return <Spinner className="py-16" />
  if (!listing) return <p className="text-center py-16 text-gray-400">Объявление не найдено</p>

  const isOwner = listing.ownerId === userId
  const isActive = listing.status === 'ACTIVE'

  return (
    <div className="max-w-3xl mx-auto">
      <Link to="/" className="text-sm text-gray-500 hover:text-brand-600 mb-4 inline-block">
        ← К объявлениям
      </Link>

      <div className="bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm">
        {/* Фото */}
        {listing.photos.length > 0 && (
          <div className="grid grid-cols-3 gap-1">
            {listing.photos.slice(0, 3).map((photo, i) => (
              <div key={photo.id} className={`overflow-hidden ${i === 0 ? 'col-span-2 row-span-2' : ''}`}>
                <img src={photo.url} alt="" className="w-full h-full object-cover aspect-square" />
              </div>
            ))}
          </div>
        )}
        {listing.photos.length === 0 && (
          <div className="h-48 bg-gray-50 flex items-center justify-center text-6xl">🌱</div>
        )}

        <div className="p-6">
          <div className="flex items-start justify-between gap-4 mb-3">
            <h1 className="text-2xl font-bold text-gray-900">{listing.title}</h1>
            <div className="flex items-center gap-2 shrink-0">
              <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${
                listing.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
              }`}>
                {listing.status === 'ACTIVE' ? 'Активно' : 'Закрыто'}
              </span>
              <span className="text-xs bg-blue-50 text-blue-700 px-2.5 py-1 rounded-full font-medium">
                {TYPE_LABELS[listing.type]}
              </span>
            </div>
          </div>

          {listing.priceAmount != null && (
            <p className="text-2xl font-bold text-brand-700 mb-3">
              {listing.priceAmount} {listing.priceCurrency ?? 'RUB'}
            </p>
          )}

          {listing.city && (
            <p className="text-gray-500 text-sm mb-4">📍 {listing.city}</p>
          )}

          {listing.description && (
            <p className="text-gray-700 leading-relaxed mb-6">{listing.description}</p>
          )}

          <div className="flex flex-wrap gap-3">
            {/* Кнопки для авторизованного чужого пользователя */}
            {accessToken && !isOwner && isActive && (
              <>
                <button
                  onClick={() => favMutation.mutate()}
                  disabled={favMutation.isPending}
                  className={`px-4 py-2 rounded-lg text-sm font-medium border transition-colors ${
                    listing.isFavorite
                      ? 'border-red-200 text-red-600 hover:bg-red-50'
                      : 'border-gray-200 text-gray-600 hover:border-brand-400'
                  }`}
                >
                  {listing.isFavorite ? '♥ Убрать' : '♡ В избранное'}
                </button>
                <button
                  onClick={() => setShowDealForm((v) => !v)}
                  className="bg-brand-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-brand-700 transition-colors"
                >
                  Предложить сделку
                </button>
              </>
            )}

            {/* Кнопки для владельца */}
            {isOwner && (
              <>
                <Link
                  to={`/listings/${id}/edit`}
                  className="px-4 py-2 rounded-lg text-sm font-medium border border-gray-200 text-gray-600 hover:border-brand-400 transition-colors"
                >
                  Редактировать
                </Link>
                {isActive && (
                  <button
                    onClick={() => { if (confirm('Закрыть объявление?')) deleteMutation.mutate() }}
                    disabled={deleteMutation.isPending}
                    className="px-4 py-2 rounded-lg text-sm font-medium border border-red-200 text-red-600 hover:bg-red-50 transition-colors"
                  >
                    Закрыть
                  </button>
                )}
              </>
            )}
          </div>

          {/* Форма предложения сделки */}
          {showDealForm && (
            <div className="mt-4 p-4 bg-gray-50 rounded-xl border border-gray-200">
              <h3 className="font-medium text-gray-800 mb-2">Сопроводительное сообщение</h3>
              <textarea
                value={dealNote}
                onChange={(e) => setDealNote(e.target.value)}
                placeholder="Расскажите о своём предложении (необязательно)..."
                rows={3}
                className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 resize-none"
              />
              <div className="flex gap-2 mt-2">
                <button
                  onClick={() => dealMutation.mutate()}
                  disabled={dealMutation.isPending}
                  className="bg-brand-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-brand-700 disabled:opacity-60 transition-colors"
                >
                  {dealMutation.isPending ? 'Отправляем...' : 'Отправить запрос'}
                </button>
                <button
                  onClick={() => setShowDealForm(false)}
                  className="px-4 py-2 rounded-lg text-sm border border-gray-200 text-gray-600 hover:bg-gray-50 transition-colors"
                >
                  Отмена
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
