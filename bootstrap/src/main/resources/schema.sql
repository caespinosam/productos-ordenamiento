
create table products
(
   id integer not null,
   name varchar(255) not null,
   sales_unit integer not null,
   stock_small integer not null,
   stock_medium integer not null,
   stock_large integer not null,
   primary key(id)
);