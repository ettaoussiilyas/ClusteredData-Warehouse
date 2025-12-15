CREATE TABLE IF NOT EXISTS fx_deals (
    deal_unique_id VARCHAR(255) NOT NULL,
    from_currency_iso_code VARCHAR(3) NOT NULL,
    to_currency_iso_code VARCHAR(3) NOT NULL,
    deal_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    deal_amount NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_fx_deals PRIMARY KEY (deal_unique_id)
);

-- Optimized lookups
CREATE UNIQUE INDEX IF NOT EXISTS idx_fx_deals_unique_id ON fx_deals (deal_unique_id);

CREATE INDEX IF NOT EXISTS idx_fx_deals_timestamp ON fx_deals (deal_timestamp);

-- Add comment to table
COMMENT ON TABLE fx_deals IS 'Stores foreign exchange deal transactions';

-- Comments to columns
COMMENT ON COLUMN fx_deals.deal_unique_id IS 'Unique identifier for the FX deal';
COMMENT ON COLUMN fx_deals.from_currency_iso_code IS 'Source currency ISO code (3 characters)';
COMMENT ON COLUMN fx_deals.to_currency_iso_code IS 'Target currency ISO code (3 characters)';
COMMENT ON COLUMN fx_deals.deal_timestamp IS 'Timestamp when the deal occurred';
COMMENT ON COLUMN fx_deals.deal_amount IS 'Amount of the deal in source currency';
COMMENT ON COLUMN fx_deals.created_at IS 'Timestamp when record was created';
COMMENT ON COLUMN fx_deals.updated_at IS 'Timestamp when record was last updated';
