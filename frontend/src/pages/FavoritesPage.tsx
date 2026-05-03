import { useQuery } from '@tanstack/react-query'
import { listingsApi } from '../api/listings'
import { useAuthStore } from '../store/authStore'
import ListingCard from '../components/ListingCard'
import Spinner from '../components/Spinner'

export default function FavoritesPage() {
  const { accessToken } = useAuthStore()

  const { data, isLoading } = useQuery({
    queryKey: ['favorites'],
    queryFn: () => listingsApi.getFavorites().then((r) => r.data),
    enabled: !!accessToken,
  })

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Избранное</h1>
      {isLoading ? (
        <Spinner className="py-16" />
      ) : !data?.content.length ? (
        <div className="text-center py-16 text-gray-400">
          <span className="text-5xl block mb-3">♡</span>
          <p>Вы ещё не добавили объявлений в избранное</p>
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
