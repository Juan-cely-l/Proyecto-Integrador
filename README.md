# Proyecto Integrador: API REST con Spring Boot y Spring Data MongoDB

Proyecto desarrollado en el marco del curso de Arquitectura y Desarrollo de Software / IETI. Esta aplicación implementa una API REST completa para la gestión de usuarios (`User`), evolucionando desde un esquema inicial de almacenamiento en memoria hacia una capa de persistencia desacoplada y robusta utilizando **Spring Data MongoDB** conectada a un clúster de **MongoDB Atlas**.

---

## 📋 Tabla de Contenidos
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [📦 Sección 1: Implementación Inicial de Controlador y Servicio REST (Memoria)](#-sección-1-implementación-inicial-de-controlador-y-servicio-rest-memoria)
- [🍃 Sección 2: Implementación de la Capa de Persistencia con Spring Data MongoDB](#-sección-2-implementación-de-la-capa-de-persistencia-con-spring-data-mongodb)
  - [Parte 1: Configuración y Conexión con el Clúster de MongoDB](#parte-1-configuración-y-conexión-con-el-clúster-de-mongodb)
  - [Parte 2: Documentos, Repositorios y Servicios CRUD](#parte-2-documentos-repositorios-y-servicios-crud)
- [🧪 Pruebas y Verificación de Endpoints (Postman & MongoDB Compass)](#-pruebas-y-verificación-de-endpoints-postman--mongodb-compass)
- [🚀 Instrucciones de Ejecución](#-instrucciones-de-ejecución)

---

## 🛠 Tecnologías Utilizadas

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.x
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-mongodb`
  - `springdoc-openapi-starter-webmvc-ui` (Swagger / OpenAPI 3)
- **Base de Datos:** MongoDB Atlas / MongoDB Compass
- **Herramienta de Construcción:** Maven
- **Cliente REST:** Postman

---

## 📁 Estructura del Proyecto

```text
edu.eci.proyecto
├── controller
│   ├── health/HealthController.java
│   └── UserController.java
├── dto
│   ├── UserRequestDTO.java
│   └── UserResponseDTO.java
├── entity
│   └── User.java
├── exception
│   └── UserNotFoundException.java
├── respository
│   └── UserRepository.java
├── service
│   ├── UserService.java
│   └── UserServiceImpl.java
└── ProyectoApplication.java
```

---

## 📦 Sección 1: Implementación Inicial de Controlador y Servicio REST (Memoria)

En la primera etapa del proyecto integrador, se diseñó e implementó la arquitectura base para la API REST utilizando almacenamiento temporal en memoria:

1. **Definición de la Interfaz y Servicio con HashMap:**
   - Se definió la interfaz `UserService` con los contratos para las operaciones CRUD sobre el recurso de usuarios (`Create`, `Read`, `Update`, `Delete`).
   - Se creó la implementación inicial respaldada por una estructura `HashMap<UUID, User>` para simular la persistencia en memoria durante el desarrollo temprano.
   - Se configuró la implementación del servicio para ser inyectable mediante la anotación `@Service`.

2. **Implementación del Controlador (`UserController`):**
   - Se creó el controlador `@RestController` inyectando el servicio de usuarios y manejando los métodos HTTP requeridos:
     - `GET`: Consulta de todos los usuarios y búsqueda por `id`.
     - `POST`: Creación de usuario retornando `201 Created` junto al header `Location`.
     - `PUT / PATCH`: Actualización de los datos del usuario.
     - `DELETE`: Eliminación del usuario retornando `204 No Content`.

3. **Verificación Inicial:**
   - Ejecución y validación funcional de cada endpoint mediante Postman.

---

## 🍃 Sección 2: Implementación de la Capa de Persistencia con Spring Data MongoDB

En esta fase se reemplazó el almacenamiento en memoria por una persistencia real y no relacional basada en **MongoDB**.

### Parte 1: Configuración y Conexión con el Clúster de MongoDB

1. **Configuración del Clúster en MongoDB Atlas:**
   - Creación del clúster del proyecto.
   - Creación del usuario y contraseña con permisos de lectura/escritura sobre el clúster.
   - Habilitación del acceso de red (Network Access) desde cualquier lugar (`0.0.0.0/0`) para el entorno de desarrollo.

2. **Inclusión de la Dependencia en `pom.xml`:**
   Se agregó la dependencia de Spring Data MongoDB en el archivo `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-mongodb</artifactId>
   </dependency>
   ```

   ![Configuración de Dependencias en pom.xml](Assets/Screenshot%20From%202026-08-25%2010-18-00.png)

3. **Configuración de Variables de Entorno y Propiedades de Conexión:**
   - Se definió el archivo `atlas-credentials.env` y se configuraron las propiedades en `application.properties` y `application-dev.properties` para la URI de conexión:
     ```properties
     spring.application.name=proyecto
     spring.profiles.active=dev
     spring.mongodb.representation.uuid=standard
     spring.mongodb.uri=mongodb+srv://<db_user>:<db_password>@estebancluster.hzvwuym.mongodb.net/users?retryWrites=true&w=majority
     ```

   ![Configuración atlas-credentials.env](Assets/Screenshot%20From%202026-08-25%2010-18-29.png)

4. **Verificación de Conexión en Consola:**
   - Al iniciar la aplicación con Spring Boot, se valida en los logs de consola la inicialización del `TomcatWebServer` en el puerto `8080`, el descubrimiento del conjunto de réplicas de MongoDB Atlas y la conexión exitosa de los hilos de monitoreo (`Monitor thread successfully connected`):

   ![Verificación de Conexión en Consola](Assets/Screenshot%20From%202026-08-25%2010-19-23.png)

---

### Parte 2: Documentos, Repositorios y Servicios CRUD

1. **Implementación de la Clase Document (`User.java`):**
   - Se mapeó la entidad con `@Document(collection = "users")`, utilizando anotaciones como `@Id` sobre el atributo `id` de tipo `UUID` y `@Field("email_address")` para personalizar el nombre del campo en la base de datos.

   ![Clase Document User](Assets/Screenshot%20From%202026-08-25%2010-20-30.png)

2. **Creación de la Interfaz del Repositorio (`UserRepository.java`):**
   - Se implementó la interfaz extendiendo de `MongoRepository<User, UUID>` para gestionar las operaciones de persistencia de manera declarativa.

   ![Interfaz UserRepository](Assets/Screenshot%20From%202026-08-25%2010-19-48.png)

3. **Implementación del Servicio CRUD (`UserServiceImpl.java`):**
   - Se implementó la lógica de negocio inyectando `UserRepository` para realizar el guardado, consulta, actualización y eliminación de documentos en MongoDB, manejando además el mapeo con los DTOs (`UserRequestDTO` y `UserResponseDTO`) y el control de excepciones (`UserNotFoundException`).

   ![Servicio UserServiceImpl](Assets/Screenshot%20From%202026-08-25%2010-21-08.png)

4. **Controlador REST (`UserController.java`):**
   - Se expusieron los siguientes endpoints para el consumo de la API:

| Método HTTP | Endpoint | Descripción | Código de Respuesta |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/users` | Obtiene el listado de todos los usuarios registrados | `200 OK` |
| `GET` | `/api/v1/users/{id}` | Obtiene un usuario específico por su UUID | `200 OK` / `404 Not Found` |
| `POST` | `/api/v1/users` | Crea un nuevo usuario en MongoDB Atlas | `201 Created` |
| `PATCH` | `/api/v1/users/{id}` | Actualiza atributos específicos de un usuario existente | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/v1/users/{id}` | Elimina un usuario de la base de datos por su UUID | `204 No Content` / `404 Not Found` |

---

## 🧪 Pruebas y Verificación de Endpoints (Postman & MongoDB Compass)

A continuación se muestra el ciclo de pruebas completo realizado desde **Postman** y la corroboración en tiempo real en la base de datos a través de **MongoDB Compass**:

### 1. Estado Inicial de la Base de Datos
- Consulta en MongoDB Compass mostrando la colección `users` vacía antes de iniciar las operaciones:

![MongoDB Compass - Colección Vacía](Assets/Screenshot%20From%202026-08-25%2010-17-35.png)

---

### 2. Consulta Inicial de Usuarios (`GET /api/v1/users`)
- Petición `GET` a `localhost:8080/api/v1/users` retornando una lista vacía con código HTTP `200 OK`:

![Postman - GET Inicial Vacío](Assets/Screenshot%20From%202026-08-25%2010-22-02.png)

---

### 3. Creación de Usuario (`POST /api/v1/users`)
- **Petición en Postman:** Se envía el cuerpo en formato JSON para crear el usuario `andres robledo`. Se recibe respuesta con código `201 Created` y el ID generado (`ab0d36e3-b941-49a4-a637-995df211b2f3`):

![Postman - POST Crear Usuario](Assets/Screenshot%20From%202026-08-25%2010-22-29.png)

- **Persistencia en MongoDB Compass:** Se valida la creación del documento dentro de la colección `users` en MongoDB Atlas:

![MongoDB Compass - Documento Creado](Assets/Screenshot%20From%202026-08-25%2010-23-25.png)

---

### 4. Actualización de Usuario (`PATCH /api/v1/users/{id}`)
- **Petición en Postman:** Se realiza una modificación sobre los campos del usuario mediante `PATCH`, obteniendo respuesta satisfactoria `200 OK`:

![Postman - PATCH Actualizar Usuario](Assets/Screenshot%20From%202026-08-25%2010-23-32.png)

- **Reflejo en MongoDB Compass:** Se constatan los cambios aplicados directamente en el documento almacenado:

![MongoDB Compass - Documento Actualizado](Assets/Screenshot%20From%202026-08-25%2010-23-42.png)

---

### 5. Eliminación de Usuario (`DELETE /api/v1/users/{id}`)
- **Petición en Postman:** Se envía la solicitud `DELETE` para el identificador del usuario, recibiendo código HTTP `204 No Content`:

![Postman - DELETE Eliminar Usuario](Assets/Screenshot%20From%202026-08-25%2010-23-56.png)

- **Verificación en MongoDB Compass:** Se confirma la remoción exitosa del documento en la colección:

![MongoDB Compass - Colección tras Eliminación](Assets/Screenshot%20From%202026-08-25%2010-24-03.png)

---

## 🚀 Instrucciones de Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Juan-cely-l/Proyecto-Integrador.git
   cd Proyecto-Integrador
   ```

2. **Configuración de Variables de Entorno:**
   Configurar la URI de conexión a MongoDB Atlas en `src/main/resources/application-dev.properties` o mediante variable de entorno del sistema:
   ```properties
   spring.mongodb.uri=mongodb+srv://<db_user>:<db_password>@<cluster_host>/users?retryWrites=true&w=majority
   ```

3. **Compilar y ejecutar la aplicación:**
   ```bash
   ./mvnw clean spring-boot:run
   ```

4. **Acceso a la Documentación Swagger / OpenAPI:**
   - Interfaz Swagger UI: `http://localhost:8080/swagger-ui/index.html`
   - Especificación OpenAPI: `http://localhost:8080/v3/api-docs`