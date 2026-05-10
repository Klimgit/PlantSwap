import { Component, type ErrorInfo, type ReactNode } from 'react'

interface Props {
  children: ReactNode
}

interface State {
  hasError: boolean
  message?: string
}

/** Ловит ошибки рендера и не даёт «белый экран» без текста. */
export default class ErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false }

  static getDerivedStateFromError(err: Error): State {
    return { hasError: true, message: err.message }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('[PlantSwap]', error, info.componentStack)
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center p-6">
          <div className="max-w-lg rounded-xl border border-red-200 bg-white p-8 shadow-sm">
            <h1 className="text-lg font-semibold text-gray-900 mb-2">Ошибка интерфейса</h1>
            <p className="text-sm text-gray-600 mb-4">
              Откройте консоль браузера (F12 → Console) для подробностей.
              Попробуйте очистить данные сайта для localhost или ключ{' '}
              <code className="bg-gray-100 px-1 rounded">plantswap-auth</code> в Local Storage.
            </p>
            {this.state.message && (
              <pre className="text-xs text-red-700 whitespace-pre-wrap bg-red-50 p-3 rounded-lg overflow-auto">
                {this.state.message}
              </pre>
            )}
            <button
              type="button"
              onClick={() => window.location.reload()}
              className="mt-6 w-full rounded-lg bg-brand-600 py-2 text-sm font-medium text-white hover:bg-brand-700"
            >
              Перезагрузить страницу
            </button>
          </div>
        </div>
      )
    }
    return this.props.children
  }
}
