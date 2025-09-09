CREATE TABLE avaliacoes (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            nota INT NOT NULL,
                            comentario TEXT,
                            data_avaliacao DATETIME NOT NULL,
                            usuario_id BIGINT NOT NULL,
                            evento_id BIGINT NOT NULL,
                            FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
                            FOREIGN KEY (evento_id) REFERENCES eventos(id),
                            UNIQUE (usuario_id, evento_id) -- Garante que um utilizador só pode avaliar um evento uma vez
);