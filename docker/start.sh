#!/bin/bash
set -e

echo "🛠 Gradleビルドを実行します..."
./gradlew build

echo "🐳 Docker Composeで起動します..."
docker compose -f docker/docker-compose.yml up --build
