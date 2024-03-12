docker-compose up postgres-primary -d
docker-compose up avito-admin-api -d

sleep 10

docker-compose restart postgres-primary
docker-compose restart avito-admin-api

sleep 10

docker-compose up avito-prices-api-primary -d

sleep 10

docker-compose up postgres-replica-1 -d
docker-compose up postgres-replica-2 -d

sleep 10

docker-compose up avito-prices-api-replica-1 -d
docker-compose up avito-prices-api-replica-2 -d
docker-compose up --build nginx -d
