CREATE TABLE dados_funcionario (
    pessoa_id BIGINT NOT NULL,
    matricula VARCHAR(20) NOT NULL,
    PRIMARY KEY (pessoa_id),
    CONSTRAINT fk_dados_funcionario_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE dados_funcionario ADD CONSTRAINT uk_matricula UNIQUE (matricula);