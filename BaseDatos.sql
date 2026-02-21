-- ============================================================
-- BANKING MICROSERVICES - SCRIPT BASE DE DATOS
-- ============================================================

-- ─────────────────────────────────────────────────────────────
-- DATABASE: cliente  (cliente-persona-service)
-- ─────────────────────────────────────────────────────────────

\c cliente;

DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS persona CASCADE;

CREATE TABLE persona (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    genero      VARCHAR(20),
    edad        INTEGER,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    direccion   VARCHAR(200),
    telefono    VARCHAR(20),
    dtype       VARCHAR(31) NOT NULL DEFAULT 'Persona'
);

CREATE TABLE cliente (
    id          BIGINT PRIMARY KEY REFERENCES persona(id) ON DELETE CASCADE,
    cliente_id  VARCHAR(50) NOT NULL UNIQUE,
    contrasena  VARCHAR(255) NOT NULL,
    estado      BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_cliente_cliente_id    ON cliente(cliente_id);
CREATE INDEX idx_persona_identificacion ON persona(identificacion);

-- Datos iniciales (Caso de Uso 1)
INSERT INTO persona (nombre, genero, edad, identificacion, direccion, telefono, dtype) VALUES
    ('Jose Lema',           'Masculino', 30, '1712345678', 'Otavalo sn y principal',   '098254785', 'Cliente'),
    ('Marianela Montalvo',  'Femenino',  28, '1723456789', 'Amazonas y NNUU',           '097548965', 'Cliente'),
    ('Juan Osorio',         'Masculino', 35, '1734567890', '13 junio y Equinoccial',    '098874587', 'Cliente');

INSERT INTO cliente (id, cliente_id, contrasena, estado)
SELECT id, CONCAT('CLI-', identificacion), '1234', TRUE FROM persona WHERE identificacion = '1712345678';

INSERT INTO cliente (id, cliente_id, contrasena, estado)
SELECT id, CONCAT('CLI-', identificacion), '5678', TRUE FROM persona WHERE identificacion = '1723456789';

INSERT INTO cliente (id, cliente_id, contrasena, estado)
SELECT id, CONCAT('CLI-', identificacion), '1245', TRUE FROM persona WHERE identificacion = '1734567890';


-- ─────────────────────────────────────────────────────────────
-- DATABASE: cuenta  (cuenta-movimiento-service)
-- ─────────────────────────────────────────────────────────────

\c cuenta;

DROP TABLE IF EXISTS movimiento CASCADE;
DROP TABLE IF EXISTS cuenta CASCADE;
DROP TABLE IF EXISTS cliente_cache CASCADE;

-- Cache local de clientes (sincronizada via RabbitMQ)
CREATE TABLE cliente_cache (
    id              BIGSERIAL PRIMARY KEY,
    cliente_id      VARCHAR(50)  NOT NULL UNIQUE,
    nombre          VARCHAR(100) NOT NULL,
    identificacion  VARCHAR(20)  NOT NULL UNIQUE,
    estado          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE cuenta (
    id               BIGSERIAL PRIMARY KEY,
    numero_cuenta    VARCHAR(20)    NOT NULL UNIQUE,
    tipo_cuenta      VARCHAR(20)    NOT NULL,
    saldo_inicial    DECIMAL(15,2)  NOT NULL DEFAULT 0,
    saldo_disponible DECIMAL(15,2)  NOT NULL DEFAULT 0,
    estado           BOOLEAN        NOT NULL DEFAULT TRUE,
    cliente_id       VARCHAR(50)    NOT NULL,
    CONSTRAINT fk_cuenta_cliente FOREIGN KEY (cliente_id) REFERENCES cliente_cache(cliente_id)
);

CREATE TABLE movimiento (
    id              BIGSERIAL PRIMARY KEY,
    fecha           TIMESTAMP     NOT NULL DEFAULT NOW(),
    tipo_movimiento VARCHAR(20)   NOT NULL,
    valor           DECIMAL(15,2) NOT NULL,
    saldo           DECIMAL(15,2) NOT NULL,
    cuenta_id       BIGINT        NOT NULL,
    CONSTRAINT fk_movimiento_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuenta(id)
);

CREATE INDEX idx_movimiento_fecha     ON movimiento(fecha);
CREATE INDEX idx_movimiento_cuenta_id ON movimiento(cuenta_id);
CREATE INDEX idx_cuenta_cliente_id    ON cuenta(cliente_id);

-- Datos iniciales
INSERT INTO cliente_cache (cliente_id, nombre, identificacion, estado) VALUES
    ('CLI-1712345678', 'Jose Lema',          '1712345678', TRUE),
    ('CLI-1723456789', 'Marianela Montalvo', '1723456789', TRUE),
    ('CLI-1734567890', 'Juan Osorio',        '1734567890', TRUE);

-- Caso de Uso 2: Creacion de Cuentas de Usuario
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id) VALUES
    ('478758', 'Ahorros',   2000.00, 2000.00, TRUE, 'CLI-1712345678'),
    ('225487', 'Corriente', 100.00,  100.00,  TRUE, 'CLI-1723456789'),
    ('495878', 'Ahorros',   0.00,    0.00,    TRUE, 'CLI-1734567890'),
    ('496825', 'Ahorros',   540.00,  540.00,  TRUE, 'CLI-1723456789');

-- Caso de Uso 3: Cuenta Corriente para Jose Lema
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id) VALUES
    ('585545', 'Corriente', 1000.00, 1000.00, TRUE, 'CLI-1712345678');

-- Caso de Uso 4: Movimientos
-- Retiro de 575 en cuenta 478758 (Jose Lema - Ahorros - saldo: 2000)
INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, cuenta_id)
SELECT '2022-02-05', 'Retiro', -575.00, 1425.00, id FROM cuenta WHERE numero_cuenta = '478758';
UPDATE cuenta SET saldo_disponible = 1425.00 WHERE numero_cuenta = '478758';

-- Deposito de 600 en cuenta 225487 (Marianela - Corriente - saldo: 100)
INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, cuenta_id)
SELECT '2022-02-10', 'Deposito', 600.00, 700.00, id FROM cuenta WHERE numero_cuenta = '225487';
UPDATE cuenta SET saldo_disponible = 700.00 WHERE numero_cuenta = '225487';

-- Deposito de 150 en cuenta 495878 (Juan Osorio - Ahorros - saldo: 0)
INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, cuenta_id)
SELECT '2022-02-12', 'Deposito', 150.00, 150.00, id FROM cuenta WHERE numero_cuenta = '495878';
UPDATE cuenta SET saldo_disponible = 150.00 WHERE numero_cuenta = '495878';

-- Retiro de 540 en cuenta 496825 (Marianela - Ahorros - saldo: 540)
INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, cuenta_id)
SELECT '2022-02-08', 'Retiro', -540.00, 0.00, id FROM cuenta WHERE numero_cuenta = '496825';
UPDATE cuenta SET saldo_disponible = 0.00 WHERE numero_cuenta = '496825';
