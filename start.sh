#!/usr/bin/env bash
# =============================================================================
# PlantSwap — скрипт локального запуска
# =============================================================================
# Использование:
#   ./start.sh          — поднять ВСЁ (Docker build + docker compose up)
#   ./start.sh --infra  — только инфраструктура (БД, Kafka, MinIO)
#                         фронтенд запускается отдельно: cd frontend && npm run dev
#   ./start.sh --down   — остановить и удалить все контейнеры
#   ./start.sh --logs   — стримить логи всех контейнеров
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
  for cmd in docker; do
    command -v "$cmd" &>/dev/null || error "Команда '$cmd' не найдена. Установите Docker Desktop."
  done
  docker info &>/dev/null || error "Docker daemon не запущен. Запустите Docker Desktop."
}

# ── .env ──────────────────────────────────────────────────────────────────────
prepare_env() {
  if [[ ! -f .env ]]; then
    cp .env.example .env
    warn ".env не найден — создан из .env.example"
    warn "Обязательно смените JWT_SECRET перед продакшн-деплоем!"
  fi
}

# ── Режим: только инфраструктура ──────────────────────────────────────────────
mode_infra() {
  info "Запуск инфраструктуры (PostgreSQL ×4, Kafka, MinIO)..."
  docker compose up -d auth-db listings-db deals-db chat-db kafka minio

  wait_healthy "plantswap-auth-db"
  wait_healthy "plantswap-listings-db"
  wait_healthy "plantswap-deals-db"
  wait_healthy "plantswap-chat-db"
  wait_healthy "plantswap-kafka"
  wait_healthy "plantswap-minio"

  success "Инфраструктура готова"
  echo ""
  echo -e "  ${GREEN}PostgreSQL auth:${NC}     localhost:5432"
  echo -e "  ${GREEN}PostgreSQL listings:${NC} localhost:5433"
  echo -e "  ${GREEN}PostgreSQL deals:${NC}    localhost:5434"
  echo -e "  ${GREEN}PostgreSQL chat:${NC}     localhost:5435"
  echo -e "  ${GREEN}Kafka:${NC}               localhost:9092"
  echo -e "  ${GREEN}MinIO UI:${NC}            http://localhost:9001  (minioadmin / minioadmin123)"
  echo ""
  echo -e "  Теперь запустите backend-сервисы в IDE или отдельных терминалах,"
  echo -e "  а фронтенд через: ${YELLOW}cd frontend && npm install && npm run dev${NC}"
}

# ── Режим: полный стек в Docker ───────────────────────────────────────────────
mode_full() {
  info "Сборка Docker-образов..."
  docker compose build --parallel

  info "Запуск всех контейнеров..."
  docker compose up -d

  info "Ожидание готовности сервисов..."
  wait_healthy "plantswap-auth-db"
  wait_healthy "plantswap-listings-db"
  wait_healthy "plantswap-deals-db"
  wait_healthy "plantswap-chat-db"
  wait_healthy "plantswap-kafka"
  wait_healthy "plantswap-minio"

  success "PlantSwap запущен!"
  echo ""
  echo -e "  ${GREEN}🌿 Приложение:${NC}   http://localhost:3000"
  echo -e "  ${GREEN}🔀 Gateway API:${NC}  http://localhost:8080"
  echo -e "  ${GREEN}📦 MinIO UI:${NC}     http://localhost:9001  (minioadmin / minioadmin123)"
  echo ""
  echo -e "  Логи:     ${YELLOW}./start.sh --logs${NC}"
  echo -e "  Остановка: ${YELLOW}./start.sh --down${NC}"
}

# ── Вспомогательная функция ожидания ──────────────────────────────────────────
wait_healthy() {
  local name="$1"
  local max=30
  local i=0
  printf "  Ожидание %-35s" "$name"
  while [[ $i -lt $max ]]; do
    local status
    status=$(docker inspect --format='{{.State.Health.Status}}' "$name" 2>/dev/null || echo "missing")
    if [[ "$status" == "healthy" ]]; then
      echo -e " ${GREEN}✓${NC}"
      return 0
    fi
    printf "."
    sleep 2
    ((i++))
  done
  echo ""
  error "$name не стал healthy за $(( max * 2 )) сек. Проверьте логи: docker logs $name"
}

# ── Точка входа ───────────────────────────────────────────────────────────────
check_deps
prepare_env

case "${1:-}" in
  --infra)  mode_infra ;;
  --down)   docker compose down; success "Контейнеры остановлены" ;;
  --logs)   docker compose logs -f ;;
  "")       mode_full ;;
  *)        error "Неизвестный параметр: $1" ;;
esac
