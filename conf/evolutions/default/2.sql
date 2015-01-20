# --- !Ups

create table user_profile (
  id bigint not null,
  birth_date date null,
  sex char,
  phone varchar(20) null,
  street varchar(20) null,
  street_no varchar(20) null,
  line2 varchar(20) null,
  zip varchar(5) null,
  city varchar(20) null,
  country varchar(20) null,
  accepted_terms char null,
  contact_permission char null,
  payEx_agreement_id varchar(40),
  constraint pk_user_profile primary key (id)
);

create sequence user_profile_seq;

insert into security_role(id, role_name) values (2, 'userProfile')

# --- !Downs

drop table user_profile cascade;

drop sequence if exists user_profile_seq;

delete from security_role where id = 2;