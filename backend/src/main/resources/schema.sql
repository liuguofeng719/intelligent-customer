CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    phone VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    stock INT NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_session (
    session_id VARCHAR(64) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    last_active_at TIMESTAMP NOT NULL,
    intent VARCHAR(32) NOT NULL,
    intent_updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_clarify_config (
    id BIGINT PRIMARY KEY,
    short_question_length INT,
    min_question_length INT,
    enable_pronoun_check TINYINT,
    generic_template TEXT,
    pronoun_template TEXT,
    order_template TEXT,
    product_template TEXT,
    customer_template TEXT,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_clarify_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64),
    reason VARCHAR(32) NOT NULL,
    question TEXT,
    created_at TIMESTAMP NOT NULL
);
