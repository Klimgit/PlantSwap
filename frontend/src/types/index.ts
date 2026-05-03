// ── Auth ──────────────────────────────────────────────────────────────────────
export interface TokenPair {
  accessToken: string
  refreshToken: string
}

export interface UserProfile {
  id: string
  email: string
  username: string
  city: string | null
  createdAt: string
}

// ── Listings ──────────────────────────────────────────────────────────────────
export type ListingType = 'GIVE_AWAY' | 'SWAP' | 'SELL'
export type ListingStatus = 'ACTIVE' | 'CLOSED'

export interface Photo {
  id: string
  url: string
  sortOrder: number
}

export interface Listing {
  id: string
  ownerId: string
  type: ListingType
  title: string
  description: string | null
  priceAmount: number | null
  priceCurrency: string | null
  city: string | null
  status: ListingStatus
  photos: Photo[]
  isFavorite: boolean
  createdAt: string
  updatedAt: string
}

export interface ListingSummary {
  id: string
  ownerId: string
  type: ListingType
  title: string
  priceAmount: number | null
  priceCurrency: string | null
  city: string | null
  status: ListingStatus
  firstPhotoUrl: string | null
  createdAt: string
}

export interface Page<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

// ── Deals ─────────────────────────────────────────────────────────────────────
export type DealStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'COMPLETED' | 'CANCELLED'

export interface Deal {
  id: string
  listingId: string
  ownerId: string
  requesterId: string
  status: DealStatus
  note: string | null
  createdAt: string
  updatedAt: string
}

// ── Chat ──────────────────────────────────────────────────────────────────────
export interface Message {
  id: string
  conversationId: string
  senderId: string
  content: string
  sentAt: string
}
