# Dockerfile for Vue.js frontend
FROM node:latest as build

WORKDIR /app

COPY . .

RUN npm install
RUN npm run build

# Nginx setup
FROM nginx:latest

COPY --from=build /app/dist /app

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]