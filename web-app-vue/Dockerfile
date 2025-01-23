# Build stage
FROM nexus.otpbank.com.ua:10443/repository/tools/node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./

ARG HTTP_PROXY
ARG HTTPS_PROXY
ARG VITE_API_PREFIX
ENV VITE_API_PREFIX=${VITE_API_PREFIX}
ENV API_GATEWAY_HOST=${API_GATEWAY_HOST}
ENV API_GATEWAY_PORT=${API_GATEWAY_PORT}

RUN if [ -n "$HTTP_PROXY" ] && [ -n "HTTPS_PROXY" ]; then \
      npm config set proxy $HTTP_PROXY; \
      npm config set https-proxy $HTTPS_PROXY; \
    fi && \
#    npm install
    yarn

COPY . .
RUN npm run build

# Production stage
FROM nexus.otpbank.com.ua:10443/repository/tools/nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY ./config/nginx.conf /etc/nginx/conf.d/default.conf
#EXPOSE 80
#CMD ["nginx", "-g", "daemon off;"]
COPY ./entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
EXPOSE 80
ENTRYPOINT ["/entrypoint.sh"]