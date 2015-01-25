# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table counter_product (
  id                        bigint not null,
  ean_code                  varchar(15),
  description               varchar(30),
  amount                    bigint,
  vat_amount                bigint,
  constraint pk_counter_product primary key (id))
;

create table linked_account (
  id                        bigint not null,
  user_id                   bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_account primary key (id))
;

create table recurring_product (
  id                        bigint not null,
  description               varchar(30),
  long_description          varchar(255),
  type                      varchar(1),
  period                    varchar(5),
  number_of_periods         integer,
  amount                    bigint,
  vat_amount                bigint,
  constraint ck_recurring_product_type check (type in ('M')),
  constraint ck_recurring_product_period check (period in ('Day','Year','Month')),
  constraint pk_recurring_product primary key (id))
;

create table s3file (
  id                        varchar(40) not null,
  user_id                   bigint,
  bucket                    varchar(255),
  name                      varchar(255),
  constraint uq_s3file_1 unique (user_id),
  constraint pk_s3file primary key (id))
;

create table security_role (
  id                        bigint not null,
  role_name                 varchar(255),
  constraint pk_security_role primary key (id))
;

create table token_action (
  id                        bigint not null,
  token                     varchar(255),
  target_user_id            bigint,
  type                      varchar(2),
  created                   timestamp,
  expires                   timestamp,
  constraint ck_token_action_type check (type in ('EV','PR')),
  constraint uq_token_action_token unique (token),
  constraint pk_token_action primary key (id))
;

create table users (
  id                        bigint not null,
  email                     varchar(30),
  name                      varchar(30),
  first_name                varchar(10),
  last_name                 varchar(20),
  last_login                timestamp,
  active                    boolean,
  email_validated           boolean,
  constraint pk_users primary key (id))
;

create table user_counter_purchase (
  id                        bigint not null,
  user_id                   bigint,
  payment_type              varchar(17),
  purchase_date             timestamp,
  payment_date              timestamp,
  constraint ck_user_counter_purchase_payment_type check (payment_type in ('RecurringPurchase','CounterCash','CounterCard')),
  constraint pk_user_counter_purchase primary key (id))
;

create table user_counter_purchase_item (
  id                        bigint not null,
  purchase_id               bigint,
  pieces                    integer,
  actual_amount             bigint,
  actual_vat_amount         bigint,
  constraint pk_user_counter_purchase_item primary key (id))
;

create table user_permission (
  id                        bigint not null,
  value                     varchar(255),
  constraint pk_user_permission primary key (id))
;

create table user_profile (
  id                        bigint not null,
  first_name                varchar(10),
  middle_name               varchar(10),
  last_name                 varchar(20),
  sex                       varchar(1),
  phone                     varchar(10),
  birth_date                timestamp,
  street                    varchar(20),
  street_no                 varchar(5),
  line2                     varchar(30),
  zip                       varchar(5),
  city                      varchar(20),
  country                   varchar(20),
  accepted_terms            varchar(1),
  contact_permission        varchar(1),
  payex_agreement_id        varchar(20),
  constraint pk_user_profile primary key (id))
;

create table user_recurring_purchase (
  id                        bigint not null,
  user_id                   bigint,
  product_id                bigint,
  purchase_date             timestamp,
  constraint pk_user_recurring_purchase primary key (id))
;

create table user_recurring_purchase_item (
  id                        bigint not null,
  start_period              timestamp,
  end_period                timestamp,
  actual_amount             bigint,
  actual_vat_amount         bigint,
  purchase_id               bigint,
  order_ref                 varchar(30),
  session_ref               varchar(30),
  initialize_redirect_url   varchar(60),
  initialize_error_code     varchar(10),
  initialize_description    varchar(30),
  complete_error_code       varchar(10),
  complete_description      varchar(30),
  complete_param_name       varchar(30),
  complete_transaction_number varchar(20),
  complete_transaction_time varchar(20),
  auto_pay_error_code       varchar(10),
  auto_pay_error_code_simple varchar(20),
  auto_pay_description      varchar(30),
  auto_pay_param_name       varchar(30),
  constraint pk_user_recurring_purchase_item primary key (id))
