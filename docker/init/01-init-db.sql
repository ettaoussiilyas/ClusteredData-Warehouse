-- Initialize FX Deals Database
CREATE DATABASE fx_deals_warehouse;

DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'fx_user') THEN

      CREATE ROLE fx_user LOGIN PASSWORD 'fx_password';
   END IF;
END
$do$;

GRANT ALL PRIVILEGES ON DATABASE fx_deals_warehouse TO fx_user;