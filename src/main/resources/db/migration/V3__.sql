CREATE TABLE cqrs.product_outbox
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    aggregate_id VARCHAR(255) NULL,
    payload      VARCHAR(255) NULL,
    created_at   datetime NULL,
    processed    BIT(1) NULL,
    CONSTRAINT pk_product_outbox PRIMARY KEY (id)
);