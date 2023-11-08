-- 1번 seller
-- 1번 주문
INSERT INTO orders
(order_id, created_at, modified_at, order_name, status, member_id, order_number)
VALUES(1, '2023-11-07 22:29:38.493615000', '2023-11-07 22:29:38.493650000', '메인 타이틀 외 1개', 'COMPLETE', 1, 'ORDER-20231107-72');

INSERT INTO order_product
(order_product_id, created_at, ea, image1, image2, image3, main_explanation, main_image, main_title, modified_at, origin, origin_price, price, producer, product_main_explanation, product_sub_explanation, purchase_inquiry, status, `type`, view_cnt, category_id, seller_id)
VALUES(1, '2023-09-26 15:09:27.363409000', 1, 'https://image1', 'https://image2', 'https://image3', '메인 설명', 'https://메인이미지', '일반상품', '2023-09-26 16:50:10.618663000', '원산지', 10000, 9000, '공급자', '상품 메인 설명', '상품 서브 설명', '취급 방법', 'NORMAL', 'NORMAL', 0, 1, 1);

INSERT INTO order_product
(order_product_id, created_at, ea, image1, image2, image3, main_explanation, main_image, main_title, modified_at, origin, origin_price, price, producer, product_main_explanation, product_sub_explanation, purchase_inquiry, status, `type`, view_cnt, category_id, seller_id)
VALUES(2, '2023-09-26 15:09:27.363409000', 1, 'https://image1', 'https://image2', 'https://image3', '메인 설명', 'https://메인이미지', '옵션 상품', '2023-09-26 16:50:10.618663000', '원산지', 10000, 9000, '공급자', '상품 메인 설명', '상품 서브 설명', '취급 방법', 'NORMAL', 'OPTION', 0, 1, 1);

INSERT INTO order_option
(order_option_id, created_at, ea, extra_price, modified_at, name, status)
VALUES(1, '2023-10-16 12:40:18.265057000', 9, 300, '2023-10-16 12:40:18.265057000', '옵션1', 'NORMAL');



INSERT INTO delivery
(delivery_id, created_at, modified_at, owner_address, owner_address_detail, owner_name, owner_tel, owner_zonecode, recipient_address, recipient_address_detail, recipient_name, recipient_tel, recipient_zonecode, status, order_id, order_item_id)
VALUES(1, '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000', '주문자 주소', '주문자 상세 주소', '주문자명', '01012341234', '123-12', '수령인 주소', '수령인 상세 주소', '수령인명', '01043124321', '321-21', 'COMPLETE', 1, 1);


-- 2번 주문
INSERT INTO orders
(order_id, member_id, order_number, order_name, status, created_at, modified_at)
VALUES(2, 2, 'ORDER-20231010-2', '주문 이름2', 'COMPLETE', '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000');

--INSERT INTO order_item
--(order_item_id, created_at, ea, modified_at, product_price, product_total_price, status, order_id, product_id, option_id)
--VALUES(3, '2023-09-26 15:09:27.363409000', 5, '2023-09-26 15:09:27.363409000', 3000, 15000, 'ORDER', 2, 3, null);
--
--INSERT INTO delivery
--(delivery_id, created_at, modified_at, owner_address, owner_address_detail, owner_name, owner_tel, owner_zonecode, recipient_address, recipient_address_detail, recipient_name, recipient_tel, recipient_zonecode, status, order_id, order_item_id)
--VALUES(2, '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000', '주문자 주소', '주문자 상세 주소', '주문자명', '01012341234', '123-12', '수령인 주소', '수령인 상세 주소', '수령인명', '01043124321', '321-21', 'COMPLETE', 2, 3);

-- 2번 seller
-- 3번 주문
INSERT INTO orders
(order_id, member_id, order_number, order_name, status, created_at, modified_at)
VALUES(3, 2, 'ORDER-20231010-3', '주문 이름3', 'COMPLETE', '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000');

--INSERT INTO order_item
--(order_item_id, created_at, ea, modified_at, product_price, product_total_price, status, order_id, product_id, option_id)
--VALUES(4, '2023-09-26 15:09:27.363409000', 5, '2023-09-26 15:09:27.363409000', 1000, 5000, 'ORDER', 3, 4, null);
--
--INSERT INTO delivery
--(delivery_id, created_at, modified_at, owner_address, owner_address_detail, owner_name, owner_tel, owner_zonecode, recipient_address, recipient_address_detail, recipient_name, recipient_tel, recipient_zonecode, status, order_id, order_item_id)
--VALUES(3, '2023-09-26 15:09:27.363409000', '2023-09-26 15:09:27.363409000', '주문자 주소', '주문자 상세 주소', '주문자명', '01012341234', '123-12', '수령인 주소', '수령인 상세 주소', '수령인명', '01043124321', '321-21', 'COMPLETE', 3, 4);

