# Charity Collection Box Management Service

This Spring Boot application provides a REST API to manage collection boxes for charity fundraising events, supporting multi-currency collections and automatic transfers with hard-coded exchange rates.

---

## 🌟 Features

- Register and unregister collection boxes  
- Assign empty boxes to fundraising events  
- Add money to boxes in multiple currencies  
- Transfer box funds to event accounts with currency conversion  
- Generate financial report of all fundraising events  

---

## 🛠 Technologies

- Java 17  
- Spring Boot 3.x  
- Spring Data JPA  
- H2 in-memory database  
- Lombok  
- Maven  

---

## ▶️ Prerequisites

- Java 17+  
- Maven 3.6+  

---

## 📦 Build & Run

# Clone repository
git clone https://github.com/your-username/charity-box-service.git
cd charity-box-service

# Build
mvn clean package

# Run JAR
java -jar target/charity-box-service-0.0.1-SNAPSHOT.jar

# Or directly:

mvn spring-boot:run


The application will start on port 8080.
H2 console is available at:

http://localhost:8080/h2-console  
JDBC URL: jdbc:h2:mem:testdb  
User: sa  
Password: (none)


# 📖 REST API

Base URL: http://localhost:8080

These are some examples to test with Postman

## 1. Create fundraising event

POST http://localhost:8080/api/events

Content-Type: application/json

{
  "name": "Charity One",
  "currency": "EUR"
}

## 2. List all events

GET http://localhost:8080/api/events

## 3. Register a new collection box

POST http://localhost:8080/api/boxes

## 4. List all collection boxes

GET http://localhost:8080/api/boxes

## 5. Assign a box to an event

PUT http://localhost:8080/api/boxes/1/assign

Content-Type: application/json

{
  "eventId": 1
}

## 6. Add money to a box

PUT http://localhost:8080/api/boxes/1/add-money

Content-Type: application/json

{
  "currency": "EUR",
  "amount": 50.00
}

## 7. Transfer box funds to event

PUT http://localhost:8080/api/boxes/1/transfer

## 8. Unregister a collection box

DELETE http://localhost:8080/api/boxes/1

## 9. Financial report

GET http://localhost:8080/api/events/report

# 🧪 Testing

To ensure the application behaves correctly, additional tests are provided

To run all tests:

mvn test

# ✍️ Author 

Karol Krawczyk
