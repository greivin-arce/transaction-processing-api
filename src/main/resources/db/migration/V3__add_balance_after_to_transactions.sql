ALTER TABLE transactions
ADD COLUMN balance_after_cents BIGINT NOT NULL DEFAULT 0;