ALTER TABLE usuarios
    ADD COLUMN token_redefinicao_senha VARCHAR(255) NULL,
ADD COLUMN token_redefinicao_expira_em DATETIME NULL;