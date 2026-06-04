# syntax=docker/dockerfile:1

# --- Build stage -----------------------------------------------------------
# Angular 18 standalone app, built with the @angular-devkit application builder.
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies first for layer caching. Uses `npm install` rather than
# `npm ci` because the committed package-lock.json is currently out of sync with
# package.json; the lock is only updated inside this image, never the repo.
COPY package.json package-lock.json ./
RUN npm install --no-audit --no-fund

# NG_CONFIGURATION selects the Angular build configuration and therefore the
# baked-in base URLs: `production` (default) -> localhost for docker-compose,
# `kind` -> the host-based ingress URLs (api.quizzler.localhost, payment.localhost).
ARG NG_CONFIGURATION=production
COPY . .
RUN npm run build -- --configuration=${NG_CONFIGURATION}

# --- Runtime stage ---------------------------------------------------------
# Serve the static bundle through nginx. The application builder emits the app
# under dist/quizzler/browser.
FROM nginx:1.27-alpine AS runtime
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/quizzler/browser /usr/share/nginx/html

EXPOSE 80
