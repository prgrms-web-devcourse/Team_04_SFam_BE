DROP TABLE token;

CREATE TABLE token
(
    user_id    bigint                             not null primary key,
    token      varchar(255)                       not null,
    expiry_date datetime default CURRENT_TIMESTAMP() not null,
    created_at datetime default CURRENT_TIMESTAMP not null,
    created_by varchar(255)                       null,
    updated_at datetime default CURRENT_TIMESTAMP not null,
    updated_by varchar(255)                       null
);