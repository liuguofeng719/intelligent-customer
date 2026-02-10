DELETE FROM orders;
DELETE FROM product;
DELETE FROM customer;

INSERT INTO customer (id, name, phone) VALUES (1, '王小明', '13800000000');
INSERT INTO customer (id, name, phone) VALUES (2, '李晓雨', '13900000001');
INSERT INTO customer (id, name, phone) VALUES (3, '陈一凡', '13700000002');

INSERT INTO product (id, name, price, stock) VALUES (1, '智能音箱', 299.00, 100);
INSERT INTO product (id, name, price, stock) VALUES (2, '智能手表', 699.00, 45);
INSERT INTO product (id, name, price, stock) VALUES (3, '家庭路由器', 199.00, 120);

INSERT INTO orders (id, customer_id, product_id, status, amount, created_at)
VALUES (1, 1, 1, 'PAID', 299.00, CURRENT_TIMESTAMP);
INSERT INTO orders (id, customer_id, product_id, status, amount, created_at)
VALUES (2, 2, 2, 'SHIPPED', 699.00, CURRENT_TIMESTAMP);
INSERT INTO orders (id, customer_id, product_id, status, amount, created_at)
VALUES (3, 3, 3, 'CREATED', 199.00, CURRENT_TIMESTAMP);
INSERT INTO orders (id, customer_id, product_id, status, amount, created_at)
VALUES (4, 1, 2, 'PAID', 699.00, CURRENT_TIMESTAMP);
