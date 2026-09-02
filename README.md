# 🚀 Proyecto Integrador — API REST con Spring Boot, MongoDB y Spring Security

Proyecto desarrollado en el marco del curso de **Arquitectura y Desarrollo de Software / IETI**.
Esta API REST para la gestión de usuarios ha evolucionado en tres etapas: desde almacenamiento en memoria, pasando por persistencia real con **MongoDB Atlas**, hasta una capa de seguridad completa basada en **Spring Security + JWT**.

---

## 📋 Tabla de Contenidos

- [🛠 Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [📁 Estructura del Proyecto](#-estructura-del-proyecto)
- [📦 Sección 1 — Implementación Inicial (Almacenamiento en Memoria)](#-sección-1--implementación-inicial-almacenamiento-en-memoria)
- [🍃 Sección 2 — Capa de Persistencia con Spring Data MongoDB](#-sección-2--capa-de-persistencia-con-spring-data-mongodb)
  - [Parte 1: Configuración y Conexión con MongoDB Atlas](#parte-1-configuración-y-conexión-con-mongodb-atlas)
  - [Parte 2: Documentos, Repositorios y Servicios CRUD](#parte-2-documentos-repositorios-y-servicios-crud)
  - [Pruebas CRUD en Postman y MongoDB Compass](#-pruebas-crud-en-postman-y-mongodb-compass)
- [🔒 Sección 3 — Capa de Seguridad con Spring Security y JWT](#-sección-3--capa-de-seguridad-con-spring-security-y-jwt)
  - [Evidencias de Seguridad](#-evidencias-de-seguridad)
- [🚀 Instrucciones de Ejecución](#-instrucciones-de-ejecución)

---

## 🛠 Tecnologías Utilizadas

| Categoría | Tecnología |
| :--- | :--- |
| **Lenguaje** | Java 17 |
| **Framework** | Spring Boot 4.1.0 |
| **Web** | `spring-boot-starter-webmvc` |
| **Persistencia** | `spring-boot-starter-data-mongodb` · MongoDB Atlas |
| **Seguridad** | `spring-boot-starter-security` · JJWT 0.12.6 · BCrypt |
| **Validación** | `spring-boot-starter-validation` |
| **Documentación** | `springdoc-openapi-starter-webmvc-ui` (Swagger / OpenAPI 3) |
| **Build** | Maven |
| **Testing** | Postman · MongoDB Compass |

---

## 📁 Estructura del Proyecto

```text
edu.eci.proyecto
├── controller
│   ├── AuthenticationController.java   ← Login y generación de JWT
│   ├── HealthController.java           ← Health check endpoint
│   └── UserController.java             ← CRUD de usuarios
├── dto
│   ├── AuthRequest.java                ← Credenciales de login
│   ├── AuthResponse.java               ← Token JWT de respuesta
│   ├── UserCreateRequestDTO.java       ← Datos para crear usuario
│   ├── UserResponseDTO.java            ← Datos de respuesta de usuario
│   └── UserUpdateRequestDTO.java       ← Datos para actualizar usuario
├── entity
│   ├── Role.java                       ← Enum de roles (ADMIN, USER)
│   └── User.java                       ← Documento MongoDB
├── exception
│   ├── EmailAlreadyExistsException.java
│   ├── GlobalExceptionHandler.java     ← Manejo centralizado de errores
│   └── UserNotFoundException.java
├── repository
│   └── UserRepository.java             ← MongoRepository<User, UUID>
├── security
│   ├── CustomUserDetails.java          ← Implementación de UserDetails
│   ├── CustomUserDetailsService.java   ← Carga usuario desde MongoDB
│   ├── JwtRequestFilter.java           ← Filtro de validación de JWT
│   ├── JwtUtil.java                    ← Generación y parseo de tokens
│   └── SecurityConfig.java             ← Configuración de seguridad
├── service
│   ├── UserService.java                ← Interfaz del servicio
│   └── UserServiceImpl.java            ← Lógica de negocio
└── ProyectoApplication.java
```

---

## 📦 Sección 1 — Implementación Inicial (Almacenamiento en Memoria)

En la primera etapa se diseñó la arquitectura base de la API REST con almacenamiento temporal en memoria:

1. **Interfaz `UserService` y HashMap:**
   - Contratos CRUD definidos en la interfaz (`Create`, `Read`, `Update`, `Delete`).
   - Implementación respaldada por `HashMap<UUID, User>` para simular persistencia durante el desarrollo temprano.
   - Servicio configurado como componente inyectable con `@Service`.

2. **Controlador REST (`UserController`):**
   - `GET /api/v1/users` — Listado completo de usuarios.
   - `GET /api/v1/users/{id}` — Búsqueda por UUID.
   - `POST /api/v1/users` — Creación, retorna `201 Created` con header `Location`.
   - `PATCH /api/v1/users/{id}` — Actualización parcial de campos.
   - `DELETE /api/v1/users/{id}` — Eliminación, retorna `204 No Content`.

3. **Verificación:** Validación funcional de todos los endpoints mediante Postman.

---

## 🍃 Sección 2 — Capa de Persistencia con Spring Data MongoDB

El almacenamiento en memoria fue reemplazado por persistencia real y no relacional con **MongoDB**.

### Parte 1: Configuración y Conexión con MongoDB Atlas

1. **Configuración del Clúster en MongoDB Atlas:**
   - Creación del clúster, usuario y contraseña con permisos de lectura/escritura.
   - Habilitación de Network Access desde cualquier IP (`0.0.0.0/0`) para el entorno de desarrollo.

2. **Dependencia en `pom.xml`:**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-mongodb</artifactId>
   </dependency>
   ```
   ![Dependencias pom.xml](Assets/Screenshot%20From%202026-08-25%2010-18-00.png)

3. **Propiedades de Conexión (`application-dev.properties`):**
   ```properties
   spring.application.name=proyecto
   spring.profiles.active=dev
   spring.mongodb.representation.uuid=standard
   spring.mongodb.uri=mongodb+srv://<db_user>:<db_password>@estebancluster.hzvwuym.mongodb.net/users?retryWrites=true&w=majority
   ```
   ![Archivo atlas-credentials.env](Assets/Screenshot%20From%202026-08-25%2010-18-29.png)

4. **Verificación de Conexión:**
   Al iniciar la aplicación se valida la inicialización del `TomcatWebServer` en el puerto `8080` y la conexión exitosa con el clúster de MongoDB Atlas:

   ![Logs de Conexión con MongoDB Atlas](Assets/Screenshot%20From%202026-08-25%2010-19-23.png)

---

### Parte 2: Documentos, Repositorios y Servicios CRUD

1. **Clase Document — `User.java`:**
   Mapeada con `@Document(collection = "users")`, usando `@Id` sobre el campo `id` de tipo `UUID` y `@Field("email_address")` para personalizar el nombre en la base de datos.

   ![Entidad User - Clase Document](Assets/Screenshot%20From%202026-08-25%2010-20-30.png)

2. **Repositorio — `UserRepository.java`:**
   Interfaz que extiende `MongoRepository<User, UUID>` para gestionar operaciones de persistencia de forma declarativa.

   ![Interfaz UserRepository](Assets/Screenshot%20From%202026-08-25%2010-19-48.png)

3. **Servicio CRUD — `UserServiceImpl.java`:**
   Lógica de negocio que inyecta `UserRepository` para guardar, consultar, actualizar y eliminar documentos, con mapeo de DTOs y manejo de excepciones.

   ![Servicio UserServiceImpl](Assets/Screenshot%20From%202026-08-25%2010-21-08.png)

4. **Endpoints expuestos (`UserController`):**

| Método | Endpoint | Descripción | Respuesta |
| :---: | :--- | :--- | :---: |
| `GET` | `/api/v1/users` | Listado de todos los usuarios | `200 OK` |
| `GET` | `/api/v1/users/{id}` | Usuario por UUID | `200` / `404` |
| `POST` | `/api/v1/users` | Crear nuevo usuario | `201 Created` |
| `PATCH` | `/api/v1/users/{id}` | Actualización parcial | `200` / `404` |
| `DELETE` | `/api/v1/users/{id}` | Eliminar usuario | `204` / `404` |

---

### 🧪 Pruebas CRUD en Postman y MongoDB Compass

**1 — Estado inicial de la colección `users` (vacía):**

![MongoDB Compass - Colección vacía](Assets/Screenshot%20From%202026-08-25%2010-17-35.png)

---

**2 — `GET /api/v1/users` — Lista vacía inicial:**

![Postman - GET lista vacía](Assets/Screenshot%20From%202026-08-25%2010-22-02.png)

---

**3 — `POST /api/v1/users` — Creación de usuario:**

Petición con cuerpo JSON — respuesta `201 Created` con ID generado (`ab0d36e3-...`):

![Postman - POST crear usuario](Assets/Screenshot%20From%202026-08-25%2010-22-29.png)

Documento persistido en MongoDB Atlas:

![MongoDB Compass - Documento creado](Assets/Screenshot%20From%202026-08-25%2010-23-25.png)

---

**4 — `PATCH /api/v1/users/{id}` — Actualización parcial:**

Modificación exitosa con respuesta `200 OK`:

![Postman - PATCH actualizar usuario](Assets/Screenshot%20From%202026-08-25%2010-23-32.png)

Cambios reflejados en la base de datos:

![MongoDB Compass - Documento actualizado](Assets/Screenshot%20From%202026-08-25%2010-23-42.png)

---

**5 — `DELETE /api/v1/users/{id}` — Eliminación:**

Solicitud DELETE con respuesta `204 No Content`:

![Postman - DELETE eliminar usuario](Assets/Screenshot%20From%202026-08-25%2010-23-56.png)

Colección vacía tras la eliminación:

![MongoDB Compass - Colección tras eliminación](Assets/Screenshot%20From%202026-08-25%2010-24-03.png)

---

## 🔒 Sección 3 — Capa de Seguridad con Spring Security y JWT

Se implementó seguridad **stateless** para proteger la API REST mediante **Spring Security** y **JSON Web Tokens (JWT)**, con autenticación, autorización basada en roles y cifrado de contraseñas con BCrypt.

El proceso se realizó en **5 commits secuenciales**, cada uno con una responsabilidad clara dentro de la arquitectura de seguridad:

| Commit | Tipo | Descripción |
| :--- | :---: | :--- |
| `57410bb` | `refactor` | Reestructurar paquetes, DTOs y manejo de excepciones |
| `e3373fe` | `build` | Agregar dependencias de Spring Security y JJWT |
| `dea2153` | `feat` | Implementar JwtUtil, CustomUserDetailsService y roles |
| `a56acdf` | `feat` | Implementar JwtRequestFilter y SecurityConfig stateless |
| `9459e34` | `feat` | Implementar AuthenticationController con DTOs y BCrypt |

### Arquitectura de Seguridad — Flujo por Petición

```mermaid
sequenceDiagram
    participant C as Cliente (Postman)
    participant F as JwtRequestFilter
    participant JU as JwtUtil
    participant CUDS as CustomUserDetailsService
    participant SC as SecurityContextHolder
    participant CTRL as Controller

    C->>F: HTTP Request + "Authorization: Bearer <token>"
    F->>F: Extraer token del header
    F->>JU: extractUsername(token)
    JU-->>F: email del usuario
    F->>CUDS: loadUserByUsername(email)
    CUDS-->>F: CustomUserDetails (User de MongoDB)
    F->>JU: isTokenValid(token, userDetails)
    JU-->>F: true / false
    alt Token válido
        F->>SC: setAuthentication(authToken con roles)
        F->>CTRL: filterChain.doFilter() → continúa
        CTRL-->>C: 200 OK con datos
    else Token inválido o ausente
        F-->>C: 401 Unauthorized
    end
```

---

### Commit 1 — `refactor`: Reestructuración de Paquetes, DTOs y Manejo de Excepciones

> **Hash:** `57410bb` · 10 archivos · **+166 / -90 líneas**

Este commit preparó la base antes de integrar la seguridad: corrigió la estructura del proyecto, separó los DTOs por caso de uso y añadió manejo centralizado de errores HTTP.

**Cambios principales:**

- `respository/` → renombrado a `repository/` (convención estándar de Spring Data).
- `controller/health/HealthController` → movido a `controller/HealthController`.
- `UserRequestDTO` → reemplazado por `UserCreateRequestDTO` y `UserUpdateRequestDTO` separados.
- Añadidos `EmailAlreadyExistsException` y `GlobalExceptionHandler`.

**`GlobalExceptionHandler.java`** — Manejo uniforme de excepciones para toda la API con `@RestControllerAdvice`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailConflict(EmailAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
```

---

### Commit 2 — `build(deps)`: Dependencias de Spring Security y JJWT

> **Hash:** `e3373fe` · 1 archivo (`pom.xml`) · **+31 líneas**

JJWT se divide en tres artefactos para separar la API pública de la implementación:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JJWT: API de contratos (compile) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<!-- JJWT: Implementación (solo en runtime) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<!-- JJWT: Serialización con Jackson (solo en runtime) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

> `jjwt-impl` y `jjwt-jackson` van en scope `runtime` porque solo se necesitan al ejecutar, no al compilar. El código fuente solo depende de la API pública `jjwt-api`, siguiendo el principio de inversión de dependencias.

---

### Commit 3 — `feat(security)`: JwtUtil, CustomUserDetailsService y Roles

> **Hash:** `dea2153` · 4 archivos nuevos · **+153 líneas**

#### `Role.java` — Enum de roles del sistema

```java
public enum Role {
    ADMIN,
    USER
}
```

El campo `role` se almacena en MongoDB como string. La entidad `User` lo inicializa en `USER` por defecto:

```java
@Document(collection = "users")
public class User {
    @Id
    private UUID id;
    private String name;
    @Field("email_address")
    private String email;
    private String password;        // almacenado como hash BCrypt
    private Role role = Role.USER;  // valor por defecto
}
```

---

#### `CustomUserDetails.java` — Adaptador entre `User` y Spring Security

Spring Security no conoce la entidad `User` del dominio. Esta clase actúa como **adaptador** que envuelve el `User` de MongoDB y lo expone bajo la interfaz `UserDetails`:

```java
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        // Spring Security requiere el prefijo "ROLE_" para @PreAuthorize("hasRole('ADMIN')")
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getEmail(); } // el email es el identificador
}
```

---

#### `CustomUserDetailsService.java` — Carga del usuario desde MongoDB

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}
```

---

#### `JwtUtil.java` — Generación y Validación de Tokens

El secreto y el tiempo de expiración se inyectan desde `application.properties` con `@Value`, evitando valores hardcodeados:

```java
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationTime) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())   // email del usuario
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }
}
```

**Estructura de un JWT generado (decodificado):**

```
Header:    { "alg": "HS512", "typ": "JWT" }
Payload:   { "sub": "admin@correo.com", "iat": 1756900000, "exp": 1756986400 }
Signature: HMAC-SHA512(base64(header) + "." + base64(payload), secretKey)
```

---

### Commit 4 — `feat(security)`: JwtRequestFilter y SecurityConfig

> **Hash:** `a56acdf` · 2 archivos nuevos · **+109 líneas**

#### `JwtRequestFilter.java` — Interceptor de cada petición HTTP

Extiende `OncePerRequestFilter`, garantizando que el filtro se ejecuta **exactamente una vez por petición**, incluso si la cadena de filtros lo llama múltiples veces:

```java
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Paso 1: Extraer el token del header "Authorization: Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                logger.warn("Expired JWT Token", e);
            } catch (JwtException | IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return; // ← corta la cadena de filtros inmediatamente
            }
        }

        // Paso 2: Solo proceder si hay username y no hay autenticación previa en el contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetails.loadUserByUsername(username);

            // Paso 3: Validar el token contra los datos actuales del usuario en BD
            if (jwtUtil.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Paso 4: Registrar la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response); // continuar con la cadena
    }
}
```

**Árbol de decisiones del filtro:**

```
Petición entrante
       │
       ▼
¿Header "Authorization: Bearer ..."?
  ├── NO  → filterChain.doFilter() → Spring rechaza si la ruta es protegida (401)
  └── SÍ  → Extraer token
               │
               ▼
        ¿Token parseable y firma válida?
          ├── NO  → response.sendError(401) y return (corta la cadena)
          └── SÍ  → extractUsername(token) → email
                       │
                       ▼
               Cargar UserDetails desde MongoDB
                       │
                       ▼
               ¿Token válido y no expirado?
                 ├── NO  → filterChain.doFilter() sin autenticación
                 └── SÍ  → SecurityContextHolder.setAuthentication(...)
                               │
                               ▼
                        filterChain.doFilter() → llega al Controller
```

---

#### `SecurityConfig.java` — Configuración de la Cadena de Filtros

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // habilita @PreAuthorize a nivel de método
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .requestMatchers(
                    "/swagger-ui/**", "/swagger-ui.html",
                    "/v3/api-docs/**", "/v3/api-docs"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

| Decisión de diseño | Razón |
| :--- | :--- |
| `SessionCreationPolicy.STATELESS` | Con JWT cada petición es autocontenida; no se crea ni consulta sesión en servidor |
| `csrf.disable()` | Las API REST con JWT son inmunes a CSRF porque no usan cookies de sesión |
| `BCryptPasswordEncoder` | Hashing adaptativo con salt integrado — resistente a rainbow tables y ataques de fuerza bruta |
| `@EnableMethodSecurity` | Permite usar `@PreAuthorize("hasRole('ADMIN')")` directamente en los métodos del controlador |
| `addFilterBefore(...)` | El filtro JWT corre antes del filtro de autenticación estándar de Spring |

---

### Commit 5 — `feat(auth)`: AuthenticationController, DTOs y BCrypt en UserService

> **Hash:** `9459e34` · 7 archivos · **+135 / -42 líneas**

#### DTOs de Autenticación — Records de Java

**`AuthRequest.java`** — Usa `record` con validaciones Bean Validation integradas:

```java
public record AuthRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    String password
) {}
```

**`AuthResponse.java`** — Record minimalista que solo devuelve el token generado:

```java
public record AuthResponse(String jwt) {}
```

> Se usan **records** de Java en lugar de clases POJO: son inmutables, tienen `equals`, `hashCode` y `toString` automáticos, y eliminan todo el boilerplate.

---

#### `AuthenticationController.java` — Endpoint de Login y Generación de Token

```java
@RestController
@RequestMapping("/api/v1/auth/login")
public class AuthenticationController {

