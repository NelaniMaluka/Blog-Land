# BlogLand Backend

A **Spring Boot backend** for the BlogLand platform — powering user authentication, post management, comments, likes, newsletters, and more.  
Built with a clean layered architecture (Controller → Service → Repository) and deployed to **Render** using Docker with automated unit tests.

---

## Features

### Authentication & Security
- JWT-based authentication  
- Google OAuth2 
- Role-based access  

### Content Management
- CRUD for posts, categories, users, comments, likes  
- Pagination for posts  
- Newsletter subscription & email integration  

### Data Seeding
- Initial seeders for users, blog posts, and categories  
- Scheduled seeder to boost under-engaged categories  

### Validation & Error Handling
- Centralized validation logic (`utils/validation`)  
- Global exception handling  

### AI Integration
- [Hugging Face API](https://huggingface.co/) for content moderation  

### Database Support
- H2 (development)  
- MySQL (production-ready, planned)  

---

## Tech Stack

- **Core**: Java 17, Spring Boot 3.5  
- **Security**: Spring Security, JWT, OAuth2  
- **Database**: H2 (dev), MySQL (planned)
- **Build Tools**: Maven, Lombok, MapStruct  
- **DevOps**: Docker, Render (deployment), Dependabot (dependency updates)  
- **Testing**: JUnit + Spring Boot Starter Test  

---

## Folder Structure

src/main/java/com/nelani/blogland  
├── config/          # App configuration (security, DB, etc.)  
├── controller/      # REST controllers (User, Post, Auth, Category, etc.)  
├── dto/            # Data Transfer Objects  
├── exception/      # Global exception handling  
├── model/          # JPA entities  
├── repository/     # Spring Data repositories  
├── response/       # Standard API response wrappers  
├── service/        # Service interfaces  
    ├── serviceImpl/    # Service implementations  
├── seeder/         # Initial + scheduled seeders  
└── utils/  
    ├── validation/ # Entity validation logic  
    └── builder/   # Response builders  

---

## Getting Started

### Prerequisites
- Java 17+  
- Maven 3.9+  
- Docker  

### Run Locally

Clone the repo:
```bash
git clone https://github.com/NelaniMaluka/blog-land-backend.git
cd blog-land-backend
```

Build & run with Maven:
```bash
mvn clean install
mvn spring-boot:run
```

Or run with Docker:
```bash
docker build -t blogland-backend .
docker run -p 8080:8080 blogland-backend
```

### Testing
All services have unit tests.  
Tests are run automatically during the Docker build and must pass before deployment.

Run locally with:
```bash
mvn test
```

### Deployment
- Hosted on Render  
- Docker build runs unit tests before deploying  

---

## Roadmap

- ✅ JWT & OAuth2 authentication  
- ✅ CRUD for posts, categories, comments, users  
- ✅ AI moderation with Hugging Face  
- ⏳ Switch from H2 → MySQL in production  
- ⏳ Expand admin dashboard endpoints  

---

## Contributing
Contributions are welcome!  
Please open an issue or submit a pull request.

---

## License
MIT License. See [LICENSE](LICENSE) for details.

---

## Notes
- Automated dependency updates powered by Dependabot.  
- Built to be production-quality (Docker + Render + tests).  
- Scalable with Redis + MySQL (planned).  
- Modern features: OAuth2, AI moderation, centralized validation.
