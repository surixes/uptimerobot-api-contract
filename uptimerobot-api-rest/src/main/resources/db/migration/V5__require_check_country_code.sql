ALTER TABLE checks ALTER COLUMN country_code SET DEFAULT 'ZZ';
UPDATE checks SET country_code='ZZ' WHERE country_code IS NULL;
ALTER TABLE checks ALTER COLUMN country_code SET NOT NULL;
