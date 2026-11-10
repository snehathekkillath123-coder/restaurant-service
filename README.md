# restaurant-service

Part of the QuickBite online food ordering system. Owns restaurants and their menus.
Spring Boot 3 / Java 17 / PostgreSQL. Port 8081.

## API
| Method | Path | Type |
|---|---|---|
| POST | /api/restaurants | command |
| POST | /api/restaurants/{id}/menu | command |
| PUT | /api/menu-items/{id}/availability?available=true | command |
| GET | /api/restaurants, /api/restaurants/{id} | query |
| GET | /api/restaurants/{id}/menu | query |
| GET | /api/menu-items/{id} | query (called by order-service) |

## Run locally
    docker run -d --name rdb -e POSTGRES_DB=restaurantdb -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:16-alpine
    mvn spring-boot:run

## Docker / Minikube
    minikube start
    eval $(minikube docker-env)
    docker build -t restaurant-service:1.0.0 .
    kubectl apply -f k8s/
    minikube service restaurant-service -n foodapp --url
