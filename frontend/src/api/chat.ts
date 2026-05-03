import api from '../lib/axios'
import type { Message, Page } from '../types'

export const chatApi = {
  getHistory: (dealId: string, page = 0, size = 50) =>
    api.get<Page<Message>>(`/conversations/${dealId}/messages`, {
      params: { page, size },
    }),
}
