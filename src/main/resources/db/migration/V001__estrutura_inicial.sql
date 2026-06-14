-- 1. Criação da Tabela Cliente (Legado/Antiga - Pacote old.Cliente)
CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT NOT NULL,
    nome_cliente VARCHAR(255) NOT NULL, -- @NotBlank gera NOT NULL
    endereco VARCHAR(255),
    CONSTRAINT pk_cliente_legado PRIMARY KEY (id)
);

-- 2. Criação da Tabela Ramo de Atividade (Legado/Suporte)
CREATE TABLE ramo_atividade (
    id BIGINT AUTO_INCREMENT NOT NULL,
    descricao VARCHAR(80) NOT NULL,
    CONSTRAINT pk_ramo_atividade PRIMARY KEY (id)
);

-- 3. Criação da Tabela Empresa (Legado/Suporte)
CREATE TABLE empresa (
    id BIGINT AUTO_INCREMENT NOT NULL,
    nome_fantasia VARCHAR(80) NOT NULL,
    razao_social VARCHAR(120) NOT NULL,
    cnpj VARCHAR(18) NOT NULL,
    data_fundacao DATE NOT NULL,
    ramo_atividade_id BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    CONSTRAINT pk_empresa PRIMARY KEY (id),
    CONSTRAINT fk_empresa_ramo_atividade FOREIGN KEY (ramo_atividade_id) REFERENCES ramo_atividade(id)
);

-- 4. Criação da Tabela Usuário Base (Necessária para o Script V002 funcionar!)
-- Como o script V002 faz "ALTER TABLE usuario ADD COLUMN...", criamos a tabela base aqui.
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT NOT NULL,
    nome_Usuario VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id)
);