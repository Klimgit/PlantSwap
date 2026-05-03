#!/usr/bin/env bash
# =============================================================================
# PlantSwap — скрипт локального запуска
# =============================================================================
# Использование:
#   ./start.sh          — поднять ВСЁ (инфраструктура + все сервисы + фронтенд)
#   ./start.sh --infra  — только инфраструктура (БД, Kafka, MinIO) + фронтенд dev
#   ./start.sh --down   — остановить и удалить все контейнеры
#   ./start.sh --logs   — прокинуть логи всех контейнеров
# =============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'

info()    { echo -e "${BLUE}[INFO]${NC}  $*"; }
success() { echo -e "${GREEN}[OK]${NC}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*"; exit 1; }

# ── Проверка зависимостей ──────────────────────────────────────────────────────
check_deps() {
  for cmd in docker docker-compose java node npm; do
    if ! command -v "$cmd" &>/dev/null; then
      error "Команда '$cmd' не найдена. Установите её перед запуском."
    fi
  done
  local java_ver; java_ver=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
  if [[ "$java_ver" -lt 21 ]]; then
    warn "Рекомендуется Java 21+. Установлена: $java_ver"
  fi
}

# ── Копируем .env если нет ────────────────────────────────────────────────────
prepare_env() {
  if [[ ! -f .env ]]; then
    cp .env.example .env
    warn ".env не найден — создан из .env.example. Проверьте JWT_SECRET перед деплоем!"
  fi
}

# ── Сборка backend ────────────────────────────────────────────────────────────
build_backend() {
  info "Сборка backend-сервисов (Gradle)..."
  ./gradlew build -x test --parallel --quiet
  success "Backend собран"
}

# ── Режим: только инфраструктура + фронтенд dev server ───────────────────────
mode_infra() {
  info "Запуск инфраструктуры (БД × 4, Kafka, MinIO)..."
  docker-compose up -d auth-db listings-db deals-db chat-db kafka minio

  info "Ожидание готовности сервисов..."
  wait_healthy "plantswap-auth-db"
  wait_healthy "plantswap-listings-db"
  wait_healthy "plantswap-deals-db"
  wait_healthy "plantswap-chat-db"
  wait_healthy "plantswap-kafka"
  wait_healthy "plantswap-minio"
  success "Инфраструктура готова"

  info "Запуск фронтенда в режиме разработки (порт 5173)..."
  info "Для работы нужно вручную запустить backend-сервисы."
  info "После запуска всех сервисов откройте: http://localhost:5173"

  cd frontend
  npm install --silent
  npm run dev
}

# ── Режим: полный стек ────────────────────────────────────────────────────────
mode_full() {
  build_backend

  info "Сборка Docker-образов..."
  docker-compose build --parallel

  info "Запуск всех сервисов..."
  docker-compose up -d

  info "Ожидание готовности сервисов..."
  wait_healthy "plantswap-auth-db"
  wait_healthy "plantswap-listings-db"
  wait_healthy "plantswap-deals-db"
  wait_healthy "plantswap-chat-db"
  wait_healthy "plantswap-kafka"
  wait_healthy "plantswap-minio"

  success "PlantSwap запущен!"
  echo ""
  echo -e "  ${GREEN}🌿 Приложение:${NC}  http://localhost:3000"
  echo -e "  ${GREEN}🔀 Gateway:${NC}     http://localhost:8080"
  echo -e "  ${GREEN}📦 MinIO UI:${NC}    http://localhost:9001  (minioadmin / minioadmin123)"
  echo ""
  echo -e "  Логи: ${YELLOW}./start.sh --logs${NC}"
  echo -e "  Стоп: ${YELLOW}./start.sh --down${NC}"
}

# ── Вспомогательные ───────────────────────────────────────────────────────────
wait_healthy() {
  local name="$1"
  local max_attempts=30
  local attempt=0
  printf "  Ожидание %s " "$name"
  while [[ $attempt -lt $max_attempts ]]; do
    local status
    status=$(docker inspect --format='{{.State.Health.Status}}' "$name" 2>/dev/null || echo "missing")
    if [[ "$status" == "healthy" ]]; then
      echo " ✓"
      return 0
    fi
    printf "."
    sleep 2
    ((attempt++))
  done
  echo ""
  error "Сервис $name не стал healthy за $(( max_attempts * 2 )) секунд"
}

# ── Точка входа ───────────────────────────────────────────────────────────────
check_deps
prepare_env

case "${1:-}" in
  --infra)  mode_infra ;;
  --down)   docker-compose down; success "Контейнеры остановлены" ;;
  --logs)   docker-compose logs -f ;;
  "")       mode_full ;;
  *)        error "Неизвестный параметр: $1. Используйте --infra, --down или --logs" ;;
esac
