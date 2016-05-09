# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table organization (
  organization_id               serial not null,
  short_name                    varchar(255),
  parental_organization_id      integer,
  constraint pk_organization primary key (organization_id)
);

create table users (
  users_id                      serial not null,
  login                         varchar(255),
  first_name                    varchar(255),
  last_name                     varchar(255),
  md5_password                  varchar(255),
  organization_id               integer,
  constraint pk_users primary key (users_id)
);

alter table organization add constraint fk_organization_parental_organization_id foreign key (parental_organization_id) references organization (organization_id) on delete restrict on update restrict;
create index ix_organization_parental_organization_id on organization (parental_organization_id);

alter table users add constraint fk_users_organization_id foreign key (organization_id) references organization (organization_id) on delete restrict on update restrict;
create index ix_users_organization_id on users (organization_id);


# --- !Downs

alter table if exists organization drop constraint if exists fk_organization_parental_organization_id;
drop index if exists ix_organization_parental_organization_id;

alter table if exists users drop constraint if exists fk_users_organization_id;
drop index if exists ix_users_organization_id;

drop table if exists organization cascade;

drop table if exists users cascade;

