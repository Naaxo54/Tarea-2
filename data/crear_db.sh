#!/bin/bash
# Script para crear la base de datos SQLite de ejemplo.
# Requiere que 'sqlite3' esté instalado en el sistema.
# Uso: ./data/crear_db.sh
# Resultado: crea el archivo data/ejercicios.db

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DB_PATH="$SCRIPT_DIR/ejercicios.db"

if command -v sqlite3 &>/dev/null; then
    sqlite3 "$DB_PATH" < "$SCRIPT_DIR/schema.sql"
    echo "Base de datos creada exitosamente en: $DB_PATH"
else
    echo "sqlite3 no está instalado. Instálelo e intente de nuevo."
    echo "  Linux: sudo apt install sqlite3"
    echo "  macOS: brew install sqlite"
    exit 1
fi
