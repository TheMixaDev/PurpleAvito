docker rmi -f purpleavitoback-avito-admin-api
docker rmi -f purpleavitoback-avito-prices-api-primary
docker rmi -f purpleavitoback-avito-prices-api-replica-1
docker rmi -f purpleavitoback-avito-prices-api-replica-2
docker-compose up --build