;


create table users_security_role (
  users_id                       bigint not null,
  security_role_id               bigint not null,
  constraint pk_users_security_role primary key (users_id, security_role_id))
;

create table users_user_permission (
  users_id                       bigint not null,
  user_permission_id             bigint not null,
  constraint pk_users_user_permission primary key (users_id, user_permission_id))
;
create sequence counter_product_seq;

create sequence linked_account_seq;

create sequence recurring_product_seq;

create sequence security_role_seq;

create sequence token_action_seq;

create sequence users_seq;

create sequence user_counter_purchase_seq;

create sequence user_counter_purchase_item_seq;

create sequence user_permission_seq;

create sequence user_profile_seq;

create sequence user_recurring_purchase_seq;

create sequence user_recurring_purchase_item_seq;

alter table linked_account add constraint fk_linked_account_user_1 foreign key (user_id) references users (id);
create index ix_linked_account_user_1 on linked_account (user_id);
alter table s3file add constraint fk_s3file_user_2 foreign key (user_id) references users (id);
create index ix_s3file_user_2 on s3file (user_id);
alter table token_action add constraint fk_token_action_targetUser_3 foreign key (target_user_id) references users (id);
create index ix_token_action_targetUser_3 on token_action (target_user_id);
alter table user_counter_purchase add constraint fk_user_counter_purchase_user_4 foreign key (user_id) references users (id);
create index ix_user_counter_purchase_user_4 on user_counter_purchase (user_id);
alter table user_counter_purchase_item add constraint fk_user_counter_purchase_item__5 foreign key (purchase_id) references user_counter_purchase (id);
create index ix_user_counter_purchase_item__5 on user_counter_purchase_item (purchase_id);
alter table user_recurring_purchase add constraint fk_user_recurring_purchase_use_6 foreign key (user_id) references users (id);
create index ix_user_recurring_purchase_use_6 on user_recurring_purchase (user_id);
alter table user_recurring_purchase add constraint fk_user_recurring_purchase_pro_7 foreign key (product_id) references recurring_product (id);
create index ix_user_recurring_purchase_pro_7 on user_recurring_purchase (product_id);
alter table user_recurring_purchase_item add constraint fk_user_recurring_purchase_ite_8 foreign key (purchase_id) references user_recurring_purchase (id);
create index ix_user_recurring_purchase_ite_8 on user_recurring_purchase_item (purchase_id);



alter table users_security_role add constraint fk_users_security_role_users_01 foreign key (users_id) references users (id);

alter table users_security_role add constraint fk_users_security_role_securi_02 foreign key (security_role_id) references security_role (id);

alter table users_user_permission add constraint fk_users_user_permission_user_01 foreign key (users_id) references users (id);

alter table users_user_permission add constraint fk_users_user_permission_user_02 foreign key (user_permission_id) references user_permission (id);

# --- !Downs

drop table if exists counter_product cascade;

drop table if exists linked_account cascade;

drop table if exists recurring_product cascade;

drop table if exists s3file cascade;

drop table if exists security_role cascade;

drop table if exists token_action cascade;

drop table if exists users cascade;

drop table if exists users_security_role cascade;

drop table if exists users_user_permission cascade;

drop table if exists user_counter_purchase cascade;

drop table if exists user_counter_purchase_item cascade;

drop table if exists user_permission cascade;

drop table if exists user_profile cascade;

drop table if exists user_recurring_purchase cascade;

drop table if exists user_recurring_purchase_item cascade;

drop sequence if exists counter_product_seq;

drop sequence if exists linked_account_seq;

drop sequence if exists recurring_product_seq;

drop sequence if exists security_role_seq;

drop sequence if exists token_action_seq;

drop sequence if exists users_seq;

drop sequence if exists user_counter_purchase_seq;

drop sequence if exists user_counter_purchase_item_seq;

drop sequence if exists user_permission_seq;

drop sequence if exists user_profile_seq;

drop sequence if exists user_recurring_purchase_seq;

drop sequence if exists user_recurring_purchase_item_seq;

