docker rmi -f purpleavitoback-avito-admin-api
docker rmi -f purpleavitoback-avito-prices-api-primary
docker rmi -f purpleavitoback-avito-prices-api-replica-1
docker rmi -f purpleavitoback-avito-prices-api-replica-2

docker-compose up postgres-primary -d
docker-compose up avito-admin-api -d

timeout 10

docker-compose restart postgres-primary
docker-compose restart avito-admin-api

timeout 10

docker-compose up avito-prices-api-primary -d

timeout 10

docker-compose up postgres-replica-1 -d
docker-compose up postgres-replica-2 -d

timeout 10

docker-compose up avito-prices-api-replica-1 -d
docker-compose up avito-prices-api-replica-2 -d
docker-compose up --build nginx -d
