# Banking Microservices

Solución de microservicios bancarios desarrollada con **Java 21** y **Spring Boot 3.2**, implementando arquitectura hexagonal (Ports & Adapters), Clean Architecture y patrones de diseño Repository.

---

## Arquitectura

```
banking-microservices/
├── cliente-persona-service/   # Puerto 8080
│   ├── domain/                # Entidades y excepciones de dominio
│   ├── application/           # Casos de uso y puertos (in/out)
│   └── infrastructure/        # Persistencia, Web REST, Mensajería RabbitMQ
│
├── cuenta-movimiento-service/ # Puerto 8081
│   ├── domain/                # Entidades: Cuenta, Movimiento, EstadoCuenta
│   ├── application/           # Servicios: Cuenta, Movimiento, Reporte
│   └── infrastructure/        # JPA, REST Controllers, Consumidor RabbitMQ
│
├── docker-compose.yml
└── BaseDatos.sql
```

### Comunicación asíncrona
Los microservicios se comunican via **RabbitMQ**:
- `cliente-persona-service` publica eventos al exchange `cliente.exchange`
- `cuenta-movimiento-service` consume eventos y mantiene un `cliente_cache` local

---

## Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.2 |
| ORM | Spring Data JPA / Hibernate |
| Base de datos | PostgreSQL 16 |
| Migraciones | **Liquibase** (YAML changelogs) |
| Mensajería | RabbitMQ 3.13 |
| Build Tool | **Gradle 8.6** |
| Testing unitario | JUnit 5 + Mockito |
| Testing API | **Karate 1.4.1** (JUnit5) |
| Contenedores | Docker + Docker Compose |

---

## Requisitos previos

- Docker Desktop (o Docker Engine + Docker Compose)
- Git

---

## Despliegue con Docker (F7)

```bash
# 1. Clonar el repositorio
git clone <URL_REPOSITORIO>
cd banking-microservices

# 2. Levantar todos los servicios
docker-compose up --build -d

# 3. Verificar que todos estén corriendo
docker-compose ps
docker-compose logs -f
```

### Servicios disponibles

| Servicio | URL | Descripción |
|---|---|---|
| cliente-persona-service | http://localhost:8080/api | CRUD Clientes/Personas |
| cuenta-movimiento-service | http://localhost:8081/api | CRUD Cuentas/Movimientos/Reportes |
| RabbitMQ Management | http://localhost:15672 | user: guest / pass: guest |

### Apagar servicios
```bash
docker-compose down
docker-compose down -v   # también elimina volúmenes
```

---

## Endpoints API

### cliente-persona-service (puerto 8080)

```
GET    /api/clientes              Lista todos los clientes
POST   /api/clientes              Crea un cliente
GET    /api/clientes/{id}         Obtiene cliente por ID
PUT    /api/clientes/{id}         Actualiza cliente completo
PATCH  /api/clientes/{id}         Actualiza parcialmente
DELETE /api/clientes/{id}         Elimina cliente
```

### cuenta-movimiento-service (puerto 8081)

```
GET    /api/cuentas               Lista cuentas
POST   /api/cuentas               Crea cuenta
GET    /api/cuentas/{id}          Obtiene cuenta
PUT    /api/cuentas/{id}          Actualiza cuenta
PATCH  /api/cuentas/{id}          Actualiza parcial
DELETE /api/cuentas/{id}          Elimina cuenta

GET    /api/movimientos           Lista movimientos
POST   /api/movimientos           Registra movimiento (F2/F3)
GET    /api/movimientos/{id}      Obtiene movimiento
DELETE /api/movimientos/{id}      Elimina movimiento

GET    /api/reportes?fecha=YYYY-MM-DD,YYYY-MM-DD&cliente=CLI-XXX   Estado de cuenta (F4)
```

---

## Funcionalidades implementadas

