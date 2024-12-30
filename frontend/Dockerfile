FROM node:18-alpine AS builder
WORKDIR /app

# Copy package files first to leverage caching
COPY package*.json ./
RUN npm install

# Add build argument for cache busting
ARG CACHEBUST=1

# Copy rest of the application
COPY . .

# Build the application
RUN npm run build

# Check if dist directory exists and has files
RUN ls -la dist || exit 1

# Production stage
FROM nginx:alpine
# Copy the built files
COPY --from=builder /app/dist /usr/share/nginx/html
# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]