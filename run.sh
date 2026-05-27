#!/bin/bash
# ============================================================
# Script de ejecución — Sistema de Rutinas de Entrenamiento
# ============================================================
# Compila si el JAR no existe, luego ejecuta la aplicación.
# Uso: ./run.sh

set -e
cd "$(dirname "$0")"

JAR="target/rutinas-entrenamiento.jar"

if [ ! -f "$JAR" ]; then
    echo "JAR no encontrado. Compilando primero..."
    ./compile.sh
fi

echo "Iniciando Sistema de Rutinas de Entrenamiento..."
java -jar "$JAR"
