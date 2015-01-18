# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table userProfile (
  id bigint not null,
  birthDate date null,
  sex char,
  phone varchar(20) null,
  street varchar(20) null,
  streetNo varchar(20) null,
  line2 varchar(20) null,
  zip varchar(5) null,
  city varchar(20) null,
  country varchar(20) null,
  acceptedTerms char null,
  contactPermission char null,
  payExAgreementId varchar(40),
  constraint pk_userProfile primary key (id)
);

create sequence userProfile_seq;

insert into security_role(id, role_name) values (2, 'userProfile')

# --- !Downs

drop table userProfile cascade;

drop sequence if exists userProfile_seq;

delete from security_role where id = 2;