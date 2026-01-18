-- Script de criação do banco de dados e tabelas

CREATE DATABASE IF NOT EXISTS LavaRapido;
USE LavaRapido;

-- Tabela de Empresas
CREATE TABLE IF NOT EXISTS empresas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cnpj VARCHAR(255) NOT NULL UNIQUE,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao DATETIME
);

-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL, -- MASTER, ADMIN, FUNCIONARIO
    ativo BOOLEAN DEFAULT TRUE,
    empresa_id BIGINT,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

-- Tabela de Clientes
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    empresa_id BIGINT NOT NULL,
    FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

-- Tabela de Veículos
CREATE TABLE IF NOT EXISTS veiculos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(20) NOT NULL,
    modelo VARCHAR(255),
    cliente_id BIGINT NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- Inserção inicial (Opcional, pois o DataInitializer do Java já faz isso)
-- INSERT INTO empresas (nome, cnpj, ativo, data_criacao) VALUES ('Lava Jato HQ', '00.000.000/0001-00', 1, NOW());
-- INSERT INTO usuarios (nome, email, senha, perfil, ativo, empresa_id) VALUES ('Super Admin', 'master@lavajato.com', '$2a$10$encryptedsenha...', 'MASTER', 1, 1);
