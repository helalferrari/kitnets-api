-- Script de Correção de Schema
-- Execute este script no banco de dados para corrigir a falta da coluna latitude e remover a coluna obsoleta nonumber.

-- Adiciona a coluna latitude se não existir
ALTER TABLE KITNET ADD COLUMN IF NOT EXISTS latitude VARCHAR(255);

-- Remove a coluna nonumber se existir
ALTER TABLE KITNET DROP COLUMN IF EXISTS nonumber;
