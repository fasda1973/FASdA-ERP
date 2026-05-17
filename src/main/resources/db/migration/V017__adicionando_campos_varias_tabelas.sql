-- =====================================================================
-- 1. NOVOS CAMPOS PARA A TABELA PAI (PESSOA)
-- Campos de contato universal, localização básica e controle de sistema
-- =====================================================================
ALTER TABLE pessoa 
    ADD COLUMN telefone VARCHAR(20) NULL,
    ADD COLUMN celular VARCHAR(20) NULL,
    ADD COLUMN logradouro VARCHAR(150) NULL,
    ADD COLUMN numero VARCHAR(20) NULL,
    ADD COLUMN complemento VARCHAR(100) NULL,
    ADD COLUMN bairro VARCHAR(100) NULL,
    ADD COLUMN cep VARCHAR(10) NULL,
    ADD COLUMN cidade VARCHAR(100) NULL,
    ADD COLUMN estado VARCHAR(2) NULL, -- Guarda a sigla do estado (EX: SP, PE, RJ)
    ADD COLUMN data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;

-- =====================================================================
-- 2. NOVOS CAMPOS PARA PESSOA FÍSICA
-- Dados importantes para contratos, RH e segurança
-- =====================================================================
ALTER TABLE pessoa_fisica 
    ADD COLUMN data_nascimento DATE NULL,
    ADD COLUMN sexo VARCHAR(15) NULL, -- Pode ser mapeado como Enum no Java
    ADD COLUMN estado_civil VARCHAR(20) NULL;

-- =====================================================================
-- 3. NOVOS CAMPOS PARA PESSOA JURÍDICA
-- Essencial para emissão de Nota Fiscal Eletrônica (NF-e)
-- =====================================================================
ALTER TABLE pessoa_juridica 
    ADD COLUMN razao_social VARCHAR(150) NULL, -- O 'nome' na tabela pai vira o Nome Fantasia
    ADD COLUMN inscricao_municipal VARCHAR(30) NULL,
    ADD COLUMN regime_tributario VARCHAR(50) NULL; -- Ex: Simples Nacional, Lucro Presumido

-- =====================================================================
-- 4. NOVOS CAMPOS PARA DADOS_CLIENTE
-- Controle financeiro, inadimplência e inteligência de vendas
-- =====================================================================
ALTER TABLE dados_cliente 
    ADD COLUMN saldo_credito_utilizado DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    ADD COLUMN data_ultima_compra DATETIME NULL,
    ADD COLUMN observacao_financeira TEXT NULL,
    ADD COLUMN bloqueado_por_atraso BOOLEAN NOT NULL DEFAULT FALSE;

-- =====================================================================
-- 5. NOVOS CAMPOS PARA DADOS_FORNECEDOR
-- Controle de compras e homologação de parceiros
-- =====================================================================
ALTER TABLE dados_fornecedor 
    ADD COLUMN chave_pix VARCHAR(100) NULL,
    ADD COLUMN banco_dados_bancarios VARCHAR(100) NULL, -- Ex: "Banco do Brasil - Ag: 1234 CC: 5678"
    ADD COLUMN email_xml_nfe VARCHAR(100) NULL, -- E-mail para onde o fornecedor manda o XML da nota
    ADD COLUMN avaliacao_qualidade VARCHAR(20) NULL; -- Ex: Nota A, B, C ou Excelente, Regular

-- =====================================================================
-- 6. NOVOS CAMPOS PARA DADOS_FUNCIONARIO
-- Rotinas básicas de Departamento Pessoal (DP)
-- =====================================================================
ALTER TABLE dados_funcionario 
    ADD COLUMN cargo VARCHAR(100) NULL,
    ADD COLUMN salario_base DECIMAL(10,2) NULL,
    ADD COLUMN data_demissao DATE NULL,
    ADD COLUMN ctps VARCHAR(30) NULL, -- Carteira de Trabalho
    ADD COLUMN pis_pasep VARCHAR(20) NULL;