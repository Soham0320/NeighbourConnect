# Website Backend (Spring Boot + Thymeleaf)

Responsive (mobile + desktop) website backend in Java.
Stack: Java 17, Spring Boot 3.3, Spring MVC, Thymeleaf, Spring Data JPA, Spring Security, H2 (dev) / MySQL (prod).

## Run
```
mvn spring-boot:run
```
Open http://localhost:8080
Seeded admin login: `admin@example.com` / `Admin@123` (change in `application.properties` before going live).
Production: `mvn spring-boot:run -Dspring-boot.run.profiles=prod` (set DB_USER, DB_PASSWORD, ADMIN_EMAIL, ADMIN_PASSWORD).

## Class map
| Package | Classes | Purpose |
|---|---|---|
| (root) | WebsiteApplication | Entry point |
| config | SecurityConfig, WebConfig, DataSeeder | Security rules + BCrypt, CORS for /api, seed admin and sample data |
| entity | AppUser, Role, Category, Item, ContactMessage | Database tables |
| repository | AppUserRepository, CategoryRepository, ItemRepository, ContactMessageRepository | Data access |
| dto | ItemDto, ContactForm, RegisterForm | Validated form / API objects |
| service | AppUserService, ItemService, CategoryService, ContactService | Business logic |
| controller | PageController, AuthController, AdminController, ItemApiController | Pages, auth, admin, JSON API |
| exception | ResourceNotFoundException, ApiExceptionHandler, WebExceptionHandler | Error handling |

## Routes
- Public: `/`, `/items`, `/items/{slug}`, `/contact`, `/login`, `/register`, `GET /api/items`
- Admin only: `/admin`, `POST/PUT/DELETE /api/items`

## Placeholder domain
`Item` + `Category` are a generic listing (product / service / menu item / property...).
Rename them and add fields to match the real site. `ContactMessage`, users, login and admin are reusable as they are.

## Integrating the Stitch designs (instructions for Antigravity)
1. Unzip the Stitch export. Copy its CSS/JS/images into `src/main/resources/static/{css,js,images}`.
2. Move the shared head, header/nav and footer markup into `templates/fragments/layout.html`.
3. Convert each Stitch page into the matching Thymeleaf template in `src/main/resources/templates/`
   (index, items, item-detail, contact, login, register, admin/dashboard, error).
   - Keep the Stitch markup and classes; add `th:` attributes for dynamic data
     (`th:each`, `th:text`, `th:href="@{...}"`, `th:field` on forms).
   - Keep form `th:action` and the field names (`name`, `email`, `phone`, `message`, etc.) so the controllers bind correctly.
   - Reference assets with `th:href="@{/css/...}"` / `th:src="@{/images/...}"`.
4. Keep the layout responsive: viewport meta tag, mobile-first CSS, hamburger menu on small screens, multi-column on desktop.
5. If Stitch includes pages not covered here, add a method to `PageController` and a template, and a model/service/repository if it needs data.

## Notes
- Brief setup: no Maven wrapper is included; use an installed Maven or let your IDE import `pom.xml`.
- CSRF is enabled for browser forms (Thymeleaf adds the token automatically) and disabled only for `/api/**`.
- The default H2 database is in memory and resets on every restart.
