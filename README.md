# calculator-devops

Calculator REST API (Java 17, Spring Boot 3, Maven) with a complete CI/CD pipeline:
**GitHub → Jenkins → Docker Hub → Docker Compose (Nginx + 2 replicas)**.

## API

| Operation | Request |
|-----------|---------|
| Add       | `GET /api/v1/add?a=2&b=3` |
| Subtract  | `GET /api/v1/subtract?a=10&b=4` |
| Multiply  | `GET /api/v1/multiply?a=6&b=7` |
| Divide    | `GET /api/v1/divide?a=9&b=2` (`b=0` → HTTP 400) |
| Health    | `GET /actuator/health` |

Success: `{"operation":"add","a":2,"b":3,"result":5}`
Error (400): `{"status":400,"error":"Bad Request","message":"Division by zero is not allowed","path":"/api/v1/divide","timestamp":"..."}`

## Build, test, run

```bash
mvn clean package          # build target/calculator.jar
mvn test                   # JUnit 5 + MockMvc; XML reports in target/surefire-reports
java -jar target/calculator.jar            # http://localhost:8080
```

```bash
curl "http://localhost:8080/api/v1/add?a=2&b=3"
curl "http://localhost:8080/api/v1/divide?a=1&b=0"     # 400
curl "http://localhost:8080/actuator/health"
```

## Docker

```bash
docker build -t calculator-devops .
docker run --rm -p 8080:8080 calculator-devops
```

## Docker Compose (Nginx proxy + 2 replicas)

```bash
docker compose up -d --build        # stack on http://localhost:8081
curl http://localhost:8081/api/v1/multiply?a=6\&b=7
docker compose ps
docker compose down
```

## Jenkins pipeline

`Jenkinsfile` stages: Checkout → Build → Test (JUnit published) → Docker Build (tag = build number) → Push (Docker Hub, Jenkins credentials `dockerhub-creds`) → Deploy (`docker compose up -d` + health verification). Email is sent on success/failure. Trigger: GitHub webhook (`githubPush()`).

Setup: configure tools `Maven3`, `JDK17`; credential `dockerhub-creds`; SMTP in *Manage Jenkins → System*; edit `DOCKER_IMAGE` and `NOTIFY_EMAIL` in the Jenkinsfile.

## Running with Docker Compose

Start the application and Nginx reverse proxy:

    docker compose up -d --build

Check the running containers:

    docker compose ps

Check the application health endpoint:

    curl http://localhost:8083/actuator/health

Stop the services:

    docker compose down

## Running with Docker Compose

Start the application and Nginx reverse proxy:

    docker compose up -d --build

Check the running containers:

    docker compose ps

Check the application health endpoint:

    curl http://localhost:8083/actuator/health

Stop the services:

    docker compose down
