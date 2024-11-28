create table user_message
(
    id char(36) not null primary key,
    user_id varchar(255) not null,
    text    varchar(255) not null,
    time    timestamp default current_timestamp
);