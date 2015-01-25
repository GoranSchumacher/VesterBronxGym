# --- !Ups

create table s3file (
  id                        varchar(40) not null,
  user_id                   bigint,
  bucket                    varchar(255),
  name                      varchar(255),
  constraint uq_s3file_1 unique (user_id),
  constraint pk_s3file primary key (id))
;

alter table s3file add constraint fk_s3file_user_2 foreign key (user_id) references users (id);
create index ix_s3file_user_2 on s3file (user_id);
# --- !Downs

drop table if exists s3file cascade;
