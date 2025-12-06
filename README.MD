# SPRING-TEMPLATE-APP

Plantilla inicial de **Spring Boot** lista para desarrollar aplicaciones backend.  
Incluye configuración de **Docker**, conexión a base de datos, variables de entorno y gestión de **roles y permisos** directamente desde la base de datos.

---

## 🔧 Requisitos

- [Java 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)  
- [Maven](https://maven.apache.org/) o [Gradle](https://gradle.org/) según tu proyecto  
- [Docker](https://www.docker.com/) y [Docker Compose](https://docs.docker.com/compose/)  
- Variables de entorno definidas en un archivo `.env`  

## Comandos docker 
Antes de ejecutar estos comandos, debes crear un archivo .env y copiar en él la información que se muestra en el archivo .env.example del proyecto. 
- **docker build -t name-app .**  compilar el docker file 
- **docker compose build** construir las imagenes 
- **docker build up --build** ejecutar el proyecto con docker  
- **docker logs -f name-app** ver logs de la app 
- **docker compose down -v** apagar + borrar datos

---

## ⚙️ Configuración del entorno de produccion

1. Copia el archivo de ejemplo `.env.example` a `.env`:

```bash
cp .env.example .env
```

## ⚙️ Configuración del entorno desarollo

Antes de ejecutar la aplicación en tu entorno local, debes configurar las variables de entorno que controlan la conexión a la base de datos, el puerto del servidor, CORS y seguridad JWT. Esto se hace modificando el archivo `application.properties`

Ejemplo de configuración en `application.properties` usando variables de entorno:
```properties
spring.application.name=${SPRING_APPLICATION_NAME:nameapp}

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/spring}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:password}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
spring.jpa.show-sql=${SPRING_JPA_SHOW_SQL:true}

server.port=${SERVER_PORT:4000}

spring.cors.allowed.origins=${CORS_ALLOWED_ORIGINS:http://localhost:5173}

security.jwt.secret-key=${SECURITY_JWT_SECRET_KEY:3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b}
security.jwt.expiration-time=${SECURITY_JWT_EXPIRATION_TIME:86400000}
```

## 🗄️ Configuración de la base de datos

Después de configurar tu entorno (producción o desarrollo) y levantar la base de datos, debes ejecutar el archivo `script.sql` incluido en el proyecto.  

Este script se encarga de:

- Crear los roles iniciales (por ejemplo: `ADMIN`, `USER`, etc.).
- Crear los permisos necesarios para los endpoints de la API.
- Asignar permisos a los roles correspondientes.
- Inicializar datos esenciales para la administración de usuarios y roles.

### 🔹 Cómo ejecutarlo

1. Abre tu cliente SQL favorito o la consola de PostgreSQL.
2. Conéctate a la base de datos configurada en tu `.env`.
3. Ejecuta el script:

```bash
psql -U <usuario> -d <basededatos> -f script.sql
```

## Rutas api
- ***POST*** : http://localhost:4000/api/auth/login

```
{
    "username": "loza.dev",
    "password": "passW12345-"
}
```
- ***POST*** :http://localhost:4000/api/auth/signup

```
{
    "username": "loza.dev",
    "name": "Roberto",
    "surname": "loza",
    "email": "robertoloxa88@gmail.com",
    "password": "passW1234-"
}
```

- ***GET*** :http://localhost:4000/api/auth/profile

- **Bearer 'token'**

----
- ***PUT*** :http://localhost:4000/api/auth/update/password

- ***Bearer 'token'***

```
{
    "oldpassword": "",
    "newpassword": "",
    "confirmpassword": ""
}
```

----
- ***PUT*** :http://localhost:4000/api/roles/{id}

- ***Bearer 'token'***

```
{
  "name": "ADMIN",
  "permissions": [{id:1}, {id:2}, {id:3}, {id:4}]
}
```
----
- ***POST*** :http://localhost:4000/users

- ***Bearer 'token'***
```
{
    "username": "johndoe",
    "name": "John",
    "surname": "Doe",
    "email": "johndoe@example.com",
    "password": "Password123",
    "role": 1
}
```
----
- ***GET*** :http://localhost:4000/api/users?page=0&size=1

- ***Bearer 'token'***
----
- ***POST*** :http://localhost:4000/api/roles

- ***Bearer 'token'***

```
{
  "name": "ADMIN",
  "permissions": [{id:1}, {id:2}, {id:3}, {id:4}]
}
```
----
- ***GET*** :http://localhost:4000/api/roles?page=0&size=1

- ***Bearer 'token'***

----
- ***GET*** :http://localhost:4000/api/roles/3

- ***Bearer 'token'***

----
- ***GET*** :http://localhost:4000/api/permissions?page=0&size=1

- ***Bearer 'token'***




