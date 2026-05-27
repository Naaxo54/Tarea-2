#!/bin/bash
# ============================================================
# Script de compilación — Sistema de Rutinas de Entrenamiento
# ============================================================
# Requiere: Maven 3.x + Java 11+ en el PATH
# Uso:      ./compile.sh
# Resultado: genera target/rutinas-entrenamiento.jar

set -e
cd "$(dirname "$0")"

echo "============================================"
echo " Compilando Sistema de Rutinas..."
echo "============================================"

mvn clean package -q

echo ""
echo "✓ Compilación exitosa."
echo "  JAR generado: target/rutinas-entrenamiento.jar"
echo ""
echo "Para ejecutar:"
echo "  java -jar target/rutinas-entrenamiento.jar"
