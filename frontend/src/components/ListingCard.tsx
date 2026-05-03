import { Link } from 'react-router-dom'
import type { ListingSummary } from '../types'

const TYPE_LABELS = { GIVE_AWAY: 'Отдам', SWAP: 'Обмен', SELL: 'Продам' }
const TYPE_COLORS = {
  GIVE_AWAY: 'bg-green-100 text-green-700',
  SWAP: 'bg-blue-100 text-blue-700',
  SELL: 'bg-amber-100 text-amber-700',
}

export default function ListingCard({ listing }: { listing: ListingSummary }) {
  return (
    <Link to={`/listings/${listing.id}`} className="group block">
      <div className="bg-white rounded-xl overflow-hidden border border-gray-100 shadow-sm hover:shadow-md transition-shadow">
        <div className="aspect-square bg-gray-100 overflow-hidden">
          {listing.firstPhotoUrl ? (
            <img
              src={listing.firstPhotoUrl}
              alt={listing.title}
              className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-4xl text-gray-300">
              🌱
            </div>
          )}
        </div>
        <div className="p-3">
          <div className="flex items-start justify-between gap-2 mb-1">
            <h3 className="font-medium text-gray-900 line-clamp-2 text-sm leading-tight">
              {listing.title}
            </h3>
            <span className={`shrink-0 text-xs px-2 py-0.5 rounded-full font-medium ${TYPE_COLORS[listing.type]}`}>
              {TYPE_LABELS[listing.type]}
            </span>
          </div>
          {listing.priceAmount != null && (
            <p className="text-brand-700 font-semibold text-sm">
              {listing.priceAmount} {listing.priceCurrency ?? 'RUB'}
            </p>
          )}
          {listing.city && (
            <p className="text-gray-400 text-xs mt-1 truncate">📍 {listing.city}</p>
          )}
        </div>
      </div>
    </Link>
  )
}
