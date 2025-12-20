# eBank Application

A full-stack banking application built with Spring Boot 3 and React.

**Kassimi Bank** - Modern Banking Solution

## 🏗️ Architecture

- **Backend**: Spring Boot 3, Spring Security, JWT, Spring Data JPA, MySQL 8
- **Frontend**: React JS with modern UI
- **CI/CD**: GitHub Actions

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.8+
- MySQL 8 (optional, H2 used by default)

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start
```

## 👥 User Roles

| Role | Features |
|------|----------|
| **AGENT_GUICHET** | Add new client, Create bank account |
| **CLIENT** | View dashboard, Make transfers |

## 📝 API Documentation

See [API Documentation](docs/API.md)

## 🔐 Security

- JWT Authentication (1 hour token validity)
- BCrypt password encryption
- Role-based access control

## 📄 License

MIT License
