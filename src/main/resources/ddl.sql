create table WIDGET (
  ID bigint primary key not null generated always as identity,
  ACCOUNT_TYPE varchar(1) not null,
  EMAIL varchar(50) not null,
  AMOUNT double default 0,
  OVERDRAFT double default 0
);
