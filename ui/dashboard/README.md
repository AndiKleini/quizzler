# Dashboard UI

Angular 18 standalone application for displaying session dashboard data.

## Overview

This application displays quiz session statistics including:
- Quiz performance (questions, correct/wrong answers, accuracy)
- Payment information (number of payments, total amount, average per payment)

## Development

```bash
# Install dependencies
npm install

# Start development server
npm start

# Build for production
npm run build
```

The app will be available at `http://localhost:4200`.

## Docker

The application is containerized and served via nginx:

```bash
# Build and run with docker-compose (from project root)
docker-compose up -d dashboard-ui
```

Access at `http://localhost:4202`

## Routes

- `/dashboard/:dashboardId` - Display dashboard for a specific session
- `/` - Redirects to default dashboard

## API Configuration

The API base URL is configured in `src/environments/environment.ts` (default: `http://localhost:8082`)

## Example Dashboard IDs

- `11111111-2222-3333-4444-555555555555`
- `22222222-3333-4444-5555-666666666666`
- `33333333-4444-5555-6666-777777777777`
- `44444444-5555-6666-7777-888888888888`
