#!/bin/sh
set -e

# Default backend URL if not set (for Railway internal networking)
# Railway services can communicate using service names or public URLs
# IMPORTANT: Use public URL of backend service for better reliability
if [ -z "$BACKEND_URL" ]; then
    echo "WARNING: BACKEND_URL not set! Using default http://backend:8080"
    echo "Please set BACKEND_URL environment variable to your backend service URL"
    echo "Example: https://your-backend-service.railway.app"
    BACKEND_URL="http://backend:8080"
fi

# Export for envsubst
export BACKEND_URL

# Log the backend URL for debugging (without sensitive info)
echo "========================================="
echo "Frontend starting with configuration:"
echo "BACKEND_URL: ${BACKEND_URL}"
echo "========================================="

# Substitute environment variables in nginx config
envsubst '$BACKEND_URL' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf

# Show the generated config for debugging (first few lines)
echo "Generated nginx config (first 20 lines):"
head -n 20 /etc/nginx/conf.d/default.conf
echo "========================================="

# Validate nginx config before starting
if ! nginx -t; then
    echo "ERROR: nginx config validation failed!"
    echo "Full config:"
    cat /etc/nginx/conf.d/default.conf
    exit 1
fi

# Create log directory if it doesn't exist
mkdir -p /var/log/nginx

# Start nginx
echo "Starting nginx..."
exec nginx -g 'daemon off;'

