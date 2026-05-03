import { useEffect, useRef, useState } from 'react'
import { Client, type IMessage } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useQuery } from '@tanstack/react-query'
import { chatApi } from '../api/chat'
import { useAuthStore } from '../store/authStore'
import type { Message } from '../types'
import Spinner from './Spinner'

export default function ChatWindow({ dealId }: { dealId: string }) {
  const { accessToken, userId } = useAuthStore()
  const bottomRef = useRef<HTMLDivElement>(null)
  const [liveMessages, setLiveMessages] = useState<Message[]>([])
  const [inputText, setInputText] = useState('')
  const clientRef = useRef<Client | null>(null)
  const [connected, setConnected] = useState(false)

  // История из REST
  const { data: historyData, isLoading } = useQuery({
    queryKey: ['chat-history', dealId],
    queryFn: () => chatApi.getHistory(dealId).then((r) => r.data),
    enabled: !!accessToken,
  })

  // История приходит от новых к старым — переворачиваем
  const history = historyData ? [...historyData.content].reverse() : []

  // STOMP подключение
  useEffect(() => {
    if (!accessToken) return

    const client = new Client({
      webSocketFactory: () =>
        new SockJS('/ws', null, {
          transports: ['websocket', 'xhr-streaming', 'xhr-polling'],
        }),
      connectHeaders: { Authorization: `Bearer ${accessToken}` },
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true)
        client.subscribe(`/topic/deal/${dealId}`, (frame: IMessage) => {
          const msg: Message = JSON.parse(frame.body)
          setLiveMessages((prev) => {
            // Дедупликация по ID
            if (prev.some((m) => m.id === msg.id)) return prev
            return [...prev, msg]
          })
        })
      },
      onDisconnect: () => setConnected(false),
    })

    client.activate()
    clientRef.current = client

    return () => {
      client.deactivate()
      clientRef.current = null
      setConnected(false)
      setLiveMessages([])
    }
  }, [dealId, accessToken])

  // Скролл к последнему сообщению
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [history.length, liveMessages.length])

  const send = () => {
    const text = inputText.trim()
    if (!text || !clientRef.current?.connected) return
    clientRef.current.publish({
      destination: `/app/deal/${dealId}`,
      body: JSON.stringify({ content: text }),
    })
    setInputText('')
  }

  const allMessages = [
    ...history,
    // live-сообщения, которых ещё нет в истории
    ...liveMessages.filter((lm) => !history.some((h) => h.id === lm.id)),
  ]

  if (isLoading) return <Spinner className="py-8" />

  return (
    <div className="flex flex-col h-[500px] border border-gray-200 rounded-xl bg-white overflow-hidden">
      {/* Заголовок */}
      <div className="px-4 py-3 border-b border-gray-100 flex items-center gap-2">
        <span className="font-medium text-gray-800">Чат по сделке</span>
        <span className={`w-2 h-2 rounded-full ${connected ? 'bg-green-400' : 'bg-gray-300'}`} />
        <span className="text-xs text-gray-400">{connected ? 'подключено' : 'отключено'}</span>
      </div>

      {/* Сообщения */}
      <div className="flex-1 overflow-y-auto px-4 py-3 space-y-3">
        {allMessages.length === 0 && (
          <p className="text-center text-gray-400 text-sm py-8">
            Начните диалог — напишите первое сообщение
          </p>
        )}
        {allMessages.map((msg) => {
          const isOwn = msg.senderId === userId
          return (
            <div key={msg.id} className={`flex ${isOwn ? 'justify-end' : 'justify-start'}`}>
              <div
                className={`max-w-xs lg:max-w-md px-3 py-2 rounded-2xl text-sm ${
                  isOwn
                    ? 'bg-brand-600 text-white rounded-br-sm'
                    : 'bg-gray-100 text-gray-900 rounded-bl-sm'
                }`}
              >
                <p className="break-words">{msg.content}</p>
                <p className={`text-xs mt-1 ${isOwn ? 'text-brand-200' : 'text-gray-400'}`}>
                  {new Date(msg.sentAt).toLocaleTimeString('ru', {
                    hour: '2-digit',
                    minute: '2-digit',
                  })}
                </p>
              </div>
            </div>
          )
        })}
        <div ref={bottomRef} />
      </div>

      {/* Поле ввода */}
      <div className="px-4 py-3 border-t border-gray-100 flex gap-2">
        <input
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && !e.shiftKey && (e.preventDefault(), send())}
          placeholder={connected ? 'Написать сообщение...' : 'Подключение...'}
          disabled={!connected}
          className="flex-1 rounded-lg border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 disabled:bg-gray-50"
        />
        <button
          onClick={send}
          disabled={!connected || !inputText.trim()}
          className="bg-brand-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-brand-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          Отправить
        </button>
      </div>
    </div>
  )
}
