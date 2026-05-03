import api from '../lib/axios'
import type { Listing, ListingSummary, Page, Photo } from '../types'

export interface ListingFilter {
  searchQuery?: string
  type?: string
  city?: string
  priceMin?: number
  priceMax?: number
  page?: number
  size?: number
}

export const listingsApi = {
  search: (filter: ListingFilter) =>
    api.get<Page<ListingSummary>>('/listings', { params: filter }),

  getById: (id: string) =>
    api.get<Listing>(`/listings/${id}`),

  getUserListings: (ownerId: string, page = 0, size = 20) =>
    api.get<Page<ListingSummary>>(`/listings`, { params: { ownerId, page, size } }),

  create: (data: {
    type: string; title: string; description?: string
    priceAmount?: number; priceCurrency?: string; city?: string
  }) => api.post<Listing>('/listings', data),

  update: (id: string, data: {
    type: string; title: string; description?: string
    priceAmount?: number; priceCurrency?: string; city?: string
  }) => api.put<Listing>(`/listings/${id}`, data),

  delete: (id: string) => api.delete(`/listings/${id}`),

  uploadPhoto: (id: string, file: File) => {
    const form = new FormData()
    form.append('file', file)
    return api.post<Photo>(`/listings/${id}/photos`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },

  removePhoto: (listingId: string, photoId: string) =>
    api.delete(`/listings/${listingId}/photos/${photoId}`),

  addFavorite: (id: string) => api.post(`/listings/${id}/favorites`),
  removeFavorite: (id: string) => api.delete(`/listings/${id}/favorites`),
  getFavorites: (page = 0, size = 20) =>
    api.get<Page<ListingSummary>>('/listings/favorites', { params: { page, size } }),
}
