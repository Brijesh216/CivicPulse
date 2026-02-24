# CivicPulse – AI‑Enabled Public Complaint & Service Management Platform

> A full‑stack Java (Spring Boot) web application that enables citizens to report civic issues and authorities to manage, track, and resolve them through a secure workflow system with analytics dashboards.

---

## 🚀 Features

### 👤 Citizen Portal

* User registration & secure login (JWT authentication)
* Submit complaints with description and image upload
* Track complaint status in real‑time
* View complaint history
* Notifications on updates

### 🧑‍💼 Officer Portal

* View assigned complaints
* Accept / reject tasks
* Update progress (In‑Progress / Resolved)
* Upload resolution proof

### 🛠 Admin Panel

* Manage users and officers
* Create and manage departments
* Automatic complaint assignment
* Monitor platform activity

### 📊 Analytics Dashboard

* Monthly complaint statistics
* Area‑wise issue analysis
* Department performance tracking
* Resolution time reports

---

## 🏗 System Architecture

The project follows a layered MVC architecture:

```
Presentation Layer (Frontend UI)
        ↓
Controller Layer (REST APIs)
        ↓
Service Layer (Business Logic)
        ↓
Repository Layer (JPA/Hibernate)
        ↓
Database Layer (MySQL)
```

---

## 🧰 Tech Stack

**Backend**

* Java 17
* Spring Boot
* Spring Security
* JWT Authentication
* Hibernate / JPA
* Maven

**Frontend**

* HTML5
* CSS3
* Bootstrap
* JavaScript

**Database**

* MySQL

**Tools & Utilities**

* Git & GitHub
* Postman (API testing)
* IntelliJ IDEA / VS Code

---

## 📂 Project Structure

```
CivicPulse/
│
├── src/main/java/com/civicpulse
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── security
│   └── config
│
├── src/main/resources
│   ├── static
│   ├── templates
│   └── application.properties
│
└── pom.xml
```

---

## ⚙️ Installation & Setup

### Prerequisites

* Java JDK 17+
* Maven
* MySQL Server
* Git

### 1️⃣ Clone the repository

```
git clone https://github.com/Brijesh216/CivicPulse.git
cd civicpulse
```

### 2️⃣ Configure Database

Create a MySQL database:

```
CREATE DATABASE civicpulse_db;
```

Update `application.properties`:

```
spring.datasource.url=jdbc:mysql://localhost:3306/civicpulse_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### 3️⃣ Run the Backend

```
mvn spring-boot:run
```

Server will start at:

```
http://localhost:8080
```

---

## 🔐 Authentication

CivicPulse uses **JWT Token Authentication**:

* Login API returns token
* Token must be included in request header

```
Authorization: Bearer <your-token>
```

---

## 📡 Sample API Endpoints

| Method | Endpoint               | Description             |
| ------ | ---------------------- | ----------------------- |
| POST   | /api/auth/register     | Register new user       |
| POST   | /api/auth/login        | User login              |
| POST   | /api/complaints        | Create complaint        |
| GET    | /api/complaints/{id}   | Get complaint details   |
| PUT    | /api/complaints/status | Update complaint status |
| GET    | /api/admin/users       | Get all users           |

---

## 🗄 Database Entities

* Users
* Roles
* Complaints
* Departments
* Officers
* Complaint Updates
* Notifications

---

## 🧪 Testing

* API testing using Postman
* Unit testing (service layer)
* Manual UI testing

---

## 🔮 Future Enhancements

* Mobile app (Android)
* AI‑based complaint classification
* Email & SMS alerts
* GIS map‑based issue tracking
* Chatbot assistance

---

## 📜 License

This project is for educational and academic purposes.

---

## 👨‍💻 Author

**Brijesh Prasad**

🌐 Connect with me: 
- 🔗 [LinkedIn](https://www.linkedin.com/in/brijesh216) 
- 💻 [GitHub](https://github.com/brijesh216)

---

⭐ If you found this project helpful, consider giving it a star on GitHub!
