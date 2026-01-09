#!/bin/sh
set -e

# Default backend URL if not set (for Railway internal networking)
# Railway services can communicate using service names or public URLs
BACKEND_URL=${BACKEND_URL:-http://backend:8080}

# Export for envsubst
export BACKEND_URL

# Substitute environment variables in nginx config
envsubst '$BACKEND_URL' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf

# Start nginx
exec nginx -g 'daemon off;'

