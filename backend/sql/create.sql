use travel_planner;

create table if not exists user
(
    id         bigint auto_increment comment 'id'
        primary key,
    username       varchar(64)                  not null comment '用户名称',
    password        varchar(128)                 not null comment '用户密码',
    email           varchar(64)                  not null comment '用户邮箱',
    create_time datetime default CURRENT_TIMESTAMP comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0 comment '是否删除'
)
    comment '用户' collate = utf8mb4_unicode_ci;



create table if not exists travel_plan
(
    id         bigint auto_increment comment 'id'
        primary key,
    title       varchar(64)                  not null comment '旅行计划名称',
    content     text                         not null comment '旅行计划内容',
    query       text                         not null comment  '用户的提问',
    user_id        bigint                    not null comment '用户id',
    create_time datetime default CURRENT_TIMESTAMP comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0 comment '是否删除'
)
    comment '旅行计划' collate = utf8mb4_unicode_ci