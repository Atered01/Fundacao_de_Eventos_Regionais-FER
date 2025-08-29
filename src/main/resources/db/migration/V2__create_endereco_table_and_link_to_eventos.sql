-- 1. Cria a nova tabela para armazenar endereços de forma estruturada
CREATE TABLE enderecos (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           logradouro VARCHAR(255) NOT NULL,
                           numero VARCHAR(20),
                           bairro VARCHAR(100),
                           cidade VARCHAR(100) NOT NULL,
                           estado VARCHAR(50) NOT NULL,
                           cep VARCHAR(20),
                           latitude DECIMAL(10, 8),  -- Para a funcionalidade de proximidade
                           longitude DECIMAL(11, 8), -- Para a funcionalidade de proximidade
                           CONSTRAINT uq_endereco UNIQUE (logradouro, numero, cidade) -- Evita endereços duplicados
);

-- 2. Adiciona a coluna de chave estrangeira na tabela de eventos
ALTER TABLE eventos
    ADD COLUMN endereco_id BIGINT;

-- 3. Adiciona a restrição (constraint) de chave estrangeira
ALTER TABLE eventos
    ADD CONSTRAINT fk_eventos_enderecos
        FOREIGN KEY (endereco_id) REFERENCES enderecos(id);

-- 4. Remove a antiga coluna 'local', pois agora os dados estão na tabela 'enderecos'
ALTER TABLE eventos
DROP COLUMN local;