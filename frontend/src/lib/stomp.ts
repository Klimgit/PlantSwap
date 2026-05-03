import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useAuthStore } from '../store/authStore'

let stompClient: Client | null = null

export function getStompClient(): Client {
  if (stompClient && stompClient.connected) return stompClient

  stompClient = new Client({
    webSocketFactory: () => new SockJS('/ws'),
    connectHeaders: {
      // X-User-Id передаётся через cookie/session после WS-апгрейда через Gateway
      // Фактически Gateway подставляет заголовок при проксировании HTTP-апгрейда
    },
    reconnectDelay: 5000,
  })

  return stompClient
}

export function disconnectStomp() {
  stompClient?.deactivate()
  stompClient = null
}
