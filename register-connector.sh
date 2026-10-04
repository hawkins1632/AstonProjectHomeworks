#!/bin/sh
set -e

CONNECT_URL="${CONNECT_URL:-http://connect:8083}"
CONNECTOR_NAME="${CONNECTOR_NAME:-user-outbox-connector}"
CONFIG_FILE="${CONFIG_FILE:-/connector-config.json}"

echo "Жду готовности Kafka Connect на ${CONNECT_URL}..."
until curl -s -o /dev/null -w "%{http_code}" "${CONNECT_URL}/" | grep -q "200"; do
  echo "  ... Connect ещё не готов"
  sleep 2
done
echo "Connect доступен."

echo "Проверяю конфиг ${CONFIG_FILE}"
test -f "${CONFIG_FILE}" || { echo "ОШИБКА: не найден ${CONFIG_FILE}"; exit 1; }

echo "Регистрирую коннектор '${CONNECTOR_NAME}'..."
curl -sS -X PUT "${CONNECT_URL}/connectors/${CONNECTOR_NAME}/config" \
  -H "Content-Type: application/json" \
  -d @"${CONFIG_FILE}"

echo ""
echo "Статус коннектора:"
curl -sS "${CONNECT_URL}/connectors/${CONNECTOR_NAME}/status"

echo ""
echo "Готово."