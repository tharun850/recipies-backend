# Recipies API

## Description
This API allows you to manage and search recipes.

## Endpoints
- `POST /api/recipes/load`: Load recipes from the client.
- `GET /api/recipes/search`: Search recipes by query.
- `GET /api/recipes/{id}`: Get a recipe by ID.
- `GET /api/recipes/all`: Get all recipes.

## Build and Run
1. Clone the repository.
2. Navigate to the project directory.
3. Run `./gradlew build` to build the project.
4. Run `./gradlew bootRun` to start the application.
5. Access the API at `http://localhost:8080`.

## Live Project
- The project is live at: [https://recepies-tksx.onrender.com](https://recepies-tksx.onrender.com)
- Live Project api documentation at : [Swagger UI](https://recepies-tksx.onrender.com/swagger-ui/index.html)

## API Documentation

The API documentation is available at the following URIs:
- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [API](http://localhost:8080/v3/api-docs)

## Configuration
Configuration parameters are externalized in `application.properties`.

## Testing
Run `./gradlew test` to execute unit tests.
