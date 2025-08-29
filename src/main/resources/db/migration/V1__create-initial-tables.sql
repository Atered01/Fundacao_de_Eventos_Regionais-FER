CREATE TABLE categorias (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            nome VARCHAR(100) NOT NULL,
                            descricao TEXT
);

CREATE TABLE organizadores (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               nome VARCHAR(255) NOT NULL,
                               email VARCHAR(100) NOT NULL UNIQUE,
                               telefone VARCHAR(20)
);


CREATE TABLE eventos (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(255) NOT NULL,
                         descricao TEXT NOT NULL,
                         data DATETIME NOT NULL,
                         local VARCHAR(255) NOT NULL,
                         limite_participantes INT,
                         categoria_id BIGINT,
                         organizador_id BIGINT,
                         FOREIGN KEY (categoria_id) REFERENCES categorias(id),
                         FOREIGN KEY (organizador_id) REFERENCES organizadores(id)
);

CREATE TABLE participantes (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               nome VARCHAR(255) NOT NULL,
                               email VARCHAR(100) NOT NULL,
                               evento_id BIGINT NOT NULL,
                               FOREIGN KEY (evento_id) REFERENCES eventos(id),
                               UNIQUE (email, evento_id)
);