| # | Descripción | Estado |
|---|---|---|
| F1 | CRUD completo: clientes, cuentas, movimientos | ✅ |
| F2 | Registro de movimientos con actualización de saldo | ✅ |
| F3 | Alerta "Saldo no disponible" (HTTP 422) | ✅ |
| F4 | Reporte Estado de Cuenta con rango de fechas | ✅ |
| F5 | Prueba unitaria ClienteService | ✅ |
| F6 | Prueba de integración MovimientoService | ✅ |
| F7 | Despliegue Docker Compose completo | ✅ |

---

## Ejemplos de uso (Casos de Uso)

### 1. Crear clientes

```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Jose Lema",
    "genero": "Masculino",
    "edad": 30,
    "identificacion": "1712345678",
    "direccion": "Otavalo sn y principal",
    "telefono": "098254785",
    "contrasena": "1234",
    "estado": true
  }'
```

### 2. Registrar movimiento (depósito)

```bash
curl -X POST http://localhost:8081/api/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "cuentaId": 1,
    "tipoMovimiento": "Deposito",
    "valor": 600.00
  }'
```

### 3. F3 - Retiro sin saldo (respuesta esperada: 422)

```bash
curl -X POST http://localhost:8081/api/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "cuentaId": 4,
    "tipoMovimiento": "Retiro",
    "valor": -9999.00
  }'
# Respuesta: {"success": false, "message": "Saldo no disponible"}
```

### 4. F4 - Reporte Estado de Cuenta

```bash
curl "http://localhost:8081/api/reportes?fecha=2022-01-01,2022-12-31&cliente=CLI-1723456789"
```

---

## Ejecutar pruebas

```bash
# ── Pruebas unitarias y de integración (sin Karate) ──────────────────
cd cliente-persona-service
./gradlew unitTest          # Solo pruebas unitarias

cd ../cuenta-movimiento-service
./gradlew unitTest          # Solo pruebas de integración (H2)

# ── Pruebas Karate (requiere servicios levantados) ────────────────────
# 1. Primero levantar los servicios:
docker-compose up -d

# 2. Ejecutar Karate en cada servicio:
cd cliente-persona-service
./gradlew karateTest        # API tests: clientes.feature

cd ../cuenta-movimiento-service
./gradlew karateTest        # API tests: cuentas, movimientos, reportes features

# ── Ejecutar TODOS los tests (unitarios + Karate) ─────────────────────
./gradlew test
```

### Estructura de tests Karate
```
src/test/resources/karate/
├── karate-config.js                        # URL base y variables globales
├── clientes/
│   └── clientes.feature                   # CRUD clientes (8 scenarios)
└── (cuenta service)
    ├── cuentas/
    │   └── cuentas.feature                # CRUD cuentas (9 scenarios)
    ├── movimientos/
    │   └── movimientos.feature            # F2/F3 movimientos (8 scenarios)
    └── reportes/
        └── reportes.feature               # F4 estado de cuenta (5 scenarios)
```

---

## Base de datos y Migraciones (Liquibase)

Las migraciones están en formato YAML bajo `src/main/resources/db/changelog/`:

```
db/changelog/
├── db.changelog-master.yaml     # Archivo maestro que incluye los demás
├── 001-create-*.yaml            # Creación de tablas e índices
└── 002-seed-data.yaml           # Datos iniciales de los casos de uso
```

Liquibase aplica automáticamente los changesets al arrancar el servicio.
El script `BaseDatos.sql` sirve como referencia SQL equivalente.

---

## Importar colección Postman

1. Abrir Postman
2. Import → `BankingMicroservices.postman_collection.json`
3. Las variables `cliente_host` y `cuenta_host` ya están configuradas

---

## Consideraciones de calidad

- **Arquitectura Hexagonal**: dominio completamente aislado de infraestructura
- **Patrón Repository**: interfaces de puerto para persistencia
- **Manejo de excepciones**: `@RestControllerAdvice` global con respuestas estructuradas
- **Resiliencia**: `restart: on-failure` en Docker, healthchecks en todos los servicios
- **Escalabilidad**: cada microservicio tiene su propia base de datos (BD por servicio)
- **Mensajería asíncrona**: RabbitMQ con Topic Exchange para desacoplamiento
- **Migraciones**: Flyway para control de versiones del esquema de BD
