# Build stage
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Runtime stage - using simple http server
FROM node:18-alpine
WORKDIR /app
RUN npm install -g http-server
COPY --from=builder /app/dist .
EXPOSE 80
CMD ["http-server", "--port", "80", "-P", "http://localhost:8080?"]