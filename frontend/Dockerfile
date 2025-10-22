# syntax=docker/dockerfile:1

# Build stage
FROM node:18-alpine AS builder
WORKDIR /app

# Install deps with cache
COPY package*.json ./
RUN npm ci

# Copy source and build
COPY . .
# Pass API base to Vite at build time (works with compose args)
ARG VITE_API_BASE_URL=http://localhost:8080/api
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
RUN npm run build-only

# Runtime stage
FROM nginx:alpine AS runner
# Copy built assets
COPY --from=builder /app/dist /usr/share/nginx/html
# SPA routing config
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx","-g","daemon off;"]