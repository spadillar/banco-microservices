-- Crear la base de datos
CREATE DATABASE banco_db;

-- Conectar a la base de datos
\c banco_db

-- Crear tabla personas
CREATE TABLE personas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    genero VARCHAR(50) NOT NULL,
    edad INTEGER NOT NULL,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(20) NOT NULL
);

-- Crear tabla clientes
CREATE TABLE clientes (
    id BIGINT PRIMARY KEY REFERENCES personas(id),
    cliente_id VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL
);

-- Crear tabla cuentas
CREATE TABLE cuentas (
    numero_cuenta VARCHAR(50) PRIMARY KEY,
    tipo_cuenta VARCHAR(50) NOT NULL,
    saldo_inicial DECIMAL(10,2) NOT NULL,
    estado BOOLEAN NOT NULL,
    cliente_id VARCHAR(50) NOT NULL
);

-- Crear tabla movimientos
CREATE TABLE movimientos (
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    tipo_movimiento VARCHAR(50) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    saldo DECIMAL(10,2) NOT NULL,
    numero_cuenta VARCHAR(50) NOT NULL REFERENCES cuentas(numero_cuenta)
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_cliente_id ON clientes(cliente_id);
CREATE INDEX idx_cuenta_cliente ON cuentas(cliente_id);
CREATE INDEX idx_movimiento_cuenta ON movimientos(numero_cuenta);
CREATE INDEX idx_movimiento_fecha ON movimientos(fecha);
