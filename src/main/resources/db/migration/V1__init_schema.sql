create extension if not exists pgcrypto;

-- ACCOUNTS
create table if not exists accounts (
  id uuid primary key default gen_random_uuid(),
  external_id varchar(64) not null unique,
  owner_name varchar(120) not null,
  currency char(3) not null default 'USD',
  balance_cents bigint not null default 0,
  status varchar(20) not null default 'ACTIVE',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint chk_currency_usd check (currency = 'USD'),
  constraint chk_balance_non_negative check (balance_cents >= 0)
);

create index if not exists idx_accounts_owner_name on accounts(owner_name);

-- TRANSACTIONS
create table if not exists transactions (
  id uuid primary key default gen_random_uuid(),
  account_id uuid not null references accounts(id),
  type varchar(20) not null,
  amount_cents bigint not null,
  currency char(3) not null default 'USD',
  idempotency_key varchar(80) null,
  description varchar(200) null,
  created_at timestamptz not null default now(),
  constraint chk_tx_currency_usd check (currency = 'USD'),
  constraint chk_amount_positive check (amount_cents > 0)
);

create index if not exists idx_transactions_account_id_created_at
  on transactions(account_id, created_at desc);

-- idempotency uniqueness per account
create unique index if not exists uq_tx_account_idempotency
  on transactions(account_id, idempotency_key)
  where idempotency_key is not null;