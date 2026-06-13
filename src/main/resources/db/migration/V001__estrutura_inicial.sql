-- 0. Criação da Tabela Cliente (Legado/Antiga - Pacote old.Cliente)
CREATE TABLE cliente (
    id BIGINT AUTO_INCREMENT NOT NULL,
    nome_cliente VARCHAR(255) NOT NULL, -- @NotBlank gera NOT NULL
    endereco VARCHAR(255),
    CONSTRAINT pk_cliente_legado PRIMARY KEY (id)
);

-- 1. Criação da Tabela Ramo de Atividade (Legado/Suporte)
CREATE TABLE ramo_atividade (
    id BIGINT AUTO_INCREMENT NOT NULL,
    descricao VARCHAR(80) NOT NULL,
    CONSTRAINT pk_ramo_atividade PRIMARY KEY (id)
);

-- 2. Criação da Tabela Empresa (Legado/Suporte)
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

-- 3. Criação da Tabela Configuração
CREATE TABLE configuracao (
    chave VARCHAR(50) NOT NULL,
    valor VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    CONSTRAINT pk_configuracao PRIMARY KEY (chave)
);

-- 4. Criação da Tabela Log de Auditoria
CREATE TABLE log_auditoria (
    id BIGINT AUTO_INCREMENT NOT NULL,
    data_hora DATETIME NOT NULL,
    operacao VARCHAR(255) NOT NULL,
    tela VARCHAR(255) NOT NULL,
    id_registro VARCHAR(255) NOT NULL,
    detalhe VARCHAR(500) NOT NULL,
    usuario VARCHAR(100) NOT NULL,
    CONSTRAINT pk_log_auditoria PRIMARY KEY (id)
);

-- 5. Criação da Tabela Base: Pessoa (Herança JOINED)
CREATE TABLE pessoa (
    id BIGINT AUTO_INCREMENT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    is_cliente BOOLEAN DEFAULT FALSE,
    is_fornecedor BOOLEAN DEFAULT FALSE,
    is_funcionario BOOLEAN DEFAULT FALSE,
    telefone VARCHAR(20),
    celular VARCHAR(20),
    logradouro VARCHAR(150),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cep VARCHAR(10),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    data_cadastro DATETIME NOT NULL,
    ativo BOOLEAN DEFAULT TRUE NOT NULL,
    foto_caminho VARCHAR(255),
    CONSTRAINT pk_pessoa PRIMARY KEY (id)
);

-- 6. Criação da Tabela Pessoa Física (Extende Pessoa)
CREATE TABLE pessoa_fisica (
    pessoa_id BIGINT NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    rg VARCHAR(255),
    data_nascimento DATE,
    sexo VARCHAR(15),
    estado_civil VARCHAR(20),
    CONSTRAINT pk_pessoa_fisica PRIMARY KEY (pessoa_id),
    CONSTRAINT fk_pessoa_fisica_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id) ON DELETE CASCADE
);

-- 7. Criação da Tabela Pessoa Jurídica (Extende Pessoa)
CREATE TABLE pessoa_juridica (
    pessoa_id BIGINT NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    inscricao_estadual VARCHAR(255),
    razao_social VARCHAR(150),
    inscricao_municipal VARCHAR(30),
    regime_tributario VARCHAR(50),
    CONSTRAINT pk_pessoa_juridica PRIMARY KEY (pessoa_id),
    CONSTRAINT fk_pessoa_juridica_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id) ON DELETE CASCADE
);

-- 8. Criação das Tabelas de Dados Extensionais (Relacionamentos OneToOne com MapsId)
CREATE TABLE dados_cliente (
    pessoa_id BIGINT NOT NULL,
    limite_credito DECIMAL(10,2),
    saldo_credito_utilizado DECIMAL(10,2) DEFAULT 0.00 NOT NULL,
    data_ultima_compra DATETIME,
    observacao_financeira TEXT,
    bloqueado_por_atraso BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_dados_cliente PRIMARY KEY (pessoa_id),
    CONSTRAINT fk_dados_cliente_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id) ON DELETE CASCADE
);

CREATE TABLE dados_fornecedor (
    pessoa_id BIGINT NOT NULL,
    prazo_pagamento BIGINT,
    chave_pix VARCHAR(100),
    banco_dados_bancarios VARCHAR(100),
    email_xml_nfe VARCHAR(100),
    avaliacao_qualidade VARCHAR(20),
    CONSTRAINT pk_dados_fornecedor PRIMARY KEY (pessoa_id),
    CONSTRAINT fk_dados_fornecedor_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id) ON DELETE CASCADE
);

CREATE TABLE dados_funcionario (
    pessoa_id BIGINT NOT NULL,
    matricula VARCHAR(20) NOT NULL UNIQUE,
    data_admissao DATE,
    cargo VARCHAR(100),
    salario_base DECIMAL(19,2) DEFAULT 0.00, -- Tratamento padrão para BigDecimals monetários amplos
    data_demissao DATE,
    ctps VARCHAR(30),
    pis_pasep VARCHAR(20),
    CONSTRAINT pk_dados_funcionario PRIMARY KEY (pessoa_id),
    CONSTRAINT fk_dados_funcionario_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa(id) ON DELETE CASCADE
);

-- 9. Criação da Tabela Usuário Base (Necessária para o Script V002 funcionar!)
-- Como seu script V002 faz "ALTER TABLE usuario ADD COLUMN...", criamos a tabela base aqui.
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id)
);