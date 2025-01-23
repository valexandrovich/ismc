#!/bin/sh

# Write environment variables to runtime-env.js
cat <<EOF > /usr/share/nginx/html/runtime-env.js
window.RUNTIME_ENV = {
  VITE_API_PREFIX: "${VITE_API_PREFIX}"
};
EOF

sed -i "s|\${VITE_API_PREFIX}|${VITE_API_PREFIX}|g" /etc/nginx/conf.d/default.conf
sed -i "s|\${API_GATEWAY_HOST}|${API_GATEWAY_HOST}|g" /etc/nginx/conf.d/default.conf
sed -i "s|\${API_GATEWAY_PORT}|${API_GATEWAY_PORT}|g" /etc/nginx/conf.d/default.conf


# Start Nginx
nginx -g 'daemon off;'
