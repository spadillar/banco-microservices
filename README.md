# Proyecto de Microservicios Bancarios

## Descripción
Este proyecto es una aplicación base o de pruebas de microservicios bancaria desarrollada con Spring Boot 3.1.5 y Spring Cloud 2022.0.4. Utiliza un modelo de programación funcional para Spring Cloud Stream y RabbitMQ para la comunicación asincrónica entre servicios. Incluye servicios para clientes y cuentas, con persistencia en PostgreSQL. El deployment en Docker crea la base de datos automáticamente en el contenedor de PostgreSQL. Para probar los endpoints, usa el archivo Banco_API.postman_collection.json. Si necesitas inicializar la base de datos manualmente, referirte a BaseDatos.sql.

## Tech Stack
- Backend: Spring Boot 3.1.5, Spring Cloud 2022.0.4, Spring Cloud Stream
- Messaging: RabbitMQ
- Database: PostgreSQL
- Containerization: Docker, Docker Compose
- Build Tool: Maven
- Testing: Postman

## Características
- Endpoints para gestión de clientes (CREAR, LEER, ACTUALIZAR, ELIMINAR).
- Endpoints para gestión de cuentas bancarias y movimientos.
- Comunicación asincrónica entre servicios utilizando RabbitMQ.
- Persistencia de datos en PostgreSQL con creación automática durante el deployment.
- Colección de Postman para pruebas integradas.

## Requisitos
- **Docker**: Para containerización y orquestación.
- **Docker Compose**: Para manejar múltiples contenedores.
- **Java 17**: Requerido para compilar y ejecutar los servicios Spring Boot.
- **Maven**: Para gestionar dependencias y compilar el código.
- **Postman**: Para probar los endpoints (opcional, pero recomendado).

## Pasos para Ejecutar y Desplegar
1. **Compilar el código**: Asegúrate de que el código esté compilado. Usa `mvn clean package` en cada servicio si es necesario.
2. **Construir las imágenes Docker**: Ejecuta `docker-compose build` en el directorio del proyecto.
3. **Iniciar los servicios**: Ejecuta `docker-compose up` para desplegar todos los contenedores.
4. **Probar los endpoints**: Usa el archivo [Banco_API.postman_collection.json](./Banco_API.postman_collection.json), que se puede importar en Postman para realizar las pruebas correspondientes de los requests de manera fácil y organizada. Por ejemplo, prueba endpoints como Clientes y Cuentas.

Asegúrate de que las variables de entorno en docker-compose.yml estén configuradas correctamente para RabbitMQ y PostgreSQL.

## Pruebas Unitarias e de Integración
En este proyecto, se han implementado pruebas unitarias y de integración para asegurar la calidad del código.

- **Pruebas Unitarias**: Estas pruebas verifican el funcionamiento individual de métodos y clases, como los servicios y repositorios, sin depender de componentes externos. Se ejecutan con `mvn test` utilizando mocks.

- **Pruebas de Integración**: Estas pruebas chequean la interacción entre componentes, incluyendo la base de datos PostgreSQL y servicios como RabbitMQ, configuradas para entornos reales en el perfil 'test'. También se ejecutan con `mvn test`.

## Configuración y Cambios
- Cambios en puertos: Edita docker-compose.yml para mapeos de puertos o application.yml en el directorio cliente-service/src/main/resources y application.yml en el directorio cuenta-service/src/main/resources en los servicios para ajustes de puertos internos.
- Otras configuraciones: Modifica variables de entorno en docker-compose.yml para RabbitMQ, PostgreSQL u otros settings. Cambios en BaseDatos.sql para esquema de base de datos.

## Notas Importantes
- **Manejo de Secrets**: No hardcodees credenciales (ej. usuario y clave de PostgreSQL o RabbitMQ). Usa variables de entorno en docker-compose.yml o un gestor de secrets como HashiCorp Vault.
