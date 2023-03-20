create table idp4k_response_storage (
    "key" varchar(64) not null,
    "group" varchar(64) not null,
    "json_value" jsonb not null,
    CONSTRAINT pk_idp4k_response_storage PRIMARY KEY ("key", "group")
);

create table idempotence_lock (
    "key" varchar(64) not null,
    "group" varchar(64) not null,
    status varchar(32) not null,
    CONSTRAINT pk_idp4k_idempotence_lock PRIMARY KEY ("key", "group")
);