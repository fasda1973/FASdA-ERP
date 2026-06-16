-- Adiciona a coluna de perfil na tabela usuario com o valor padrão 'COMUM'
ALTER TABLE usuario ADD COLUMN perfil VARCHAR(30) NOT NULL DEFAULT 'COMUM';

-- Garante que o administrador master do sistema seja ADMINISTRADOR
UPDATE usuario SET perfil = 'ADMINISTRADOR' WHERE login = 'admin';