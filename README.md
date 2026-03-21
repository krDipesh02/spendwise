# Spendwise

Personal spend management backend built with Spring Boot, Spring Security, and PostgreSQL.

Current implementation includes:
- Google OAuth for browser login
- Per-user API keys for `n8n` and MCP-style tool routes
- User profiles, categories, expenses, budgets, receipts, recurring expenses
- Analytics, reminders, and activity logs

Project layout:
- Backend: [backend](/Users/krdipesh/Desktop/spendwise/backend)
- Frontend: [frontend](/Users/krdipesh/Desktop/spendwise/frontend)

Backend configuration is in [application.properties](/Users/krdipesh/Desktop/spendwise/backend/src/main/resources/application.properties).

Docker:
- Backend compose: [docker-compose.yml](/Users/krdipesh/Desktop/spendwise/backend/docker-compose.yml)
- Backend image: [Dockerfile](/Users/krdipesh/Desktop/spendwise/backend/Dockerfile)
- Frontend compose: [docker-compose.yml](/Users/krdipesh/Desktop/spendwise/frontend/docker-compose.yml)
- Frontend image: [Dockerfile](/Users/krdipesh/Desktop/spendwise/frontend/Dockerfile)

Run backend stack with:
```bash
cd backend && docker compose up --build
```

Run frontend with:
```bash
cd frontend && docker compose up --build
```
