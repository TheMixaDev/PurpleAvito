docker-compose up -d postgres-primary
docker-compose up -d avito-admin-api

sleep 10

docker-compose restart postgres-primary
docker-compose restart avito-admin-api

sleep 10

docker-compose up -d avito-prices-api-primary

sleep 10

docker-compose up -d postgres-replica-1
docker-compose up -d postgres-replica-2

sleep 10

docker-compose up -d avito-prices-api-replica-1
docker-compose up -d avito-prices-api-replica-2
docker-compose up -d --build frontend

sleep 5

docker-compose up --build -d nginx
