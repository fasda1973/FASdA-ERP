ALTER TABLE pessoa_juridica MODIFY cnpj VARCHAR(14) NOT NULL;
ALTER TABLE pessoa_juridica ADD CONSTRAINT uk_cnpj UNIQUE (cnpj);