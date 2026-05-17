# Transaction Monitor

A Java 17 / Spring Boot prototype for monitoring financial transactions and flagging suspicious activity.

## Requirements

- Java 17
- Maven 3+

## Build and Test

```bash
mvn clean verify
```

The project is configured with JaCoCo. The coverage check is intentionally strict for the application code that contains the prototype logic.

## Run

```bash
mvn spring-boot:run
```

On startup the application reads `src/main/resources/transactions.json`, evaluates the alert rules, stores the monitored transactions in memory, and prints flagged transactions to stdout.

## REST API

### Search transactions

```http
GET /transactions
```

Supported query parameters:

| Parameter | Description |
|---|---|
| `sender` | Exact sender match |
| `receiver` | Exact receiver match |
| `minAmount` | Minimum transaction amount |
| `maxAmount` | Maximum transaction amount |
| `from` | Start timestamp, ISO-8601 format |
| `to` | End timestamp, ISO-8601 format |
| `alertStatus` | `FLAGGED` or `NOT_FLAGGED` |
| `alertType` | `LARGE_AMOUNT` or `HIGH_FREQUENCY` |


Examples:

```http
GET /transactions?alertStatus=FLAGGED
GET /transactions?minAmount=100&maxAmount=1000&from=2026-05-10T09:00:00Z
```

## Alert Rules

- `LARGE_AMOUNT`: flags transactions with amount greater than `10000`
- `HIGH_FREQUENCY`: flags when the same sender makes more than 3 transactions within a 60-second window

## Package Overview

```text
api          REST controller, search criteria, API responses, error handling
application  processing workflow and startup runner
domain       core business records and enums
input        transaction reader abstraction and JSON implementation
output       alert reporter abstraction and stdout implementation
rules        alert rule abstraction, rule engine, and rule implementations
storage      transaction store abstraction and in-memory implementation
```

```personal use contanarize and deploy image
Docker commands:
minikube start --driver=docker
kubectl get pods -A
kubectl apply -f k8/deployment.yaml
kubectl describe pod springboot-deployment-86cb4bbd58-c22p6
kubectl get nodes
docker save springboot-devops-demo -o springboot-devops-demo.tar
docker cp springboot-devops-demo.tar minikube:/springboot-devops-demo.tar
docker exec minikube ctr images import /springboot-devops-demo.tar
kubectl describe pod springboot-deployment-86cb4bbd58-59csf
docker build -t springboot_devops_demo .
docker save springboot_devops_demo -o springboot_devops_demo.tar  
docker cp springboot_devops_demo.tar minikube:/springboot_devops_demo.tar
docker exec minikube ctr -n k8s.io images import /springboot_devops_demo.tar
docker exec minikube ctr -n k8s.io images ls | findstr springboot   
kubectl describe pod springboot-deployment-66775f896b-kbrq7
docker rmi springboot_devops_demo
kubectl config get-contexts
kubectl config use-context docker-desktop
minikube image build -t springboot-devops-demo .
kubectl describe pod springboot-deployment-8bc9b6bd9-ngx2j  
kubectl delete deployment springboot-deployment      
kubectl apply -f k8s/deployment.yaml
kubectl get pods    
kubectl describe pod springboot-deployment-86cb4bbd58-r54vq
kubectl apply -f k8s/service.yaml
kubectl get svc  
minikube service springboot-service 

new image 
kubectl rollout restart deployment springboot-deployment 
minikube image build -t springboot-devops-demo:v2 .
kubectl set image deployment/springboot-deployment springboot-container=springboot-devops-demo:v2
kubectl rollout history deployment/springboot-deployment
kubectl rollout undo deployment/springboot-deployment
kubectl rollout status deployment/springboot-deployment
```


