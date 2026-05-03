import type { DealStatus } from '../types'

const CONFIG: Record<DealStatus, { label: string; className: string }> = {
  PENDING:   { label: 'Ожидает ответа', className: 'bg-yellow-100 text-yellow-700' },
  ACCEPTED:  { label: 'Принята',        className: 'bg-blue-100 text-blue-700' },
  REJECTED:  { label: 'Отклонена',      className: 'bg-red-100 text-red-600' },
  COMPLETED: { label: 'Завершена',      className: 'bg-green-100 text-green-700' },
  CANCELLED: { label: 'Отменена',       className: 'bg-gray-100 text-gray-500' },
}

export default function DealStatusBadge({ status }: { status: DealStatus }) {
  const { label, className } = CONFIG[status]
  return (
    <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${className}`}>
      {label}
    </span>
  )
}
