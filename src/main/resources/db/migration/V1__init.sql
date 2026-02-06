-- Enable UUID generation (Postgres)
create extension if not exists "pgcrypto";

-- ============
-- Notification (1 per event/user/type)
-- ============
create table if not exists notification (
                                            id uuid primary key,
                                            event_id uuid not null,
                                            user_id varchar(64) not null,
    tenant_id varchar(64),
    type varchar(64) not null, -- e.g. USER_SIGNED_UP, INVOICE_READY
    created_at timestamptz not null default now()
    );

create index if not exists idx_notification_user_created
    on notification(user_id, created_at desc);

create unique index if not exists uq_notification_event_user_type
    on notification(event_id, user_id, type);

-- ============
-- Notification delivery (1 per channel per notification)
-- ============
create table if not exists notification_delivery (
                                                     id uuid primary key,
                                                     notification_id uuid not null references notification(id) on delete cascade,

    channel varchar(16) not null,              -- EMAIL / SLACK / PUSH
    recipient varchar(256) not null,           -- email / slack target key / device token reference

    template_key varchar(128) not null,
    template_version int not null,

    status varchar(16) not null,               -- PENDING / SENT / FAILED / DEAD
    attempts int not null default 0,
    next_attempt_at timestamptz,

    last_error text,
    provider_message_id varchar(128),

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
    );

create unique index if not exists uq_delivery_notification_channel
    on notification_delivery(notification_id, channel);

create index if not exists idx_delivery_status_next_attempt
    on notification_delivery(status, next_attempt_at);

-- ============
-- Templates
-- ============
create table if not exists notification_template (
                                                     template_key varchar(128) not null,
    version int not null,
    channel varchar(16) not null,              -- EMAIL / SLACK / PUSH
    subject text,                              -- email only, nullable
    body text not null,
    active boolean not null default true,
    created_at timestamptz not null default now(),
    primary key (template_key, version, channel)
    );

-- ============
-- User preferences
-- ============
create table if not exists user_notification_preferences (
                                                             user_id varchar(64) primary key,
    allow_email boolean not null default true,
    allow_slack boolean not null default true,
    allow_push boolean not null default true,

    quiet_hours_start smallint,                -- 0..23 optional
    quiet_hours_end smallint,                  -- 0..23 optional
    locale varchar(16) default 'en'
    );

-- ============
-- Outbox (reliable publishing to Kafka)
-- ============
create table if not exists notification_outbox (
                                                   id uuid primary key,
                                                   topic varchar(128) not null,
    key varchar(128) not null,
    payload jsonb not null,

    status varchar(16) not null,               -- NEW / PUBLISHED / FAILED
    attempts int not null default 0,
    last_error text,

    created_at timestamptz not null default now(),
    published_at timestamptz
    );

create index if not exists idx_outbox_status_created
    on notification_outbox(status, created_at);

-- Optional: basic constraints (kept simple to avoid friction early)
-- You can tighten these later (e.g., enums via check constraints).
