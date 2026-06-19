# Radix Tech Assessment – Loan Payment System

This repository contains the solution for the Radix Tech Senior Developer technical assessment. It is a single Spring Boot application that implements a Loan Payment System with two core domains: Loan and Payment.

## 1. Overview

The system provides REST APIs to manage loans and process payments against them. It uses an in-memory H2 database for persistence, which is reset every time the application restarts.

- **Loan Domain**: Manages loan creation and retrieval.
- **Payment Domain**: Handles payments made towards loans.

## 2. Technology Stack

- **Java 11**: Core programming language.
- **Spring Boot 2.7.18**: Application framework.
- **Spring Web**: For building RESTful APIs.
- **Spring Data JPA**: For database interaction.
- **H2 Database**: In-memory database for persistence.
- **Maven**: Build automation and dependency management.
- **JUnit 5 & Mockito**: For unit testing.

## 3. Building and Running the Application

### Prerequisites

- Java Development Kit (JDK) 11 or later.
- Apache Maven 3.6.0 or later.

### Build Steps

1.  **Clone the repository:**

    ```bash
    git clone <repository-url>
    cd loan-payment-system
    ```

2.  **Build the project using Maven:**

    This command will compile the source code, run all unit tests, and package the application into a single executable JAR file in the `target` directory.

    ```bash
    mvn clean install
    ```

### Run Steps

Once the build is successful, you can run the application using the `java -jar` command:

```bash
java -jar target/loan-payment-system-1.0.0.jar
```

The application will start on the default port `8080`. You can access the H2 database console in your browser at `http://localhost:8080/h2-console` to inspect the database. Use the default credentials from `application.properties` (`JDBC URL: jdbc:h2:mem:loandb`, `User: sa`, `Password: `).

## 4. API Endpoints & Usage

You can test the APIs using tools like `curl` or Postman.

### Loan Domain

#### Create a New Loan

- **Endpoint**: `POST /loans`
- **Description**: Creates a new loan with a specified amount and term.
- **`curl` Example**:

  ```bash
  curl -X POST http://localhost:8080/loans \
  -H "Content-Type: application/json" \
  -d '{
    "loanAmount": 10000.00,
    "term": 12
  }'
  ```

- **Success Response (201 Created)**:

  ```json
  {
    "loanId": 1,
    "loanAmount": 10000.00,
    "term": 12,
    "remainingBalance": 10000.00,
    "status": "ACTIVE"
  }
  ```

#### Retrieve Loan Details

- **Endpoint**: `GET /loans/{loanId}`
- **Description**: Retrieves the details of a loan by its ID.
- **`curl` Example**:

  ```bash
  curl http://localhost:8080/loans/1
  ```

- **Success Response (200 OK)**:

  ```json
  {
    "loanId": 1,
    "loanAmount": 10000.00,
    "term": 12,
    "remainingBalance": 10000.00,
    "status": "ACTIVE"
  }
  ```

### Payment Domain

#### Record a Payment

- **Endpoint**: `POST /payments`
- **Description**: Records a payment made towards a specific loan.
- **`curl` Example**:

  ```bash
  curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -d '{
    "loanId": 1,
    "paymentAmount": 500.00
  }'
  ```

- **Success Response (201 Created)**:

  The response includes the payment record and the updated state of the loan.

  ```json
  {
    "paymentId": 1,
    "loanId": 1,
    "paymentAmount": 500.00,
    "updatedLoan": {
      "loanId": 1,
      "loanAmount": 10000.00,
      "term": 12,
      "remainingBalance": 9500.00,
      "status": "ACTIVE"
    }
  }
  ```

### Error Handling

- **Overpayment**: If a payment amount exceeds the remaining balance, a `400 Bad Request` is returned.

  ```json
  {
    "status": 400,
    "message": "Payment amount 11000.00 exceeds the remaining balance of 10000.00 for loan 1.",
    "timestamp": "..."
  }
  ```

- **Loan Not Found**: If a `loanId` does not exist, a `404 Not Found` is returned.

  ```json
  {
    "status": 404,
    "message": "Loan not found with id: 999",
    "timestamp": "..."
  }
  ```

## 5. Project Structure

The project follows a standard Maven layout. The core logic is separated into two main packages, `com.radix.loanpayment.loan` and `com.radix.loanpayment.payment`, to maintain a clear separation of concerns between the two domains.

```
src/main/java/com/radix/loanpayment
├── LoanPaymentApplication.java
├── common/exception/         # Global exception handlers
├── loan/
│   ├── controller/           # Loan REST Controller
│   ├── dto/                  # Loan DTOs (Request/Response)
│   ├── entity/               # Loan JPA Entity
│   ├── repository/           # Loan Spring Data Repository
│   └── service/              # Loan business logic
└── payment/
    ├── controller/           # Payment REST Controller
    ├── dto/                  # Payment DTOs
    ├── entity/               # Payment JPA Entity
    ├── repository/           # Payment Spring Data Repository
    └── service/              # Payment business logic
```

## 6. Unit Testing

The project includes comprehensive unit tests for the service layer of both the Loan and Payment domains. These tests use Mockito to isolate the business logic from the persistence layer.

To run the tests, execute the following Maven command:

```bash
./mvnw test
```

The tests cover:
- Successful loan creation.
- Successful payment processing and balance reduction.
- Rejection of overpayments.
- Transitioning a loan to `SETTLED` status upon full payment.
