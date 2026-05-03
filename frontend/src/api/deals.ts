import api from '../lib/axios'
import type { Deal, Page } from '../types'

export const dealsApi = {
  create: (listingId: string, ownerId: string, note?: string) =>
    api.post<Deal>('/deals', { listingId, ownerId, note }),

  getById: (id: string) => api.get<Deal>(`/deals/${id}`),

  getMyDeals: (page = 0, size = 20) =>
    api.get<Page<Deal>>('/deals/my', { params: { page, size } }),

  accept: (id: string) => api.post<Deal>(`/deals/${id}/accept`),
  reject: (id: string) => api.post<Deal>(`/deals/${id}/reject`),
  complete: (id: string) => api.post<Deal>(`/deals/${id}/complete`),
  cancel: (id: string) => api.post<Deal>(`/deals/${id}/cancel`),
}
