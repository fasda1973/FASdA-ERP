ALTER TABLE log_auditoria
ADD COLUMN tela VARCHAR(255) AFTER operacao,
ADD COLUMN id_registro BIGINT AFTER tela;