CREATE DATABASE IF NOT EXISTS langchain4jstudy
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE langchain4jstudy;

CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    phone VARCHAR(20) NOT NULL,
    communication_time DATETIME NOT NULL,
    province VARCHAR(50),
    estimated_score INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_reservation_phone (phone)
);
