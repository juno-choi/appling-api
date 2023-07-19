insert into Member (email, name, nickname, password, birth, role, created_at, modified_at) values ('seller@appling.com', '김셀러', '셀러', '$2a$10$0WXVKP1oBfC0oOYRDt8uYuWJcmgK9QY1bAqkg6HwytyhaCoxBdZ1i', '20001122', 'SELLER', '2023-05-31 10:21:03.961557', '2023-05-31 10:21:03.961557');
insert into Member (email, name, nickname, password, birth, role, created_at, modified_at) values ('seller2@appling.com', '김셀러2', '셀러2', '$2a$10$0WXVKP1oBfC0oOYRDt8uYuWJcmgK9QY1bAqkg6HwytyhaCoxBdZ1i', '20001122', 'SELLER', '2023-05-31 10:21:03.961557', '2023-05-31 10:21:03.961557');
insert into Member (email, name, nickname, password, birth, role, created_at, modified_at) values ('member@appling.com', '김멤버', '멤버', '$2a$10$0WXVKP1oBfC0oOYRDt8uYuWJcmgK9QY1bAqkg6HwytyhaCoxBdZ1i', '20001122', 'MEMBER', '2023-05-31 10:21:03.961557', '2023-05-31 10:21:03.961557');

insert into Category (name, status, created_at, modified_at) values ('과일', 'USE', '2023-07-04 10:21:03.961557', '2023-07-04 10:21:03.961557');
insert into Category (name, status, created_at, modified_at) values ('채소', 'USE', '2023-07-04 10:21:03.961557', '2023-07-04 10:21:03.961557');
insert into Category (name, status, created_at, modified_at) values ('육류', 'USE', '2023-07-04 10:21:03.961557', '2023-07-04 10:21:03.961557');

insert into Seller (member_id, company, tel, address, email, created_at, modified_at) values ('1', '회사명', '01012341234', '회사 주소', 'seller@mail.com', '2023-07-04 10:21:03.961557', '2023-07-04 10:21:03.961557');
insert into Seller (member_id, company, tel, address, email, created_at, modified_at) values ('2', '과수원명', '01012341234', '회사 주소', 'seller@mail.com', '2023-07-04 10:21:03.961557', '2023-07-04 10:21:03.961557');