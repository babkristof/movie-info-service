# Movie Info Service

Spring Boot service for searching movie data from external movie providers. The API currently supports `omdb` and `tmdb`, caches search results in Redis, and logs each search asynchronously into MySQL.

## Table of Contents

- [What It Does](#what-it-does)
- [Stack](#stack)
- [Architecture Summary](#architecture-summary)
- [API](#api)
- [OpenAPI](#openapi)
- [Configuration](#configuration)
- [Environment Variables](#environment-variables)
- [Run Locally](#run-locally)
- [Build](#build)
- [Verify](#verify)
- [Data Storage](#data-storage)
- [Provider Behavior](#provider-behavior)
- [Error Handling](#error-handling)
- [Docker Compose Services](#docker-compose-services)
- [Current Limitations](#current-limitations)

## What It Does

- Exposes a single HTTP endpoint: `GET /movies/{movieTitle}?api=...`
- Fetches movie data from OMDb or TMDb
- Returns a unified JSON response with title, year, and directors
- Caches responses in Redis using a normalized title-based key
- Persists search metadata in MySQL asynchronously
- Publishes OpenAPI documentation through Swagger UI

## Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring WebFlux `WebClient`
- Spring Data JPA
- Spring Data Redis
- MySQL 8
- Redis 7
- Spring Cache
- Springdoc OpenAPI / Swagger UI
- Maven
- Docker
- Docker Compose

## Architecture Summary

Request flow:

1. `MovieController` receives `GET /movies/{movieTitle}?api=...`.
2. The `api` query param is resolved into a `MovieSource`.
3. `MovieSearchCacheService` checks Redis first.
4. On cache miss, `MovieProviderRegistry` resolves the provider implementation.
5. `OmdbMovieProvider` or `TmdbMovieProvider` fetches external data and maps it into `MovieSummary`.
6. The controller returns the JSON response.
7. A `MovieSearchLogEvent` is published.
8. `MovieSearchLogListener` stores search metadata in MySQL on a background executor.

Notes:

- Cache keys include provider name and normalized movie title.
- Search logging is best-effort and does not block the HTTP response.
- External provider timeouts are mapped to `504 Gateway Timeout`.

## API

### Search Movies

```http
GET /movies/{movieTitle}?api=omdb
```

Parameters:

- `movieTitle`: required path parameter, max 100 characters
- `api`: required query parameter, allowed values: `omdb`, `tmdb`

Example:

```bash
curl "http://localhost:8080/movies/Inception?api=omdb"
```

Example response:

```json
{
  "movies": [
    {
      "Title": "Inception",
      "Year": "2010",
      "Director": ["Christopher Nolan"]
    }
  ]
}
```

Response codes:

- `200` successful search
- `400` invalid provider, invalid input, or missing required parameter
- `502` external provider failure
- `504` external provider timeout
- `500` unexpected internal server error

## OpenAPI

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Configuration

Main configuration: [`application.yml`](src/main/resources/application.yml)

Docker-specific overrides: [`application-docker.yml`](src/main/resources/application-docker.yml)

Important defaults:

- Server port: `8080`
- Local MySQL URL: `jdbc:mysql://localhost:3306/movie_info`
- Docker MySQL URL: `jdbc:mysql://mysql:3306/movie_info`
- Redis host locally: `localhost`
- Redis host in Docker: `redis`
- Cache TTL: `60m`
- External API timeout: `3s`

## Environment Variables

External API keys are read from environment variables:

- `OMDB_API_KEY`
- `TMDB_API_KEY`

The repository includes a sample [`.env`](.env) file:

```dotenv
OMDB_API_KEY=your_omdb_key
TMDB_API_KEY=your_tmdb_key
```

Docker Compose reads these values and passes them into the application container.

## Run Locally

### Option 1: Run Everything With Docker Compose

This is the simplest way to run the full stack.

1. Fill in [`.env`](.env) with valid API keys.
2. Start the stack:

```bash
docker compose up --build
```

This starts:

- `mysql` on `localhost:3306`
- `redis` on `localhost:6379`
- `app` on `localhost:8080`

The application container is built from [`Dockerfile`](Dockerfile), which:

- builds the project with Java 21
- packages the application with Maven
- runs the generated jar in a lightweight JRE image
- activates the `docker` Spring profile

Stop the stack:

```bash
docker compose down
```

Stop the stack and remove volumes:

```bash
docker compose down -v
```

### Option 2: Run the App Locally and Dependencies in Docker

Prerequisites:

- Java 21
- Maven
- Docker and Docker Compose

1. Fill in [`.env`](.env) or export the same variables in your shell.
2. Start only MySQL and Redis:

```bash
docker compose up -d mysql redis
```

3. Export API keys if needed:

```bash
export OMDB_API_KEY=your_omdb_key
export TMDB_API_KEY=your_tmdb_key
```

4. Run the application:

```bash
mvn spring-boot:run
```

The application is available at:

```text
http://localhost:8080
```

## Build

Compile:

```bash
mvn compile
```

Run tests:

```bash
mvn test
```

Build the Docker image directly:

```bash
docker build -t movie-info-service .
```

## Verify

Example request:

```bash
curl "http://localhost:8080/movies/Inception?api=tmdb"
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Data Storage

### Redis

Used for caching search responses.

- Cache name: `movie-search`
- Cache TTL: `60 minutes`

### MySQL

Used for asynchronous logging in the `movie_search_log` table.

Stored metadata includes:

- provider name
- raw movie title
- normalized movie title
- search timestamp
- returned result count

## Provider Behavior

### OMDb

- Performs a search request
- Fetches details for up to 10 matched items
- Limits concurrent detail fetches

### TMDb

- Performs a search request
- Fetches details and credits for up to 10 matched items
- Extracts directors from credits data

## Error Handling

Structured JSON errors are returned by the global exception handler.

Handled cases include:

- unsupported `api` values
- missing `api` parameter
- validation failures
- external API timeout
- external API failure
- unexpected internal server errors

## Docker Compose Services

[`docker-compose.yml`](docker-compose.yml) defines:

- `mysql` using `mysql:8.4`
- `redis` using `redis:7.2`
- `app` built from the local `Dockerfile`

MySQL data is persisted in the `mysql_data` Docker volume.

## Current Limitations

- The public API currently exposes a single search endpoint
- No test coverage
