# BlogLand Backend

A **Spring Boot backend** for the BlogLand platform — powering user authentication, post management, comments, likes, newsletters, and more.  
Built with a clean layered architecture (Controller → Service → Repository) and deployed to **Render** using Docker with automated unit tests.

---

[📄 API Documentation](https://blog-land.onrender.com/swagger-ui/index.html)


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
- **Testing**: JUnit 5, AssertJ, Mockito, Spring Boot Starter Test  

---

## Folder Structure

```
src/main/java/com/nelani/blogland
├── cache/          # Cache configuration  
├── config/         # App configuration (security, DB, etc.)  
├── controller/     # REST controllers (User, Post, Auth, Category, etc.)  
├── dto/            # Data Transfer Objects  
├── exception/      # Global exception handling
├── mapper/         # Response builders
├── model/          # JPA entities
├── notifications/  # Emails etc.
├── repository/     # Spring Data repositories  
├── response/       # Standard API response wrappers
├── schedule/       # Scheduled methods
├── seeder/         # Initial seeders 
├── service/        # Service interfaces
│   └── impl/       # Service implementations
├── sockets/        # For live updates 
└── utils/  
     └── validation/ # Entity validation logic  
    
```

---

## 🧪 Unit & Integration Tests

All major layers — Repository, Service, and Controller — are covered by automated tests using JUnit 5, Mockito, and Spring Boot Test.

### Test Coverage
| Layer              | Framework                                    | Description                                                  |
|--------------------|----------------------------------------------|-------------------------------------------------------------|
| Repository Tests   | `@DataJpaTest`                               | Tests CRUD operations, custom queries, and pagination using H2 in-memory DB |
| Service Tests      | `@ExtendWith(MockitoExtension.class)`        | Mocks repository interactions and validates business logic   |
| Controller Tests   | `@WebMvcTest`                                | Validates HTTP endpoints, request validation, and response handling |
| Validation Tests   | JUnit + Jakarta Validation                   | Verifies custom validators under `utils/validation`          |
| Integration Tests  | `@SpringBootTest`                            | Tests multiple layers together for end-to-end behavior       |

### Example Tests
```java
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void shouldFindPostsByKeyword() {
        // Arrange
        postRepository.save(new Post("Title", "summary", "imgUrl"));
        Pageable pageable = PageRequest.of(0, 5);

        // Act
        var result = postRepository.searchByKeyword("Title", pageable);

        // Assert
        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getContent().get(0).getTitle()).isEqualTo("Title");
    }
}
```

### Running Tests
Run all tests locally:
```bash
mvn test
```

During Docker builds (and deployment to Render), the same command is automatically executed. The deployment only proceeds if all tests pass successfully.

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

### Deployment
- Hosted on Render  
- Docker image build runs tests before deployment  
- CI/CD ensures only passing builds are deployed  

---

## Roadmap

- ✅ JWT & OAuth2 authentication  
- ✅ CRUD for posts, categories, comments, users  
- ✅ AI moderation with Hugging Face   
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
- Dockerized build with full test validation.  
- Scalable architecture ready for MySQL + Redis.  
- Full test coverage for all core modules.