    @PostMapping
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String email = request.email().toLowerCase();

        // Paso 1: Delegar la validación de credenciales a Spring Security
        // Si son incorrectas, lanza BadCredentialsException → 401 automáticamente
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, request.password())
        );

        // Paso 2: Credenciales correctas → cargar el usuario y generar el JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
```

**Ejemplo de uso:**

```json
// POST /api/v1/auth/login
// Body:
{
  "email": "admin@correo.com",
  "password": "miContraseña123"
}

// Response 200 OK:
{
  "jwt": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBjb3Jy..."
}
```

Luego usar el token en peticiones protegidas:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

#### Integración de BCrypt en `UserServiceImpl.java`

Al crear un usuario, la contraseña se cifra antes de persistirla en MongoDB:

```java
@Override
public UserResponseDTO create(UserCreateRequestDTO dto) {
    // Verificar que el email no esté ya registrado
    if (userRepository.findByEmail(dto.email()).isPresent()) {
        throw new EmailAlreadyExistsException("Email already in use: " + dto.email());
    }
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setName(dto.name());
    user.setEmail(dto.email());
    user.setPassword(passwordEncoder.encode(dto.password())); // ← hash BCrypt
    user.setRole(dto.role() != null ? dto.role() : Role.USER);
    return toResponseDTO(userRepository.save(user));
}
```

---

### 5. Resumen de Endpoints y Nivel de Acceso

| Método | Endpoint | Acceso | Descripción |
| :---: | :--- | :---: | :--- |
| `POST` | `/api/v1/auth/login` | 🔓 Público | Autentica credenciales y retorna token JWT |
| `POST` | `/api/v1/users` | 🔓 Público | Registro de nuevo usuario (contraseña cifrada con BCrypt) |
| `GET` | `/swagger-ui/**` | 🔓 Público | Documentación interactiva de la API |
| `GET` | `/api/v1/users` | 🔐 ADMIN | Listado completo de usuarios |
| `GET` | `/api/v1/users/{id}` | 🔐 ADMIN | Consulta usuario por UUID |
| `PATCH` | `/api/v1/users/{id}` | 🔐 Autenticado | Actualización parcial de usuario |
| `DELETE` | `/api/v1/users/{id}` | 🔐 Autenticado | Eliminación de usuario |

---

### 🧪 Evidencias de Seguridad

**1 — Contraseñas almacenadas con hash BCrypt en MongoDB:**

Al registrar un usuario, la contraseña queda cifrada — nunca en texto plano:

![MongoDB Compass - Contraseñas encriptadas con BCrypt](Assets/Screenshot%20From%202026-09-02%2008-57-33.png)

---

**2 — Login exitoso → token JWT generado (`200 OK`):**

`POST /api/v1/auth/login` con credenciales válidas retorna el Bearer token:

![Postman - Login y token JWT generado](Assets/Screenshot%20From%202026-09-02%2008-58-38.png)

---

**3 — `GET /api/v1/users` con rol ADMIN → `200 OK`:**

El token de un usuario `ADMIN` permite acceder al listado completo:

![Postman - GET all usuarios con rol ADMIN](Assets/Screenshot%20From%202026-09-02%2008-59-22.png)

---

**4 — `GET /api/v1/users/{id}` con rol ADMIN → `200 OK`:**

Consulta por ID exitosa con token ADMIN:

![Postman - GET por ID con rol ADMIN](Assets/Screenshot%20From%202026-09-02%2008-59-46.png)

---

**5 — `GET /api/v1/users/{id}` sin token → `401 Unauthorized`:**

Sin el header `Authorization`, el `JwtRequestFilter` rechaza la petición antes de que llegue al controlador:

![Postman - GET sin token, 401 Unauthorized](Assets/Screenshot%20From%202026-09-02%2009-01-57.png)

---

**6 — Login con usuario de rol USER → token generado:**

Un usuario con rol `USER` se autentica y recibe su token JWT:

![Postman - Login usuario rol USER](Assets/Screenshot%20From%202026-09-02%2009-06-28.png)

---

**7 — `GET /api/v1/users` con rol USER → `403 Forbidden`:**

El listado está protegido con `@PreAuthorize("hasRole('ADMIN')")`. Un `USER` autenticado pero sin el rol correcto recibe `403`:

![Postman - GET all con rol USER, 403 Forbidden](Assets/Screenshot%20From%202026-09-02%2009-07-04.png)

---

**8 — Documento del usuario con campo `role` en MongoDB:**

El rol queda persistido en el documento, permitiendo su consulta en `CustomUserDetails` al validar cada petición:

![MongoDB Compass - Documento con rol USER](Assets/Screenshot%20From%202026-09-02%2009-07-37.png)

---

## 🚀 Instrucciones de Ejecución

### 1. Clonar el repositorio
```bash
git clone https://github.com/Juan-cely-l/Proyecto-Integrador.git
cd Proyecto-Integrador
```

### 2. Configurar la conexión a MongoDB Atlas
En `src/main/resources/application-dev.properties`:
```properties
spring.mongodb.uri=mongodb+srv://<db_user>:<db_password>@<cluster_host>/users?retryWrites=true&w=majority
```

### 3. Compilar y ejecutar
```bash
./mvnw clean spring-boot:run
```

### 4. Acceso a la API

| Recurso | URL |
| :--- | :--- |
| **Swagger UI** | `http://localhost:8080/swagger-ui/index.html` |
| **OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |
| **Login** | `POST http://localhost:8080/api/v1/auth/login` |

### 5. Autenticarse y usar la API

```json
// POST /api/v1/auth/login
{
  "email": "usuario@correo.com",
  "password": "tu_contraseña"
}
```

Incluir el token retornado en el header de las siguientes peticiones:
```
Authorization: Bearer <token_jwt>
```
