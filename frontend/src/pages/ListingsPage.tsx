import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { listingsApi } from '../api/listings'
import { useAuthStore } from '../store/authStore'
import ListingCard from '../components/ListingCard'
import Spinner from '../components/Spinner'

const TYPES = [
  { value: '', label: 'Все' },
  { value: 'GIVE_AWAY', label: 'Отдам' },
  { value: 'SWAP', label: 'Обмен' },
  { value: 'SELL', label: 'Продам' },
]

export default function ListingsPage() {
  const { accessToken } = useAuthStore()
  const [search, setSearch] = useState('')
  const [type, setType] = useState('')
  const [city, setCity] = useState('')
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['listings', search, type, city, page],
    queryFn: () =>
      listingsApi.search({
        searchQuery: search || undefined,
        type: type || undefined,
        city: city || undefined,
        page,
        size: 20,
      }).then((r) => r.data),
    placeholderData: (prev) => prev,
  })

  return (
    <div>
      <div className="flex flex-col sm:flex-row gap-3 mb-6">
        <input
          value={search} onChange={(e) => { setSearch(e.target.value); setPage(0) }}
          placeholder="Поиск растений..."
          className="flex-1 border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
        />
        <input
          value={city} onChange={(e) => { setCity(e.target.value); setPage(0) }}
          placeholder="Город"
          className="w-40 border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400"
        />
        <div className="flex gap-1">
          {TYPES.map((t) => (
            <button
              key={t.value}
              onClick={() => { setType(t.value); setPage(0) }}
              className={`px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                type === t.value
                  ? 'bg-brand-600 text-white'
                  : 'bg-white border border-gray-200 text-gray-600 hover:border-brand-400'
              }`}
            >
              {t.label}
            </button>
          ))}
        </div>
        {accessToken && (
          <Link
            to="/listings/new"
            className="bg-brand-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-brand-700 transition-colors whitespace-nowrap"
          >
            + Создать
          </Link>
        )}
      </div>

      {isLoading ? (
        <Spinner className="py-16" />
      ) : !data?.content.length ? (
        <div className="text-center py-16 text-gray-400">
          <span className="text-5xl block mb-3">🌱</span>
          <p>Объявлений не найдено</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            {data.content.map((listing) => (
              <ListingCard key={listing.id} listing={listing} />
            ))}
          </div>
          {data.totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-8">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-4 py-2 rounded-lg border border-gray-200 text-sm disabled:opacity-40 hover:border-brand-400 transition-colors"
              >
                ← Назад
              </button>
              <span className="px-4 py-2 text-sm text-gray-500">
                {page + 1} / {data.totalPages}
              </span>
              <button
                onClick={() => setPage((p) => p + 1)}
                disabled={page >= data.totalPages - 1}
                className="px-4 py-2 rounded-lg border border-gray-200 text-sm disabled:opacity-40 hover:border-brand-400 transition-colors"
              >
                Вперёд →
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}
