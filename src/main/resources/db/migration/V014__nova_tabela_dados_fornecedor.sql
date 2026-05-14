CREATE TABLE dados_fornecedor (
    pessoa_id BIGINT NOT NULL,
    prazo_pagamento BIGINT,
    PRIMARY KEY (pessoa_id),
    CONSTRAINT fk_dados_fornecedor_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
