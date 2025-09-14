-- Remove a coluna antiga que guardava o URL
ALTER TABLE eventos DROP COLUMN imagem_url;

-- Adiciona a nova coluna para guardar a imagem como BLOB
ALTER TABLE eventos ADD COLUMN imagem MEDIUMBLOB NULL;