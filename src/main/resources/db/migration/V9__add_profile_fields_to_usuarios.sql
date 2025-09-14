ALTER TABLE usuarios
ADD COLUMN biografia VARCHAR(500) NULL,
ADD COLUMN cidade VARCHAR(100) NULL,
-- ALTERADO: De VARCHAR para um tipo que suporta dados binários grandes
ADD COLUMN imagem_perfil MEDIUMBLOB NULL;