CREATE TABLE cqrs.product_command
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    product_code  VARCHAR(255) NULL,
    name          VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    price DOUBLE NULL,
    version       INT NOT NULL,
    CONSTRAINT pk_product_command PRIMARY KEY (id)
);