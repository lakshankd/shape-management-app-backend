
# Shape Management Backend (Spring Boot)

This is the backend component of the Shape Management Application built with **Spring Boot** and **MySQL**. It provides RESTful APIs to manage and visualize geometric shapes, including overlap detection, user authentication, and persistent data storage.


## Features

-  RESTful CRUD operations for shapes (circle, rectangle, polygon, etc.)
-  MySQL integration using Spring Data JPA
-  Overlap detection algorithm between shapes
-  Basic authentication using Spring Security
-  Input validation on both API and DB levels
-  Unit & Integration Testing using JUnit and MockMVC## Installation & Running Locally

#### Configure Environment Variables (Optional)
The application uses the following application.properties file with default values. You can override them via environment variables if needed:

```bash
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/shape-management?            createDatabaseIfNotExist=true}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:password}
```

#### Set up MySQL
- Create a MySQL database (optional if createDatabaseIfNotExist=true is used).
- Default DB name: shape-management

#### Run the Application
```bash
./mvnw spring-boot:run
```
App will run on: http://localhost:8080
## API Endpoints

#### Auth
| Method      | Endpoint    | Description   |
| ----------- | ----------- |---------------|
| `POST` | `/api/v1/auth/register` | User registration|
| `POST` | `/api/v1/auth/login` | User login|
| `POST` | `/api/v1/auth/me` | Get logged in user|

#### Shapes

| Method      | Endpoint    | Description   |
| ----------- | ----------- |---------------|
| `GET` | `/api/v1/shapes` |Fetch all shapes|
| `GET` | `/api/v1/shapes/{id}` |Fetch shapes by Id|
| `POST` | `/api/v1/shapes` | Create new shape|
| `PUT` | `/api/v1/shapes/{id}` | Update shape |
| `DELETE`   | `/api/v1/shapes/{id}`| Delete shape|
| `GET`   | `/api/v1/shapes/{id}` | Get overlapping shapes|

## Testing
This application includes unit tests for business logic and integration tests for REST APIs.

#### Run All Tests
```bash
mvn test -Dtest=AllTestsRunner
```

#### Run Unit Tests Only
```bash
mvn test -Dtest=UnitTestRunner
```

#### Run Integration Tests Only
```bash
mvn test -Dtest=IntegrationTestRunner
```

## Test Results

#### Unit Test Results
<img width="100%" height="512" alt="Screenshot 2025-07-12 at 01 02 45" src="https://github.com/user-attachments/assets/87fbaf30-4844-4916-bc0f-4c2aa4fe6f87" />
<img width="100%" height="512" alt="Screenshot 2025-07-12 at 01 03 13" src="https://github.com/user-attachments/assets/e8b837d8-0f7a-43aa-8cf8-d019af70b264" />


#### Integration Test Results
<img width="100%" height="512" alt="Screenshot 2025-07-12 at 01 04 04" src="https://github.com/user-attachments/assets/2bf8c868-93c5-4ce2-8b4e-f7c4907a1325" />
<img width="100%" height="512" alt="Screenshot 2025-07-12 at 01 04 19" src="https://github.com/user-attachments/assets/e0888582-de15-4c0e-ae6d-ff04d8001617" />


