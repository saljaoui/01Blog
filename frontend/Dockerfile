# ---------- Build stage ----------
FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci --legacy-peer-deps || npm install --legacy-peer-deps

COPY . .
RUN npm run build

# ---------- Runtime stage ----------
FROM nginx:alpine

# Backend address used by the /api and /auth reverse proxy (override at runtime)
ENV BACKEND_URL=http://backend:8080

RUN rm -f /etc/nginx/conf.d/default.conf
COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist/01blog-frontend/browser /usr/share/nginx/html

EXPOSE 8080
