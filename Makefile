.PHONY: help build java-build up down restart logs clean deploy feed test health

# Default target
default: compile

help:
	@echo "RAG Demo - Available Commands:"
	@echo ""
	@echo "Docker Compose Commands:"
	@echo "  make build          - Build the Docker images"
	@echo "  make up             - Start all services"
	@echo "  make down           - Stop all services"
	@echo "  make restart        - Restart all services"
	@echo "  make logs           - View logs (all services)"
	@echo "  make logs-vespa     - View Vespa logs"
	@echo "  make logs-app       - View RAG app logs"
	@echo "  make clean          - Stop services and remove volumes"
	@echo ""
	@echo "Vespa Commands:"
	@echo "  make deploy         - Deploy Vespa schema"
	@echo "  make feed           - Feed data to Vespa"
	@echo "  make vespa-status   - Check Vespa status"
	@echo "  make vespa-health   - Check Vespa health"
	@echo ""
	@echo "Testing Commands:"
	@echo "  make health         - Check all health endpoints"
	@echo "  make test-search    - Test search endpoint"
	@echo "  make test-rag       - Test RAG endpoint"
	@echo ""
	@echo "Development Commands:"
	@echo "  make rebuild-app    - Rebuild and restart RAG app"
	@echo "  make shell-vespa    - Open shell in Vespa container"
	@echo "  make shell-app      - Open shell in RAG app container"
	@echo ""

# Docker Compose Commands
docker-build:
	@echo "Building Docker images..."
	docker compose build

docker-up:
	@echo "Starting services..."
	docker compose up -d
	@echo "Waiting for services to be ready..."
	@sleep 5
	@make health

docker-down:
	@echo "Stopping services..."
	docker compose down

docker-restart:
	@make docker-down
	@make docker-up

docker-logs:
	docker compose logs -f

docker-logs-vespa:
	docker compose logs -f vespa

docker-logs-app:
	docker compose logs -f rag-app

docker-clean:
	@echo "Stopping services and removing volumes..."
	docker compose down -v
	@echo "Cleaning Docker system..."
	docker system prune -f

# Vespa Commands
vespa-deploy:
	@echo "Deploying Vespa schema..."
	vespa config set target local
	vespa deploy --wait 300 ./app
	@echo "Deployment complete!"

vespa-feed:
	@echo "Feeding data to Vespa..."
	vespa feed dataset/documents.jsonl
	@echo "Data feed complete!"

vespa-status:
	vespa status

vespa-health:
	@curl -s http://localhost:19071/state/v1/health | jq '.'

# Development Commands
docker-rebuild:
	@echo "Rebuilding RAG application..."
	docker compose up -d --build rag-app
	@echo "Waiting for app to start..."
	@sleep 10
	@make logs-app

shell-vespa:
	docker compose exec vespa bash

shell-app:
	docker compose exec rag-app bash

# Quick start command
quickstart: up
	@echo "Waiting for Vespa to be ready (60s)..."
	@sleep 60
	@make vespa-deploy
	@make vespa-feed
	@echo ""
	@echo "=== Quick Start Complete! ==="
	@echo "Vespa: http://localhost:8080"
	@echo "RAG API: http://localhost:8081"
	@echo ""
	@echo "Try: make test-search"
	@echo "Try: make test-rag"

# Compile
compile:
	gradle assemble --no-build-cache --warning-mode none

build:
	gradle build --no-build-cache --warning-mode none

run:
	$(eval include .env)
	$(eval export $(shell sed 's/=.*//' .env))
	@echo "Starting application with local profile..."
	@gradle bootRun --no-build-cache --warning-mode none -Dspring.profiles.active=local

clean:
	gradle clean --no-build-cache --warning-mode none  

# Testing Commands
health:
	@echo "\n=== Vespa Health ==="
	@curl -s http://localhost:19071/state/v1/health | jq -r '.status.code' || echo "Vespa not ready"
	@echo "\n=== RAG App Search Health ==="
	@curl -s http://localhost:8081/api/search/health || echo "RAG app not ready"
	@echo "\n=== RAG App RAG Health ==="
	@curl -s http://localhost:8081/api/rag/health || echo "RAG app not ready"
	@echo ""

test-search:
	@echo "Testing search endpoint..."
	@curl -X POST http://localhost:8081/api/search \
		-H "Content-Type: application/json" \
		-d '{"query": "rock music", "maxResults": 3, "searchMode": "hybrid"}' | jq '.'

test-rag:
	@echo "Testing RAG endpoint..."
	@curl -X POST http://localhost:8081/api/rag/query \
		-H "Content-Type: application/json" \
		-d '{"query": "What are some good rock albums?", "maxResults": 3, "searchMode": "hybrid"}' | jq '.'
