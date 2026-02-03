DELETE FROM orders;
DELETE FROM product;
DELETE FROM customer;

INSERT INTO customer (id, name, phone) VALUES (1, '王小明', '13800000000');
INSERT INTO product (id, name, price, stock) VALUES (1, '智能音箱', 299.00, 100);
INSERT INTO orders (id, customer_id, product_id, status, amount, created_at)
VALUES (1, 1, 1, 'PAID', 299.00, CURRENT_TIMESTAMP);
