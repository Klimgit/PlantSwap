import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { dealsApi } from '../api/deals'
import { useAuthStore } from '../store/authStore'
import DealStatusBadge from '../components/DealStatusBadge'
import ChatWindow from '../components/ChatWindow'
import Spinner from '../components/Spinner'

export default function DealDetailPage() {
  const { id } = useParams<{ id: string }>()
  const { userId } = useAuthStore()
  const qc = useQueryClient()

  const { data: deal, isLoading } = useQuery({
    queryKey: ['deal', id],
    queryFn: () => dealsApi.getById(id!).then((r) => r.data),
    enabled: !!id,
  })

  const mutation = useMutation({
    mutationFn: (action: string) => {
      if (action === 'accept') return dealsApi.accept(id!)
      if (action === 'reject') return dealsApi.reject(id!)
      if (action === 'complete') return dealsApi.complete(id!)
      return dealsApi.cancel(id!)
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['deal', id] })
      qc.invalidateQueries({ queryKey: ['my-deals'] })
    },
  })

  if (isLoading) return <Spinner className="py-16" />
  if (!deal) return <p className="text-center py-16 text-gray-400">Сделка не найдена</p>

  const isOwner = deal.ownerId === userId

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center gap-3">
        <Link to="/deals" className="text-sm text-gray-500 hover:text-brand-600">← К сделкам</Link>
      </div>

      {/* Карточка сделки */}
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
        <div className="flex items-start justify-between gap-4 mb-4">
          <div>
            <h1 className="text-xl font-bold text-gray-900 mb-1">Сделка по объявлению</h1>
            <Link to={`/listings/${deal.listingId}`}
              className="text-sm text-brand-600 hover:underline">
              Перейти к объявлению →
            </Link>
          </div>
          <DealStatusBadge status={deal.status} />
        </div>

        {deal.note && (
          <div className="bg-gray-50 rounded-xl px-4 py-3 mb-4">
            <p className="text-xs text-gray-400 mb-1">Сопроводительное сообщение</p>
            <p className="text-sm text-gray-700 italic">«{deal.note}»</p>
          </div>
        )}

        <div className="text-xs text-gray-400 mb-4">
          Создана: {new Date(deal.createdAt).toLocaleString('ru')} ·
          Обновлена: {new Date(deal.updatedAt).toLocaleString('ru')}
        </div>

        {/* Действия */}
        <div className="flex flex-wrap gap-2">
          {isOwner && deal.status === 'PENDING' && (
            <>
              <button onClick={() => mutation.mutate('accept')} disabled={mutation.isPending}
                className="bg-brand-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-brand-700 disabled:opacity-60 transition-colors">
                Принять
              </button>
              <button onClick={() => mutation.mutate('reject')} disabled={mutation.isPending}
                className="border border-red-200 text-red-600 px-4 py-2 rounded-lg text-sm font-medium hover:bg-red-50 transition-colors">
                Отклонить
              </button>
            </>
          )}
          {deal.status === 'ACCEPTED' && (
            <button onClick={() => mutation.mutate('complete')} disabled={mutation.isPending}
              className="bg-brand-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-brand-700 disabled:opacity-60 transition-colors">
              Подтвердить передачу растения
            </button>
          )}
          {(deal.status === 'PENDING' || deal.status === 'ACCEPTED') && (
            <button onClick={() => mutation.mutate('cancel')} disabled={mutation.isPending}
              className="border border-gray-200 text-gray-600 px-4 py-2 rounded-lg text-sm font-medium hover:bg-gray-50 transition-colors">
              Отменить сделку
            </button>
          )}
        </div>
      </div>

      {/* Чат (доступен пока сделка не отклонена/отменена) */}
      {deal.status !== 'REJECTED' && deal.status !== 'CANCELLED' && (
        <div>
          <h2 className="text-lg font-semibold text-gray-800 mb-3">Переписка</h2>
          <ChatWindow dealId={deal.id} />
        </div>
      )}
    </div>
  )
}
