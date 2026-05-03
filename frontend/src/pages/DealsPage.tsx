import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { dealsApi } from '../api/deals'
import { useAuthStore } from '../store/authStore'
import DealStatusBadge from '../components/DealStatusBadge'
import Spinner from '../components/Spinner'
import type { Deal } from '../types'

export default function DealsPage() {
  const { userId } = useAuthStore()
  const qc = useQueryClient()
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['my-deals', page],
    queryFn: () => dealsApi.getMyDeals(page).then((r) => r.data),
    placeholderData: (prev) => prev,
  })

  const actionMutation = useMutation({
    mutationFn: ({ id, action }: { id: string; action: string }) => {
      if (action === 'accept') return dealsApi.accept(id)
      if (action === 'reject') return dealsApi.reject(id)
      if (action === 'complete') return dealsApi.complete(id)
      return dealsApi.cancel(id)
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['my-deals'] }),
  })

  if (isLoading) return <Spinner className="py-16" />

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Мои сделки</h1>

      {!data?.content.length ? (
        <div className="text-center py-16 text-gray-400">
          <span className="text-5xl block mb-3">🤝</span>
          <p>Сделок пока нет</p>
        </div>
      ) : (
        <div className="space-y-4">
          {data.content.map((deal) => (
            <DealCard
              key={deal.id} deal={deal} userId={userId!}
              onAction={(action) => actionMutation.mutate({ id: deal.id, action })}
              loading={actionMutation.isPending}
            />
          ))}
          {data.totalPages > 1 && (
            <div className="flex justify-center gap-2 pt-4">
              <button onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}
                className="px-4 py-2 rounded-lg border border-gray-200 text-sm disabled:opacity-40">← Назад</button>
              <span className="px-4 py-2 text-sm text-gray-500">{page + 1} / {data.totalPages}</span>
              <button onClick={() => setPage((p) => p + 1)} disabled={page >= data.totalPages - 1}
                className="px-4 py-2 rounded-lg border border-gray-200 text-sm disabled:opacity-40">Вперёд →</button>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

function DealCard({ deal, userId, onAction, loading }: {
  deal: Deal; userId: string
  onAction: (action: string) => void; loading: boolean
}) {
  const isOwner = deal.ownerId === userId

  return (
    <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-5">
      <div className="flex items-start justify-between gap-4 mb-3">
        <div>
          <Link to={`/listings/${deal.listingId}`}
            className="text-sm text-brand-600 hover:underline font-medium">
            Объявление {deal.listingId.slice(0, 8)}…
          </Link>
          <p className="text-xs text-gray-400 mt-0.5">
            {isOwner ? 'Входящая заявка' : 'Ваша заявка'} · {new Date(deal.createdAt).toLocaleDateString('ru')}
          </p>
        </div>
        <DealStatusBadge status={deal.status} />
      </div>

      {deal.note && (
        <p className="text-sm text-gray-600 bg-gray-50 rounded-lg px-3 py-2 mb-3 italic">
          «{deal.note}»
        </p>
      )}

      <div className="flex flex-wrap gap-2">
        {/* Действия владельца при PENDING */}
        {isOwner && deal.status === 'PENDING' && (
          <>
            <ActionBtn onClick={() => onAction('accept')} loading={loading} variant="primary">Принять</ActionBtn>
            <ActionBtn onClick={() => onAction('reject')} loading={loading} variant="danger">Отклонить</ActionBtn>
          </>
        )}

        {/* Завершить при ACCEPTED */}
        {deal.status === 'ACCEPTED' && (
          <>
            <Link to={`/deals/${deal.id}`}
              className="px-3 py-1.5 rounded-lg text-sm font-medium bg-blue-50 text-blue-700 hover:bg-blue-100 transition-colors">
              Открыть чат
            </Link>
            <ActionBtn onClick={() => onAction('complete')} loading={loading} variant="primary">
              Завершить
            </ActionBtn>
          </>
        )}

        {/* Отмена из PENDING или ACCEPTED */}
        {(deal.status === 'PENDING' || deal.status === 'ACCEPTED') && (
          <ActionBtn onClick={() => onAction('cancel')} loading={loading} variant="ghost">Отменить</ActionBtn>
        )}
      </div>
    </div>
  )
}

function ActionBtn({ onClick, loading, variant, children }: {
  onClick: () => void; loading: boolean
  variant: 'primary' | 'danger' | 'ghost'; children: React.ReactNode
}) {
  const cls = {
    primary: 'bg-brand-600 text-white hover:bg-brand-700',
    danger: 'border border-red-200 text-red-600 hover:bg-red-50',
    ghost: 'border border-gray-200 text-gray-600 hover:bg-gray-50',
  }[variant]
  return (
    <button onClick={onClick} disabled={loading}
      className={`px-3 py-1.5 rounded-lg text-sm font-medium disabled:opacity-50 transition-colors ${cls}`}>
      {children}
    </button>
  )
}
