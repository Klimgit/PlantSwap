import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { listingsApi } from '../api/listings'
import { useAuthStore } from '../store/authStore'
import ListingCard from '../components/ListingCard'
import Spinner from '../components/Spinner'

export default function MyListingsPage() {
  const { userId } = useAuthStore()

  const { data, isLoading } = useQuery({
    queryKey: ['my-listings', userId],
    queryFn: () => listingsApi.getUserListings(userId!).then((r) => r.data),
    enabled: !!userId,
  })

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Мои объявления</h1>
        <Link to="/listings/new"
          className="bg-brand-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-brand-700 transition-colors"
        >
          + Создать
        </Link>
      </div>

      {isLoading ? (
        <Spinner className="py-16" />
      ) : !data?.content.length ? (
        <div className="text-center py-16 text-gray-400">
          <span className="text-5xl block mb-3">🌱</span>
          <p className="mb-4">У вас пока нет объявлений</p>
          <Link to="/listings/new"
            className="bg-brand-600 text-white px-6 py-2.5 rounded-lg text-sm font-medium hover:bg-brand-700 transition-colors"
          >
            Создать первое объявление
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          {data.content.map((listing) => (
            <ListingCard key={listing.id} listing={listing} />
          ))}
        </div>
      )}
    </div>
  )
}
