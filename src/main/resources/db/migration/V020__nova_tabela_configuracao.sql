CREATE TABLE configuracao (
    chave VARCHAR(50) PRIMARY KEY,
    valor VARCHAR(255) NOT NULL,
    descricao VARCHAR(255)
);

-- Inserindo os padrões de fábrica do seu ERP
INSERT INTO configuracao (chave, valor, descricao) VALUES 
('CAMINHO_UPLOAD_IMAGENS', '/uploads/imagens', 'Diretório para salvar as imagens do sistema'),
('PERMITIR_ESTOQUE_NEGATIVO', 'false', 'Permite ou bloqueia a venda de produtos sem saldo (true/false)'),
('MARGEM_LUCRO_MINIMA', '15.00', 'Porcentagem mínima de lucro exigida nas vendas'),
('DIAS_CARENCIA_COBRANCA', '5', 'Dias após o vencimento antes de bloquear o cliente'),
('SMTP_HOST', 'smtp.gmail.com', 'Servidor de saída de e-mails'),
('SMTP_PORT', '587', 'Porta do servidor SMTP'),
('SMTP_USER', 'sistema@suaempresa.com', 'Usuário do e-mail disparador');