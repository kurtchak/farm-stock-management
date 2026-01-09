#!/bin/sh
set -e

# Default backend URL if not set (for Railway internal networking)
# Railway services can communicate using service names or public URLs
# Try to detect Railway service URL if available
if [ -z "$BACKEND_URL" ]; then
    # Check if RAILWAY_SERVICE_URL is available (Railway provides this)
    if [ -n "$RAILWAY_SERVICE_URL" ]; then
        # This is the current service URL, we need backend service URL
        # Try common Railway service names
        BACKEND_URL="http://backend:8080"
    else
        BACKEND_URL="http://backend:8080"
    fi
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

# Show the generated config for debugging
echo "Generated nginx config:"
cat /etc/nginx/conf.d/default.conf
echo "========================================="

# Validate nginx config before starting
if ! nginx -t; then
    echo "ERROR: nginx config validation failed!"
    exit 1
fi

# Start nginx
echo "Starting nginx..."
exec nginx -g 'daemon off;'

