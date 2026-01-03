-- TRANSACTIONS LEDGER
create table if not exists transactions (
  id uuid primary key default gen_random_uuid(),
  account_id uuid not null references accounts(id),
  type varchar(20) not null,
  amount_cents bigint not null,
  currency char(3) not null default 'USD',
  idempotency_key varchar(80),
  description varchar(200),
  created_at timestamptz not null default now(),

  constraint chk_tx_amount_positive check (amount_cents > 0),
  constraint chk_tx_currency_usd check (currency = 'USD')
);

create index if not exists idx_transactions_account_id_created_at
  on transactions(account_id, created_at desc);

create unique index if not exists uq_tx_account_idempotency
  on transactions(account_id, idempotency_key)
  where idempotency_key is not null;