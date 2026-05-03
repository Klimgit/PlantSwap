import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ListingsPage from './pages/ListingsPage'
import ListingDetailPage from './pages/ListingDetailPage'
import CreateListingPage from './pages/CreateListingPage'
import MyListingsPage from './pages/MyListingsPage'
import DealsPage from './pages/DealsPage'
import DealDetailPage from './pages/DealDetailPage'
import FavoritesPage from './pages/FavoritesPage'

export default function App() {
  return (
    <Routes>
      {/* Публичные страницы без шапки */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      {/* Страницы с шапкой */}
      <Route path="/" element={<Layout><ListingsPage /></Layout>} />
      <Route path="/listings/:id" element={<Layout><ListingDetailPage /></Layout>} />

      {/* Защищённые маршруты */}
      <Route path="/listings/new" element={
        <ProtectedRoute><Layout><CreateListingPage /></Layout></ProtectedRoute>
      } />
      <Route path="/my-listings" element={
        <ProtectedRoute><Layout><MyListingsPage /></Layout></ProtectedRoute>
      } />
      <Route path="/favorites" element={
        <ProtectedRoute><Layout><FavoritesPage /></Layout></ProtectedRoute>
      } />
      <Route path="/deals" element={
        <ProtectedRoute><Layout><DealsPage /></Layout></ProtectedRoute>
      } />
      <Route path="/deals/:id" element={
        <ProtectedRoute><Layout><DealDetailPage /></Layout></ProtectedRoute>
      } />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
