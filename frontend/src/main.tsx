import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { QueryClientProvider } from '@tanstack/react-query'
import { queryClient } from './lib/queryClient'
import App from './App'
import ErrorBoundary from './components/ErrorBoundary'
import './index.css'

const rootEl = document.getElementById('root')
if (!rootEl) {
  document.body.insertAdjacentHTML(
    'beforeend',
    '<p style="padding:24px;color:#b91c1c;font-family:system-ui">Не найден элемент #root.</p>',
  )
} else {
  try {
    createRoot(rootEl).render(
      <StrictMode>
        <ErrorBoundary>
          <BrowserRouter>
            <QueryClientProvider client={queryClient}>
              <App />
            </QueryClientProvider>
          </BrowserRouter>
        </ErrorBoundary>
      </StrictMode>,
    )
  } catch (err) {
    console.error(err)
    const msg = err instanceof Error ? err.stack || err.message : String(err)
    rootEl.innerHTML =
      '<div style="padding:24px;font-family:system-ui;color:#991b1b;background:#fef2f2;border-radius:8px;max-width:640px;margin:24px auto">' +
      '<strong style="display:block;margin-bottom:8px">Не удалось запустить интерфейс</strong>' +
      '<pre style="white-space:pre-wrap;word-break:break-word;font-size:12px;margin:0">' +
      msg.replace(/</g, '&lt;') +
      '</pre></div>'
  }
}
