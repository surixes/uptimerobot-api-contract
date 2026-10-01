ALTER TABLE checks ADD COLUMN country_code VARCHAR(2);
ALTER TABLE checks ADD CONSTRAINT ck_checks_country_code CHECK (country_code IN ('RU','BY','KZ','ZZ'));
