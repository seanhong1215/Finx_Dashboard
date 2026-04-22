CREATE DATABASE IF NOT EXISTS finx_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE finx_db;

CREATE USER IF NOT EXISTS 'finx_user'@'localhost' IDENTIFIED BY 'finx_password';
GRANT ALL PRIVILEGES ON finx_db.* TO 'finx_user'@'localhost';
FLUSH PRIVILEGES;

CREATE TABLE IF NOT EXISTS users (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255) DEFAULT NULL,
    role       ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    is_active  TINYINT(1)   NOT NULL DEFAULT 1,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS accounts (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    user_id        BIGINT       NOT NULL,
    account_number VARCHAR(20)  NOT NULL UNIQUE,
    account_type   ENUM('CHECKING','SAVINGS','INVESTMENT') NOT NULL DEFAULT 'CHECKING',
    balance        DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    currency       VARCHAR(3)   NOT NULL DEFAULT 'TWD',
    is_active      TINYINT(1)   NOT NULL DEFAULT 1,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS transactions (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    account_id    BIGINT        NOT NULL,
    type          ENUM('INCOME','EXPENSE','TRANSFER') NOT NULL,
    category      VARCHAR(50)   NOT NULL,
    description   VARCHAR(200)  NOT NULL,
    amount        DECIMAL(15,2) NOT NULL,
    direction     ENUM('IN','OUT') NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    ref_number    VARCHAR(50)   DEFAULT NULL,
    transacted_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_tx_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Data
INSERT INTO users (username, email, password, full_name, role) VALUES
('james', 'james.wilson@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe1cdZ5pRFNRa/Nh3hpJ1V2P8sMJBK9a',
 'James Wilson', 'USER');

INSERT INTO accounts (user_id, account_number, account_type, balance, currency) VALUES
(1, 'TW001-00000001', 'CHECKING', 156500.00, 'TWD');

INSERT INTO transactions (account_id, type, category, description, amount, direction, balance_after, transacted_at) VALUES
(1, 'INCOME',  'Salary',          '三月薪資入帳',  80000.00, 'IN',  156500.00, '2025-03-05 09:00:00'),
(1, 'EXPENSE', 'Food & Drink',    'Starbucks',       195.00, 'OUT', 156305.00, '2025-03-04 10:30:00'),
(1, 'EXPENSE', 'Entertainment',   'Netflix 月費',    449.00, 'OUT', 155856.00, '2025-03-03 08:00:00'),
(1, 'EXPENSE', 'Shopping',        'Amazon',         2699.00, 'OUT', 153157.00, '2025-03-02 14:20:00'),
(1, 'EXPENSE', 'Health',          '健身房月費',     1050.00, 'OUT', 152107.00, '2025-03-01 07:00:00');
