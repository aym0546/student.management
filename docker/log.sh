#!/bin/bash
echo "📋 Docker Composeのログを表示します..."
docker-compose -f docker/docker-compose.yml logs -f
